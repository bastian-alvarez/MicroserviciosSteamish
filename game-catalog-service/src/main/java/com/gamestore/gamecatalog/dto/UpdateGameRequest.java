package com.gamestore.gamecatalog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameRequest {
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    private String descripcion;

    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private Double precio;

    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;

    private String imagenUrl;

    private String desarrollador;

    private String fechaLanzamiento;

    private Long categoriaId;

    private Long generoId;

    private Integer descuento;

    private Boolean activo;
}

