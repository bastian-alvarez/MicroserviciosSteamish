package com.gamestore.gamecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear o actualizar una calificación de juego")
public class CreateRatingRequest {
    @NotNull(message = "El ID del juego es requerido")
    @Schema(description = "ID del juego a calificar", example = "10", required = true)
    private Long juegoId;
    
    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación debe ser máximo 5")
    @Schema(description = "Calificación del juego (1-5 estrellas)", example = "5", required = true, minimum = "1", maximum = "5")
    private Integer calificacion;
    
    @Schema(description = "ID del usuario (opcional, se puede obtener del token JWT)", example = "5")
    private Long usuarioId;
}

