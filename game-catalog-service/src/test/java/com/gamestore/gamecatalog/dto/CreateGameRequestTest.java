package com.gamestore.gamecatalog.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameRequestTest {

    private Validator validator;
    private CreateGameRequest createGameRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        createGameRequest = new CreateGameRequest();
        createGameRequest.setNombre("Test Game");
        createGameRequest.setDescripcion("Test Description");
        createGameRequest.setPrecio(29.99);
        createGameRequest.setStock(10);
        createGameRequest.setCategoriaId(1L);
        createGameRequest.setGeneroId(1L);
        createGameRequest.setDesarrollador("Test Developer");
        createGameRequest.setFechaLanzamiento("2024");
    }

    @Test
    void testValidRequest() {
        Set<ConstraintViolation<CreateGameRequest>> violations = validator.validate(createGameRequest);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @Test
    void testNombreNotBlank() {
        createGameRequest.setNombre("");
        Set<ConstraintViolation<CreateGameRequest>> violations = validator.validate(createGameRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @Test
    void testPrecioNotNull() {
        createGameRequest.setPrecio(null);
        Set<ConstraintViolation<CreateGameRequest>> violations = validator.validate(createGameRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precio")));
    }

    @Test
    void testPrecioPositive() {
        createGameRequest.setPrecio(-10.0);
        Set<ConstraintViolation<CreateGameRequest>> violations = validator.validate(createGameRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("precio")));
    }

    @Test
    void testCategoriaIdNotNull() {
        createGameRequest.setCategoriaId(null);
        Set<ConstraintViolation<CreateGameRequest>> violations = validator.validate(createGameRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("categoriaId")));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("Test Game", createGameRequest.getNombre());
        assertEquals("Test Description", createGameRequest.getDescripcion());
        assertEquals(29.99, createGameRequest.getPrecio());
        assertEquals(10, createGameRequest.getStock());
        assertEquals(1L, createGameRequest.getCategoriaId());
        assertEquals(1L, createGameRequest.getGeneroId());
    }
}

