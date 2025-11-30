package com.gamestore.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired(required = false)
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired(required = false)
    private CorsFilter corsFilter;

    @Test
    void testCorsConfigurationSourceBean() {
        assertNotNull(corsConfigurationSource, "CorsConfigurationSource bean should be created");
    }

    @Test
    void testCorsFilterBean() {
        assertNotNull(corsFilter, "CorsFilter bean should be created");
    }
}

