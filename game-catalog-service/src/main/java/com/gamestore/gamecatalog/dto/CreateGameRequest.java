package com.gamestore.gamecatalog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private Double precio;
    
    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;
    
    private String imagenUrl;
    
    @NotBlank(message = "El desarrollador es requerido")
    private String desarrollador;
    
    @NotBlank(message = "La fecha de lanzamiento es requerida")
    private String fechaLanzamiento;
    
    @NotNull(message = "La categoría es requerida")
    private Long categoriaId;
    
    @NotNull(message = "El género es requerido")
    private Long generoId;
    
    private Integer descuento = 0;
    
    private Boolean activo = true;
}

