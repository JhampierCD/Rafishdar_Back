package pe.edu.upc.rafishdar_back.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.JwtException;

@Component
public class JwtRequestFilter
        extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtilService jwtUtilService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/rafishdar/users/login".equals(path)
                || "/rafishdar/users/register".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader =
                request.getHeader(
                        "Authorization"
                );

        String username = null;
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7).trim();
            if (!token.isEmpty()) {
                try {
                    username = jwtUtilService.extractUsername(token);
                } catch (JwtException | IllegalArgumentException exception) {
                    // Token expirado o malformado: continúa como petición no autenticada.
                    SecurityContextHolder.clearContext();
                }
            }
        }

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserSecurity securityUser = (UserSecurity)
                        userDetailsService.loadUserByUsername(username);

                if (securityUser.isEnabled() && jwtUtilService.validateToken(token, securityUser)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    securityUser,
                                    null,
                                    securityUser.getAuthorities()
                            );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (RuntimeException exception) {
                // Una cuenta eliminada/desactivada o un JWT inválido no crea una sesión.
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(
                request,
                response
        );
    }
}