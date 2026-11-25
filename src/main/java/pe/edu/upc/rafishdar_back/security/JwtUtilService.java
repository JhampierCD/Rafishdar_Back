package pe.edu.upc.rafishdar_back.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtilService {

    @Value("${jwt.secret:}")
    private String jwtSecret;

    private static final Long JWT_TOKEN_VALIDITY =
            1000 * 60 * 60 * (long) 3;

    // Solo para ejecutar localmente sin variables de entorno. En despliegues,
    // define JWT_SECRET con una clave Base64 aleatoria de al menos 32 bytes.
    private static final String DEVELOPMENT_SECRET_BASE64 =
            "cmFmaXNoZGFyLWRldi1qd3Qtc2VjcmV0LWZvci1sb2NhbC1kZXZlbG9wbWVudC1vbmx5";


    private SecretKey getSigningKey() {

        String configuredSecret = (jwtSecret == null || jwtSecret.isBlank())
                ? DEVELOPMENT_SECRET_BASE64
                : jwtSecret.trim();
        final byte[] decodedKey;
        try {
            decodedKey = Base64.getDecoder().decode(configuredSecret);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("JWT_SECRET debe ser una cadena Base64 válida.", ex);
        }
        if (decodedKey.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe representar al menos 32 bytes para HS256.");
        }
        return Keys.hmacShaKeyFor(decodedKey);
    }


    private Claims extractAllClaims(
            String token) {

        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsFunction) {

        return claimsFunction.apply(
                extractAllClaims(token)
        );
    }


    public String extractUsername(
            String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    public Date extractExpiration(
            String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    public boolean isTokenExpired(
            String token) {

        return extractExpiration(token)
                .before(new Date());
    }


    public boolean validateToken(
            String token,
            UserSecurity user) {

        String username =
                extractUsername(token);

        return (!isTokenExpired(token))
                &&
                username.equals(
                        user.getUsername()
                );
    }


    private String createToken(
            String subject,
            Map<String, Object> claims) {

        return Jwts
                .builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(
                        new Date(
                                System.currentTimeMillis()
                        )
                )
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + JWT_TOKEN_VALIDITY
                        )
                )
                .signWith(
                        getSigningKey(),
                        Jwts.SIG.HS256
                )
                .compact();
    }


    public String generateToken(
            UserSecurity securityUser) {

        Map<String, Object> claims =
                new HashMap<>();

        Object authorities =
                securityUser
                        .getAuthorities()
                        .stream()
                        .map(
                                n -> String.valueOf(
                                        n.getAuthority()
                                )
                        )
                        .toList();

        claims.put(
                "authorities",
                authorities
        );

        claims.put(
                "user_id",
                securityUser
                        .getUser()
                        .getId()
        );

        return createToken(
                securityUser.getUsername(),
                claims
        );
    }
}