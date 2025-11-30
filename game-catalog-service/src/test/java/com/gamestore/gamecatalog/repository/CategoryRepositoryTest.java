package com.gamestore.gamecatalog.repository;

import com.gamestore.gamecatalog.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setNombre("Action");
    }

    @Test
    void testSaveCategory() {
        Category saved = categoryRepository.save(testCategory);
        
        assertNotNull(saved.getId());
        assertEquals("Action", saved.getNombre());
    }

    @Test
    void testFindByNombre_Success() {
        categoryRepository.save(testCategory);
        
        Optional<Category> found = categoryRepository.findByNombre("Action");
        
        assertTrue(found.isPresent());
        assertEquals("Action", found.get().getNombre());
    }

    @Test
    void testFindByNombre_NotFound() {
        Optional<Category> found = categoryRepository.findByNombre("Nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindById() {
        Category saved = categoryRepository.save(testCategory);
        
        Optional<Category> found = categoryRepository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }
}

