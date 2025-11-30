package com.gamestore.gamecatalog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "file.upload-dir=uploads/test-game-images"
})
class WebMvcConfigTest {

    @Autowired(required = false)
    private WebMvcConfig webMvcConfig;

    @Test
    void testWebMvcConfigBean() {
        assertNotNull(webMvcConfig, "WebMvcConfig bean should be created");
    }
}

