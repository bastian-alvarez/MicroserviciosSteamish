package com.gamestore.auth.controller;

import com.gamestore.auth.dto.ChangePasswordRequest;
import com.gamestore.auth.dto.UpdateProfilePhotoRequest;
import com.gamestore.auth.dto.UserResponse;
import com.gamestore.auth.service.AuthService;
import com.gamestore.auth.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Perfil de Usuario", description = "API para gestión del perfil del usuario - Sin autenticación")
public class UserProfileController {
    private final AuthService authService;
    private final FileStorageService fileStorageService;
    
    @Operation(
        summary = "Obtener perfil del usuario actual", 
        description = "Obtiene los datos del perfil del usuario actual, incluyendo información personal, " +
                      "foto de perfil y estado de la cuenta."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Perfil obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario no encontrado\"}"))
        )
    })
    @GetMapping("/me")
    public ResponseEntity<EntityModel<UserResponse>> getCurrentUser(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            UserResponse user = authService.getCurrentUser(userId);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            resource.add(linkTo(methodOn(UserProfileController.class).getCurrentUser(userId)).withSelfRel());
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Actualizar foto de perfil (URL)", 
        description = "Actualiza la foto de perfil del usuario proporcionando una URL. La URL debe ser accesible " +
                      "públicamente para que la imagen se pueda mostrar."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Foto de perfil actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido o URL inválida",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario no encontrado\"}"))
        )
    })
    @PutMapping("/me/photo")
    public ResponseEntity<EntityModel<UserResponse>> updateProfilePhoto(
            @Valid @RequestBody UpdateProfilePhotoRequest request, 
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            UserResponse user = authService.updateProfilePhoto(userId, request.getProfilePhotoUri());
            return ResponseEntity.ok(EntityModel.of(user));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Subir foto de perfil", 
        description = "Sube una imagen directamente como foto de perfil. Formatos aceptados: JPG, PNG, GIF. " +
                      "Tamaño máximo: 5MB. Si el usuario ya tiene una foto, la anterior será reemplazada."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Foto de perfil subida exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido, archivo inválido, formato no soportado o archivo demasiado grande",
            content = @Content(schema = @Schema(example = "{\"error\": \"El archivo es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al guardar el archivo",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al guardar el archivo\"}"))
        )
    })
    @PostMapping(value = "/me/photo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityModel<UserResponse>> uploadProfilePhoto(
            @Parameter(description = "Archivo de imagen a subir", required = true)
            @RequestParam("file") MultipartFile file, 
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            if (file == null || file.isEmpty()) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "El archivo es requerido");
            }
            UserResponse currentUser = authService.getCurrentUser(userId);
            String oldPhotoUrl = currentUser.getProfilePhotoUri();
            String photoUrl;
            try {
                photoUrl = fileStorageService.storeProfilePhoto(file, userId);
            } catch (IllegalArgumentException e) {
                // IllegalArgumentException indica un problema de validación (tipo, tamaño, etc.)
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
            } catch (IOException e) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo: " + e.getMessage());
            }
            UserResponse user = authService.updateProfilePhoto(userId, photoUrl);
            if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
                try {
                    String oldFilename = fileStorageService.extractFilenameFromUrl(oldPhotoUrl);
                    if (oldFilename != null) fileStorageService.deleteProfilePhoto(oldFilename);
                } catch (Exception ignored) {}
            }
            return ResponseEntity.ok(EntityModel.of(user));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Cambiar contraseña", 
        description = "Cambia la contraseña del usuario. Requiere la contraseña actual para validar la identidad " +
                      "y la nueva contraseña que debe tener al menos 6 caracteres."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Contraseña cambiada exitosamente",
            content = @Content(schema = @Schema(example = "{\"message\": \"Contraseña cambiada exitosamente\", \"userId\": 5}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: contraseña actual incorrecta, nueva contraseña muy corta, o campos faltantes",
            content = @Content(schema = @Schema(example = "{\"error\": \"La contraseña actual es incorrecta\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario no encontrado\"}"))
        )
    })
    @PutMapping("/{userId}/password")
    public ResponseEntity<java.util.Map<String, Object>> changePassword(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Contraseña cambiada exitosamente");
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            if (message != null && message.contains("CONTRASENA_ACTUAL_INCORRECTA")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, message != null ? message : "Error al cambiar la contraseña");
        }
    }
}
