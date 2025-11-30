package com.gamestore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los detalles completos de una orden de compra")
public class OrderResponse {
    @Schema(description = "ID único de la orden", example = "1")
    private Long id;
    
    @Schema(description = "ID del usuario que realizó la orden", example = "5")
    private Long userId;
    
    @Schema(description = "Fecha y hora de creación de la orden", example = "2024-01-15T10:30:00")
    private String fechaOrden;
    
    @Schema(description = "Total de la orden en dólares", example = "149.99")
    private Double total;
    
    @Schema(description = "ID del estado de la orden", example = "1")
    private Long estadoId;
    
    @Schema(description = "Nombre del estado de la orden", example = "Pendiente")
    private String estadoNombre;
    
    @Schema(description = "Método de pago utilizado", example = "Tarjeta de crédito")
    private String metodoPago;
    
    @Schema(description = "Dirección de envío", example = "Calle Principal 123, Ciudad")
    private String direccionEnvio;
    
    @Schema(description = "Lista de detalles de los juegos incluidos en la orden")
    private List<OrderDetailResponse> detalles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalle de un juego incluido en la orden")
    public static class OrderDetailResponse {
        @Schema(description = "ID del detalle de orden", example = "1")
        private Long id;
        
        @Schema(description = "ID del juego", example = "10")
        private Long juegoId;
        
        @Schema(description = "Nombre del juego", example = "Cyberpunk 2077")
        private String juegoNombre;
        
        @Schema(description = "Cantidad de unidades compradas", example = "2")
        private Integer cantidad;
        
        @Schema(description = "Precio unitario del juego", example = "59.99")
        private Double precioUnitario;
        
        @Schema(description = "Subtotal (precio unitario × cantidad)", example = "119.98")
        private Double subtotal;
    }
}

