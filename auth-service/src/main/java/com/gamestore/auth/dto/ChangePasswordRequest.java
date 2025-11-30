package com.gamestore.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "La contraseña actual es requerida")
    private String currentPassword;
    
    @NotBlank(message = "La nueva contraseña es requerida")
    private String newPassword;
}

