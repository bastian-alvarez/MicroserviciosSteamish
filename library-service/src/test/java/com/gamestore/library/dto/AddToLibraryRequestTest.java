package com.gamestore.library.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AddToLibraryRequestTest {

    private Validator validator;
    private AddToLibraryRequest addToLibraryRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        addToLibraryRequest = new AddToLibraryRequest();
        addToLibraryRequest.setUserId(1L);
        addToLibraryRequest.setJuegoId("1");
        addToLibraryRequest.setName("Test Game");
        addToLibraryRequest.setPrice(29.99);
    }

    @Test
    void testValidRequest() {
        Set<ConstraintViolation<AddToLibraryRequest>> violations = validator.validate(addToLibraryRequest);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @Test
    void testUserIdNotNull() {
        addToLibraryRequest.setUserId(null);
        Set<ConstraintViolation<AddToLibraryRequest>> violations = validator.validate(addToLibraryRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void testJuegoIdNotBlank() {
        addToLibraryRequest.setJuegoId("");
        Set<ConstraintViolation<AddToLibraryRequest>> violations = validator.validate(addToLibraryRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("juegoId")));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, addToLibraryRequest.getUserId());
        assertEquals("1", addToLibraryRequest.getJuegoId());
    }
}

