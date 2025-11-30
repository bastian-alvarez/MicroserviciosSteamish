package com.gamestore.gamecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.gamecatalog.dto.CreateGameRequest;
import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.dto.UpdateGameRequest;
import com.gamestore.gamecatalog.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

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
    void testGetAllGames_Success() throws Exception {
        List<GameResponse> games = Arrays.asList(gameResponse);
        when(gameService.getAllGames()).thenReturn(games);

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.gameResponseList").exists());
    }

    @Test
    void testGetAllGames_WithCategoryFilter() throws Exception {
        List<GameResponse> games = Arrays.asList(gameResponse);
        when(gameService.getGamesByCategory(1L)).thenReturn(games);

        mockMvc.perform(get("/api/games")
                .param("categoria", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllGames_WithSearchFilter() throws Exception {
        List<GameResponse> games = Arrays.asList(gameResponse);
        when(gameService.searchGames("test")).thenReturn(games);

        mockMvc.perform(get("/api/games")
                .param("search", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllGames_WithDiscountFilter() throws Exception {
        List<GameResponse> games = Arrays.asList(gameResponse);
        when(gameService.getGamesWithDiscount()).thenReturn(games);

        mockMvc.perform(get("/api/games")
                .param("descuento", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetGameById_Success() throws Exception {
        when(gameService.getGameById(1L)).thenReturn(gameResponse);

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Test Game"));
    }

    @Test
    void testGetGameById_NotFound() throws Exception {
        when(gameService.getGameById(999L))
                .thenThrow(new RuntimeException("Juego no encontrado"));

        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStock_Success() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        request.put("stock", 20);

        when(gameService.updateStock(eq(1L), eq(20))).thenReturn(gameResponse);

        mockMvc.perform(put("/api/games/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDecreaseStockInternal_Success() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        request.put("quantity", 5);

        when(gameService.decreaseStock(eq(1L), eq(5))).thenReturn(gameResponse);

        mockMvc.perform(post("/api/games/internal/1/decrease-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
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

        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateGame_Success() throws Exception {
        UpdateGameRequest request = new UpdateGameRequest();
        request.setNombre("Updated Game");

        when(gameService.updateGame(eq(1L), any(UpdateGameRequest.class)))
                .thenReturn(gameResponse);

        mockMvc.perform(put("/api/games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteGame_Success() throws Exception {
        doNothing().when(gameService).deleteGame(1L);

        mockMvc.perform(delete("/api/games/1"))
                .andExpect(status().isOk());
    }
}

