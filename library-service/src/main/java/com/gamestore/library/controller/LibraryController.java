package com.gamestore.library.controller;

import com.gamestore.library.dto.AddToLibraryRequest;
import com.gamestore.library.dto.LibraryItemResponse;
import com.gamestore.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Biblioteca", description = "API para gestión de la biblioteca de juegos del usuario")
public class LibraryController {
    private final LibraryService libraryService;
    
    @Operation(
        summary = "Agregar juego a la biblioteca", 
        description = "Agrega un juego a la biblioteca del usuario. Valida que el juego no esté ya en la biblioteca " +
                      "y que el usuario y juego existan. Los juegos se agregan automáticamente cuando se completa una orden."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Juego agregado exitosamente a la biblioteca del usuario",
            content = @Content(schema = @Schema(implementation = LibraryItemResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes o IDs inválidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"El userId es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto: el juego ya existe en la biblioteca del usuario",
            content = @Content(schema = @Schema(example = "{\"error\": \"El juego ya está en la biblioteca\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario o juego no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<LibraryItemResponse>> addToLibrary(@Valid @RequestBody AddToLibraryRequest request) {
        try {
            LibraryItemResponse item = libraryService.addToLibrary(request);
            EntityModel<LibraryItemResponse> resource = EntityModel.of(item);
            
            resource.add(linkTo(methodOn(LibraryController.class).addToLibrary(request)).withSelfRel());
            resource.add(linkTo(methodOn(LibraryController.class).getUserLibrary(item.getUserId())).withRel("user-library"));
            resource.add(linkTo(methodOn(LibraryController.class).userOwnsGame(item.getUserId(), item.getJuegoId())).withRel("check-ownership"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            // Preservar el status HTTP original de ResponseStatusException
            throw e;
        } catch (RuntimeException e) {
            // Verificar si es un error de conflicto (juego ya existe)
            String message = e.getMessage();
            if (message != null && (message.contains("ya está en la biblioteca") || 
                                    message.contains("already exists") || 
                                    message.contains("duplicate"))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, message);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message != null ? message : "Error al agregar juego a la biblioteca");
        }
    }
    
    @Operation(
        summary = "Obtener biblioteca del usuario", 
        description = "Obtiene todos los juegos en la biblioteca de un usuario. Retorna la lista completa de juegos " +
                      "que el usuario ha comprado y agregado a su biblioteca."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Biblioteca obtenida exitosamente. Retorna lista vacía si el usuario no tiene juegos.",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de usuario inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"ID de usuario inválido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<LibraryItemResponse>>> getUserLibrary(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long userId) {
        List<LibraryItemResponse> library = libraryService.getUserLibrary(userId);
        
        List<EntityModel<LibraryItemResponse>> libraryResources = library.stream()
                .map(item -> {
                    EntityModel<LibraryItemResponse> resource = EntityModel.of(item);
                    resource.add(linkTo(methodOn(LibraryController.class).getUserLibrary(userId)).withSelfRel());
                    resource.add(linkTo(methodOn(LibraryController.class).userOwnsGame(userId, item.getJuegoId())).withRel("check-ownership"));
                    resource.add(linkTo(methodOn(LibraryController.class).removeFromLibrary(userId, item.getJuegoId())).withRel("remove"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<LibraryItemResponse>> collection = CollectionModel.of(libraryResources);
        collection.add(linkTo(methodOn(LibraryController.class).getUserLibrary(userId)).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(
        summary = "Verificar si usuario posee un juego", 
        description = "Verifica si un usuario tiene un juego específico en su biblioteca. Útil para verificar " +
                      "permisos de acceso o mostrar estado de propiedad en la UI."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación completada. Retorna true si el usuario posee el juego, false en caso contrario.",
            content = @Content(schema = @Schema(example = "{\"owns\": true}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "IDs inválidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"IDs inválidos\"}"))
        )
    })
    @GetMapping("/user/{userId}/game/{juegoId}")
    public ResponseEntity<EntityModel<Map<String, Boolean>>> userOwnsGame(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID del juego", example = "10", required = true)
            @PathVariable String juegoId) {
        boolean owns = libraryService.userOwnsGame(userId, juegoId);
        Map<String, Boolean> response = Map.of("owns", owns);
        EntityModel<Map<String, Boolean>> resource = EntityModel.of(response);
        
        resource.add(linkTo(methodOn(LibraryController.class).userOwnsGame(userId, juegoId)).withSelfRel());
        resource.add(linkTo(methodOn(LibraryController.class).getUserLibrary(userId)).withRel("user-library"));
        
        return ResponseEntity.ok(resource);
    }
    
    @Operation(
        summary = "Eliminar juego de la biblioteca", 
        description = "Elimina un juego de la biblioteca del usuario. Esta acción no elimina el juego del catálogo, " +
                      "solo lo remueve de la biblioteca personal del usuario."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Juego eliminado exitosamente de la biblioteca",
            content = @Content(schema = @Schema(example = "{\"message\": \"Juego eliminado de la biblioteca\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado en la biblioteca del usuario",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego no encontrado en la biblioteca\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "IDs inválidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"IDs inválidos\"}"))
        )
    })
    @DeleteMapping("/user/{userId}/game/{juegoId}")
    public ResponseEntity<EntityModel<Map<String, String>>> removeFromLibrary(
            @PathVariable Long userId,
            @PathVariable String juegoId) {
        libraryService.removeFromLibrary(userId, juegoId);
        Map<String, String> response = Map.of("message", "Juego eliminado de la biblioteca");
        EntityModel<Map<String, String>> resource = EntityModel.of(response);
        
        resource.add(linkTo(methodOn(LibraryController.class).removeFromLibrary(userId, juegoId)).withSelfRel());
        resource.add(linkTo(methodOn(LibraryController.class).getUserLibrary(userId)).withRel("user-library"));
        
        return ResponseEntity.ok(resource);
    }
    
    @Operation(summary = "Agregar juego a la biblioteca (interno)", description = "Endpoint interno para uso entre microservicios. Agrega un juego a la biblioteca del usuario sin requerir autenticación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o juego ya existe en la biblioteca")
    })
    @PostMapping("/internal/add")
    public ResponseEntity<EntityModel<LibraryItemResponse>> addToLibraryInternal(@Valid @RequestBody AddToLibraryRequest request) {
        try {
            LibraryItemResponse item = libraryService.addToLibrary(request);
            EntityModel<LibraryItemResponse> resource = EntityModel.of(item);
            
            resource.add(linkTo(methodOn(LibraryController.class).addToLibraryInternal(request)).withSelfRel());
            resource.add(linkTo(methodOn(LibraryController.class).getUserLibrary(item.getUserId())).withRel("user-library"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            // Preservar el status HTTP original de ResponseStatusException
            throw e;
        } catch (RuntimeException e) {
            // Verificar si es un error de conflicto (juego ya existe)
            String message = e.getMessage();
            if (message != null && (message.contains("ya está en la biblioteca") || 
                                    message.contains("already exists") || 
                                    message.contains("duplicate"))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, message);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message != null ? message : "Error al agregar juego a la biblioteca");
        }
    }
}

