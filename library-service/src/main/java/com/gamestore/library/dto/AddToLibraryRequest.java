package com.gamestore.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToLibraryRequest {
    @NotNull(message = "El userId es requerido")
    private Long userId;
    
    @NotBlank(message = "El juegoId es requerido")
    private String juegoId;
    
    @NotBlank(message = "El nombre es requerido")
    private String name;
    
    @NotNull(message = "El precio es requerido")
    private Double price;
    
    private String status = "Disponible";
    private String genre = "Acción";
}

