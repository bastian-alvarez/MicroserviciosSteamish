package com.gamestore.order.service;

import com.gamestore.order.dto.CreateOrderRequest;
import com.gamestore.order.dto.OrderResponse;
import com.gamestore.order.entity.Order;
import com.gamestore.order.entity.OrderDetail;
import com.gamestore.order.entity.OrderStatus;
import com.gamestore.order.repository.OrderRepository;
import com.gamestore.order.repository.OrderStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest createRequest;
    private OrderStatus testStatus;
    private Order testOrder;
    private OrderDetail testDetail;

    @BeforeEach
    void setUp() {
        // Las URLs de servicios son constantes estáticas finales, no necesitan ser configuradas

        createRequest = new CreateOrderRequest();
        createRequest.setUserId(1L);
        createRequest.setMetodoPago("Tarjeta");
        createRequest.setDireccionEnvio("Test Address");
        
        CreateOrderRequest.OrderItem item = new CreateOrderRequest.OrderItem();
        item.setJuegoId(1L);
        item.setCantidad(2);
        createRequest.setItems(Arrays.asList(item));

        testStatus = new OrderStatus();
        testStatus.setId(1L);
        testStatus.setNombre("Pendiente");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setTotal(59.98);
        testOrder.setEstadoId(1L);
        testOrder.setMetodoPago("Tarjeta");
        testOrder.setDireccionEnvio("Test Address");
        testOrder.setFechaOrden("2025-11-20T10:00:00");
        testOrder.setEstado(testStatus);

        testDetail = new OrderDetail();
        testDetail.setId(1L);
        testDetail.setOrdenId(1L);
        testDetail.setJuegoId(1L);
        testDetail.setCantidad(2);
        testDetail.setPrecioUnitario(29.99);
        testDetail.setSubtotal(59.98);
        testOrder.setDetalles(Arrays.asList(testDetail));
    }

    @Test
    void testGetOrdersByUserId() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(testOrder));

        List<OrderResponse> result = orderService.getOrdersByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        List<OrderResponse> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getId());
        assertEquals(testOrder.getUserId(), response.getUserId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
    }
}

