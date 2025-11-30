package com.gamestore.library.repository;

import com.gamestore.library.entity.LibraryItem;
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
class LibraryRepositoryTest {

    @Autowired
    private LibraryRepository libraryRepository;

    private LibraryItem testLibraryItem;

    @BeforeEach
    void setUp() {
        testLibraryItem = new LibraryItem();
        testLibraryItem.setUserId(1L);
        testLibraryItem.setJuegoId("1");
        testLibraryItem.setName("Test Game");
        testLibraryItem.setPrice(29.99);
        testLibraryItem.setDateAdded("2024-01-01");
    }

    @Test
    void testSaveLibraryItem() {
        LibraryItem saved = libraryRepository.save(testLibraryItem);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals("1", saved.getJuegoId());
    }

    @Test
    void testFindByUserId() {
        libraryRepository.save(testLibraryItem);
        
        List<LibraryItem> items = libraryRepository.findByUserId(1L);
        
        assertFalse(items.isEmpty());
        assertTrue(items.stream().allMatch(item -> item.getUserId().equals(1L)));
    }

    @Test
    void testFindByUserIdAndJuegoId_Success() {
        libraryRepository.save(testLibraryItem);
        
        Optional<LibraryItem> found = libraryRepository.findByUserIdAndJuegoId(1L, "1");
        
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getUserId());
        assertEquals("1", found.get().getJuegoId());
    }

    @Test
    void testFindByUserIdAndJuegoId_NotFound() {
        Optional<LibraryItem> found = libraryRepository.findByUserIdAndJuegoId(999L, "999");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUserIdAndJuegoId_True() {
        libraryRepository.save(testLibraryItem);
        
        boolean exists = libraryRepository.existsByUserIdAndJuegoId(1L, "1");
        
        assertTrue(exists);
    }

    @Test
    void testExistsByUserIdAndJuegoId_False() {
        boolean exists = libraryRepository.existsByUserIdAndJuegoId(999L, "999");
        
        assertFalse(exists);
    }
}

