package com.gamestore.gamecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comentario de un usuario sobre un juego")
public class CommentResponse {
    @Schema(description = "ID único del comentario", example = "1")
    private Long id;
    
    @Schema(description = "ID del juego comentado", example = "10")
    private Long juegoId;
    
    @Schema(description = "ID del usuario que comentó", example = "5")
    private Long usuarioId;
    
    @Schema(description = "Nombre del usuario que comentó", example = "Juan Pérez")
    private String usuarioNombre;
    
    @Schema(description = "Contenido del comentario", example = "Excelente juego, muy recomendado!")
    private String contenido;
    
    @Schema(description = "Indica si el comentario está oculto por moderadores", example = "false")
    private Boolean isHidden;
    
    @Schema(description = "Fecha y hora de creación del comentario", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Fecha y hora de última actualización", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}




