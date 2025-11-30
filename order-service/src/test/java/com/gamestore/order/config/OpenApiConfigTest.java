package com.gamestore.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OpenApiConfig.class)
class OpenApiConfigTest {

    @Autowired(required = false)
    private OpenApiConfig openApiConfig;

    @Test
    void testOpenApiConfigBean() {
        assertNotNull(openApiConfig, "OpenApiConfig bean should be created");
    }

    @Test
    void testOpenApiConfigAnnotation() {
        io.swagger.v3.oas.annotations.OpenAPIDefinition annotation = 
            OpenApiConfig.class.getAnnotation(io.swagger.v3.oas.annotations.OpenAPIDefinition.class);
        assertNotNull(annotation, "OpenApiConfig should have @OpenAPIDefinition annotation");
        
        var info = annotation.info();
        assertNotNull(info, "Info should not be null");
        assertEquals("Order Service API", info.title(), "Title should match");
        assertEquals("1.0.0", info.version(), "Version should match");
    }
}

