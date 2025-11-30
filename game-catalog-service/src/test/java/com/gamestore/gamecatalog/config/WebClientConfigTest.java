package com.gamestore.gamecatalog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = WebClientConfig.class)
class WebClientConfigTest {

    @Autowired(required = false)
    private WebClient.Builder webClientBuilder;

    @Test
    void testWebClientBuilderBean() {
        assertNotNull(webClientBuilder, "WebClient.Builder bean should be created");
    }

    @Test
    void testWebClientBuilderCanCreateWebClient() {
        if (webClientBuilder != null) {
            WebClient webClient = webClientBuilder.build();
            assertNotNull(webClient, "WebClient should be created from builder");
        }
    }
}

