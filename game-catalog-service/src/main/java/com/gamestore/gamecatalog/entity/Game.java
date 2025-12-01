package com.gamestore.gamecatalog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "juegos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un juego en el catálogo")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del juego", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Nombre del juego", example = "Cyberpunk 2077", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada del juego", example = "Un juego de rol de acción en un mundo abierto", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descripcion;
    
    @Column(nullable = false)
    @Schema(description = "Precio del juego en dólares", example = "59.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precio;
    
    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades disponibles en stock", example = "100", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "0")
    private Integer stock = 0;
    
    @Column(name = "imagen_url", length = 500)
    @Schema(description = "URL de la imagen del juego", example = "http://example.com/images/game1.jpg", maxLength = 500)
    private String imagenUrl;
    
    @Schema(description = "Nombre del desarrollador del juego", example = "CD Projekt RED", defaultValue = "Desarrollador")
    private String desarrollador = "Desarrollador";
    
    @Column(name = "fecha_lanzamiento")
    @Schema(description = "Fecha de lanzamiento del juego", example = "2024-01-15", defaultValue = "2024")
    private String fechaLanzamiento = "2024";
    
    @Column(name = "categoria_id", nullable = false)
    @Schema(description = "ID de la categoría del juego", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoriaId;
    
    @Column(name = "genero_id", nullable = false)
    @Schema(description = "ID del género del juego", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long generoId;
    
    @Column(nullable = false)
    @Schema(description = "Indica si el juego está activo en el catálogo", example = "true", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "true")
    private Boolean activo = true;
    
    @Column(nullable = false)
    @Schema(description = "Porcentaje de descuento aplicado al juego (0-100)", example = "20", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "0")
    private Integer descuento = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    private Category categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genero_id", insertable = false, updatable = false)
    private Genre genero;
    
    public Double getDiscountedPrice() {
        if (descuento > 0) {
            return precio * (1 - descuento / 100.0);
        }
        return precio;
    }
    
    public Boolean getHasDiscount() {
        return descuento > 0;
    }
}

