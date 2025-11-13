package com.gamestore.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String fechaOrden;
    private Double total;
    private Long estadoId;
    private String estadoNombre;
    private String metodoPago;
    private String direccionEnvio;
    private List<OrderDetailResponse> detalles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailResponse {
        private Long id;
        private Long juegoId;
        private String juegoNombre;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}

