package com.gamestore.order.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un detalle de una orden de compra")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del detalle de orden", example = "1")
    private Long id;
    
    @Column(name = "orden_id", nullable = false)
    @Schema(description = "ID de la orden a la que pertenece este detalle", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ordenId;
    
    @Column(name = "juego_id", nullable = false)
    @Schema(description = "ID del juego incluido en este detalle", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long juegoId;
    
    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades del juego", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    @Schema(description = "Precio unitario del juego al momento de la compra", example = "59.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precioUnitario;
    
    @Column(nullable = false)
    @Schema(description = "Subtotal de este detalle (cantidad × precio_unitario)", example = "119.98", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double subtotal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", insertable = false, updatable = false)
    private Order orden;
}

