package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.EmbarcacionRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;

import java.util.List;

@Service
public class EmbarcacionServiceImpl implements EmbarcacionService {
    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FaenaAuthorizationService autorizacionFaena;

    @Override
    public List<Embarcacion> listarEmbarcaciones() {
        if (!autorizacionFaena.esAdministrador()) {
            return embarcacionRepository.findByUsuarioId(autorizacionFaena.obtenerUsuarioAutenticadoId());
        }
        return embarcacionRepository.findAll();
    }

    @Override
    public Embarcacion buscarPorId(Long id) {
        if (id == null) {
            throw new BadRequestException("El ID de la embarcación es obligatorio");
        }
        Embarcacion embarcacion = embarcacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no encontrada"));
        validarPropiedad(embarcacion);
        return embarcacion;
    }

    @Override
    public Embarcacion insertar(Embarcacion embarcacion) {
        if (embarcacion == null) {
            throw new BadRequestException("Los datos de la embarcación son obligatorios");
        }
        validarDatos(embarcacion);
        if (embarcacion.getUsuario() == null || embarcacion.getUsuario().getId() == null) {
            throw new BadRequestException("El ID del propietario es obligatorio");
        }
        if (existeMatricula(embarcacion.getMatricula())) {
            throw new ConflictException("Ya existe una embarcación con esa matrícula");
        }
        Authentication authentication = obtenerAutenticacion();
        if (tieneRol(authentication, "PESCADOR")) {
            User usuario = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
            embarcacion.setUsuario(usuario);
        } else if (!tieneRol(authentication, "ADMIN")) {
            throw new AccessDeniedException("No tiene permiso para registrar embarcaciones");
        } else {
            Long propietarioId = embarcacion.getUsuario().getId();
            embarcacion.setUsuario(userRepository.findById(propietarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Propietario no encontrado")));
        }
        return embarcacionRepository.save(embarcacion);
    }

    @Override
    public Embarcacion actualizar(Embarcacion embarcacion) {
        if (embarcacion == null || embarcacion.getId() == null) {
            throw new BadRequestException("El ID y los datos de actualización son obligatorios");
        }
        Embarcacion foundEmbarcacion = buscarPorId(embarcacion.getId());
        String matriculaActual = foundEmbarcacion.getMatricula();

        if (embarcacion.getNombre() != null) {
            foundEmbarcacion.setNombre(embarcacion.getNombre().trim());
        }
        if (embarcacion.getMatricula() != null) {
            foundEmbarcacion.setMatricula(embarcacion.getMatricula().trim());
        }
        if (embarcacion.getCapacidadToneladas() != null) {
            foundEmbarcacion.setCapacidadToneladas(embarcacion.getCapacidadToneladas());
        }

        if (embarcacion.getNombre() == null && embarcacion.getMatricula() == null &&
                embarcacion.getCapacidadToneladas() == null) {
            throw new BadRequestException("Debe proporcionar al menos un campo para actualizar");
        }
        validarDatos(foundEmbarcacion);
        if (!foundEmbarcacion.getMatricula().equalsIgnoreCase(matriculaActual) &&
                existeMatricula(foundEmbarcacion.getMatricula())) {
            throw new ConflictException("Ya existe una embarcación con esa matrícula");
        }
        return embarcacionRepository.save(foundEmbarcacion);
    }

    @Override
    public Boolean eliminar(Long id) {
        Embarcacion foundEmbarcacion = buscarPorId(id);
        if (foundEmbarcacion.getFaenas() != null && !foundEmbarcacion.getFaenas().isEmpty()) {
            throw new ConflictException("No se puede eliminar una embarcación con faenas asociadas");
        }
        embarcacionRepository.delete(foundEmbarcacion);
        return true;
    }

    @Override
    public List<Embarcacion> listarPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new BadRequestException("El ID del usuario es obligatorio");
        }
        autorizacionFaena.validarAccesoUsuario(usuarioId);
        return embarcacionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public boolean existeMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new BadRequestException("La matrícula es obligatoria");
        }
        return embarcacionRepository.existsByMatriculaIgnoreCase(matricula.trim());
    }

    private void validarDatos(Embarcacion embarcacion) {
        if (embarcacion.getNombre() == null || embarcacion.getNombre().isBlank() ||
                embarcacion.getMatricula() == null || embarcacion.getMatricula().isBlank()) {
            throw new BadRequestException("El nombre y la matrícula son obligatorios");
        }
        Double capacidad = embarcacion.getCapacidadToneladas();
        if (capacidad == null || !Double.isFinite(capacidad) || capacidad <= 0) {
            throw new BadRequestException("La capacidad debe ser positiva y finita");
        }
    }

    private void validarPropiedad(Embarcacion embarcacion) {
        if (autorizacionFaena.esAdministrador()) {
            return;
        }
        if (embarcacion.getUsuario() == null ||
                !embarcacion.getUsuario().getId().equals(autorizacionFaena.obtenerUsuarioAutenticadoId())) {
            throw new AccessDeniedException("No tiene permiso para acceder a esta embarcación");
        }
    }

    private Authentication obtenerAutenticacion() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere una sesión autenticada");
        }
        return authentication;
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> rol.equals(authority.getAuthority()));
    }
}
