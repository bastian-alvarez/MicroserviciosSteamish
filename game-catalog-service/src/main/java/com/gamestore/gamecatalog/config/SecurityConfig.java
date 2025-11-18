package com.gamestore.gamecatalog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger y documentación públicos
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // GET endpoints públicos (lectura)
                .requestMatchers("GET", "/api/games", "/api/games/{id}", "/api/categories", "/api/genres").permitAll()
                // Endpoints de administrador
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Mantener compatibilidad con endpoints antiguos (deprecated)
                .requestMatchers("POST", "/api/games").hasRole("ADMIN")
                .requestMatchers("PUT", "/api/games/**").hasRole("ADMIN")
                .requestMatchers("POST", "/api/games/**/stock", "/api/games/**/decrease-stock").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/games/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

