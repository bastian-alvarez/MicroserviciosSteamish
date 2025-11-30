package com.gamestore.gamecatalog.service;

import com.gamestore.gamecatalog.dto.CreateRatingRequest;
import com.gamestore.gamecatalog.dto.RatingResponse;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.entity.Rating;
import com.gamestore.gamecatalog.repository.GameRepository;
import com.gamestore.gamecatalog.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private RatingService ratingService;

    private CreateRatingRequest createRequest;
    private Game testGame;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        createRequest = new CreateRatingRequest();
        createRequest.setJuegoId(1L);
        createRequest.setCalificacion(5);
        createRequest.setUsuarioId(2L);

        testGame = new Game();
        testGame.setId(1L);
        testGame.setNombre("Test Game");

        testRating = new Rating();
        testRating.setId(1L);
        testRating.setJuegoId(1L);
        testRating.setUsuarioId(2L);
        testRating.setCalificacion(5);
    }

    @Test
    void testCreateOrUpdateRating_NewRating() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(ratingRepository.findByJuegoIdAndUsuarioId(1L, 2L)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);
        when(ratingRepository.getAverageRatingByJuegoId(1L)).thenReturn(5.0);
        when(ratingRepository.getRatingCountByJuegoId(1L)).thenReturn(1L);

        RatingResponse response = ratingService.createOrUpdateRating(createRequest, 2L);

        assertNotNull(response);
        assertEquals(testRating.getCalificacion(), response.getCalificacion());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testCreateOrUpdateRating_UpdateExisting() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(ratingRepository.findByJuegoIdAndUsuarioId(1L, 2L)).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);
        when(ratingRepository.getAverageRatingByJuegoId(1L)).thenReturn(4.5);
        when(ratingRepository.getRatingCountByJuegoId(1L)).thenReturn(1L);

        createRequest.setCalificacion(4);
        RatingResponse response = ratingService.createOrUpdateRating(createRequest, 2L);

        assertNotNull(response);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testCreateOrUpdateRating_GameNotFound() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        createRequest.setJuegoId(999L);
        assertThrows(RuntimeException.class, () -> ratingService.createOrUpdateRating(createRequest, 2L));
    }

    @Test
    void testGetRatingByUserAndGame_Success() {
        when(ratingRepository.findByJuegoIdAndUsuarioId(1L, 2L)).thenReturn(Optional.of(testRating));

        RatingResponse response = ratingService.getRatingByUserAndGame(1L, 2L);

        assertNotNull(response);
        assertEquals(testRating.getCalificacion(), response.getCalificacion());
    }

    @Test
    void testGetRatingByUserAndGame_NotFound() {
        when(ratingRepository.findByJuegoIdAndUsuarioId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ratingService.getRatingByUserAndGame(1L, 2L));
    }

    @Test
    void testGetAverageRating() {
        when(ratingRepository.getAverageRatingByJuegoId(1L)).thenReturn(4.5);

        Double average = ratingService.getAverageRating(1L);

        assertEquals(4.5, average);
    }

    @Test
    void testGetAverageRating_NoRatings() {
        when(ratingRepository.getAverageRatingByJuegoId(1L)).thenReturn(null);

        Double average = ratingService.getAverageRating(1L);

        assertEquals(0.0, average);
    }

    @Test
    void testGetRatingCount() {
        when(ratingRepository.getRatingCountByJuegoId(1L)).thenReturn(10L);

        Long count = ratingService.getRatingCount(1L);

        assertEquals(10L, count);
    }
}

