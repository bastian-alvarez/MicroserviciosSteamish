package com.gamestore.gamecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.gamecatalog.dto.CreateGameRequest;
import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.dto.UpdateGameRequest;
import com.gamestore.gamecatalog.service.FileStorageService;
import com.gamestore.gamecatalog.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminGameController.class)
class AdminGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    private GameResponse gameResponse;

    @BeforeEach
    void setUp() {
        gameResponse = new GameResponse();
        gameResponse.setId(1L);
        gameResponse.setNombre("Test Game");
        gameResponse.setDescripcion("Test Description");
        gameResponse.setPrecio(29.99);
        gameResponse.setStock(10);
    }

    @Test
    void testCreateGame_Success() throws Exception {
        CreateGameRequest request = new CreateGameRequest();
        request.setNombre("New Game");
        request.setPrecio(39.99);
        request.setStock(10);
        request.setCategoriaId(1L);
        request.setGeneroId(1L);
        request.setDesarrollador("Test Developer");
        request.setFechaLanzamiento("2024");

        when(gameService.createGame(any(CreateGameRequest.class))).thenReturn(gameResponse);

        mockMvc.perform(post("/api/admin/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetGameById_Success() throws Exception {
        when(gameService.getGameById(1L)).thenReturn(gameResponse);

        mockMvc.perform(get("/api/admin/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetGameById_NotFound() throws Exception {
        when(gameService.getGameById(999L))
                .thenThrow(new RuntimeException("Juego no encontrado"));

        mockMvc.perform(get("/api/admin/games/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateGame_Success() throws Exception {
        UpdateGameRequest request = new UpdateGameRequest();
        request.setNombre("Updated Game");

        when(gameService.updateGame(eq(1L), any(UpdateGameRequest.class)))
                .thenReturn(gameResponse);

        mockMvc.perform(put("/api/admin/games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteGame_Success() throws Exception {
        doNothing().when(gameService).deleteGame(1L);

        mockMvc.perform(delete("/api/admin/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testUpdateStock_Success() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        request.put("stock", 20);

        when(gameService.updateStock(eq(1L), eq(20))).thenReturn(gameResponse);

        mockMvc.perform(put("/api/admin/games/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDecreaseStock_Success() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        request.put("quantity", 5);

        when(gameService.decreaseStock(eq(1L), eq(5))).thenReturn(gameResponse);

        mockMvc.perform(post("/api/admin/games/1/decrease-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadGameImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(gameService.getGameById(1L)).thenReturn(gameResponse);
        when(fileStorageService.storeGameImage(any(), eq(1L)))
                .thenReturn("http://example.com/game.jpg");
        
        UpdateGameRequest updateRequest = new UpdateGameRequest();
        updateRequest.setImagenUrl("http://example.com/game.jpg");
        
        gameResponse.setImagenUrl("http://example.com/game.jpg");
        when(gameService.updateGame(eq(1L), any(UpdateGameRequest.class)))
                .thenReturn(gameResponse);

        mockMvc.perform(multipart("/api/admin/games/1/image/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUploadGameImage_GameNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(gameService.getGameById(999L))
                .thenThrow(new RuntimeException("Juego no encontrado"));

        mockMvc.perform(multipart("/api/admin/games/999/image/upload")
                .file(file))
                .andExpect(status().isNotFound());
    }
}

