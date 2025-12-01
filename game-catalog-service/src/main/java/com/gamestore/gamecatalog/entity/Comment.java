package com.gamestore.gamecatalog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un comentario de un juego")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del comentario", example = "1")
    private Long id;
    
    @Column(name = "juego_id", nullable = false)
    @Schema(description = "ID del juego al que pertenece el comentario", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long juegoId;
    
    @Column(name = "usuario_id", nullable = false)
    @Schema(description = "ID del usuario que escribió el comentario", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioId;
    
    @Column(name = "usuario_nombre", nullable = false)
    @Schema(description = "Nombre del usuario que escribió el comentario", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String usuarioNombre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Contenido del comentario", example = "Excelente juego, muy recomendado", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contenido;
    
    @Column(name = "is_hidden", nullable = false)
    @Schema(description = "Indica si el comentario está oculto por moderación", example = "false", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "false")
    private Boolean isHidden = false;
    
    @Column(name = "created_at")
    @Schema(description = "Fecha y hora de creación del comentario", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Fecha y hora de última actualización del comentario", example = "2024-01-15T10:30:00")
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




