package com.gamestore.order.repository;

import com.gamestore.order.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderStatusRepositoryTest {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    private OrderStatus testStatus;

    @BeforeEach
    void setUp() {
        testStatus = new OrderStatus();
        testStatus.setNombre("PENDIENTE");
    }

    @Test
    void testSaveOrderStatus() {
        OrderStatus saved = orderStatusRepository.save(testStatus);
        
        assertNotNull(saved.getId());
        assertEquals("PENDIENTE", saved.getNombre());
    }

    @Test
    void testFindByNombreIgnoreCase_Success() {
        orderStatusRepository.save(testStatus);
        
        OrderStatus found = orderStatusRepository.findByNombreIgnoreCase("pendiente");
        
        assertNotNull(found);
        assertEquals("PENDIENTE", found.getNombre());
    }

    @Test
    void testFindByNombreIgnoreCase_CaseInsensitive() {
        orderStatusRepository.save(testStatus);
        
        OrderStatus found = orderStatusRepository.findByNombreIgnoreCase("PENDIENTE");
        
        assertNotNull(found);
        assertEquals("PENDIENTE", found.getNombre());
    }

    @Test
    void testFindByNombreIgnoreCase_NotFound() {
        OrderStatus found = orderStatusRepository.findByNombreIgnoreCase("NONEXISTENT");
        
        assertNull(found);
    }

    @Test
    void testFindById() {
        OrderStatus saved = orderStatusRepository.save(testStatus);
        
        var found = orderStatusRepository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }
}

