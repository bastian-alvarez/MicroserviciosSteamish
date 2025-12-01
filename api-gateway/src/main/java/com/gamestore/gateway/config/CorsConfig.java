package com.gamestore.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Permitir todos los orígenes (en producción, especificar dominios específicos)
        corsConfiguration.setAllowedOrigins(List.of("*"));
        
        // Métodos HTTP permitidos
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Headers permitidos
        corsConfiguration.setAllowedHeaders(List.of("*"));
        
        // Headers expuestos
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Total-Count", "Access-Control-Allow-Origin"
        ));
        
        // Permitir credenciales (cookies, auth headers)
        corsConfiguration.setAllowCredentials(false);
        
        // Cache de preflight requests
        corsConfiguration.setMaxAge(3600L);
        
        // Aplicar a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsWebFilter(source);
    }
}

