package com.gamestore.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private Validator validator;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("+56912345678");
        registerRequest.setPassword("password123");
        registerRequest.setGender("M");
    }

    @Test
    void testValidRequest() {
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @Test
    void testNameNotBlank() {
        registerRequest.setName("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testEmailNotBlank() {
        registerRequest.setEmail("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testEmailFormat() {
        registerRequest.setEmail("invalid-email");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testPasswordMinLength() {
        registerRequest.setPassword("12345"); // Less than 6 characters
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("Test User", registerRequest.getName());
        assertEquals("test@example.com", registerRequest.getEmail());
        assertEquals("+56912345678", registerRequest.getPhone());
        assertEquals("password123", registerRequest.getPassword());
        assertEquals("M", registerRequest.getGender());
    }

    @Test
    void testDefaultGender() {
        RegisterRequest request = new RegisterRequest();
        request.setName("User");
        request.setEmail("user@example.com");
        request.setPhone("123456");
        request.setPassword("password");
        // Gender defaults to empty string
        assertEquals("", request.getGender());
    }
}

