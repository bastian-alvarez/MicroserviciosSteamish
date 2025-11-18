package com.gamestore.auth.controller;

import com.gamestore.auth.dto.GameRequest;
import com.gamestore.auth.service.GameCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "Administración de Juegos", description = "API para gestión de juegos desde auth-service (solo administradores)")
public class AdminGameController {
    private final GameCatalogService gameCatalogService;
    
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Token no proporcionado");
    }
    
    @Operation(summary = "Crear nuevo juego", description = "Crea un nuevo juego en el catálogo (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Juego creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Object>> createGame(
            @Valid @RequestBody GameRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            Object game = gameCatalogService.createGame(request, token);
            EntityModel<Object> resource = EntityModel.of(game);
            return ResponseEntity.status(201).body(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Obtener juego por ID", description = "Obtiene los detalles de un juego específico (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego encontrado"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Object>> getGameById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            Object game = gameCatalogService.getGameById(id, token);
            EntityModel<Object> resource = EntityModel.of(game);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Actualizar juego", description = "Actualiza los datos de un juego existente (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Object>> updateGame(
            @PathVariable Long id,
            @Valid @RequestBody GameRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            Object game = gameCatalogService.updateGame(id, request, token);
            EntityModel<Object> resource = EntityModel.of(game);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Eliminar juego", description = "Elimina un juego del catálogo permanentemente (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> deleteGame(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            gameCatalogService.deleteGame(id, token);
            Map<String, String> response = Map.of(
                "message", "Juego eliminado exitosamente",
                "id", id.toString()
            );
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Actualizar stock", description = "Actualiza el stock disponible de un juego (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<EntityModel<Object>> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            Integer stock = request.get("stock");
            Object game = gameCatalogService.updateStock(id, stock, token);
            EntityModel<Object> resource = EntityModel.of(game);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Disminuir stock", description = "Disminuye el stock disponible de un juego (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock disminuido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente")
    })
    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<EntityModel<Object>> decreaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            Integer quantity = request.get("quantity");
            Object game = gameCatalogService.decreaseStock(id, quantity, token);
            EntityModel<Object> resource = EntityModel.of(game);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

