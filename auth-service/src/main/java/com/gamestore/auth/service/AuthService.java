package com.gamestore.auth.service;

import com.gamestore.auth.dto.*;
import com.gamestore.auth.entity.Admin;
import com.gamestore.auth.entity.User;
import com.gamestore.auth.repository.AdminRepository;
import com.gamestore.auth.repository.UserRepository;
import com.gamestore.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
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
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(10)));
        user.setGender(request.getGender());
        user.setIsBlocked(false);
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), false, false);
        
        UserResponse userResponse = mapToUserResponse(user);
        return new AuthResponse(userResponse, token, "Bearer", jwtUtil.getExpirationTime());
    }
    
    public AuthResponse login(LoginRequest request) {
        // Intentar login como admin primero
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (BCrypt.checkpw(request.getPassword(), admin.getPassword())) {
                // Determinar si es admin o moderador
                boolean isAdmin = admin.getRole() == Admin.AdminRole.SUPER_ADMIN || 
                                 admin.getRole() == Admin.AdminRole.GAME_MANAGER;
                boolean isModerator = admin.getRole() == Admin.AdminRole.MODERATOR;
                
                String token = jwtUtil.generateToken(admin.getEmail(), admin.getId(), isAdmin, isModerator);
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
                throw new RuntimeException("BLOQUEADA");
            }
            if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail(), user.getId(), false, false);
                UserResponse userResponse = mapToUserResponse(user);
                return new AuthResponse(userResponse, token, "Bearer", jwtUtil.getExpirationTime());
            }
        }
        
        throw new RuntimeException("CREDENCIALES_INVALIDAS");
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
    
    @Transactional
    public UserResponse updateProfilePhoto(Long userId, String profilePhotoUri) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setProfilePhotoUri(profilePhotoUri);
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que la contraseña actual sea correcta
        if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
            throw new RuntimeException("CONTRASENA_ACTUAL_INCORRECTA");
        }
        
        // Validar que la nueva contraseña sea diferente
        if (BCrypt.checkpw(newPassword, user.getPassword())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }
        
        // Hashear y guardar la nueva contraseña
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(10)));
        userRepository.save(user);
    }
    
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToUserResponse(user);
    }
    
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
    
    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::mapToAdminResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public AdminResponse getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        return mapToAdminResponse(admin);
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
    
    private AdminResponse mapToAdminResponse(Admin admin) {
        return new AdminResponse(
            admin.getId(),
            admin.getName(),
            admin.getEmail(),
            admin.getPhone(),
            admin.getRole().name(),
            admin.getProfilePhotoUri(),
            admin.getCreatedAt(),
            admin.getUpdatedAt()
        );
    }
}
