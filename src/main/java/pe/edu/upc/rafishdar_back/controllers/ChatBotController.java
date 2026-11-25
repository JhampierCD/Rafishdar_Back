package pe.edu.upc.rafishdar_back.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.rafishdar_back.entities.CuotaPesca;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.entities.Temporada;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.repositories.DetalleCapturaRepository;
import pe.edu.upc.rafishdar_back.repositories.FaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.CuotaPescaRepository;
import pe.edu.upc.rafishdar_back.repositories.TemporadaRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.serviceimpl.GeminiService;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/rafishdar/chatbot")
public class ChatBotController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private FaenaRepository faenaRepository;

    @Autowired
    private DetalleCapturaRepository detalleCapturaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemporadaRepository temporadaRepository;

    @Autowired
    private CuotaPescaRepository cuotaPescaRepository;

    private static final String SYSTEM_CONTEXT = """
        Eres RafiBot, el asistente de Rafishdar, una plataforma digital para pescadores
        artesanales e industriales del Perú. Ayudas con:
        - Consejos de pesca y zonas seguras
        - Interpretación de condiciones climáticas
        - Normativa pesquera peruana (vedas, cuotas)
        - Registro de faenas y bitácoras
        - Eficiencia de combustible y costos operativos
        Responde siempre en español, de forma clara y concisa (máx 200 palabras).
        Si te preguntan algo fuera de pesca o la plataforma, indica amablemente que
        solo puedes ayudar con temas relacionados a Rafishdar y la pesca.
        """;

    /** Pregunta simple (un turno) */
    @PostMapping("/preguntar")
    public ResponseEntity<Map<String, String>> preguntar(@RequestBody Map<String, String> body) {
        String mensaje = body.getOrDefault("mensaje", "").trim();
        if (mensaje.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El mensaje no puede estar vacío"));
        }
        String prompt = SYSTEM_CONTEXT + "\n\nUsuario: " + mensaje;
        String respuesta = geminiService.generarTexto(prompt);
        return ResponseEntity.ok(Map.of("respuesta", respuesta, "tipo", "model"));
    }

    /** Conversación multi-turno sin datos */
    @PostMapping("/conversar")
    public ResponseEntity<Map<String, String>> conversar(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> historial = (List<Map<String, String>>) body.get("historial");
        if (historial == null || historial.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El historial no puede estar vacío"));
        }

        List<Map<String, String>> mensajesCompletos = new ArrayList<>();
        mensajesCompletos.add(Map.of("role", "user", "text", SYSTEM_CONTEXT + "\n\nConfirma que entendiste tu rol."));
        mensajesCompletos.add(Map.of("role", "model", "text", "Entendido. Soy RafiBot, listo para ayudarte."));
        mensajesCompletos.addAll(historial);

        String respuesta = geminiService.generarTextoConHistorial(mensajesCompletos);
        return ResponseEntity.ok(Map.of("respuesta", respuesta, "tipo", "model"));
    }

    /**
     * Conversación con contexto de datos reales del usuario (faenas, capturas, zonas).
     * Si no se envía usuarioId, intenta tomar el ID del usuario autenticado.
     */
    @PostMapping("/conversar-con-datos")
    public ResponseEntity<Map<String, String>> conversarConDatos(
            @RequestParam(required = false) Long usuarioId,
            @RequestBody Map<String, Object> body) {

        @SuppressWarnings("unchecked")
        List<Map<String, String>> historial = (List<Map<String, String>>) body.get("historial");
        if (historial == null || historial.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El historial no puede estar vacío"));
        }

        Long targetUserId = usuarioId;
        if (targetUserId == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                targetUserId = userRepository.findByEmail(auth.getName())
                        .map(User::getId)
                        .orElse(null);
            }
        }

        if (targetUserId == null) {
            List<Map<String, String>> mensajesCompletos = new ArrayList<>();
            mensajesCompletos.add(Map.of("role", "user", "text", SYSTEM_CONTEXT + "\n\nConfirma que entendiste tu rol."));
            mensajesCompletos.add(Map.of("role", "model", "text", "Entendido. Soy RafiBot, listo para ayudarte."));
            mensajesCompletos.addAll(historial);
            String respuesta = geminiService.generarTextoConHistorial(mensajesCompletos);
            return ResponseEntity.ok(Map.of("respuesta", respuesta, "tipo", "model"));
        }

        String contexto = construirContextoUsuario(targetUserId);
        String respuesta = geminiService.generarTextoConContexto(historial, contexto);
        return ResponseEntity.ok(Map.of("respuesta", respuesta, "tipo", "model"));
    }

    private String construirContextoUsuario(Long usuarioId) {
        StringBuilder ctx = new StringBuilder();
        ctx.append(SYSTEM_CONTEXT).append("\n\n");
        ctx.append("=== DATOS REALES DEL PESCADOR (usuario ID: ").append(usuarioId).append(") ===\n");

        try {
            List<Faena> faenas = faenaRepository.findByUsuarioId(usuarioId);
            int total = faenas.size();
            long finalizadas = faenas.stream().filter(f -> "Finalizada".equals(f.getEstado())).count();
            long enCurso = faenas.stream().filter(f -> "En curso".equals(f.getEstado())).count();

            ctx.append("Faenas totales registradas: ").append(total).append("\n");
            ctx.append("Faenas finalizadas: ").append(finalizadas).append("\n");
            ctx.append("Faenas en curso actualmente: ").append(enCurso).append("\n");

            faenas.stream()
                    .sorted(Comparator.comparing(Faena::getFechaHoraSalida, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(5)
                    .forEach(f -> {
                        ctx.append("- Faena #").append(f.getId())
                                .append(" | Estado: ").append(f.getEstado())
                                .append(" | Embarcación: ").append(f.getEmbarcacion() != null ? f.getEmbarcacion().getNombre() : "N/A")
                                .append(" | Salida: ").append(f.getFechaHoraSalida() != null ? f.getFechaHoraSalida() : f.getFechaHoraSalidaProgramada())
                                .append("\n");

                        if (f.getBitacoras() != null && !f.getBitacoras().isEmpty()) {
                            f.getBitacoras().forEach(b -> {
                                ctx.append("  Bitácora #").append(b.getId());
                                if (b.getZonaPesca() != null) {
                                    ctx.append(" | Zona: ").append(b.getZonaPesca().getNombreZona());
                                }
                                ctx.append(" | Coord: (").append(b.getLatitudPesca()).append(", ").append(b.getLongitudPesca()).append(")");
                                if (b.getObservaciones() != null && !b.getObservaciones().isBlank()) {
                                    ctx.append(" | Obs: ").append(b.getObservaciones());
                                }
                                ctx.append("\n");

                                if (b.getDetallesCaptura() != null) {
                                    b.getDetallesCaptura().forEach(dc -> {
                                        ctx.append("    Captura: ").append(dc.getVolumenKg()).append(" kg de ")
                                                .append(dc.getEspecie() != null ? dc.getEspecie().getNombreComun() : "especie desconocida")
                                                .append("\n");
                                    });
                                }
                            });
                        }
                    });

            Map<String, Double> capturasPorEspecie = new HashMap<>();
            faenas.forEach(f -> {
                if (f.getBitacoras() != null) {
                    f.getBitacoras().forEach(b -> {
                        if (b.getDetallesCaptura() != null) {
                            b.getDetallesCaptura().forEach(dc -> {
                                String especie = dc.getEspecie() != null ? dc.getEspecie().getNombreComun() : "desconocida";
                                capturasPorEspecie.merge(especie, dc.getVolumenKg(), Double::sum);
                            });
                        }
                    });
                }
            });

            if (!capturasPorEspecie.isEmpty()) {
                ctx.append("\nResumen de capturas históricas por especie:\n");
                capturasPorEspecie.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .forEach(e -> ctx.append("- ").append(e.getKey()).append(": ").append(String.format("%.1f", e.getValue())).append(" kg total\n"));
            }

            Map<String, Long> frecuenciaZonas = new HashMap<>();
            faenas.forEach(f -> {
                if (f.getBitacoras() != null) {
                    f.getBitacoras().forEach(b -> {
                        if (b.getZonaPesca() != null) {
                            frecuenciaZonas.merge(b.getZonaPesca().getNombreZona(), 1L, Long::sum);
                        }
                    });
                }
            });

            if (!frecuenciaZonas.isEmpty()) {
                ctx.append("\nZonas más frecuentadas:\n");
                frecuenciaZonas.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .forEach(e -> ctx.append("- ").append(e.getKey()).append(": ").append(e.getValue()).append(" bitácoras\n"));
            }

        } catch (Exception ex) {
            ctx.append("(No se pudieron cargar todos los datos históricos)\n");
        }


        // Temporadas programadas / registradas en el sistema
        try {
            List<Temporada> temporadas = temporadaRepository.findAll();
            if (temporadas != null && !temporadas.isEmpty()) {
                ctx.append("\nTemporadas programadas en el sistema:\n");
                for (Temporada t : temporadas) {
                    ctx.append("- ").append(t.getNombreTemporada())
                            .append(" (ID: ").append(t.getId()).append(")")
                            .append(" | Desde: ").append(t.getFechaInicio())
                            .append(" hasta ").append(t.getFechaFin());
                    if (t.getDescripcion() != null && !t.getDescripcion().isBlank()) {
                        ctx.append(" | Desc: ").append(t.getDescripcion());
                    }
                    ctx.append("\n");
                }
            }
            List<CuotaPesca> cuotas = cuotaPescaRepository.findAll();
            if (cuotas != null && !cuotas.isEmpty()) {
                ctx.append("\nCuotas de pesca programadas:\n");
                for (CuotaPesca c : cuotas) {
                    String esp = c.getEspecie() != null ? c.getEspecie().getNombreComun() : "Especie";
                    String temp = c.getTemporada() != null ? c.getTemporada().getNombreTemporada() : "Temporada";
                    ctx.append("- ").append(esp)
                            .append(" | Temporada: ").append(temp)
                            .append(" | Límite industrial: ").append(c.getLimiteToneladasIndustrial()).append(" t\n");
                }
            }
        } catch (Exception ignoredTemp) {}

        ctx.append("\nUsa estos datos reales para responder las preguntas del pescador de forma personalizada y precisa.");
        return ctx.toString();
    }
}