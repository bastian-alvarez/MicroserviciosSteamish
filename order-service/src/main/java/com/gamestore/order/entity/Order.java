package com.gamestore.order.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una orden de compra en el sistema")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la orden", example = "1")
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    @Schema(description = "ID del usuario que realizó la orden", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    
    @Column(name = "fecha_orden", nullable = false)
    @Schema(description = "Fecha de creación de la orden", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fechaOrden;
    
    @Column(nullable = false)
    @Schema(description = "Total de la orden en dólares", example = "119.98", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;
    
    @Column(name = "estado_id", nullable = false)
    @Schema(description = "ID del estado de la orden", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long estadoId;
    
    @Column(name = "metodo_pago", nullable = false)
    @Schema(description = "Método de pago utilizado", example = "Tarjeta de crédito", requiredMode = Schema.RequiredMode.REQUIRED)
    private String metodoPago;
    
    @Column(name = "direccion_envio", length = 500)
    @Schema(description = "Dirección de envío de la orden", example = "Calle Principal 123, Ciudad", maxLength = 500)
    private String direccionEnvio;
    
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> detalles;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", insertable = false, updatable = false)
    private OrderStatus estado;
}

