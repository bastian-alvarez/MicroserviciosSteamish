package com.gamestore.auth.service;

import com.gamestore.auth.dto.*;
import com.gamestore.auth.entity.Admin;
import com.gamestore.auth.entity.User;
import com.gamestore.auth.repository.AdminRepository;
import com.gamestore.auth.repository.UserRepository;
import com.gamestore.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());
        user.setIsBlocked(false);
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), false);
        
        UserResponse userResponse = mapToUserResponse(user);
        return new AuthResponse(userResponse, token, "Bearer", jwtUtil.getExpirationTime());
    }
    
    public AuthResponse login(LoginRequest request) {
        // Intentar login como admin primero
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                String token = jwtUtil.generateToken(admin.getEmail(), admin.getId(), true);
                UserResponse userResponse = new UserResponse(
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPhone(),
                    admin.getProfilePhotoUri(),
                    false,
                    ""
                );
                return new AuthResponse(userResponse, token, "Bearer", jwtUtil.getExpirationTime());
            }
        }
        
        // Intentar login como usuario
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getIsBlocked()) {
                throw new RuntimeException("Tu cuenta ha sido bloqueada. Contacta al administrador.");
            }
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail(), user.getId(), false);
                UserResponse userResponse = mapToUserResponse(user);
                return new AuthResponse(userResponse, token, "Bearer", jwtUtil.getExpirationTime());
            }
        }
        
        throw new RuntimeException("Credenciales inválidas");
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse updateUser(Long userId, com.gamestore.auth.dto.UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getIsBlocked() != null) {
            user.setIsBlocked(request.getIsBlocked());
        }
        
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        userRepository.delete(user);
    }
    
    @Transactional
    public UserResponse blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setIsBlocked(true);
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setIsBlocked(false);
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getProfilePhotoUri(),
            user.getIsBlocked(),
            user.getGender()
        );
    }
}

