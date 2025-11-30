package com.gamestore.order.repository;

import com.gamestore.order.entity.Order;
import com.gamestore.order.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    private Order testOrder;
    private OrderStatus testStatus;

    @BeforeEach
    void setUp() {
        testStatus = new OrderStatus();
        testStatus.setNombre("PENDIENTE");
        testStatus = orderStatusRepository.save(testStatus);

        testOrder = new Order();
        testOrder.setUserId(1L);
        testOrder.setTotal(59.98);
        testOrder.setEstadoId(testStatus.getId());
        testOrder.setMetodoPago("Tarjeta");
        testOrder.setDireccionEnvio("Test Address");
        testOrder.setFechaOrden("2024-01-01T10:00:00");
    }

    @Test
    void testSaveOrder() {
        Order saved = orderRepository.save(testOrder);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals(59.98, saved.getTotal());
    }

    @Test
    void testFindByUserId() {
        orderRepository.save(testOrder);
        
        List<Order> orders = orderRepository.findByUserId(1L);
        
        assertFalse(orders.isEmpty());
        assertTrue(orders.stream().allMatch(order -> order.getUserId().equals(1L)));
    }

    @Test
    void testFindById() {
        Order saved = orderRepository.save(testOrder);
        
        Optional<Order> found = orderRepository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testFindAll() {
        orderRepository.save(testOrder);
        
        List<Order> orders = orderRepository.findAll();
        
        assertFalse(orders.isEmpty());
    }
}

