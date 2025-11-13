package com.gamestore.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "El userId es requerido")
    private Long userId;
    
    @NotEmpty(message = "Los items son requeridos")
    private List<OrderItem> items;
    
    private String metodoPago = "Tarjeta";
    private String direccionEnvio;
    
    @Data
    public static class OrderItem {
        @NotNull(message = "El juegoId es requerido")
        private Long juegoId;
        
        @NotNull(message = "La cantidad es requerida")
        private Integer cantidad;
    }
}

