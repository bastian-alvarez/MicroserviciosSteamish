package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CommentResponse;
import com.gamestore.gamecatalog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Moderador", description = "API para moderadores - gestión de comentarios y usuarios")
public class ModeratorController {
    private final CommentService commentService;
    private final WebClient.Builder webClientBuilder;
    
    // Usando Eureka para descubrimiento de servicios
    private static final String AUTH_SERVICE = "http://auth-service";
    
    @Operation(
        summary = "Obtener datos de un usuario", 
        description = "Obtiene los datos completos de un usuario específico desde el servicio de autenticación. " +
                      "Útil para moderadores que necesitan información del usuario al revisar comentarios. " +
                      "Solo moderadores pueden acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Datos del usuario obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de moderador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado - Se requiere rol de moderador\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al comunicarse con el servicio de autenticación",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al obtener datos del usuario\"}"))
        )
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<EntityModel<Map<String, Object>>> getUserData(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long userId) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(AUTH_SERVICE).build();
            Map<String, Object> userData = webClient.get()
                    .uri("/api/admin/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            
            EntityModel<Map<String, Object>> resource = EntityModel.of(userData);
            resource.add(linkTo(methodOn(ModeratorController.class).getUserData(userId)).withSelfRel());
            resource.add(linkTo(methodOn(ModeratorController.class).getUserComments(userId)).withRel("user-comments"));
            
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Obtener comentarios de un usuario", description = "Obtiene todos los comentarios (incluyendo ocultos) publicados por un usuario específico. Solo moderadores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol de moderador")
    })
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<CollectionModel<EntityModel<CommentResponse>>> getUserComments(@PathVariable Long userId) {
        List<CommentResponse> comments = commentService.getCommentsByUser(userId, true); // includeHidden = true
        
        List<EntityModel<CommentResponse>> commentResources = comments.stream()
                .map(comment -> {
                    EntityModel<CommentResponse> resource = EntityModel.of(comment);
                    resource.add(linkTo(methodOn(ModeratorController.class).getUserComments(userId)).withSelfRel());
                    resource.add(linkTo(methodOn(ModeratorController.class).hideComment(comment.getId())).withRel("hide-comment"));
                    resource.add(linkTo(methodOn(ModeratorController.class).showComment(comment.getId())).withRel("show-comment"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<CommentResponse>> collection = CollectionModel.of(commentResources);
        collection.add(linkTo(methodOn(ModeratorController.class).getUserComments(userId)).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(
        summary = "Ocultar comentario", 
        description = "Oculta un comentario público para que no sea visible en las listas públicas. " +
                      "El comentario sigue existiendo en la base de datos pero no se muestra a usuarios regulares. " +
                      "Solo moderadores pueden ocultar comentarios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Comentario ocultado exitosamente. Retorna el comentario con isHidden=true.",
            content = @Content(schema = @Schema(implementation = CommentResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Comentario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Comentario con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de moderador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
    })
    @PostMapping("/comments/{commentId}/hide")
    public ResponseEntity<EntityModel<CommentResponse>> hideComment(@PathVariable Long commentId) {
        try {
            CommentResponse comment = commentService.hideComment(commentId);
            EntityModel<CommentResponse> resource = EntityModel.of(comment);
            
            resource.add(linkTo(methodOn(ModeratorController.class).hideComment(commentId)).withSelfRel());
            resource.add(linkTo(methodOn(ModeratorController.class).showComment(commentId)).withRel("show-comment"));
            resource.add(linkTo(methodOn(ModeratorController.class).getUserComments(comment.getUsuarioId())).withRel("user-comments"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Mostrar comentario", 
        description = "Muestra un comentario previamente oculto, restaurando su visibilidad pública. " +
                      "El comentario volverá a aparecer en las listas públicas. Solo moderadores pueden mostrar comentarios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Comentario mostrado exitosamente. Retorna el comentario con isHidden=false.",
            content = @Content(schema = @Schema(implementation = CommentResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Comentario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Comentario con ID 1 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de moderador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
    })
    @PostMapping("/comments/{commentId}/show")
    public ResponseEntity<EntityModel<CommentResponse>> showComment(@PathVariable Long commentId) {
        try {
            CommentResponse comment = commentService.showComment(commentId);
            EntityModel<CommentResponse> resource = EntityModel.of(comment);
            
            resource.add(linkTo(methodOn(ModeratorController.class).showComment(commentId)).withSelfRel());
            resource.add(linkTo(methodOn(ModeratorController.class).hideComment(commentId)).withRel("hide-comment"));
            resource.add(linkTo(methodOn(ModeratorController.class).getUserComments(comment.getUsuarioId())).withRel("user-comments"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

