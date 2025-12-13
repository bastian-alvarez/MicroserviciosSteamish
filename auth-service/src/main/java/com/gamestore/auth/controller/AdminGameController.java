package com.gamestore.auth.controller;

import com.gamestore.auth.dto.GameRequest;
import com.gamestore.auth.service.GameCatalogService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Administración de Juegos", description = "API para gestión completa de juegos desde el servicio de autenticación. CRUD completo y gestión de stock.")
public class AdminGameController {
    private final GameCatalogService gameCatalogService;
    
    @Operation(
        summary = "Crear nuevo juego", 
        description = "Crea un nuevo juego en el catálogo con todos los datos proporcionados. Valida que los campos requeridos " +
                      "estén presentes y que la categoría y género existan. El juego se crea con stock inicial y precio especificados. " +
                      "Solo administradores pueden crear juegos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201", 
            description = "Juego creado exitosamente. Retorna el juego con su ID asignado y todos los datos.",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes, formato inválido, o categoría/género no encontrados",
            content = @Content(schema = @Schema(example = "{\"error\": \"El nombre del juego es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Categoría o género no encontrado con los IDs especificados",
            content = @Content(schema = @Schema(example = "{\"error\": \"Categoría con ID 1 no encontrada\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al crear el juego",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al crear el juego\"}"))
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Object>> createGame(@Valid @RequestBody GameRequest request) {
        String token = "";
        Object game = gameCatalogService.createGame(request, token);
        return ResponseEntity.status(201).body(EntityModel.of(game));
    }
    
    @Operation(
        summary = "Obtener juego por ID", 
        description = "Obtiene los detalles completos de un juego específico, incluyendo información de categoría, " +
                      "género, precio, stock disponible, descuentos y calificaciones promedio."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Juego encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de juego inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"ID de juego inválido\"}"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Object>> getGameById(
            @Parameter(description = "ID del juego", example = "1", required = true)
            @PathVariable Long id) {
        String token = "";
        Object game = gameCatalogService.getGameById(id, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
    
    @Operation(
        summary = "Actualizar juego completo", 
        description = "Actualiza todos los datos de un juego existente. Permite modificar nombre, descripción, precio, " +
                      "stock, categoría, género, descuentos y otros atributos. Solo administradores pueden actualizar juegos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Juego actualizado exitosamente. Retorna el juego con los datos actualizados.",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: formato inválido o campos requeridos faltantes",
            content = @Content(schema = @Schema(example = "{\"error\": \"El precio debe ser mayor a 0\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al actualizar el juego",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al actualizar el juego\"}"))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Object>> updateGame(
            @Parameter(description = "ID del juego", example = "1", required = true)
            @PathVariable Long id, 
            @Valid @RequestBody GameRequest request) {
        String token = "";
        Object game = gameCatalogService.updateGame(id, request, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
    
    @Operation(
        summary = "Eliminar juego", 
        description = "Elimina un juego del catálogo permanentemente. Esta acción no se puede deshacer y elimina " +
                      "todos los datos asociados al juego (comentarios, calificaciones, etc.). Solo administradores pueden eliminar juegos."
    )
    @ApiResponses({
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
    public ResponseEntity<EntityModel<Map<String, String>>> deleteGame(
            @Parameter(description = "ID del juego", example = "1", required = true)
            @PathVariable Long id) {
        String token = "";
        gameCatalogService.deleteGame(id, token);
        Map<String, String> response = Map.of("message", "Juego eliminado exitosamente", "id", id.toString());
        return ResponseEntity.ok(EntityModel.of(response));
    }
    
    @Operation(
        summary = "Actualizar stock de un juego", 
        description = "Actualiza el stock disponible de un juego a un valor específico. Útil para ajustar inventario " +
                      "después de recibir nuevos productos o corregir discrepancias. Solo administradores pueden actualizar stock."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Stock actualizado exitosamente. Retorna el juego con el nuevo stock.",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: stock negativo o valor inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"El stock no puede ser negativo\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 1 no encontrado\"}"))
        )
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<EntityModel<Object>> updateStock(
            @Parameter(description = "ID del juego", example = "1", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Objeto con el nuevo valor de stock", example = "{\"stock\": 100}")
            @RequestBody Map<String, Integer> request) {
        String token = "";
        Integer stock = request.get("stock");
        Object game = gameCatalogService.updateStock(id, stock, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
    
    @Operation(
        summary = "Disminuir stock de un juego", 
        description = "Disminuye el stock disponible de un juego por una cantidad específica. Útil para registrar ventas " +
                      "o ajustes de inventario. Valida que haya stock suficiente antes de disminuir. Solo administradores pueden disminuir stock."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Stock disminuido exitosamente. Retorna el juego con el stock actualizado.",
            content = @Content(schema = @Schema(implementation = Object.class))
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
    public ResponseEntity<EntityModel<Object>> decreaseStock(
            @Parameter(description = "ID del juego", example = "1", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Objeto con la cantidad a disminuir", example = "{\"quantity\": 5}")
            @RequestBody Map<String, Integer> request) {
        String token = "";
        Integer quantity = request.get("quantity");
        Object game = gameCatalogService.decreaseStock(id, quantity, token);
        return ResponseEntity.ok(EntityModel.of(game));
    }
}