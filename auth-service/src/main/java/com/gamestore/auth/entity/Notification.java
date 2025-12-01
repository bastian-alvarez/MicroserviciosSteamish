package com.gamestore.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una notificación en el sistema")
public class Notification {
    
    public enum NotificationType {
        USER_BLOCKED,
        COMMENT_HIDDEN,
        PHOTO_UPLOADED,
        ORDER_CREATED,
        GAME_ADDED,
        OTHER
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    @Schema(description = "ID del usuario que recibe la notificación", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo de notificación", example = "ORDER_CREATED", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"USER_BLOCKED", "COMMENT_HIDDEN", "PHOTO_UPLOADED", "ORDER_CREATED", "GAME_ADDED", "OTHER"})
    private NotificationType type;
    
    @Column(nullable = false, length = 200)
    @Schema(description = "Título de la notificación", example = "Orden creada exitosamente", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 200)
    private String title;
    
    @Column(nullable = false, length = 500)
    @Schema(description = "Mensaje de la notificación", example = "Tu orden #123 ha sido creada correctamente", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 500)
    private String message;
    
    @Column(name = "is_read", nullable = false)
    @Schema(description = "Indica si la notificación ha sido leída", example = "false", defaultValue = "false")
    private Boolean isRead = false;
    
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Fecha y hora de creación de la notificación", example = "2024-01-15T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }
}

