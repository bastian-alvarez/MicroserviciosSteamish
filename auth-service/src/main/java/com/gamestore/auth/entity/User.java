package com.gamestore.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un usuario en el sistema")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Column(nullable = false, unique = true)
    @Schema(description = "Email único del usuario", example = "juan.perez@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @Column(nullable = false)
    @Schema(description = "Número de teléfono del usuario", example = "+1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    
    @Column(nullable = false)
    @Schema(description = "Contraseña encriptada del usuario", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
    
    @Column(name = "profile_photo_uri", length = 500)
    @Schema(description = "URL de la foto de perfil del usuario", example = "http://example.com/photos/user1.jpg", maxLength = 500)
    private String profilePhotoUri;
    
    @Column(name = "is_blocked")
    @Schema(description = "Indica si la cuenta del usuario está bloqueada", example = "false", defaultValue = "false")
    private Boolean isBlocked = false;
    
    @Schema(description = "Género del usuario", example = "Masculino")
    private String gender = "";
    
    @Column(name = "created_at")
    @Schema(description = "Fecha y hora de creación del usuario", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Fecha y hora de última actualización del usuario", example = "2024-01-15T10:30:00")
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

