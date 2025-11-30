package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    private Genre testGenre;

    @BeforeEach
    void setUp() {
        testGenre = new Genre();
        testGenre.setNombre("Action");
    }

    @Test
    void testSaveGenre() {
        Genre saved = genreRepository.save(testGenre);
        
        assertNotNull(saved.getId());
        assertEquals("Action", saved.getNombre());
    }

    @Test
    void testFindByNombre_Success() {
        genreRepository.save(testGenre);
        
        Optional<Genre> found = genreRepository.findByNombre("Action");
        
        assertTrue(found.isPresent());
        assertEquals("Action", found.get().getNombre());
    }

    @Test
    void testFindByNombre_NotFound() {
        Optional<Genre> found = genreRepository.findByNombre("Nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindById() {
        Genre saved = genreRepository.save(testGenre);
        
        Optional<Genre> found = genreRepository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }
}

