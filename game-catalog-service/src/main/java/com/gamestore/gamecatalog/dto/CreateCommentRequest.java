package com.gamestore.gamecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear un nuevo comentario sobre un juego")
public class CreateCommentRequest {
    @NotNull(message = "El ID del juego es requerido")
    @Schema(description = "ID del juego a comentar", example = "10", required = true)
    private Long juegoId;
    
    @NotBlank(message = "El contenido del comentario es requerido")
    @Size(min = 1, max = 2000, message = "El comentario debe tener entre 1 y 2000 caracteres")
    @Schema(description = "Contenido del comentario", example = "Excelente juego, muy recomendado!", required = true, minLength = 1, maxLength = 2000)
    private String contenido;
    
    @Schema(description = "ID del usuario (opcional, se puede obtener del token JWT)", example = "5")
    private Long usuarioId;
    
    @Schema(description = "Nombre del usuario (opcional, se puede obtener del token JWT)", example = "Juan Pérez")
    private String usuarioNombre;
}

