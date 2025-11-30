package com.gamestore.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.library.dto.AddToLibraryRequest;
import com.gamestore.library.dto.LibraryItemResponse;
import com.gamestore.library.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @Autowired
    private ObjectMapper objectMapper;

    private LibraryItemResponse libraryItemResponse;

    @BeforeEach
    void setUp() {
        libraryItemResponse = new LibraryItemResponse();
        libraryItemResponse.setId(1L);
        libraryItemResponse.setUserId(1L);
        libraryItemResponse.setJuegoId("1");
        libraryItemResponse.setDateAdded("2024-01-01");
    }

    @Test
    void testAddToLibrary_Success() throws Exception {
        AddToLibraryRequest request = new AddToLibraryRequest();
        request.setUserId(1L);
        request.setJuegoId("1");
        request.setName("Test Game");
        request.setPrice(29.99);

        when(libraryService.addToLibrary(any(AddToLibraryRequest.class)))
                .thenReturn(libraryItemResponse);

        mockMvc.perform(post("/api/library")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void testAddToLibrary_AlreadyExists() throws Exception {
        AddToLibraryRequest request = new AddToLibraryRequest();
        request.setUserId(1L);
        request.setJuegoId("1");
        request.setName("Test Game");
        request.setPrice(29.99);

        when(libraryService.addToLibrary(any(AddToLibraryRequest.class)))
                .thenThrow(new RuntimeException("El juego ya está en la biblioteca"));

        mockMvc.perform(post("/api/library")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetUserLibrary_Success() throws Exception {
        List<LibraryItemResponse> library = Arrays.asList(libraryItemResponse);
        when(libraryService.getUserLibrary(1L)).thenReturn(library);

        mockMvc.perform(get("/api/library/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.libraryItemResponseList").exists())
                .andExpect(jsonPath("$._embedded.libraryItemResponseList[0].id").value(1L));
    }

    @Test
    void testUserOwnsGame_True() throws Exception {
        when(libraryService.userOwnsGame(1L, "1")).thenReturn(true);

        mockMvc.perform(get("/api/library/user/1/game/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owns").value(true));
    }

    @Test
    void testUserOwnsGame_False() throws Exception {
        when(libraryService.userOwnsGame(1L, "2")).thenReturn(false);

        mockMvc.perform(get("/api/library/user/1/game/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owns").value(false));
    }

    @Test
    void testRemoveFromLibrary_Success() throws Exception {
        doNothing().when(libraryService).removeFromLibrary(1L, "1");

        mockMvc.perform(delete("/api/library/user/1/game/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testAddToLibraryInternal_Success() throws Exception {
        AddToLibraryRequest request = new AddToLibraryRequest();
        request.setUserId(1L);
        request.setJuegoId("1");
        request.setName("Test Game");
        request.setPrice(29.99);

        when(libraryService.addToLibrary(any(AddToLibraryRequest.class)))
                .thenReturn(libraryItemResponse);

        mockMvc.perform(post("/api/library/internal/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}

