package com.gamestore.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos del usuario")
public class UserResponse {
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String name;
    
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;
    
    @Schema(description = "Número de teléfono", example = "+1234567890")
    private String phone;
    
    @Schema(description = "URI de la foto de perfil", example = "http://localhost:3001/uploads/profile-photos/user_1_photo.jpg")
    private String profilePhotoUri;
    
    @Schema(description = "Indica si el usuario está bloqueado", example = "false")
    private Boolean isBlocked;
    
    @Schema(description = "Género del usuario", example = "M", allowableValues = {"M", "F", ""})
    private String gender;
}

