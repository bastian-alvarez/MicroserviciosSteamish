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
    
    // CorsWebFilter deshabilitado temporalmente
    // CORS se maneja mediante:
    // 1. AddResponseHeader filters en las rutas (application.properties)
    // 2. CorsGlobalFilter (CorsGlobalFilter.java)
    // Esto evita conflictos entre múltiples filtros CORS
    
    /*
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Permitir todos los orígenes - usar addAllowedOriginPattern cuando allowCredentials es false
        // o usar setAllowedOriginPatterns en lugar de setAllowedOrigins
        corsConfiguration.addAllowedOriginPattern("*");
        
        // Métodos HTTP permitidos
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Headers permitidos
        corsConfiguration.addAllowedHeader("*");
        
        // Headers expuestos
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Total-Count"
        ));
        
        // NO permitir credenciales cuando se usa * (causa problemas)
        corsConfiguration.setAllowCredentials(false);
        
        // Cache de preflight requests
        corsConfiguration.setMaxAge(3600L);
        
        // Aplicar a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsWebFilter(source);
    }
    */
}

