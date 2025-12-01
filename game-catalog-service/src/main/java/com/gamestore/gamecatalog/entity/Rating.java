package com.gamestore.gamecatalog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"juego_id", "usuario_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una calificación de un juego")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la calificación", example = "1")
    private Long id;
    
    @Column(name = "juego_id", nullable = false)
    @Schema(description = "ID del juego calificado", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long juegoId;
    
    @Column(name = "usuario_id", nullable = false)
    @Schema(description = "ID del usuario que realizó la calificación", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioId;
    
    @Column(nullable = false)
    @Schema(description = "Calificación del juego (1-5 estrellas)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "5")
    private Integer calificacion; // 1-5 estrellas
    
    @Column(name = "created_at")
    @Schema(description = "Fecha y hora de creación de la calificación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Fecha y hora de última actualización de la calificación", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




