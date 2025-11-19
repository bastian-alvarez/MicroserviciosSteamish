package com.gamestore.auth.controller;

import com.gamestore.auth.dto.UpdateProfilePhotoRequest;
import com.gamestore.auth.dto.UserResponse;
import com.gamestore.auth.service.AuthService;
import com.gamestore.auth.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Perfil de Usuario", description = "API para gestión del perfil del usuario autenticado")
public class UserProfileController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    private Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
    
    @Operation(summary = "Obtener perfil del usuario actual", description = "Obtiene los datos del perfil del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserResponse>> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            UserResponse user = authService.getCurrentUser(userId);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            
            resource.add(linkTo(methodOn(UserProfileController.class).getCurrentUser(request)).withSelfRel());
            resource.add(linkTo(methodOn(UserProfileController.class).updateProfilePhoto(new UpdateProfilePhotoRequest(), request)).withRel("update-photo"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Actualizar foto de perfil", description = "Actualiza la foto de perfil del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Foto de perfil actualizada exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("/me/photo")
    public ResponseEntity<EntityModel<UserResponse>> updateProfilePhoto(
            @Valid @RequestBody UpdateProfilePhotoRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = extractUserId(httpRequest);
            UserResponse user = authService.updateProfilePhoto(userId, request.getProfilePhotoUri());
            EntityModel<UserResponse> resource = EntityModel.of(user);
            
            resource.add(linkTo(methodOn(UserProfileController.class).updateProfilePhoto(request, httpRequest)).withSelfRel());
            resource.add(linkTo(methodOn(UserProfileController.class).getCurrentUser(httpRequest)).withRel("profile"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

