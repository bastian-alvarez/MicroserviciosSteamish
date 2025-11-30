package com.gamestore.library.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryItemResponseTest {

    private LibraryItemResponse libraryItemResponse;

    @BeforeEach
    void setUp() {
        libraryItemResponse = new LibraryItemResponse();
        libraryItemResponse.setId(1L);
        libraryItemResponse.setUserId(1L);
        libraryItemResponse.setJuegoId("1");
        libraryItemResponse.setName("Test Game");
        libraryItemResponse.setPrice(29.99);
        libraryItemResponse.setDateAdded("2024-01-01");
        libraryItemResponse.setStatus("ACTIVE");
        libraryItemResponse.setGenre("Action");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, libraryItemResponse.getId());
        assertEquals(1L, libraryItemResponse.getUserId());
        assertEquals("1", libraryItemResponse.getJuegoId());
        assertEquals("Test Game", libraryItemResponse.getName());
        assertEquals(29.99, libraryItemResponse.getPrice());
        assertEquals("2024-01-01", libraryItemResponse.getDateAdded());
        assertEquals("ACTIVE", libraryItemResponse.getStatus());
        assertEquals("Action", libraryItemResponse.getGenre());
    }

    @Test
    void testNoArgsConstructor() {
        LibraryItemResponse response = new LibraryItemResponse();
        assertNull(response.getId());
        assertNull(response.getUserId());
        assertNull(response.getJuegoId());
    }

    @Test
    void testAllArgsConstructor() {
        LibraryItemResponse response = new LibraryItemResponse(
            1L, 1L, "1", "Game", 29.99, 
            "2024-01-01", "ACTIVE", "Action"
        );
        assertEquals(1L, response.getId());
        assertEquals("Game", response.getName());
        assertEquals(29.99, response.getPrice());
    }

    @Test
    void testEquals() {
        LibraryItemResponse response1 = new LibraryItemResponse(
            1L, 1L, "1", "Game", 29.99, 
            "2024-01-01", "ACTIVE", "Action"
        );
        LibraryItemResponse response2 = new LibraryItemResponse(
            1L, 1L, "1", "Game", 29.99, 
            "2024-01-01", "ACTIVE", "Action"
        );
        
        assertEquals(response1, response2);
    }

    @Test
    void testHashCode() {
        LibraryItemResponse response1 = new LibraryItemResponse(
            1L, 1L, "1", "Game", 29.99, 
            "2024-01-01", "ACTIVE", "Action"
        );
        LibraryItemResponse response2 = new LibraryItemResponse(
            1L, 1L, "1", "Game", 29.99, 
            "2024-01-01", "ACTIVE", "Action"
        );
        
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        String toString = libraryItemResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("LibraryItemResponse"));
    }
}

