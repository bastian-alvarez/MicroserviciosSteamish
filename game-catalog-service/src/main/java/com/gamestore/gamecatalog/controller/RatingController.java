package com.gamestore.gamecatalog.controller;

import com.gamestore.gamecatalog.dto.CreateRatingRequest;
import com.gamestore.gamecatalog.dto.RatingResponse;
import com.gamestore.gamecatalog.service.RatingService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Calificaciones", description = "API para gestión de calificaciones de juegos")
public class RatingController {
    private final RatingService ratingService;
    
    // Sin autenticación requerida - todos los endpoints públicos
    
    @Operation(
        summary = "Calificar un juego", 
        description = "Crea o actualiza la calificación de un usuario para un juego. La calificación debe estar " +
                      "entre 1 y 5 estrellas. Si el usuario ya calificó el juego, se actualiza la calificación existente."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Calificación guardada exitosamente. Retorna la calificación creada o actualizada.",
            content = @Content(schema = @Schema(implementation = RatingResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: calificación fuera del rango 1-5, campos requeridos faltantes, o IDs inválidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"La calificación debe estar entre 1 y 5\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego o usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 10 no encontrado\"}"))
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<RatingResponse>> createOrUpdateRating(
            @Valid @RequestBody CreateRatingRequest request) {
        // Sin autenticación - usar userId del request o valor por defecto
        Long userId = request.getUsuarioId() != null ? request.getUsuarioId() : 1L;
        RatingResponse rating = ratingService.createOrUpdateRating(request, userId);
        EntityModel<RatingResponse> resource = EntityModel.of(rating);
        
        resource.add(linkTo(methodOn(RatingController.class).createOrUpdateRating(request)).withSelfRel());
        resource.add(linkTo(methodOn(RatingController.class).getUserRating(request.getJuegoId(), userId)).withRel("user-rating"));
        resource.add(linkTo(methodOn(RatingController.class).getGameRating(request.getJuegoId())).withRel("game-rating"));
        
        return ResponseEntity.ok(resource);
    }
    
    @Operation(
        summary = "Obtener calificación del usuario para un juego", 
        description = "Obtiene la calificación que un usuario específico le dio a un juego. Útil para mostrar " +
                      "la calificación del usuario actual en la UI."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Calificación encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = RatingResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Calificación no encontrada: el usuario no ha calificado este juego",
            content = @Content(schema = @Schema(example = "{\"error\": \"Calificación no encontrada\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "IDs inválidos",
            content = @Content(schema = @Schema(example = "{\"error\": \"IDs inválidos\"}"))
        )
    })
    @GetMapping("/game/{juegoId}/user/{usuarioId}")
    public ResponseEntity<EntityModel<RatingResponse>> getUserRating(
            @Parameter(description = "ID del juego", example = "10", required = true)
            @PathVariable Long juegoId,
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long usuarioId) {
        RatingResponse rating = ratingService.getRatingByUserAndGame(juegoId, usuarioId);
        EntityModel<RatingResponse> resource = EntityModel.of(rating);
        
        resource.add(linkTo(methodOn(RatingController.class).getUserRating(juegoId, usuarioId)).withSelfRel());
        resource.add(linkTo(methodOn(RatingController.class).getGameRating(juegoId)).withRel("game-rating"));
        
        return ResponseEntity.ok(resource);
    }
    
    @Operation(
        summary = "Obtener promedio de calificaciones de un juego", 
        description = "Obtiene el promedio de calificaciones (1-5 estrellas) y el total de calificaciones recibidas " +
                      "por un juego específico. Útil para mostrar la calificación promedio en el catálogo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Información de calificaciones obtenida exitosamente. Si no hay calificaciones, el promedio es null.",
            content = @Content(schema = @Schema(example = "{\"juegoId\": 10, \"averageRating\": 4.5, \"ratingCount\": 1250}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Juego no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 10 no encontrado\"}"))
        )
    })
    @GetMapping("/game/{juegoId}")
    public ResponseEntity<EntityModel<Map<String, Object>>> getGameRating(
            @Parameter(description = "ID del juego", example = "10", required = true)
            @PathVariable Long juegoId) {
        Double averageRating = ratingService.getAverageRating(juegoId);
        Long ratingCount = ratingService.getRatingCount(juegoId);
        
        Map<String, Object> ratingInfo = Map.of(
            "juegoId", juegoId,
            "averageRating", averageRating,
            "ratingCount", ratingCount
        );
        
        EntityModel<Map<String, Object>> resource = EntityModel.of(ratingInfo);
        resource.add(linkTo(methodOn(RatingController.class).getGameRating(juegoId)).withSelfRel());
        
        return ResponseEntity.ok(resource);
    }
}


