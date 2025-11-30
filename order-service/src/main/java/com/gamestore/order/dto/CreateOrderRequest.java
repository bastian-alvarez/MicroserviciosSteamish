package com.gamestore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Solicitud para crear una nueva orden de compra")
public class CreateOrderRequest {
    @NotNull(message = "El userId es requerido")
    @Schema(description = "ID del usuario que realiza la orden", example = "5", required = true)
    private Long userId;
    
    @NotEmpty(message = "Los items son requeridos")
    @Schema(description = "Lista de juegos a comprar", required = true)
    private List<OrderItem> items;
    
    @Schema(description = "Método de pago a utilizar", example = "Tarjeta", defaultValue = "Tarjeta")
    private String metodoPago = "Tarjeta";
    
    @Schema(description = "Dirección de envío para la orden", example = "Calle Principal 123, Ciudad")
    private String direccionEnvio;
    
    @Data
    @Schema(description = "Item individual de la orden")
    public static class OrderItem {
        @NotNull(message = "El juegoId es requerido")
        @Schema(description = "ID del juego a comprar", example = "10", required = true)
        private Long juegoId;
        
        @NotNull(message = "La cantidad es requerida")
        @Schema(description = "Cantidad de unidades a comprar", example = "2", required = true, minimum = "1")
        private Integer cantidad;
    }
}

