package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CreateGameRequest;
import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.dto.UpdateGameRequest;
import com.gamestore.gamecatalog.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/admin/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Administración de Juegos", description = "API para gestión de juegos (solo administradores)")
public class AdminGameController {
    private final GameService gameService;
    
    @Operation(summary = "Crear nuevo juego", description = "Crea un nuevo juego en el catálogo con los datos proporcionados (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Juego creado exitosamente",
                content = @Content(schema = @Schema(implementation = GameResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o faltan campos requeridos"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol de administrador")
    })
    @PostMapping
    public ResponseEntity<EntityModel<GameResponse>> createGame(@Valid @RequestBody CreateGameRequest request) {
        try {
            GameResponse game = gameService.createGame(request);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(AdminGameController.class).createGame(request)).withSelfRel());
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(game.getId())).withRel("game"));
            resource.add(linkTo(methodOn(AdminGameController.class).updateGame(game.getId(), new UpdateGameRequest())).withRel("update"));
            
            return ResponseEntity.status(201).body(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Obtener juego por ID", description = "Obtiene los detalles de un juego específico por su ID (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego encontrado",
                content = @Content(schema = @Schema(implementation = GameResponse.class))),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GameResponse>> getGameById(@PathVariable Long id) {
        try {
            GameResponse game = gameService.getGameById(id);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminGameController.class).updateGame(id, new UpdateGameRequest())).withRel("update"));
            resource.add(linkTo(methodOn(AdminGameController.class).deleteGame(id)).withRel("delete"));
            resource.add(linkTo(methodOn(AdminGameController.class).updateStock(id, Map.of("stock", 0))).withRel("update-stock"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Actualizar juego completo", description = "Actualiza los datos de un juego existente (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = GameResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<GameResponse>> updateGame(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGameRequest request) {
        try {
            GameResponse game = gameService.updateGame(id, request);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(AdminGameController.class).updateGame(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(id)).withRel("game"));
            resource.add(linkTo(methodOn(AdminGameController.class).deleteGame(id)).withRel("delete"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Eliminar juego", description = "Elimina un juego del catálogo permanentemente (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol de administrador")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> deleteGame(@PathVariable Long id) {
        try {
            gameService.deleteGame(id);
            Map<String, String> response = Map.of(
                "message", "Juego eliminado exitosamente",
                "id", id.toString()
            );
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(id)).withRel("deleted-game"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Actualizar stock de un juego", description = "Actualiza el stock disponible de un juego (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<EntityModel<GameResponse>> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer newStock = request.get("stock");
            GameResponse game = gameService.updateStock(id, newStock);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(AdminGameController.class).updateStock(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(id)).withRel("game"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Disminuir stock de un juego", description = "Disminuye el stock disponible de un juego por una cantidad específica (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock disminuido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos")
    })
    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<EntityModel<GameResponse>> decreaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            GameResponse game = gameService.decreaseStock(id, quantity);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(AdminGameController.class).decreaseStock(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(id)).withRel("game"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

