package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Category;
import com.gamestore.gamecatalog.entity.Game;
import com.gamestore.gamecatalog.entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private GenreRepository genreRepository;

    private Game testGame;
    private Category testCategory;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        // Create category and genre first
        testCategory = new Category();
        testCategory.setNombre("Test Category");
        testCategory.setDescripcion("Test Category Description");
        testCategory = categoryRepository.save(testCategory);
        
        testGenre = new Genre();
        testGenre.setNombre("Test Genre");
        testGenre.setDescripcion("Test Genre Description");
        testGenre = genreRepository.save(testGenre);
        
        testGame = new Game();
        testGame.setNombre("Test Game");
        testGame.setDescripcion("Test Description");
        testGame.setPrecio(29.99);
        testGame.setStock(10);
        testGame.setCategoriaId(testCategory.getId());
        testGame.setGeneroId(testGenre.getId());
        testGame.setActivo(true);
        testGame.setDescuento(0);
    }

    @Test
    void testSaveGame() {
        Game saved = gameRepository.save(testGame);
        
        assertNotNull(saved.getId());
        assertEquals("Test Game", saved.getNombre());
        assertEquals(29.99, saved.getPrecio());
    }

    @Test
    void testFindByActivoTrue() {
        Game activeGame = new Game();
        activeGame.setNombre("Active Game");
        activeGame.setDescripcion("Description");
        activeGame.setPrecio(19.99);
        activeGame.setStock(5);
        activeGame.setCategoriaId(testCategory.getId());
        activeGame.setGeneroId(testGenre.getId());
        activeGame.setActivo(true);
        activeGame.setDescuento(0);
        
        Game inactiveGame = new Game();
        inactiveGame.setNombre("Inactive Game");
        inactiveGame.setDescripcion("Description");
        inactiveGame.setPrecio(19.99);
        inactiveGame.setStock(5);
        inactiveGame.setCategoriaId(testCategory.getId());
        inactiveGame.setGeneroId(testGenre.getId());
        inactiveGame.setActivo(false);
        inactiveGame.setDescuento(0);
        
        gameRepository.save(activeGame);
        gameRepository.save(inactiveGame);
        
        List<Game> activeGames = gameRepository.findByActivoTrue();
        
        assertFalse(activeGames.isEmpty());
        assertTrue(activeGames.stream().allMatch(Game::getActivo));
    }

    @Test
    void testFindByCategoriaId() {
        gameRepository.save(testGame);
        
        List<Game> games = gameRepository.findByCategoriaId(testCategory.getId());
        
        assertFalse(games.isEmpty());
        assertTrue(games.stream().allMatch(g -> g.getCategoriaId().equals(testCategory.getId())));
    }

    @Test
    void testFindByGeneroId() {
        gameRepository.save(testGame);
        
        List<Game> games = gameRepository.findByGeneroId(testGenre.getId());
        
        assertFalse(games.isEmpty());
        assertTrue(games.stream().allMatch(g -> g.getGeneroId().equals(testGenre.getId())));
    }

    @Test
    void testFindByDescuentoGreaterThan() {
        Game discountedGame = new Game();
        discountedGame.setNombre("Discounted Game");
        discountedGame.setDescripcion("Description");
        discountedGame.setPrecio(29.99);
        discountedGame.setStock(10);
        discountedGame.setCategoriaId(testCategory.getId());
        discountedGame.setGeneroId(testGenre.getId());
        discountedGame.setActivo(true);
        discountedGame.setDescuento(20);
        
        gameRepository.save(testGame);
        gameRepository.save(discountedGame);
        
        List<Game> discountedGames = gameRepository.findByDescuentoGreaterThan(0);
        
        assertFalse(discountedGames.isEmpty());
        assertTrue(discountedGames.stream().allMatch(g -> g.getDescuento() > 0));
    }

    @Test
    void testSearchByName() {
        gameRepository.save(testGame);
        
        List<Game> games = gameRepository.searchByName("Test");
        
        assertFalse(games.isEmpty());
    }

    @Test
    void testFindById() {
        Game saved = gameRepository.save(testGame);
        
        Optional<Game> found = gameRepository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }
}

