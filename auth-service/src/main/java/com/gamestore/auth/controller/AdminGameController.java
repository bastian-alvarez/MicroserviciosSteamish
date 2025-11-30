package com.gamestore.auth.controller;

import com.gamestore.auth.dto.GameRequest;
import com.gamestore.auth.service.GameCatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AdministraciÃ³n de Juegos", description = "API para gestiÃ³n de juegos - Sin autenticaciÃ³n")
public class AdminGameController {
    private final GameCatalogService gameCatalogService;
    
    @PostMapping
    public ResponseEntity<EntityModel<Object>> createGame(@Valid @RequestBody GameRequest request) {
        String token = "";
        Object game = gameCatalogService.createGame(request, token);
        return ResponseEntity.status(201).body(EntityModel.of(game));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Object>> getGameById(@PathVariable Long id) {
        String token = "";
        Object game = gameCatalogService.getGameById(id, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Object>> updateGame(@PathVariable Long id, @Valid @RequestBody GameRequest request) {
        String token = "";
        Object game = gameCatalogService.updateGame(id, request, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> deleteGame(@PathVariable Long id) {
        String token = "";
        gameCatalogService.deleteGame(id, token);
        Map<String, String> response = Map.of("message", "Juego eliminado exitosamente", "id", id.toString());
        return ResponseEntity.ok(EntityModel.of(response));
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<EntityModel<Object>> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        String token = "";
        Integer stock = request.get("stock");
        Object game = gameCatalogService.updateStock(id, stock, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
    
    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<EntityModel<Object>> decreaseStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        String token = "";
        Integer quantity = request.get("quantity");
        Object game = gameCatalogService.decreaseStock(id, quantity, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
}