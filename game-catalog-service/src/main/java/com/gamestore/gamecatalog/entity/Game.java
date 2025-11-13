package com.gamestore.gamecatalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "juegos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    private String desarrollador = "Desarrollador";
    
    @Column(name = "fecha_lanzamiento")
    private String fechaLanzamiento = "2024";
    
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;
    
    @Column(name = "genero_id", nullable = false)
    private Long generoId;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(nullable = false)
    private Integer descuento = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    private Category categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genero_id", insertable = false, updatable = false)
    private Genre genero;
    
    public Double getDiscountedPrice() {
        if (descuento > 0) {
            return precio * (1 - descuento / 100.0);
        }
        return precio;
    }
    
    public Boolean getHasDiscount() {
        return descuento > 0;
    }
}

