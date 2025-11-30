package com.gamestore.auth.service;

import com.gamestore.auth.dto.GameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameCatalogService {
    private final WebClient.Builder webClientBuilder;
    
    // Usando Eureka para descubrimiento de servicios
    private static final String GAME_CATALOG_SERVICE = "http://game-catalog-service";
    
    private WebClient getWebClient(String token) {
        return webClientBuilder
                .baseUrl(GAME_CATALOG_SERVICE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }
    
    public Object createGame(GameRequest request, String token) {
        try {
            return getWebClient(token)
                    .post()
                    .uri("/api/admin/games")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear juego: " + e.getMessage());
        }
    }
    
    public Object getGameById(Long id, String token) {
        try {
            return getWebClient(token)
                    .get()
                    .uri("/api/admin/games/{id}", id)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener juego: " + e.getMessage());
        }
    }
    
    public Object updateGame(Long id, GameRequest request, String token) {
        try {
            return getWebClient(token)
                    .put()
                    .uri("/api/admin/games/{id}", id)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar juego: " + e.getMessage());
        }
    }
    
    public Object deleteGame(Long id, String token) {
        try {
            return getWebClient(token)
                    .delete()
                    .uri("/api/admin/games/{id}", id)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar juego: " + e.getMessage());
        }
    }
    
    public Object updateStock(Long id, Integer stock, String token) {
        try {
            return getWebClient(token)
                    .put()
                    .uri("/api/admin/games/{id}/stock", id)
                    .bodyValue(Map.of("stock", stock))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
        }
    }
    
    public Object decreaseStock(Long id, Integer quantity, String token) {
        try {
            return getWebClient(token)
                    .post()
                    .uri("/api/admin/games/{id}/decrease-stock", id)
                    .bodyValue(Map.of("quantity", quantity))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al disminuir stock: " + e.getMessage());
        }
    }
}

