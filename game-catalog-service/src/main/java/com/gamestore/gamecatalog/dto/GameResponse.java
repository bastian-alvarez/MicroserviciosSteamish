package com.gamestore.gamecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un juego del catálogo")
public class GameResponse {
    @Schema(description = "ID único del juego", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del juego", example = "Cyberpunk 2077")
    private String nombre;
    
    @Schema(description = "Descripción detallada del juego", example = "Un RPG de acción ambientado en el futuro distópico de Night City")
    private String descripcion;
    
    @Schema(description = "Precio original del juego en dólares", example = "59.99")
    private Double precio;
    
    @Schema(description = "Cantidad disponible en stock", example = "50", minimum = "0")
    private Integer stock;
    
    @Schema(description = "URL de la imagen del juego", example = "http://localhost:3002/uploads/game-images/game_1_image.jpg")
    private String imagenUrl;
    
    @Schema(description = "Nombre del desarrollador", example = "CD Projekt RED")
    private String desarrollador;
    
    @Schema(description = "Fecha de lanzamiento del juego", example = "2020-12-10")
    private String fechaLanzamiento;
    
    @Schema(description = "ID de la categoría del juego", example = "1")
    private Long categoriaId;
    
    @Schema(description = "ID del género del juego", example = "3")
    private Long generoId;
    
    @Schema(description = "Indica si el juego está activo en el catálogo", example = "true")
    private Boolean activo;
    
    @Schema(description = "Porcentaje de descuento (0-100)", example = "15", minimum = "0", maximum = "100")
    private Integer descuento;
    
    @Schema(description = "Precio con descuento aplicado", example = "50.99")
    private Double discountedPrice;
    
    @Schema(description = "Indica si el juego tiene descuento activo", example = "true")
    private Boolean hasDiscount;
    
    @Schema(description = "Nombre de la categoría", example = "Acción")
    private String categoriaNombre;
    
    @Schema(description = "Nombre del género", example = "RPG")
    private String generoNombre;
    
    @Schema(description = "Promedio de calificaciones (1-5 estrellas)", example = "4.5", minimum = "1", maximum = "5")
    private Double averageRating;
    
    @Schema(description = "Número total de calificaciones recibidas", example = "1250", minimum = "0")
    private Long ratingCount;
}

