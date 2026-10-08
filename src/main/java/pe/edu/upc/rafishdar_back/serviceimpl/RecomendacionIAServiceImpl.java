package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.BitacoraFaena;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.repositories.BitacoraFaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.CondicionClimaticaRepository;
import pe.edu.upc.rafishdar_back.repositories.RecomendacionIARepository;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecomendacionIAServiceImpl implements RecomendacionIAService {

    private static final Set<String> NIVELES_VALIDOS = Set.of("BAJO", "MEDIO", "ALTO");

    @Autowired private RecomendacionIARepository recomendacionIARepository;
    @Autowired private BitacoraFaenaRepository bitacoraFaenaRepository;
    @Autowired private CondicionClimaticaRepository condicionClimaticaRepository;

    @Override
    public List<RecomendacionIA> listarTodoRecomendaciones() {
        return recomendacionIARepository.findAll();
    }

    @Override
    public RecomendacionIA insertarRecomendaciones(RecomendacionIA recomendacionIA) {
        // Toda inserción directa pasa por la validación BN-24
        validarNivelRiesgo(recomendacionIA.getNivelRiesgo());
        return recomendacionIARepository.save(recomendacionIA);
    }

    @Override
    public RecomendacionIA actualizarRecomendaciones(RecomendacionIA recomendacionIA) {
        RecomendacionIA encontrada = buscarPorId(recomendacionIA.getId());
        if (encontrada == null) return null;

        if (recomendacionIA.getAnalisisTexto() != null)
            encontrada.setAnalisisTexto(recomendacionIA.getAnalisisTexto());

        if (recomendacionIA.getNivelRiesgo() != null) {
            validarNivelRiesgo(recomendacionIA.getNivelRiesgo());
            encontrada.setNivelRiesgo(recomendacionIA.getNivelRiesgo());
        }

        if (recomendacionIA.getFechaGeneracion() != null)
            encontrada.setFechaGeneracion(recomendacionIA.getFechaGeneracion());

        // BN-23: la referencia a bitácora no se cambia en un update
        return recomendacionIARepository.save(encontrada);
    }

    @Override
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


    // --- Métodos adicionales ---

    /**
     * US-24, BN-23, BN-25: Genera la recomendación IA para una bitácora.
     * Construye el contexto disponible sin inventar datos.
     * Si ya existe una recomendación para esa bitácora, lanza excepción:
     * el pescador debe usar regenerarParaBitacora (US-26).
     */

    @Override
    public RecomendacionIA generarParaBitacora(Long bitacoraId, Long usuarioId) {
        // BN-23: bloquear doble generación
        if (recomendacionIARepository.existsByBitacoraId(bitacoraId)) {
            throw new IllegalStateException(
                    "Ya existe una recomendación para esta faena. Use regenerar para actualizarla."
            );
        }

        BitacoraFaena bitacora = obtenerBitacoraValidada(bitacoraId, usuarioId);
        String prompt = construirPrompt(bitacora);

        // --- Llamada al servicio de IA ---
        // TODO: integrar cliente HTTP real (OpenAI / Anthropic / etc.)
        // El siguiente bloque es el contrato que debe cumplir la integración:
        //   - String respuestaIA = clienteIA.solicitar(prompt);
        //   - String nivelRiesgo = parsearNivel(respuestaIA);   // BN-24
        //   - String analisis    = parsearAnalisis(respuestaIA);
        // Por ahora se usa un placeholder que el equipo reemplazará:
        String nivelRiesgoObtenido = "MEDIO";   // reemplazar con parseo real
        String analisisObtenido    = "Contexto enviado a IA: " + prompt; // reemplazar

        validarNivelRiesgo(nivelRiesgoObtenido); // BN-24

        RecomendacionIA nueva = new RecomendacionIA();
        nueva.setBitacora(bitacora);
        nueva.setNivelRiesgo(nivelRiesgoObtenido);
        nueva.setAnalisisTexto(analisisObtenido);
        nueva.setFechaGeneracion(LocalDateTime.now());

        return recomendacionIARepository.save(nueva);
    }


    /**
     * US-26, BN-23: Regenera la recomendación actualizando el registro 1:1.
     * Si no existe, crea uno nuevo (primera generación equivalente).
     * Actualiza fechaGeneracion con el momento actual.
     */
    @Override
    public RecomendacionIA regenerarParaBitacora(Long bitacoraId, Long usuarioId) {
        BitacoraFaena bitacora = obtenerBitacoraValidada(bitacoraId, usuarioId);
        String prompt = construirPrompt(bitacora);

        // --- Llamada al servicio de IA (mismo contrato que generarParaBitacora) ---
        String nivelRiesgoObtenido = "MEDIO";   // reemplazar con parseo real
        String analisisObtenido    = "Contexto enviado a IA: " + prompt; // reemplazar

        validarNivelRiesgo(nivelRiesgoObtenido); // BN-24

        // BN-23: upsert — actualizar si existe, crear si no
        Optional<RecomendacionIA> existente =
                recomendacionIARepository.findByBitacoraId(bitacoraId);

        if (existente.isPresent()) {
            RecomendacionIA actual = existente.get();
            actual.setNivelRiesgo(nivelRiesgoObtenido);
            actual.setAnalisisTexto(analisisObtenido);
            actual.setFechaGeneracion(LocalDateTime.now()); // US-26: actualiza timestamp
            return recomendacionIARepository.save(actual);
        }

        RecomendacionIA nueva = new RecomendacionIA();
        nueva.setBitacora(bitacora);
        nueva.setNivelRiesgo(nivelRiesgoObtenido);
        nueva.setAnalisisTexto(analisisObtenido);
        nueva.setFechaGeneracion(LocalDateTime.now());
        return recomendacionIARepository.save(nueva);
    }


    /**
     * US-25, BN-12 aplicado a IA: consulta validando que el usuario
     * sea propietario de la bitácora.
     * Devuelve Optional vacío si no es el propietario o no existe.
     */
    @Override
    public Optional<RecomendacionIA> consultarPorBitacoraConPermisoUsuario(Long bitacoraId, Long usuarioId) {
        return recomendacionIARepository.findByBitacoraIdAndUsuarioId(bitacoraId, usuarioId);
    }

    /**
     * US-25: consulta sin validación de propietario.
     * Para Admin y Técnico.
     * Si no existe devuelve Optional vacío (US-25: mostrar estado vacío claro).
     */
    @Override
    public Optional<RecomendacionIA> consultarPorBitacora(Long bitacoraId) {
        return recomendacionIARepository.findByBitacoraId(bitacoraId);
    }

    @Override
    public boolean existeRecomendacionParaBitacora(Long bitacoraId) {
        return recomendacionIARepository.existsByBitacoraId(bitacoraId);
    }

    @Override
    public List<RecomendacionIA> listarUltimasPorUsuario(Long usuarioId) {
        return recomendacionIARepository.findUltimasByUsuario(usuarioId);
    }

    @Override
    public List<RecomendacionIA> listarAltoRiesgoRecientes(int horas) {
        if (horas <= 0) throw new IllegalArgumentException("Las horas deben ser positivas");
        LocalDateTime desde = LocalDateTime.now().minusHours(horas);
        return recomendacionIARepository.findRecomendacionesAltoRiesgoDesde(desde);
    }

    /**
     * US-34: conteo de recomendaciones por nivel de riesgo para un usuario.
     * Permite al dashboard mostrar: X faenas con riesgo ALTO, Y con MEDIO, etc.
     */
    @Override
    public long contarPorNivelRiesgoYUsuario(String nivelRiesgo, Long usuarioId) {
        validarNivelRiesgo(nivelRiesgo);
        return recomendacionIARepository
                .countByNivelRiesgoAndBitacoraUsuarioId(nivelRiesgo, usuarioId);
    }

    // --- Métodos privados de apoyo ---

    /**
     * BN-24: valida que el nivel de riesgo sea uno de los tres valores aceptados.
     * Cualquier valor externo (incluyendo el devuelto por la IA) pasa por aquí.
     */
    private void validarNivelRiesgo(String nivelRiesgo) {
        if (nivelRiesgo == null || !NIVELES_VALIDOS.contains(nivelRiesgo.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Nivel de riesgo inválido: '" + nivelRiesgo
                            + "'. Valores aceptados: BAJO, MEDIO, ALTO"
            );
        }
    }

    /**
     * BN-03 aplicado a IA: valida que la bitácora exista y pertenezca al usuario.
     * Lanza excepción controlada si no se cumplen las condiciones.
     */
    private BitacoraFaena obtenerBitacoraValidada(Long bitacoraId, Long usuarioId) {
        BitacoraFaena bitacora = bitacoraFaenaRepository
                .findById(bitacoraId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La bitácora con id " + bitacoraId + " no existe"
                ));

        if (!bitacora.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException(
                    "El usuario no tiene permiso para operar sobre esta bitácora"
            );
        }

        return bitacora;
    }

    /**
     * BN-25: construye el prompt para la IA con los datos disponibles.
     * No inventa valores. Documenta explícitamente qué información falta.
     */
    private String construirPrompt(BitacoraFaena bitacora) {
        StringBuilder sb = new StringBuilder();
        sb.append("Faena ID: ").append(bitacora.getId()).append("\n");

        if (bitacora.getZonaPesca() != null) {
            sb.append("Zona: ").append(bitacora.getZonaPesca().getNombreZona())
                    .append(", distancia a costa: ")
                    .append(bitacora.getZonaPesca().getDistanciaCostaKm()).append(" km\n");
        } else {
            sb.append("Zona: sin datos disponibles\n");
        }

        if (bitacora.getFechaHoraSalida() != null) {
            sb.append("Fecha/hora salida: ").append(bitacora.getFechaHoraSalida()).append("\n");
        } else {
            sb.append("Fecha/hora salida: sin datos disponibles\n");
        }

        Optional<CondicionClimatica> condicion =
                condicionClimaticaRepository.findByBitacoraId(bitacora.getId());

        if (condicion.isPresent()) {
            CondicionClimatica c = condicion.get();
            sb.append("Temperatura: ").append(c.getTemperaturaCelsius()).append(" °C\n");
            sb.append("Viento: ").append(c.getVelocidadVientoNudos()).append(" nudos\n");
            sb.append("Oleaje: ").append(c.getEstadoOleaje()).append("\n");
        } else {
            sb.append("Condición climática: sin registro disponible\n");
        }

        sb.append("Genera un análisis breve de riesgo para esta faena de pesca. ")
                .append("Responde ÚNICAMENTE con un JSON: ")
                .append("{\"nivelRiesgo\": \"BAJO|MEDIO|ALTO\", \"analisis\": \"texto\"}");

        return sb.toString();
    }
}
