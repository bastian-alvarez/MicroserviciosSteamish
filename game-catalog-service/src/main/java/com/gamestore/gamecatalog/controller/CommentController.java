package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CommentResponse;
import com.gamestore.gamecatalog.dto.CreateCommentRequest;
import com.gamestore.gamecatalog.service.CommentService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Comentarios", description = "API para gestión de comentarios de juegos")
public class CommentController {
    private final CommentService commentService;
    
    // Sin autenticación requerida - todos los endpoints públicos
    // Los métodos extractUserId y extractUserName ya no son necesarios
    
    @Operation(
        summary = "Obtener todos los comentarios", 
        description = "Obtiene todos los comentarios del sistema. Por defecto solo muestra los comentarios visibles. " +
                      "El parámetro includeHidden permite incluir comentarios ocultos por moderadores."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de comentarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "No hay comentarios registrados"
        )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CommentResponse>>> getAllComments(
            @Parameter(description = "Incluir comentarios ocultos (solo para moderadores)", example = "false")
            @RequestParam(defaultValue = "false") boolean includeHidden) {
        List<CommentResponse> comments = commentService.getAllComments(includeHidden);
        
        List<EntityModel<CommentResponse>> commentResources = comments.stream()
                .map(comment -> {
                    EntityModel<CommentResponse> resource = EntityModel.of(comment);
                    resource.add(linkTo(methodOn(CommentController.class).getAllComments(includeHidden)).withSelfRel());
                    resource.add(linkTo(methodOn(CommentController.class).getCommentsByGame(comment.getJuegoId(), false)).withRel("game-comments"));
                    resource.add(linkTo(methodOn(CommentController.class).getCommentsByUser(comment.getUsuarioId(), false)).withRel("user-comments"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<CommentResponse>> collection = CollectionModel.of(commentResources);
        collection.add(linkTo(methodOn(CommentController.class).getAllComments(includeHidden)).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(
        summary = "Crear comentario", 
        description = "Crea un nuevo comentario para un juego. Los comentarios permiten a los usuarios " +
                      "compartir sus opiniones sobre los juegos. Valida que el juego y usuario existan."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Comentario creado exitosamente. Retorna el comentario con su ID asignado.",
            content = @Content(schema = @Schema(implementation = CommentResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes, texto vacío, o IDs inválidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"El texto del comentario es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego o usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 10 no encontrado\"}"))
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<CommentResponse>> createComment(
            @Valid @RequestBody CreateCommentRequest request) {
        // Sin autenticación - usar valores por defecto o del request
        Long userId = request.getUsuarioId() != null ? request.getUsuarioId() : 1L;
        String userName = request.getUsuarioNombre() != null ? request.getUsuarioNombre() : "Usuario";
        
        CommentResponse comment = commentService.createComment(request, userId, userName);
        EntityModel<CommentResponse> resource = EntityModel.of(comment);
        
        resource.add(linkTo(methodOn(CommentController.class).createComment(request)).withSelfRel());
        resource.add(linkTo(methodOn(CommentController.class).getCommentsByGame(comment.getJuegoId(), false)).withRel("game-comments"));
        resource.add(linkTo(methodOn(CommentController.class).getCommentsByUser(userId, false)).withRel("user-comments"));
        
        return ResponseEntity.ok(resource);
    }
    
    @Operation(
        summary = "Obtener comentarios de un juego", 
        description = "Obtiene todos los comentarios visibles de un juego específico. Útil para mostrar los " +
                      "comentarios en la página de detalles del juego."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de comentarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "No hay comentarios para este juego"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 10 no encontrado\"}"))
        )
    })
    @GetMapping("/game/{juegoId}")
    public ResponseEntity<CollectionModel<EntityModel<CommentResponse>>> getCommentsByGame(
            @Parameter(description = "ID del juego", example = "10", required = true)
            @PathVariable Long juegoId,
            @Parameter(description = "Incluir comentarios ocultos", example = "false")
            @RequestParam(defaultValue = "false") boolean includeHidden) {
        List<CommentResponse> comments = commentService.getCommentsByGame(juegoId, includeHidden);
        
        List<EntityModel<CommentResponse>> commentResources = comments.stream()
                .map(comment -> {
                    EntityModel<CommentResponse> resource = EntityModel.of(comment);
                    resource.add(linkTo(methodOn(CommentController.class).getCommentsByGame(juegoId, includeHidden)).withSelfRel());
                    resource.add(linkTo(methodOn(CommentController.class).getCommentsByUser(comment.getUsuarioId(), false)).withRel("user-comments"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<CommentResponse>> collection = CollectionModel.of(commentResources);
        collection.add(linkTo(methodOn(CommentController.class).getCommentsByGame(juegoId, includeHidden)).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(
        summary = "Obtener comentarios de un usuario", 
        description = "Obtiene todos los comentarios publicados por un usuario específico. Útil para mostrar " +
                      "el historial de comentarios de un usuario en su perfil."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de comentarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "El usuario no tiene comentarios"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        )
    })
    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<CommentResponse>>> getCommentsByUser(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "Incluir comentarios ocultos", example = "false")
            @RequestParam(defaultValue = "false") boolean includeHidden) {
        List<CommentResponse> comments = commentService.getCommentsByUser(usuarioId, includeHidden);
        
        List<EntityModel<CommentResponse>> commentResources = comments.stream()
                .map(comment -> {
                    EntityModel<CommentResponse> resource = EntityModel.of(comment);
                    resource.add(linkTo(methodOn(CommentController.class).getCommentsByUser(usuarioId, includeHidden)).withSelfRel());
                    resource.add(linkTo(methodOn(CommentController.class).getCommentsByGame(comment.getJuegoId(), false)).withRel("game-comments"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<CommentResponse>> collection = CollectionModel.of(commentResources);
        collection.add(linkTo(methodOn(CommentController.class).getCommentsByUser(usuarioId, includeHidden)).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(
        summary = "Eliminar comentario", 
        description = "Elimina permanentemente un comentario del sistema. Esta acción no se puede deshacer. " +
                      "Útil para moderación de contenido."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204", 
            description = "Comentario eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Comentario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Comentario con ID 1 no encontrado\"}"))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
