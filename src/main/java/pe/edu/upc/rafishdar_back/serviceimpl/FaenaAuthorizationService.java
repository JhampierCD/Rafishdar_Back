package pe.edu.upc.rafishdar_back.serviceimpl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.rafishdar_back.entities.Faena;
import pe.edu.upc.rafishdar_back.entities.User;
import pe.edu.upc.rafishdar_back.exceptions.ResourceNotFoundException;
import pe.edu.upc.rafishdar_back.repositories.UserRepository;

import java.util.Objects;

@Service
public class FaenaAuthorizationService {

    private final UserRepository userRepository;

    public FaenaAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validarAcceso(Faena faena, boolean escritura) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere una sesión autenticada");
        }

        if (tieneRol(authentication, "ADMIN")) {
            return;
        }

        User usuario = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        if (tieneRol(authentication, "PESCADOR") && faena != null &&
                faena.getUsuario() != null &&
                Objects.equals(faena.getUsuario().getId(), usuario.getId())) {
            return;
        }

        if (!escritura && tieneRol(authentication, "TECNICO")) {
            return;
        }

        throw new AccessDeniedException("No tiene permiso para acceder a esta faena");
    }

    public void validarAccesoUsuario(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere una sesión autenticada");
        }
        if (tieneRol(authentication, "ADMIN")) {
            return;
        }
        User usuario = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        if (tieneRol(authentication, "PESCADOR") && Objects.equals(usuario.getId(), usuarioId)) {
            return;
        }
        throw new AccessDeniedException("No tiene permiso para acceder a estos datos");
    }

    public Long obtenerUsuarioAutenticadoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere una sesión autenticada");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"))
                .getId();
    }

    public boolean esAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                tieneRol(authentication, "ADMIN");
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> rol.equals(authority.getAuthority()));
    }
}