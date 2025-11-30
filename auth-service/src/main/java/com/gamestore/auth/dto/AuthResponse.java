package com.gamestore.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT y datos del usuario")
public class AuthResponse {
    @Schema(description = "Datos del usuario autenticado")
    private UserResponse user;
    
    @Schema(description = "Token JWT para autenticación en peticiones posteriores", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Tipo de token", example = "Bearer", defaultValue = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Tiempo de expiración del token en milisegundos", example = "86400000")
    private Long expiresIn;
}

