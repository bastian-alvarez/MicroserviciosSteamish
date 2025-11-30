package com.gamestore.gamecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear un nuevo juego en el catálogo")
public class CreateGameRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Schema(description = "Nombre del juego", example = "Cyberpunk 2077", required = true, maxLength = 255)
    private String nombre;
    
    @Schema(description = "Descripción detallada del juego", example = "Un RPG de acción ambientado en el futuro distópico")
    private String descripcion;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    @Schema(description = "Precio del juego en dólares", example = "59.99", required = true, minimum = "0.0")
    private Double precio;
    
    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    @Schema(description = "Cantidad inicial en stock", example = "100", required = true, minimum = "0")
    private Integer stock;
    
    @Schema(description = "URL de la imagen del juego (opcional, se puede subir después)", example = "http://example.com/image.jpg")
    private String imagenUrl;
    
    @NotBlank(message = "El desarrollador es requerido")
    @Schema(description = "Nombre del desarrollador del juego", example = "CD Projekt RED", required = true)
    private String desarrollador;
    
    @NotBlank(message = "La fecha de lanzamiento es requerida")
    @Schema(description = "Fecha de lanzamiento del juego (formato: YYYY-MM-DD)", example = "2020-12-10", required = true)
    private String fechaLanzamiento;
    
    @NotNull(message = "La categoría es requerida")
    @Schema(description = "ID de la categoría del juego", example = "1", required = true)
    private Long categoriaId;
    
    @NotNull(message = "El género es requerido")
    @Schema(description = "ID del género del juego", example = "3", required = true)
    private Long generoId;
    
    @Schema(description = "Porcentaje de descuento (0-100)", example = "15", defaultValue = "0", minimum = "0", maximum = "100")
    private Integer descuento = 0;
    
    @Schema(description = "Indica si el juego está activo en el catálogo", example = "true", defaultValue = "true")
    private Boolean activo = true;
}

