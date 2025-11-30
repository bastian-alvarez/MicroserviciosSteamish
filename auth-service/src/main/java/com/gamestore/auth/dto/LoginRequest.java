package com.gamestore.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Solicitud de inicio de sesión")
public class LoginRequest {
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    @Schema(description = "Email del usuario", example = "usuario@example.com", required = true)
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Schema(description = "Contraseña del usuario", example = "password123", required = true, format = "password")
    private String password;
}

