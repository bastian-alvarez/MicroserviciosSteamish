package com.gamestore.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String imagenUrl;
    private String desarrollador;
    private String fechaLanzamiento;
    private Long categoriaId;
    private Long generoId;
    private Integer descuento;
    private Boolean activo;
}

