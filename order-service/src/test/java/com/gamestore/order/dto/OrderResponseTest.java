package com.gamestore.order.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setFechaOrden("2024-01-01");
        orderResponse.setTotal(59.98);
        orderResponse.setEstadoId(1L);
        orderResponse.setEstadoNombre("PENDIENTE");
        orderResponse.setMetodoPago("Tarjeta");
        orderResponse.setDireccionEnvio("Test Address");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, orderResponse.getId());
        assertEquals(1L, orderResponse.getUserId());
        assertEquals("2024-01-01", orderResponse.getFechaOrden());
        assertEquals(59.98, orderResponse.getTotal());
        assertEquals(1L, orderResponse.getEstadoId());
        assertEquals("PENDIENTE", orderResponse.getEstadoNombre());
        assertEquals("Tarjeta", orderResponse.getMetodoPago());
        assertEquals("Test Address", orderResponse.getDireccionEnvio());
    }

    @Test
    void testNoArgsConstructor() {
        OrderResponse response = new OrderResponse();
        assertNull(response.getId());
        assertNull(response.getUserId());
        assertNull(response.getTotal());
    }

    @Test
    void testOrderDetailResponse() {
        OrderResponse.OrderDetailResponse detail = new OrderResponse.OrderDetailResponse();
        detail.setId(1L);
        detail.setJuegoId(1L);
        detail.setJuegoNombre("Test Game");
        detail.setCantidad(2);
        detail.setPrecioUnitario(29.99);
        detail.setSubtotal(59.98);

        assertEquals(1L, detail.getId());
        assertEquals(1L, detail.getJuegoId());
        assertEquals("Test Game", detail.getJuegoNombre());
        assertEquals(2, detail.getCantidad());
        assertEquals(29.99, detail.getPrecioUnitario());
        assertEquals(59.98, detail.getSubtotal());
    }

    @Test
    void testOrderDetailResponseAllArgsConstructor() {
        OrderResponse.OrderDetailResponse detail = new OrderResponse.OrderDetailResponse(
            1L, 1L, "Game", 2, 29.99, 59.98
        );
        assertEquals(1L, detail.getId());
        assertEquals("Game", detail.getJuegoNombre());
    }

    @Test
    void testDetallesList() {
        OrderResponse.OrderDetailResponse detail1 = new OrderResponse.OrderDetailResponse(
            1L, 1L, "Game 1", 1, 29.99, 29.99
        );
        OrderResponse.OrderDetailResponse detail2 = new OrderResponse.OrderDetailResponse(
            2L, 2L, "Game 2", 1, 19.99, 19.99
        );
        
        List<OrderResponse.OrderDetailResponse> detalles = Arrays.asList(detail1, detail2);
        orderResponse.setDetalles(detalles);
        
        assertNotNull(orderResponse.getDetalles());
        assertEquals(2, orderResponse.getDetalles().size());
    }

    @Test
    void testEquals() {
        OrderResponse response1 = new OrderResponse(
            1L, 1L, "2024-01-01", 59.98, 1L, 
            "PENDIENTE", "Tarjeta", "Address", null
        );
        OrderResponse response2 = new OrderResponse(
            1L, 1L, "2024-01-01", 59.98, 1L, 
            "PENDIENTE", "Tarjeta", "Address", null
        );
        
        assertEquals(response1, response2);
    }

    @Test
    void testHashCode() {
        OrderResponse response1 = new OrderResponse(
            1L, 1L, "2024-01-01", 59.98, 1L, 
            "PENDIENTE", "Tarjeta", "Address", null
        );
        OrderResponse response2 = new OrderResponse(
            1L, 1L, "2024-01-01", 59.98, 1L, 
            "PENDIENTE", "Tarjeta", "Address", null
        );
        
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        String toString = orderResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("OrderResponse"));
    }
}

