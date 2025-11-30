package com.gamestore.auth.service;

import com.gamestore.auth.dto.*;
import com.gamestore.auth.entity.Admin;
import com.gamestore.auth.entity.User;
import com.gamestore.auth.repository.AdminRepository;
import com.gamestore.auth.repository.UserRepository;
import com.gamestore.auth.util.JwtUtil;
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
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("+56912345678");
        registerRequest.setGender("M");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+56912345678");
        // Will be set with real BCrypt hash in individual tests
        testUser.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10)));
        testUser.setIsBlocked(false);
        testUser.setGender("M");

        testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setName("Admin");
        testAdmin.setEmail("admin@example.com");
        // Will be set with real BCrypt hash in individual tests
        testAdmin.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10)));
        testAdmin.setRole(Admin.AdminRole.SUPER_ADMIN);
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(anyString(), anyLong(), anyBoolean(), anyBoolean())).thenReturn("test-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(anyString(), anyLong(), eq(false), eq(false));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_UserSuccess() {
        // Use a real BCrypt hash for "password123"
        String realHash = org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10));
        testUser.setPassword(realHash);
        
        when(adminRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString(), anyLong(), anyBoolean(), anyBoolean())).thenReturn("test-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getTokenType());
        verify(jwtUtil).generateToken(anyString(), anyLong(), eq(false), eq(false));
    }

    @Test
    void testLogin_AdminSuccess() {
        // Use a real BCrypt hash for "password123"
        String realHash = org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10));
        testAdmin.setPassword(realHash);
        
        loginRequest.setEmail("admin@example.com");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testAdmin));
        when(jwtUtil.generateToken(anyString(), anyLong(), anyBoolean(), anyBoolean())).thenReturn("test-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        verify(jwtUtil).generateToken(anyString(), anyLong(), eq(true), eq(false));
    }

    @Test
    void testLogin_UserBlocked() {
        testUser.setIsBlocked(true);
        when(adminRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("BLOQUEADA", exception.getMessage());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(adminRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("CREDENCIALES_INVALIDAS", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(testUser));

        var users = authService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = authService.getUserById(1L);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getEmail(), response.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.getUserById(999L));
    }

    @Test
    void testChangePassword_Success() {
        // Use a real BCrypt hash for "password123"
        String currentPasswordHash = org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10));
        testUser.setPassword(currentPasswordHash);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.changePassword(1L, "password123", "NewPassword123!");

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testChangePassword_CurrentPasswordIncorrect() {
        // Use a real BCrypt hash for "password123"
        String currentPasswordHash = org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10));
        testUser.setPassword(currentPasswordHash);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.changePassword(1L, "WrongPassword", "NewPassword123!"));
        
        assertEquals("CONTRASENA_ACTUAL_INCORRECTA", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_NewPasswordSameAsCurrent() {
        // Use a real BCrypt hash for "password123"
        String currentPasswordHash = org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt(10));
        testUser.setPassword(currentPasswordHash);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.changePassword(1L, "password123", "password123"));
        
        assertTrue(exception.getMessage().contains("debe ser diferente"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.changePassword(999L, "password123", "NewPassword123!"));
    }
}

