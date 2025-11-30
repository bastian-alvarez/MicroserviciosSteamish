package com.gamestore.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Solicitud para agregar un juego a la biblioteca del usuario")
public class AddToLibraryRequest {
    @NotNull(message = "El userId es requerido")
    @Schema(description = "ID del usuario", example = "5", required = true)
    private Long userId;
    
    @NotBlank(message = "El juegoId es requerido")
    @Schema(description = "ID del juego a agregar", example = "10", required = true)
    private String juegoId;
    
    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre del juego", example = "Cyberpunk 2077", required = true)
    private String name;
    
    @NotNull(message = "El precio es requerido")
    @Schema(description = "Precio del juego al momento de la compra", example = "59.99", required = true, minimum = "0.0")
    private Double price;
    
    @Schema(description = "Estado del juego en la biblioteca", example = "Disponible", defaultValue = "Disponible")
    private String status = "Disponible";
    
    @Schema(description = "Género del juego", example = "RPG", defaultValue = "Acción")
    private String genre = "Acción";
}

