package com.kairos.catalog.config;

import com.kairos.catalog.security.KeycloakJwtConverter;
import com.kairos.catalog.security.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtConverter keycloakJwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ENABLE CORS (CONNECTED TO CorsConfig)
                .cors(withDefaults())

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Allow preflight (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        // READ: USER or ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**")
                        .hasAnyRole(Roles.USER, Roles.ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/v2/products/**")
                        .hasAnyRole(Roles.USER, Roles.ADMIN)

                        // WRITE: ADMIN only
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**")
                        .hasRole(Roles.ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/v2/products/**")
                        .hasRole(Roles.ADMIN)

                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**")
                        .hasRole(Roles.ADMIN)

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**")
                        .hasRole(Roles.ADMIN)

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(keycloakJwtConverter);
        return converter;
    }
}