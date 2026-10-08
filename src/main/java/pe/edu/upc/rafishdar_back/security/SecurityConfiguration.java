package pe.edu.upc.rafishdar_back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private static String[] LISTA_PERMIT_ALL = {

            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",

            "/rafishdar/usuarios/login/**",
            "/rafishdar/usuarios/register/**"
    };


    @Autowired
    JwtRequestFilter jwtRequestFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager
    authenticationManager(
            AuthenticationConfiguration
                    authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration
                .getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain
    securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http.addFilterBefore(
                jwtRequestFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.cors(
                Customizer.withDefaults()
        );

        http.csrf(
                AbstractHttpConfigurer::disable
        );


        http.authorizeHttpRequests(
                authorizationRegistry ->
                        authorizationRegistry

                                /*
                                 * LOGIN Y REGISTRO
                                 */
                                .requestMatchers(
                                        LISTA_PERMIT_ALL
                                )
                                .permitAll()


                                /*
                                 * USUARIOS
                                 */

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/rafishdar/usuarios/password"
                                )
                                .authenticated()

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/rafishdar/usuarios/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/rafishdar/usuarios/**"
                                )
                                .hasAuthority("ADMIN")


                                /*
                                 * ESPECIES
                                 */

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/rafishdar/especies/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/rafishdar/especies/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/rafishdar/especies/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/rafishdar/especies/**"
                                )
                                .hasAuthority("ADMIN")


                                /*
                                 * TEMPORADAS
                                 */

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/rafishdar/temporadas/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/rafishdar/temporadas/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/rafishdar/temporadas/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/rafishdar/temporadas/**"
                                )
                                .hasAuthority("ADMIN")


                                /*
                                 * CUOTAS
                                 */

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/rafishdar/cuotas/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/rafishdar/cuotas/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/rafishdar/cuotas/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/rafishdar/cuotas/**"
                                )
                                .hasAuthority("ADMIN")


                                /*
                                 * ZONAS
                                 */

                                // Para la siguiente entrega se piensa que el pescador pueda crear, actualizar y eliminar zonas de pesca de manera personalizada.

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/rafishdar/zonas-pesca/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/rafishdar/zonas-pesca/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/rafishdar/zonas-pesca/**"
                                )
                                .hasAuthority("ADMIN")

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/rafishdar/zonas-pesca/**"
                                )
                                .hasAuthority("ADMIN")


                                /*
                                 * FUNCIONALIDADES DEL PESCADOR
                                 */

                                .requestMatchers(
                                        "/rafishdar/embarcaciones/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        "/rafishdar/bitacoras_faena/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        "/rafishdar/capturas/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        "/rafishdar/gastos_operativos/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        "/rafishdar/condiciones/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        "/rafishdar/bitacoras/*/condicion-climatica"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )


                                /*
                                 * DIAGNOSTICO TECNICO
                                 */

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/rafishdar/recomendaciones/alto-riesgo"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "TECNICO"
                                )


                                /*
                                 * RECOMENDACIONES
                                 */

                                .requestMatchers(
                                        "/rafishdar/recomendaciones/**"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )

                                .requestMatchers(
                                        "/rafishdar/bitacoras/*/recomendacion"
                                )
                                .hasAnyAuthority(
                                        "ADMIN",
                                        "PESCADOR"
                                )


                                /*
                                 * Cualquier endpoint restante
                                 * exige iniciar sesión.
                                 */
                                .anyRequest()
                                .authenticated()
        );


        http.sessionManagement(
                sessionManager ->
                        sessionManager
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
        );


        return http.build();
    }
}