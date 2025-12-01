package com.gamestore.gamecatalog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una categoría de juegos")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;
    
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre único de la categoría", example = "Acción", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción de la categoría", example = "Juegos de acción y aventura")
    private String descripcion;
}

