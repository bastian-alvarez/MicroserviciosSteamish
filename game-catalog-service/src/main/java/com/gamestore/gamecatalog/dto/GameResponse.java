package com.gamestore.gamecatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String imagenUrl;
    private String desarrollador;
    private String fechaLanzamiento;
    private Long categoriaId;
    private Long generoId;
    private Boolean activo;
    private Integer descuento;
    private Double discountedPrice;
    private Boolean hasDiscount;
    private String categoriaNombre;
    private String generoNombre;
}

