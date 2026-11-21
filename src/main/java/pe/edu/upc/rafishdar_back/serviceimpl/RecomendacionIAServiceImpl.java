package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.Bitacora;
import pe.edu.upc.rafishdar_back.entities.CondicionClimatica;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.entities.RecomendacionIA;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.CondicionClimaticaRepository;
import pe.edu.upc.rafishdar_back.repositories.FaenaRepository;
import pe.edu.upc.rafishdar_back.repositories.RecomendacionIARepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.services.RecomendacionIAService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecomendacionIAServiceImpl implements RecomendacionIAService {

    private static final Set<String> NIVELES_VALIDOS = Set.of("BAJO", "MEDIO", "ALTO");

    @Autowired
    private RecomendacionIARepository recomendacionIARepository;

    @Autowired
    private FaenaRepository faenaRepository;

    @Autowired
    private CondicionClimaticaRepository condicionClimaticaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FaenaAuthorizationService autorizacionFaena;

    @Override
    public List<RecomendacionIA> listarTodoRecomendaciones() {
        return recomendacionIARepository.findAll();
    }

    @Override
    public RecomendacionIA insertarRecomendaciones(RecomendacionIA recomendacionIA) {
        if (recomendacionIA == null || recomendacionIA.getFaena() == null ||
                recomendacionIA.getFaena().getId() == null) {
            throw new BadRequestException("La recomendación debe asociarse a una faena");
        }
        validarNivelRiesgo(recomendacionIA.getNivelRiesgo());
        Faena faena = faenaRepository.findById(recomendacionIA.getFaena().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
        autorizacionFaena.validarAcceso(faena, true);
        if (recomendacionIARepository.existsByFaenaId(faena.getId())) {
            throw new ConflictException("La faena ya tiene una recomendación");
        }
        recomendacionIA.setFaena(faena);
        if (recomendacionIA.getFechaGeneracion() == null) {
            recomendacionIA.setFechaGeneracion(LocalDateTime.now());
        }
        return recomendacionIARepository.save(recomendacionIA);
    }

    @Override
    public RecomendacionIA actualizarRecomendaciones(RecomendacionIA recomendacionIA) {
        if (recomendacionIA == null || recomendacionIA.getId() == null) {
            throw new BadRequestException("El ID y los datos de actualización son obligatorios");
        }
        RecomendacionIA encontrada = recomendacionIARepository.findById(recomendacionIA.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recomendación no encontrada"));
        autorizacionFaena.validarAcceso(encontrada.getFaena(), true);
        if (recomendacionIA.getAnalisisTexto() != null) {
            if (recomendacionIA.getAnalisisTexto().isBlank()) {
                throw new BadRequestException("El análisis no puede estar vacío");
            }
            encontrada.setAnalisisTexto(recomendacionIA.getAnalisisTexto());
        }
        if (recomendacionIA.getNivelRiesgo() != null) {
            validarNivelRiesgo(recomendacionIA.getNivelRiesgo());
            encontrada.setNivelRiesgo(recomendacionIA.getNivelRiesgo().trim().toUpperCase());
        }
        if (recomendacionIA.getFechaGeneracion() != null) {
            encontrada.setFechaGeneracion(recomendacionIA.getFechaGeneracion());
        }
        return recomendacionIARepository.save(encontrada);
    }

    @Override
    public RecomendacionIA buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de la recomendación es obligatorio");
        }
        RecomendacionIA recommendation = recomendacionIARepository.findById(id).orElse(null);
        if (recommendation != null) {
            autorizacionFaena.validarAcceso(recommendation.getFaena(), false);
        }
        return recommendation;
    }

    @Override
    public Boolean eliminarRecomendaciones(Long id) {
        RecomendacionIA encontrada = buscarPorId(id);
        if (encontrada == null) {
            return false;
        }
        autorizacionFaena.validarAcceso(encontrada.getFaena(), true);
        recomendacionIARepository.delete(encontrada);
        return true;
    }

    @Override
    public RecomendacionIA generarParaBitacora(Long faenaId, Long usuarioId) {
        validarIdYUsuario(faenaId, usuarioId);
        Faena faena = obtenerFaenaValidada(faenaId, usuarioId);
        if (recomendacionIARepository.existsByFaenaId(faenaId)) {
            throw new ConflictException("Ya existe una recomendación para esta faena. Use regenerar para actualizarla.");
        }
        return crearRecomendacion(faena);
    }

    @Override
    public RecomendacionIA regenerarParaBitacora(Long faenaId, Long usuarioId) {
        validarIdYUsuario(faenaId, usuarioId);
        Faena faena = obtenerFaenaValidada(faenaId, usuarioId);
        RecomendacionIA recomendacion = recomendacionIARepository.findByFaenaId(faenaId)
                .orElseGet(() -> {
                    RecomendacionIA nueva = new RecomendacionIA();
                    nueva.setFaena(faena);
                    return nueva;
                });
        completarRecomendacion(recomendacion, faena);
        return recomendacionIARepository.save(recomendacion);
    }

    @Override
    public Optional<RecomendacionIA> consultarPorBitacoraConPermisoUsuario(Long faenaId, Long usuarioId) {
        validarIdYUsuario(faenaId, usuarioId);
        obtenerFaenaValidada(faenaId, usuarioId);
        return recomendacionIARepository.findByFaenaIdAndUsuarioId(faenaId, usuarioId);
    }

    @Override
    public Optional<RecomendacionIA> consultarPorBitacora(Long faenaId) {
        validarFaenaId(faenaId);
        Faena faena = faenaRepository.findById(faenaId)
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
        autorizacionFaena.validarAcceso(faena, false);
        return recomendacionIARepository.findByFaenaId(faenaId);
    }

    @Override
    public boolean existeRecomendacionParaBitacora(Long faenaId) {
        return faenaId != null && recomendacionIARepository.existsByFaenaId(faenaId);
    }

    @Override
    public List<RecomendacionIA> listarUltimasPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new BadRequestException("El ID del usuario es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(usuarioId);
        return recomendacionIARepository.findUltimasByUsuario(usuarioId);
    }

    @Override
    public List<RecomendacionIA> listarAltoRiesgoRecientes(int horas) {
        if (horas <= 0) {
            throw new BadRequestException("Las horas deben ser positivas");
        }
        return recomendacionIARepository.findRecomendacionesAltoRiesgoDesde(
                LocalDateTime.now().minusHours(horas));
    }

    @Override
    public long contarPorNivelRiesgoYUsuario(String nivelRiesgo, Long usuarioId) {
        validarNivelRiesgo(nivelRiesgo);
        if (usuarioId == null) {
            throw new BadRequestException("El ID del usuario es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(usuarioId);
        return recomendacionIARepository.countByNivelRiesgoAndFaenaUsuarioId(
                nivelRiesgo.trim().toUpperCase(), usuarioId);
    }

    private RecomendacionIA crearRecomendacion(Faena faena) {
        RecomendacionIA recomendacion = new RecomendacionIA();
        recomendacion.setFaena(faena);
        completarRecomendacion(recomendacion, faena);
        return recomendacionIARepository.save(recomendacion);
    }

    private void completarRecomendacion(RecomendacionIA recomendacion, Faena faena) {
        String prompt = construirPrompt(faena);
        // Sustituir estos valores cuando se integre el cliente real de IA.
        recomendacion.setNivelRiesgo("MEDIO");
        recomendacion.setAnalisisTexto("Contexto enviado a IA: " + prompt);
        recomendacion.setFechaGeneracion(LocalDateTime.now());
    }

    private void validarNivelRiesgo(String nivelRiesgo) {
        if (nivelRiesgo == null || !NIVELES_VALIDOS.contains(nivelRiesgo.trim().toUpperCase())) {
            throw new BadRequestException("Nivel de riesgo inválido. Valores aceptados: BAJO, MEDIO, ALTO");
        }
    }

    private void validarIdYUsuario(Long faenaId, Long usuarioId) {
        validarFaenaId(faenaId);
        if (usuarioId == null) {
            throw new BadRequestException("El ID del usuario es obligatorio");
        }
    }

    private void validarFaenaId(Long faenaId) {
        if (faenaId == null) {
            throw new BadRequestException("El ID de la faena es obligatorio");
        }
    }

    private Faena obtenerFaenaValidada(Long faenaId, Long usuarioId) {
        Faena faena = faenaRepository.findById(faenaId)
                .orElseThrow(() -> new ResourceNotFoundException("Faena no encontrada"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere una sesión autenticada");
        }
        User autenticado = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
        boolean esPescador = authentication.getAuthorities().stream()
                .anyMatch(authority -> "PESCADOR".equals(authority.getAuthority()));
        if (!esAdmin && (!esPescador || faena.getUsuario() == null ||
                !faena.getUsuario().getId().equals(autenticado.getId()) ||
                !faena.getUsuario().getId().equals(usuarioId))) {
            throw new AccessDeniedException("No tiene permiso para operar sobre esta faena");
        }
        return faena;
    }

    private String construirPrompt(Faena faena) {
        StringBuilder sb = new StringBuilder();
        sb.append("Faena ID: ").append(faena.getId()).append('\n');

        Bitacora ultimaBitacora = null;
        if (faena.getBitacoras() != null) {
            ultimaBitacora = faena.getBitacoras().stream()
                    .filter(bitacora -> bitacora.getFechaHoraRegistro() != null)
                    .max(Comparator.comparing(Bitacora::getFechaHoraRegistro))
                    .orElse(null);
        }
        if (ultimaBitacora != null && ultimaBitacora.getZonaPesca() != null) {
            sb.append("Zona: ").append(ultimaBitacora.getZonaPesca().getNombreZona())
                    .append(", distancia a costa: ")
                    .append(ultimaBitacora.getZonaPesca().getDistanciaCostaKm()).append(" km\n");
        } else {
            sb.append("Zona: sin datos disponibles\n");
        }

        if (faena.getFechaHoraSalida() != null) {
            sb.append("Fecha/hora salida: ").append(faena.getFechaHoraSalida()).append('\n');
        } else {
            sb.append("Fecha/hora salida: sin datos disponibles\n");
        }

        Optional<CondicionClimatica> condicion = ultimaBitacora == null
                ? Optional.empty()
                : condicionClimaticaRepository.findByBitacoraId(ultimaBitacora.getId());
        if (condicion.isPresent()) {
            CondicionClimatica clima = condicion.get();
            sb.append("Temperatura: ").append(clima.getTemperaturaCelsius()).append(" °C\n");
            sb.append("Viento: ").append(clima.getVelocidadVientoNudos()).append(" nudos\n");
            sb.append("Oleaje: ").append(clima.getEstadoOleaje()).append('\n');
        } else {
            sb.append("Condición climática: sin registro disponible\n");
        }
        return sb.append("Genera un análisis breve de riesgo para esta faena. ")
                .append("Responde únicamente con un JSON: ")
                .append("{\"nivelRiesgo\": \"BAJO|MEDIO|ALTO\", \"analisis\": \"texto\"}")
                .toString();
    }
}
