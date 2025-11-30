package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    private Rating testRating;

    @BeforeEach
    void setUp() {
        testRating = new Rating();
        testRating.setJuegoId(1L);
        testRating.setUsuarioId(1L);
        testRating.setCalificacion(5);
    }

    @Test
    void testSaveRating() {
        Rating saved = ratingRepository.save(testRating);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getJuegoId());
        assertEquals(1L, saved.getUsuarioId());
        assertEquals(5, saved.getCalificacion());
    }

    @Test
    void testFindByJuegoIdAndUsuarioId_Success() {
        ratingRepository.save(testRating);
        
        Optional<Rating> found = ratingRepository.findByJuegoIdAndUsuarioId(1L, 1L);
        
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getJuegoId());
        assertEquals(1L, found.get().getUsuarioId());
    }

    @Test
    void testFindByJuegoIdAndUsuarioId_NotFound() {
        Optional<Rating> found = ratingRepository.findByJuegoIdAndUsuarioId(999L, 999L);
        
        assertFalse(found.isPresent());
    }

    @Test
    void testGetAverageRatingByJuegoId() {
        Rating rating1 = new Rating();
        rating1.setJuegoId(1L);
        rating1.setUsuarioId(1L);
        rating1.setCalificacion(5);
        
        Rating rating2 = new Rating();
        rating2.setJuegoId(1L);
        rating2.setUsuarioId(2L);
        rating2.setCalificacion(3);
        
        ratingRepository.save(rating1);
        ratingRepository.save(rating2);
        
        Double average = ratingRepository.getAverageRatingByJuegoId(1L);
        
        assertNotNull(average);
        assertEquals(4.0, average, 0.1);
    }

    @Test
    void testGetRatingCountByJuegoId() {
        Rating rating1 = new Rating();
        rating1.setJuegoId(1L);
        rating1.setUsuarioId(1L);
        rating1.setCalificacion(5);
        
        Rating rating2 = new Rating();
        rating2.setJuegoId(1L);
        rating2.setUsuarioId(2L);
        rating2.setCalificacion(4);
        
        ratingRepository.save(rating1);
        ratingRepository.save(rating2);
        
        Long count = ratingRepository.getRatingCountByJuegoId(1L);
        
        assertNotNull(count);
        assertTrue(count >= 2);
    }

    @Test
    void testExistsByJuegoIdAndUsuarioId_True() {
        ratingRepository.save(testRating);
        
        boolean exists = ratingRepository.existsByJuegoIdAndUsuarioId(1L, 1L);
        
        assertTrue(exists);
    }

    @Test
    void testExistsByJuegoIdAndUsuarioId_False() {
        boolean exists = ratingRepository.existsByJuegoIdAndUsuarioId(999L, 999L);
        
        assertFalse(exists);
    }
}

