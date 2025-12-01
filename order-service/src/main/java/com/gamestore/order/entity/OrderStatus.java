package com.gamestore.order.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un estado de orden")
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado", example = "1")
    private Long id;
    
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre único del estado", example = "Pendiente", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción del estado", example = "Orden pendiente de procesamiento")
    private String descripcion;
}

