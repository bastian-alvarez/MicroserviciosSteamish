package com.gamestore.gamecatalog.service;

import com.gamestore.gamecatalog.dto.GameResponse;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private GameService gameService;

    private Game testGame;

    @BeforeEach
    void setUp() {
        testGame = new Game();
        testGame.setId(1L);
        testGame.setNombre("Test Game");
        testGame.setDescripcion("Test Description");
        testGame.setPrecio(29.99);
        testGame.setStock(10);
        testGame.setActivo(true);
        testGame.setCategoriaId(1L);
        testGame.setGeneroId(1L);
    }

    @Test
    void testGetAllGames() {
        when(gameRepository.findByActivoTrue()).thenReturn(Arrays.asList(testGame));

        List<GameResponse> result = gameService.getAllGames();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findByActivoTrue();
    }

    @Test
    void testGetGameById_Success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        GameResponse response = gameService.getGameById(1L);

        assertNotNull(response);
        assertEquals(testGame.getId(), response.getId());
        assertEquals(testGame.getNombre(), response.getNombre());
    }

    @Test
    void testGetGameById_NotFound() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gameService.getGameById(999L));
    }

    @Test
    void testGetGamesByCategory() {
        when(gameRepository.findByCategoriaId(1L)).thenReturn(Arrays.asList(testGame));

        List<GameResponse> result = gameService.getGamesByCategory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findByCategoriaId(1L);
    }

    @Test
    void testGetGamesWithDiscount() {
        testGame.setDescuento(10);
        when(gameRepository.findByDescuentoGreaterThan(0)).thenReturn(Arrays.asList(testGame));

        List<GameResponse> result = gameService.getGamesWithDiscount();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findByDescuentoGreaterThan(0);
    }

    @Test
    void testSearchGames() {
        when(gameRepository.searchByName("test")).thenReturn(Arrays.asList(testGame));

        List<GameResponse> result = gameService.searchGames("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).searchByName("test");
    }

    @Test
    void testUpdateStock() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        GameResponse response = gameService.updateStock(1L, 20);

        assertNotNull(response);
        verify(gameRepository).findById(1L);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void testDecreaseStock_Success() {
        testGame.setStock(10);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        GameResponse response = gameService.decreaseStock(1L, 5);

        assertNotNull(response);
        verify(gameRepository).findById(1L);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void testDecreaseStock_InsufficientStock() {
        testGame.setStock(3);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        assertThrows(RuntimeException.class, () -> gameService.decreaseStock(1L, 5));
        verify(gameRepository, never()).save(any(Game.class));
    }
}

