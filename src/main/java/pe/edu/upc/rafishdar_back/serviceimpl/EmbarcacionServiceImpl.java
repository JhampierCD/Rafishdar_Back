package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.Embarcacion;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionRequestDTO;
import pe.edu.upc.rafishdar_back.dtos.embarcacion.EmbarcacionUpdateDTO;
import pe.edu.upc.rafishdar_back.exceptions.BadRequestException;
import pe.edu.upc.rafishdar_back.exceptions.ConflictException;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.EmbarcacionRepository;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;
import pe.edu.upc.rafishdar_back.services.EmbarcacionService;

import java.util.List;
import java.util.ArrayList;

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
    public List<EmbarcacionDTO> listarEmbarcacionesDTO() {
        List<Embarcacion> embarcaciones = listarEmbarcaciones();
        List<EmbarcacionDTO> result = new ArrayList<>();
        for (Embarcacion embarcacion : embarcaciones) {
            EmbarcacionDTO dto = new EmbarcacionDTO();
            dto.setId(embarcacion.getId());
            dto.setNombre(embarcacion.getNombre());
            dto.setMatricula(embarcacion.getMatricula());
            dto.setCapacidadToneladas(embarcacion.getCapacidadToneladas());
            dto.setUsuarioId(embarcacion.getUsuario() != null ? embarcacion.getUsuario().getId() : null);
            result.add(dto);
        }
        return result;
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
    public Embarcacion insertar(EmbarcacionRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de la embarcación son obligatorios");
        }
        Embarcacion embarcacion = new Embarcacion();
        embarcacion.setNombre(request.getNombre());
        embarcacion.setMatricula(request.getMatricula());
        embarcacion.setCapacidadToneladas(request.getCapacidadToneladas());
        validarDatos(embarcacion);
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
            if (request.getUsuarioId() == null) {
                throw new BadRequestException("El ID del propietario es obligatorio");
            }
            Long propietarioId = request.getUsuarioId();
            embarcacion.setUsuario(userRepository.findById(propietarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Propietario no encontrado")));
        }
        return embarcacionRepository.save(embarcacion);
    }

    @Override
    public Embarcacion actualizar(Long id, EmbarcacionUpdateDTO request) {
        if (request == null) throw new BadRequestException("Los datos de actualización son obligatorios");
        Embarcacion foundEmbarcacion = buscarPorId(id);
        String matriculaActual = foundEmbarcacion.getMatricula();

        if (request.getNombre() != null) {
            foundEmbarcacion.setNombre(request.getNombre().trim());
        }
        if (request.getMatricula() != null) {
            foundEmbarcacion.setMatricula(request.getMatricula().trim());
        }
        if (request.getCapacidadToneladas() != null) {
            foundEmbarcacion.setCapacidadToneladas(request.getCapacidadToneladas());
        }

        if (request.getNombre() == null && request.getMatricula() == null &&
                request.getCapacidadToneladas() == null) {
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