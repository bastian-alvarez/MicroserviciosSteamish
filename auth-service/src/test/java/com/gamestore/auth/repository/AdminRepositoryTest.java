package com.gamestore.auth.repository;

import com.gamestore.auth.entity.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setName("Admin User");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPhone("+56912345678");
        testAdmin.setPassword("hashedPassword");
        testAdmin.setRole(Admin.AdminRole.SUPER_ADMIN);
    }

    @Test
    void testSaveAdmin() {
        Admin savedAdmin = adminRepository.save(testAdmin);
        
        assertNotNull(savedAdmin.getId());
        assertEquals("admin@example.com", savedAdmin.getEmail());
        assertEquals("Admin User", savedAdmin.getName());
    }

    @Test
    void testFindByEmail_Success() {
        adminRepository.save(testAdmin);
        
        Optional<Admin> found = adminRepository.findByEmail("admin@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("admin@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<Admin> found = adminRepository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        adminRepository.save(testAdmin);
        
        boolean exists = adminRepository.existsByEmail("admin@example.com");
        
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        boolean exists = adminRepository.existsByEmail("nonexistent@example.com");
        
        assertFalse(exists);
    }

    @Test
    void testFindById_Success() {
        Admin savedAdmin = adminRepository.save(testAdmin);
        
        Optional<Admin> found = adminRepository.findById(savedAdmin.getId());
        
        assertTrue(found.isPresent());
        assertEquals(savedAdmin.getId(), found.get().getId());
    }
}

