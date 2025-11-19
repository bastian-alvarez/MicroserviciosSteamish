package com.gamestore.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfilePhotoRequest {
    @NotBlank(message = "La URL de la foto de perfil es requerida")
    private String profilePhotoUri;
}

