package com.gamestore.auth.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    private AuthResponse authResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setName("Test User");

        authResponse = new AuthResponse();
        authResponse.setUser(userResponse);
        authResponse.setToken("test-token");
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(86400000L);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(userResponse, authResponse.getUser());
        assertEquals("test-token", authResponse.getToken());
        assertEquals("Bearer", authResponse.getTokenType());
        assertEquals(86400000L, authResponse.getExpiresIn());
    }

    @Test
    void testNoArgsConstructor() {
        AuthResponse response = new AuthResponse();
        assertNull(response.getUser());
        assertNull(response.getToken());
        assertEquals("Bearer", response.getTokenType()); // Default value
        assertNull(response.getExpiresIn());
    }

    @Test
    void testAllArgsConstructor() {
        AuthResponse response = new AuthResponse(userResponse, "token", "Bearer", 3600L);
        assertEquals(userResponse, response.getUser());
        assertEquals("token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void testEquals() {
        AuthResponse response1 = new AuthResponse(userResponse, "token", "Bearer", 3600L);
        AuthResponse response2 = new AuthResponse(userResponse, "token", "Bearer", 3600L);
        
        assertEquals(response1, response2);
    }

    @Test
    void testHashCode() {
        AuthResponse response1 = new AuthResponse(userResponse, "token", "Bearer", 3600L);
        AuthResponse response2 = new AuthResponse(userResponse, "token", "Bearer", 3600L);
        
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        String toString = authResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AuthResponse"));
    }
}

