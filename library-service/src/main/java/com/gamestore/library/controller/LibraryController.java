package com.gamestore.library.controller;

import com.gamestore.library.dto.AddToLibraryRequest;
import com.gamestore.library.dto.LibraryItemResponse;
import com.gamestore.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Biblioteca", description = "API para gestión de la biblioteca de juegos del usuario")
public class LibraryController {
    private final LibraryService libraryService;
    
    @Operation(summary = "Agregar juego a la biblioteca", description = "Agrega un juego a la biblioteca del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego agregado exitosamente",
                content = @Content(schema = @Schema(implementation = LibraryItemResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o juego ya existe en la biblioteca")
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
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Obtener biblioteca del usuario", description = "Obtiene todos los juegos en la biblioteca de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Biblioteca obtenida exitosamente")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<LibraryItemResponse>>> getUserLibrary(@PathVariable Long userId) {
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
    
    @Operation(summary = "Verificar si usuario posee un juego", description = "Verifica si un usuario tiene un juego específico en su biblioteca")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada")
    })
    @GetMapping("/user/{userId}/game/{juegoId}")
    public ResponseEntity<EntityModel<Map<String, Boolean>>> userOwnsGame(
            @PathVariable Long userId,
            @PathVariable String juegoId) {
        boolean owns = libraryService.userOwnsGame(userId, juegoId);
        Map<String, Boolean> response = Map.of("owns", owns);
        EntityModel<Map<String, Boolean>> resource = EntityModel.of(response);
        
        resource.add(linkTo(methodOn(LibraryController.class).userOwnsGame(userId, juegoId)).withSelfRel());
        resource.add(linkTo(methodOn(LibraryController.class).getUserLibrary(userId)).withRel("user-library"));
        
        return ResponseEntity.ok(resource);
    }
    
    @Operation(summary = "Eliminar juego de la biblioteca", description = "Elimina un juego de la biblioteca del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Juego no encontrado en la biblioteca")
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
}

