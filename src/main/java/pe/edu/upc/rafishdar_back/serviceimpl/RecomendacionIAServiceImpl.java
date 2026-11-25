package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.rafishdar_back.entities.*;
import pe.edu.upc.rafishdar_back.repositories.*;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class RecomendacionIAServiceImpl implements RecomendacionIAService {

    private static final Set<String> NIVELES_VALIDOS = Set.of("BAJO", "MEDIO", "ALTO");

    @Autowired private RecomendacionIARepository recomendacionIARepository;
    @Autowired private FaenaRepository faenaRepository;
    @Autowired private BitacoraRepository bitacoraRepository;
    @Autowired private CondicionClimaticaRepository condicionClimaticaRepository;
    @Autowired private GastoOperativoRepository gastoOperativoRepository;
    @Autowired private GeminiService geminiService;
    @Autowired private TemporadaRepository temporadaRepository;
    @Autowired private CuotaPescaRepository cuotaPescaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RecomendacionIA> listarTodoRecomendaciones() {
        return recomendacionIARepository.findAll();
    }

    @Override
    public RecomendacionIA insertarRecomendaciones(RecomendacionIA recomendacionIA) {
        validarNivelRiesgo(recomendacionIA.getNivelRiesgo());
        recomendacionIA.setNivelRiesgo(recomendacionIA.getNivelRiesgo().toUpperCase());

        if (recomendacionIA.getFaena() == null || recomendacionIA.getFaena().getId() == null) {
            throw new IllegalArgumentException("La recomendación debe estar asociada a una faena válida");
        }
        Faena faena = faenaRepository.findById(recomendacionIA.getFaena().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La faena con id " + recomendacionIA.getFaena().getId() + " no existe"));

        if (recomendacionIARepository.existsByFaenaId(faena.getId())) {
            throw new IllegalStateException("Ya existe una recomendación para esta faena. Use regenerar para actualizarla.");
        }

        recomendacionIA.setFaena(faena);
        if (recomendacionIA.getFechaGeneracion() == null) {
            recomendacionIA.setFechaGeneracion(LocalDateTime.now());
        }
        return recomendacionIARepository.save(recomendacionIA);
    }

    @Override
    public RecomendacionIA actualizarRecomendaciones(RecomendacionIA recomendacionIA) {
        if (recomendacionIA.getId() == null) return null;
        RecomendacionIA encontrada = buscarPorId(recomendacionIA.getId());
        if (encontrada == null) return null;

        if (recomendacionIA.getAnalisisTexto() != null)
            encontrada.setAnalisisTexto(recomendacionIA.getAnalisisTexto());
        if (recomendacionIA.getNivelRiesgo() != null) {
            validarNivelRiesgo(recomendacionIA.getNivelRiesgo());
            encontrada.setNivelRiesgo(recomendacionIA.getNivelRiesgo().toUpperCase());
        }
        if (recomendacionIA.getFechaGeneracion() != null)
            encontrada.setFechaGeneracion(recomendacionIA.getFechaGeneracion());

        return recomendacionIARepository.save(encontrada);
    }

    @Override
    @Transactional(readOnly = true)
    public RecomendacionIA buscarPorId(Long id) {
        return recomendacionIARepository.findById(id).orElse(null);
    }

    @Override
    public Boolean eliminarRecomendaciones(Long id) {
        RecomendacionIA encontrada = buscarPorId(id);
        if (encontrada == null) return false;
        recomendacionIARepository.deleteById(id);
        return true;
    }

    // US-24, BN-23: genera la recomendación IA para una faena con Gemini y la guarda en BD
    @Override
    public RecomendacionIA generarParaBitacora(Long faenaId, Long usuarioId) {
        Faena faena = obtenerFaenaValidada(faenaId, usuarioId);

        if (recomendacionIARepository.existsByFaenaId(faenaId)) {
            throw new IllegalStateException("Ya existe una recomendación para esta faena. Use regenerar.");
        }

        String prompt = construirPromptPescador(faena);
        String analisis = geminiService.generarTexto(prompt);
        if (analisis.startsWith("No se pudo obtener respuesta de Gemini") || analisis.startsWith("El asistente está temporalmente ocupado")) {
            throw new IllegalStateException(analisis);
        }
        String nivel = determinarNivelRiesgo(analisis);

        RecomendacionIA rec = new RecomendacionIA();
        rec.setFaena(faena);
        rec.setAnalisisTexto(limpiarEtiquetaRiesgo(analisis));
        rec.setNivelRiesgo(nivel);
        rec.setFechaGeneracion(LocalDateTime.now());

        return recomendacionIARepository.saveAndFlush(rec);
    }

    // US-26, BN-23: regenera la recomendación usando Gemini (upsert)
    @Override
    public RecomendacionIA regenerarParaBitacora(Long faenaId, Long usuarioId) {
        Faena faena = obtenerFaenaValidada(faenaId, usuarioId);

        Optional<RecomendacionIA> existenteOpt = recomendacionIARepository.findByFaenaId(faenaId);
        if (existenteOpt.isPresent()) {
            if ("Finalizada".equalsIgnoreCase(faena.getEstado()) || "Cancelada".equalsIgnoreCase(faena.getEstado())) {
                throw new IllegalStateException("La faena se encuentra finalizada, por lo que un nuevo análisis no mostrará un resultado diferente.");
            }
            LocalDateTime ultimaGen = existenteOpt.get().getFechaGeneracion();
            Optional<Bitacora> ultimaBit = bitacoraRepository.findFirstByFaenaIdOrderByFechaHoraRegistroDesc(faenaId);
            boolean sinBitacorasNuevas = ultimaBit.isEmpty()
                    || (ultimaGen != null && ultimaBit.get().getFechaHoraRegistro() != null
                    && !ultimaBit.get().getFechaHoraRegistro().isAfter(ultimaGen));
            if (sinBitacorasNuevas) {
                throw new IllegalStateException("No existen nuevos registros de bitácora desde el último análisis, por lo que un nuevo análisis no mostrará un resultado diferente.");
            }
        }

        String prompt = construirPromptPescador(faena);
        String analisis = geminiService.generarTexto(prompt);
        if (analisis.startsWith("No se pudo obtener respuesta de Gemini") || analisis.startsWith("El asistente está temporalmente ocupado")) {
            throw new IllegalStateException(analisis);
        }
        String nivel = determinarNivelRiesgo(analisis);
        validarNivelRiesgo(nivel);

        RecomendacionIA recomendacion = existenteOpt.orElse(new RecomendacionIA());
        recomendacion.setFaena(faena);
        recomendacion.setNivelRiesgo(nivel);
        recomendacion.setAnalisisTexto(limpiarEtiquetaRiesgo(analisis));
        recomendacion.setFechaGeneracion(LocalDateTime.now());

        return recomendacionIARepository.saveAndFlush(recomendacion);
    }

    // US-25, BN-24
    @Override
    @Transactional(readOnly = true)
    public Optional<RecomendacionIA> consultarPorBitacoraConPermisoUsuario(Long faenaId, Long usuarioId) {
        return recomendacionIARepository.findByFaenaIdAndUsuarioId(faenaId, usuarioId);
    }

    // US-25
    @Override
    @Transactional(readOnly = true)
    public Optional<RecomendacionIA> consultarPorBitacora(Long faenaId) {
        return recomendacionIARepository.findByFaenaId(faenaId);
    }

    // BN-23
    @Override
    @Transactional(readOnly = true)
    public boolean existeRecomendacionParaBitacora(Long faenaId) {
        return recomendacionIARepository.existsByFaenaId(faenaId);
    }

    // US-34 Dashboard
    @Override
    @Transactional(readOnly = true)
    public List<RecomendacionIA> listarUltimasPorUsuario(Long usuarioId) {
        return recomendacionIARepository.findUltimasByUsuario(usuarioId);
    }

    // US-33 Técnico
    @Override
    @Transactional(readOnly = true)
    public List<RecomendacionIA> listarAltoRiesgoRecientes(int horas) {
        if (horas <= 0) throw new IllegalArgumentException("Las horas deben ser positivas");
        LocalDateTime desde = LocalDateTime.now().minusHours(horas);
        return recomendacionIARepository.findRecomendacionesAltoRiesgoDesde(desde);
    }

    // US-34
    @Override
    @Transactional(readOnly = true)
    public long contarPorNivelRiesgoYUsuario(String nivelRiesgo, Long usuarioId) {
        validarNivelRiesgo(nivelRiesgo);
        return recomendacionIARepository.countByNivelRiesgoAndFaenaUsuarioId(
                nivelRiesgo.toUpperCase(), usuarioId
        );
    }

    // --- Construcción de Prompt completo con toda la data de la faena ---

    private String construirPromptPescador(Faena faena) {
        // 1. Zonas, observaciones, capturas por especie y conteo de especies en veda
        Set<String> zonasSet = new LinkedHashSet<>();
        List<String> observacionesList = new ArrayList<>();
        Map<String, Double> volumenPorEspecie = new LinkedHashMap<>();
        Set<String> especiesEnVedaSet = new LinkedHashSet<>();
        double volumenTotalKg = 0.0;
        double volumenEnVedaKg = 0.0;

        List<Bitacora> bitacoras = bitacoraRepository.findByFaenaId(faena.getId());
        if (bitacoras != null && !bitacoras.isEmpty()) {
            for (Bitacora bit : bitacoras) {
                if (bit.getZonaPesca() != null) {
                    zonasSet.add(bit.getZonaPesca().getNombreZona()
                            + " (lat: " + bit.getLatitudPesca() + ", lon: " + bit.getLongitudPesca() + ")");
                } else if (bit.getLatitudPesca() != null && bit.getLongitudPesca() != null) {
                    zonasSet.add("Coordenadas (lat: " + bit.getLatitudPesca() + ", lon: " + bit.getLongitudPesca() + ")");
                }
                if (bit.getObservaciones() != null && !bit.getObservaciones().isBlank()) {
                    observacionesList.add(bit.getObservaciones().trim());
                }
                if (bit.getDetallesCaptura() != null) {
                    for (DetalleCaptura dc : bit.getDetallesCaptura()) {
                        String nombreEsp = dc.getEspecie() != null ? dc.getEspecie().getNombreComun() : "Especie desconocida";
                        double kg = dc.getVolumenKg() != null ? dc.getVolumenKg() : 0.0;
                        boolean enVeda = dc.getEspecie() != null && Boolean.TRUE.equals(dc.getEspecie().getEstadoVeda());

                        volumenPorEspecie.merge(nombreEsp + (enVeda ? " [EN VEDA]" : ""), kg, Double::sum);
                        volumenTotalKg += kg;
                        if (enVeda) {
                            especiesEnVedaSet.add(nombreEsp);
                            volumenEnVedaKg += kg;
                        }
                    }
                }
            }
        }

        String zonasTexto = zonasSet.isEmpty() ? "Sin zona registrada en bitácoras" : String.join("; ", zonasSet);
        String observacionesTexto = observacionesList.isEmpty() ? "Sin observaciones" : String.join(" | ", observacionesList);

        StringBuilder capturasPorEspecieTexto = new StringBuilder();
        if (volumenPorEspecie.isEmpty()) {
            capturasPorEspecieTexto.append("Ninguna captura registrada aún");
        } else {
            volumenPorEspecie.forEach((esp, kg) ->
                    capturasPorEspecieTexto.append(esp).append(": ").append(String.format(Locale.US, "%.2f", kg)).append(" kg; "));
            capturasPorEspecieTexto.append("(Total capturado: ").append(String.format(Locale.US, "%.2f", volumenTotalKg)).append(" kg)");
        }

        String vedaResumenTexto = especiesEnVedaSet.isEmpty()
                ? "0 especies en veda capturadas (0.00 kg)"
                : especiesEnVedaSet.size() + " especie(s) en veda (" + String.join(", ", especiesEnVedaSet)
                + ") con un total de " + String.format(Locale.US, "%.2f", volumenEnVedaKg) + " kg";

        // 2. Gasto operativo de la faena
        String gastoTexto = "Sin gastos operativos registrados";
        Optional<GastoOperativo> gastoOpt = gastoOperativoRepository.findByFaenaId(faena.getId());
        if (gastoOpt.isPresent()) {
            GastoOperativo g = gastoOpt.get();
            double galones = g.getGalonesCombustible() != null ? g.getGalonesCombustible() : 0.0;
            double cComb = g.getCostoCombustible() != null ? g.getCostoCombustible() : 0.0;
            double cHielo = g.getCostoHieloInsumos() != null ? g.getCostoHieloInsumos() : 0.0;
            double totalSoles = cComb + cHielo;
            gastoTexto = String.format(Locale.US,
                    "Combustible: %.2f galones (S/ %.2f) | Hielo e insumos: S/ %.2f | Gasto total: S/ %.2f",
                    galones, cComb, cHielo, totalSoles);
        }

        // 3. Tiempo de duración de la faena
        String duracionTexto = "No disponible (faena aún no iniciada)";
        LocalDateTime inicio = faena.getFechaHoraSalida();
        LocalDateTime fin = faena.getFechaHoraLlegada();
        if (inicio != null) {
            LocalDateTime referenciaFin = (fin != null) ? fin : LocalDateTime.now();
            long minutos = Math.max(0, Duration.between(inicio, referenciaFin).toMinutes());
            long horas = minutos / 60;
            long minsRestantes = minutos % 60;
            duracionTexto = (fin != null ? "Duración real: " : "Tiempo transcurrido en curso: ")
                    + horas + " h " + minsRestantes + " min (" + minutos + " minutos totales)";
        } else if (faena.getFechaHoraSalidaProgramada() != null && faena.getFechaHoraLlegadaEstimada() != null) {
            long minsEst = Math.max(0, Duration.between(faena.getFechaHoraSalidaProgramada(), faena.getFechaHoraLlegadaEstimada()).toMinutes());
            duracionTexto = "Duración estimada planificada: " + minsEst + " minutos";
        }

        // 4. Condición climática de la última bitácora
        String climaInfo = "Sin datos climáticos registrados";
        Optional<Bitacora> ultimaBit = bitacoraRepository.findFirstByFaenaIdOrderByFechaHoraRegistroDesc(faena.getId());
        if (ultimaBit.isPresent()) {
            Optional<CondicionClimatica> clima = condicionClimaticaRepository.findByBitacoraId(ultimaBit.get().getId());
            if (clima.isPresent()) {
                CondicionClimatica c = clima.get();
                climaInfo = c.getTemperaturaCelsius() + "°C | Viento: " + c.getVelocidadVientoNudos()
                        + " nudos | Oleaje: " + c.getEstadoOleaje();
            }
        }

        // 5. Temporadas programadas
        StringBuilder temporadasInfo = new StringBuilder();
        try {
            List<Temporada> temps = temporadaRepository.findAll();
            if (temps != null && !temps.isEmpty()) {
                for (Temporada t : temps) {
                    temporadasInfo.append(t.getNombreTemporada())
                            .append(" [").append(t.getFechaInicio()).append(" a ").append(t.getFechaFin()).append("], ");
                }
            }
        } catch (Exception ignored) {}

        String embNombre = faena.getEmbarcacion() != null ? faena.getEmbarcacion().getNombre() : "No especificada";
        double embCap = (faena.getEmbarcacion() != null && faena.getEmbarcacion().getCapacidadToneladas() != null)
                ? faena.getEmbarcacion().getCapacidadToneladas() : 0.0;

        return String.format("""
        Eres un asistente experto en pesca artesanal e industrial peruana (IMARPE / PRODUCE).
        Analiza integralmente los siguientes datos de la faena de pesca y redacta una recomendación operativa clara y concreta (máx. 170 palabras)
        evaluando: (1) seguridad marítima y clima en la zona, (2) cumplimiento normativo respecto a especies en veda y temporadas,
        y (3) eficiencia económica comparando la captura por especie frente al gasto operativo y duración de la faena.

        Datos completos de la faena #%d:
        - Estado de la faena: %s
        - Embarcación: %s (capacidad %.1f toneladas)
        - Zona(s) de pesca: %s
        - Tiempo que duró la faena: %s
        - Cantidad pescada por especie: %s
        - Cantidad de especies en veda capturadas: %s
        - Gasto operativo de la faena: %s
        - Condición climática reportada: %s
        - Observaciones en bitácora: %s
        - Temporadas programadas: %s

        Incluye al final una línea exacta con el nivel de riesgo de la operación:
        NIVEL_RIESGO: BAJO | NIVEL_RIESGO: MEDIO | NIVEL_RIESGO: ALTO
        """,
                faena.getId(),
                faena.getEstado(),
                embNombre,
                embCap,
                zonasTexto,
                duracionTexto,
                capturasPorEspecieTexto.toString(),
                vedaResumenTexto,
                gastoTexto,
                climaInfo,
                observacionesTexto,
                temporadasInfo.length() > 0 ? temporadasInfo.toString() : "Sin temporadas registradas"
        );
    }

    private String determinarNivelRiesgo(String analisis) {
        if (analisis == null) return "BAJO";
        String upper = analisis.toUpperCase();
        if (upper.contains("NIVEL_RIESGO: ALTO"))  return "ALTO";
        if (upper.contains("NIVEL_RIESGO: MEDIO")) return "MEDIO";
        return "BAJO";
    }

    private String limpiarEtiquetaRiesgo(String analisis) {
        if (analisis == null) return "";
        return analisis.replaceAll("(?i)NIVEL_RIESGO:\s*(BAJO|MEDIO|ALTO)", "").trim();
    }

    private void validarNivelRiesgo(String nivelRiesgo) {
        if (nivelRiesgo == null || !NIVELES_VALIDOS.contains(nivelRiesgo.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Nivel de riesgo inválido: '" + nivelRiesgo
                            + "'. Valores aceptados: BAJO, MEDIO, ALTO"
            );
        }
    }

    private Faena obtenerFaenaValidada(Long faenaId, Long usuarioId) {
        Faena faena = faenaRepository.findById(faenaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La faena con id " + faenaId + " no existe"
                ));
        if (faena.getUsuario() == null || !faena.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("El usuario no tiene permiso para operar sobre esta faena");
        }
        return faena;
    }
}