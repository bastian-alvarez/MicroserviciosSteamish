package com.gamestore.gamecatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamestore.gamecatalog.dto.CreateRatingRequest;
import com.gamestore.gamecatalog.dto.RatingResponse;
import com.gamestore.gamecatalog.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private RatingResponse ratingResponse;

    @BeforeEach
    void setUp() {
        ratingResponse = new RatingResponse();
        ratingResponse.setId(1L);
        ratingResponse.setJuegoId(1L);
        ratingResponse.setUsuarioId(1L);
        ratingResponse.setCalificacion(5);
    }

    @Test
    void testCreateOrUpdateRating_Success() throws Exception {
        CreateRatingRequest request = new CreateRatingRequest();
        request.setJuegoId(1L);
        request.setUsuarioId(1L);
        request.setCalificacion(5);

        when(ratingService.createOrUpdateRating(any(CreateRatingRequest.class), eq(1L)))
                .thenReturn(ratingResponse);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.calificacion").value(5));
    }

    @Test
    void testGetUserRating_Success() throws Exception {
        when(ratingService.getRatingByUserAndGame(1L, 1L)).thenReturn(ratingResponse);

        mockMvc.perform(get("/api/ratings/game/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetGameRating_Success() throws Exception {
        when(ratingService.getAverageRating(1L)).thenReturn(4.5);
        when(ratingService.getRatingCount(1L)).thenReturn(10L);

        mockMvc.perform(get("/api/ratings/game/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.juegoId").value(1L))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.ratingCount").value(10L));
    }
}

