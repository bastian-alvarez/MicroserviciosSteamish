package com.gamestore.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Solicitud de registro de nuevo usuario")
public class RegisterRequest {
    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", required = true, maxLength = 255)
    private String name;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    @Schema(description = "Email del usuario (debe ser único)", example = "juan.perez@example.com", required = true)
    private String email;
    
    @NotBlank(message = "El teléfono es requerido")
    @Schema(description = "Número de teléfono del usuario", example = "+1234567890", required = true)
    private String phone;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña del usuario (mínimo 6 caracteres)", example = "password123", required = true, format = "password", minLength = 6)
    private String password;
    
    @Schema(description = "Género del usuario (opcional)", example = "M", allowableValues = {"M", "F", ""})
    private String gender = "";
}

