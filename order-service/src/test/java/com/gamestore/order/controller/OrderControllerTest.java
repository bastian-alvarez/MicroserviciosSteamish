package com.gamestore.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.order.dto.CreateOrderRequest;
import com.gamestore.order.dto.OrderResponse;
import com.gamestore.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderResponse orderResponse;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(1L);
        CreateOrderRequest.OrderItem item = new CreateOrderRequest.OrderItem();
        item.setJuegoId(1L);
        item.setCantidad(2);
        createOrderRequest.setItems(Arrays.asList(item));

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setFechaOrden("2024-01-01");
        orderResponse.setTotal(59.98);
        orderResponse.setEstadoNombre("PENDIENTE");
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.total").value(59.98));
    }

    @Test
    void testCreateOrder_InvalidData() throws Exception {
        createOrderRequest.setUserId(null);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOrdersByUserId_Success() throws Exception {
        List<OrderResponse> orders = Arrays.asList(orderResponse);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orderResponseList").exists())
                .andExpect(jsonPath("$._embedded.orderResponseList[0].id").value(1L));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("Orden no encontrada"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOrders_Success() throws Exception {
        List<OrderResponse> orders = Arrays.asList(orderResponse);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orderResponseList").exists());
    }
}

