package com.gamestore.auth.repository;

import com.gamestore.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+56912345678");
        testUser.setPassword("hashedPassword");
        testUser.setIsBlocked(false);
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(testUser);
        
        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test User", savedUser.getName());
    }

    @Test
    void testFindByEmail_Success() {
        userRepository.save(testUser);
        
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        userRepository.save(testUser);
        
        boolean exists = userRepository.existsByEmail("test@example.com");
        
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        assertFalse(exists);
    }

    @Test
    void testFindById_Success() {
        User savedUser = userRepository.save(testUser);
        
        Optional<User> found = userRepository.findById(savedUser.getId());
        
        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getId());
    }

    @Test
    void testDeleteUser() {
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();
        
        userRepository.delete(savedUser);
        
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }
}

