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

