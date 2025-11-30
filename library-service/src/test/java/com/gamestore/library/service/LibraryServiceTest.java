package com.gamestore.library.service;

import com.gamestore.library.dto.AddToLibraryRequest;
import com.gamestore.library.dto.LibraryItemResponse;
import com.gamestore.library.entity.LibraryItem;
import com.gamestore.library.repository.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private LibraryService libraryService;

    private AddToLibraryRequest addRequest;
    private LibraryItem testItem;

    @BeforeEach
    void setUp() {
        addRequest = new AddToLibraryRequest();
        addRequest.setUserId(1L);
        addRequest.setJuegoId("123");
        addRequest.setName("Test Game");
        addRequest.setPrice(29.99);
        addRequest.setStatus("Disponible");
        addRequest.setGenre("Acción");

        testItem = new LibraryItem();
        testItem.setId(1L);
        testItem.setUserId(1L);
        testItem.setJuegoId("123");
        testItem.setName("Test Game");
        testItem.setPrice(29.99);
        testItem.setStatus("Disponible");
        testItem.setGenre("Acción");
        testItem.setDateAdded("2025-11-20 10:00:00");
    }

    @Test
    void testAddToLibrary_Success() {
        when(libraryRepository.existsByUserIdAndJuegoId(1L, "123")).thenReturn(false);
        when(libraryRepository.save(any(LibraryItem.class))).thenReturn(testItem);

        LibraryItemResponse response = libraryService.addToLibrary(addRequest);

        assertNotNull(response);
        assertEquals(testItem.getId(), response.getId());
        assertEquals(testItem.getUserId(), response.getUserId());
        assertEquals(testItem.getJuegoId(), response.getJuegoId());
        verify(libraryRepository).existsByUserIdAndJuegoId(1L, "123");
        verify(libraryRepository).save(any(LibraryItem.class));
    }

    @Test
    void testAddToLibrary_GameAlreadyExists() {
        when(libraryRepository.existsByUserIdAndJuegoId(1L, "123")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> libraryService.addToLibrary(addRequest));
        
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, exception.getStatusCode());
        verify(libraryRepository, never()).save(any(LibraryItem.class));
    }

    @Test
    void testGetUserLibrary() {
        when(libraryRepository.findByUserId(1L)).thenReturn(Arrays.asList(testItem));

        List<LibraryItemResponse> result = libraryService.getUserLibrary(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItem.getId(), result.get(0).getId());
        verify(libraryRepository).findByUserId(1L);
    }

    @Test
    void testUserOwnsGame_True() {
        when(libraryRepository.existsByUserIdAndJuegoId(1L, "123")).thenReturn(true);

        boolean owns = libraryService.userOwnsGame(1L, "123");

        assertTrue(owns);
        verify(libraryRepository).existsByUserIdAndJuegoId(1L, "123");
    }

    @Test
    void testUserOwnsGame_False() {
        when(libraryRepository.existsByUserIdAndJuegoId(1L, "123")).thenReturn(false);

        boolean owns = libraryService.userOwnsGame(1L, "123");

        assertFalse(owns);
        verify(libraryRepository).existsByUserIdAndJuegoId(1L, "123");
    }

    @Test
    void testRemoveFromLibrary() {
        when(libraryRepository.findByUserIdAndJuegoId(1L, "123")).thenReturn(Optional.of(testItem));
        doNothing().when(libraryRepository).delete(testItem);

        libraryService.removeFromLibrary(1L, "123");

        verify(libraryRepository).findByUserIdAndJuegoId(1L, "123");
        verify(libraryRepository).delete(testItem);
    }

    @Test
    void testRemoveFromLibrary_NotFound() {
        when(libraryRepository.findByUserIdAndJuegoId(1L, "123")).thenReturn(Optional.empty());

        libraryService.removeFromLibrary(1L, "123");

        verify(libraryRepository).findByUserIdAndJuegoId(1L, "123");
        verify(libraryRepository, never()).delete(any());
    }
}

