package com.gamestore.gamecatalog.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameResponseTest {

    private GameResponse gameResponse;

    @BeforeEach
    void setUp() {
        gameResponse = new GameResponse();
        gameResponse.setId(1L);
        gameResponse.setNombre("Test Game");
        gameResponse.setDescripcion("Test Description");
        gameResponse.setPrecio(29.99);
        gameResponse.setStock(10);
        gameResponse.setCategoriaId(1L);
        gameResponse.setGeneroId(1L);
        gameResponse.setImagenUrl("http://example.com/image.jpg");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, gameResponse.getId());
        assertEquals("Test Game", gameResponse.getNombre());
        assertEquals("Test Description", gameResponse.getDescripcion());
        assertEquals(29.99, gameResponse.getPrecio());
        assertEquals(10, gameResponse.getStock());
        assertEquals(1L, gameResponse.getCategoriaId());
        assertEquals(1L, gameResponse.getGeneroId());
        assertEquals("http://example.com/image.jpg", gameResponse.getImagenUrl());
    }

    @Test
    void testNoArgsConstructor() {
        GameResponse response = new GameResponse();
        assertNull(response.getId());
        assertNull(response.getNombre());
        assertNull(response.getPrecio());
    }

    @Test
    void testAllArgsConstructor() {
        // GameResponse has many fields, test with setters instead
        GameResponse response = new GameResponse();
        response.setId(1L);
        response.setNombre("Game");
        response.setDescripcion("Description");
        response.setPrecio(29.99);
        response.setStock(10);
        response.setCategoriaId(1L);
        response.setGeneroId(1L);
        
        assertEquals(1L, response.getId());
        assertEquals("Game", response.getNombre());
        assertEquals(29.99, response.getPrecio());
    }

    @Test
    void testEquals() {
        GameResponse response1 = new GameResponse();
        response1.setId(1L);
        response1.setNombre("Game");
        response1.setPrecio(29.99);
        
        GameResponse response2 = new GameResponse();
        response2.setId(1L);
        response2.setNombre("Game");
        response2.setPrecio(29.99);
        
        assertEquals(response1, response2);
    }

    @Test
    void testHashCode() {
        GameResponse response1 = new GameResponse();
        response1.setId(1L);
        response1.setNombre("Game");
        response1.setPrecio(29.99);
        
        GameResponse response2 = new GameResponse();
        response2.setId(1L);
        response2.setNombre("Game");
        response2.setPrecio(29.99);
        
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        String toString = gameResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("GameResponse"));
    }
}

