package com.gamestore.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Email(message = "Formato de email inválido")
    private String email;
    
    private String phone;
    
    private String gender;
    
    private Boolean isBlocked;
}

