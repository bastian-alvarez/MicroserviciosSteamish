package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CreateGameRequest;
import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.dto.UpdateGameRequest;
import com.gamestore.gamecatalog.service.FileStorageService;
import com.gamestore.gamecatalog.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileStorageService fileStorageService;
    
    @Operation(
        summary = "Crear nuevo juego", 
        description = "Crea un nuevo juego en el catálogo con los datos proporcionados. Valida todos los campos " +
                      "requeridos, verifica que la categoría y género existan. Solo administradores pueden crear juegos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Juego creado exitosamente. Retorna el juego creado con su ID asignado.",
            content = @Content(schema = @Schema(implementation = GameResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes, precio negativo, stock negativo, o categoría/género no válidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"El nombre es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado - Se requiere rol de administrador\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Categoría o género no encontrados",
            content = @Content(schema = @Schema(example = "{\"error\": \"Categoría con ID 1 no encontrada\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al crear el juego",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al crear el juego\"}"))
        )
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
    
    @Operation(
        summary = "Obtener juego por ID", 
        description = "Obtiene los detalles completos de un juego específico para administradores. Incluye información " +
                      "detallada de stock, descuentos y estado del juego."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Juego encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = GameResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GameResponse>> getGameById(
            @Parameter(description = "ID del juego", example = "1", required = true)
            @PathVariable Long id) {
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
    
    @Operation(
        summary = "Actualizar juego completo", 
        description = "Actualiza los datos de un juego existente. Permite modificar cualquier campo del juego " +
                      "incluyendo nombre, precio, stock, descuento, etc. Solo administradores pueden actualizar juegos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Juego actualizado exitosamente. Retorna el juego con los datos actualizados.",
            content = @Content(schema = @Schema(implementation = GameResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: precio negativo, stock negativo, o valores fuera de rango",
            content = @Content(schema = @Schema(example = "{\"error\": \"El precio debe ser mayor o igual a 0\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
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
    
    @Operation(
        summary = "Eliminar juego", 
        description = "Elimina un juego del catálogo permanentemente. Esta acción no se puede deshacer. " +
                      "Solo administradores pueden eliminar juegos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Juego eliminado exitosamente",
            content = @Content(schema = @Schema(example = "{\"message\": \"Juego eliminado exitosamente\", \"id\": \"1\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
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
    
    @Operation(
        summary = "Actualizar stock de un juego", 
        description = "Actualiza el stock disponible de un juego. Permite establecer un valor específico de stock. " +
                      "Útil para reposición de inventario. Solo administradores pueden actualizar stock."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Stock actualizado exitosamente. Retorna el juego con el stock actualizado.",
            content = @Content(schema = @Schema(implementation = GameResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: stock negativo o valor no numérico",
            content = @Content(schema = @Schema(example = "{\"error\": \"El stock debe ser mayor o igual a 0\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        )
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
    
    @Operation(
        summary = "Disminuir stock de un juego", 
        description = "Disminuye el stock disponible de un juego por una cantidad específica. Valida que haya " +
                      "stock suficiente antes de disminuir. Solo administradores pueden disminuir stock."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Stock disminuido exitosamente. Retorna el juego con el stock actualizado.",
            content = @Content(schema = @Schema(implementation = GameResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Stock insuficiente o datos inválidos: cantidad mayor al stock disponible o cantidad negativa",
            content = @Content(schema = @Schema(example = "{\"error\": \"Stock insuficiente. Stock disponible: 5\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        )
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
    
    @Operation(
        summary = "Subir imagen de juego", 
        description = "Sube una imagen directamente para un juego específico. Formatos aceptados: JPG, PNG, GIF. " +
                      "Tamaño máximo: 10MB. Si el juego ya tiene una imagen, la anterior será reemplazada. " +
                      "Solo administradores pueden subir imágenes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Imagen subida exitosamente. Retorna el juego con la nueva URL de imagen.",
            content = @Content(schema = @Schema(implementation = GameResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Archivo inválido: formato no soportado, archivo demasiado grande (>10MB), o archivo vacío",
            content = @Content(schema = @Schema(example = "{\"error\": \"Formato de archivo no soportado. Use JPG, PNG o GIF\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al guardar la imagen",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al subir la imagen\"}"))
        )
    })
    @PostMapping(value = "/{id}/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityModel<GameResponse>> uploadGameImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            // Verificar que el juego existe
            GameResponse currentGame = gameService.getGameById(id);
            String oldImageUrl = currentGame.getImagenUrl();
            
            // Guardar el nuevo archivo
            String imageUrl = fileStorageService.storeGameImage(file, id);
            
            // Actualizar el juego con la nueva URL de imagen
            UpdateGameRequest updateRequest = new UpdateGameRequest();
            updateRequest.setImagenUrl(imageUrl);
            GameResponse updatedGame = gameService.updateGame(id, updateRequest);
            
            // Eliminar imagen anterior si existe
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                String oldFilename = fileStorageService.extractFilenameFromUrl(oldImageUrl);
                if (oldFilename != null) {
                    fileStorageService.deleteGameImage(oldFilename);
                }
            }
            
            EntityModel<GameResponse> resource = EntityModel.of(updatedGame);
            resource.add(linkTo(methodOn(AdminGameController.class).uploadGameImage(id, file)).withSelfRel());
            resource.add(linkTo(methodOn(AdminGameController.class).getGameById(id)).withRel("game"));
            resource.add(linkTo(methodOn(AdminGameController.class).updateGame(id, new UpdateGameRequest())).withRel("update"));
            
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
        }
    }
}

