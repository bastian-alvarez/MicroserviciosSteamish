package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CreateGameRequest;
import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.dto.UpdateGameRequest;
import com.gamestore.gamecatalog.service.GameService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Juegos", description = "API para gestión de juegos del catálogo")
public class GameController {
    private final GameService gameService;
    
    @Operation(summary = "Listar todos los juegos", description = "Obtiene la lista de juegos con filtros opcionales por categoría, género, descuento o búsqueda")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de juegos obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GameResponse>>> getAllGames(
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) Long genero,
            @RequestParam(required = false) Boolean descuento,
            @RequestParam(required = false) String search) {
        
        List<GameResponse> games;
        
        if (search != null && !search.isEmpty()) {
            games = gameService.searchGames(search);
        } else if (categoria != null) {
            games = gameService.getGamesByCategory(categoria);
        } else if (descuento != null && descuento) {
            games = gameService.getGamesWithDiscount();
        } else {
            games = gameService.getAllGames();
        }
        
        List<EntityModel<GameResponse>> gameResources = games.stream()
                .map(game -> {
                    EntityModel<GameResponse> resource = EntityModel.of(game);
                    resource.add(linkTo(methodOn(GameController.class).getGameById(game.getId())).withSelfRel());
                    resource.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<GameResponse>> collection = CollectionModel.of(gameResources);
        collection.add(linkTo(methodOn(GameController.class).getAllGames(categoria, genero, descuento, search)).withSelfRel());
        collection.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        collection.add(linkTo(methodOn(GenreController.class).getAllGenres()).withRel("genres"));
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(summary = "Obtener juego por ID", description = "Obtiene los detalles de un juego específico por su ID")
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
            
            resource.add(linkTo(methodOn(GameController.class).getGameById(id)).withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
            resource.add(linkTo(methodOn(GameController.class).updateStock(id, Map.of("stock", 0))).withRel("update-stock"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Actualizar stock de un juego", description = "[DEPRECATED] Usar /api/admin/games/{id}/stock. Actualiza el stock disponible de un juego (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}/stock")
    @Deprecated
    public ResponseEntity<EntityModel<GameResponse>> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer newStock = request.get("stock");
            GameResponse game = gameService.updateStock(id, newStock);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(GameController.class).updateStock(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).getGameById(id)).withRel("game"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Crear nuevo juego", description = "[DEPRECATED] Usar /api/admin/games. Crea un nuevo juego en el catálogo con los datos proporcionados (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Juego creado exitosamente",
                content = @Content(schema = @Schema(implementation = GameResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o faltan campos requeridos")
    })
    @PostMapping
    @Deprecated
    public ResponseEntity<EntityModel<GameResponse>> createGame(@Valid @RequestBody CreateGameRequest request) {
        try {
            GameResponse game = gameService.createGame(request);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(GameController.class).createGame(request)).withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).getGameById(game.getId())).withRel("game"));
            resource.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
            
            return ResponseEntity.status(201).body(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Disminuir stock de un juego", description = "[DEPRECATED] Usar /api/admin/games/{id}/decrease-stock. Disminuye el stock disponible de un juego por una cantidad específica (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock disminuido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos")
    })
    @PostMapping("/{id}/decrease-stock")
    @Deprecated
    public ResponseEntity<EntityModel<GameResponse>> decreaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            GameResponse game = gameService.decreaseStock(id, quantity);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(GameController.class).decreaseStock(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).getGameById(id)).withRel("game"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Actualizar juego completo", description = "[DEPRECATED] Usar /api/admin/games/{id}. Actualiza los datos de un juego existente (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = GameResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @PutMapping("/{id}")
    @Deprecated
    public ResponseEntity<EntityModel<GameResponse>> updateGame(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGameRequest request) {
        try {
            GameResponse game = gameService.updateGame(id, request);
            EntityModel<GameResponse> resource = EntityModel.of(game);
            
            resource.add(linkTo(methodOn(GameController.class).updateGame(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).getGameById(id)).withRel("game"));
            resource.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Eliminar juego", description = "[DEPRECATED] Usar /api/admin/games/{id}. Elimina un juego del catálogo permanentemente (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol de administrador")
    })
    @DeleteMapping("/{id}")
    @Deprecated
    public ResponseEntity<EntityModel<Map<String, String>>> deleteGame(@PathVariable Long id) {
        try {
            gameService.deleteGame(id);
            Map<String, String> response = Map.of(
                "message", "Juego eliminado exitosamente",
                "id", id.toString()
            );
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(GameController.class).getAllGames(null, null, null, null)).withRel("games"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

