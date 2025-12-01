package com.gamestore.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un administrador en el sistema")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del administrador", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Nombre completo del administrador", example = "Admin Principal", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Column(nullable = false, unique = true)
    @Schema(description = "Email único del administrador", example = "admin@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @Column(nullable = false)
    @Schema(description = "Número de teléfono del administrador", example = "+1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    
    @Column(nullable = false)
    @Schema(description = "Contraseña encriptada del administrador", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Rol del administrador en el sistema", example = "SUPER_ADMIN", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"SUPER_ADMIN", "GAME_MANAGER", "SUPPORT", "MODERATOR"})
    private AdminRole role;
    
    @Column(name = "profile_photo_uri", length = 500)
    @Schema(description = "URL de la foto de perfil del administrador", example = "http://example.com/photos/admin1.jpg", maxLength = 500)
    private String profilePhotoUri;
    
    @Column(name = "created_at")
    @Schema(description = "Fecha y hora de creación del administrador", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Fecha y hora de última actualización del administrador", example = "2024-01-15T10:30:00")
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
    
    public enum AdminRole {
        SUPER_ADMIN,
        GAME_MANAGER,
        SUPPORT,
        MODERATOR
    }
}

