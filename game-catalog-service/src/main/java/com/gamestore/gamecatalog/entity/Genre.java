package com.gamestore.gamecatalog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "generos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un género de juegos")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del género", example = "1")
    private Long id;
    
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre único del género", example = "RPG", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción del género", example = "Juegos de rol")
    private String descripcion;
}

