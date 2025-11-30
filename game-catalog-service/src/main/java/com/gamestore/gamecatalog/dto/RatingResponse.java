package com.gamestore.gamecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Calificación de un juego por un usuario")
public class RatingResponse {
    @Schema(description = "ID único de la calificación", example = "1")
    private Long id;
    
    @Schema(description = "ID del juego calificado", example = "10")
    private Long juegoId;
    
    @Schema(description = "ID del usuario que calificó", example = "5")
    private Long usuarioId;
    
    @Schema(description = "Calificación otorgada (1-5 estrellas)", example = "5", minimum = "1", maximum = "5")
    private Integer calificacion;
    
    @Schema(description = "Fecha y hora de creación de la calificación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Fecha y hora de última actualización", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}




