package com.gamestore.auth.controller;

import com.gamestore.auth.dto.AuthResponse;
import com.gamestore.auth.dto.LoginRequest;
import com.gamestore.auth.dto.RegisterRequest;
import com.gamestore.auth.service.AuthService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "API para registro, login de usuarios y administradores")
public class AuthController {
    private final AuthService authService;
    
    @Operation(
        summary = "Registrar nuevo usuario", 
        description = "Crea una nueva cuenta de usuario en el sistema. Valida que el email no esté en uso, " +
                      "encripta la contraseña y genera un token JWT para autenticación inmediata."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario registrado exitosamente. Retorna token JWT y datos del usuario.",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes, formato de email inválido, o contraseña muy corta",
            content = @Content(schema = @Schema(example = "{\"error\": \"El email debe ser válido\"}"))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto: el email ya está registrado en el sistema",
            content = @Content(schema = @Schema(example = "{\"error\": \"El email ya está registrado\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al registrar el usuario",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al registrar usuario\"}"))
        )
    })
    @PostMapping("/register")
    public ResponseEntity<EntityModel<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            EntityModel<AuthResponse> resource = EntityModel.of(response);
            
            // Agregar enlaces HATEOAS
            resource.add(linkTo(methodOn(AuthController.class).register(request)).withSelfRel());
            resource.add(linkTo(methodOn(AuthController.class).login(new LoginRequest())).withRel("login"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("ya está registrado") || e.getMessage().contains("ya está en uso")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Login de usuario", 
        description = "Autentica un usuario con email y contraseña. Valida las credenciales, verifica si la cuenta " +
                      "está bloqueada y genera un token JWT para autenticación en peticiones posteriores."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login exitoso. Retorna token JWT y datos del usuario.",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: email o contraseña faltantes o formato inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"El email es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Credenciales inválidas: email o contraseña incorrectos",
            content = @Content(schema = @Schema(example = "{\"error\": \"Credenciales inválidas\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Cuenta bloqueada: el usuario no puede iniciar sesión",
            content = @Content(schema = @Schema(example = "{\"error\": \"Tu cuenta ha sido bloqueada. Contacta al administrador.\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al procesar el login",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error en el login\"}"))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<EntityModel<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            EntityModel<AuthResponse> resource = EntityModel.of(response);
            
            // Agregar enlaces HATEOAS
            resource.add(linkTo(methodOn(AuthController.class).login(request)).withSelfRel());
            resource.add(linkTo(methodOn(AuthController.class).register(new RegisterRequest())).withRel("register"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            // Usar códigos específicos en lugar de verificar el contenido del mensaje
            // para evitar problemas de encoding
            if (message != null && message.equals("CREDENCIALES_INVALIDAS")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
            }
            if (message != null && message.equals("BLOQUEADA")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido bloqueada. Contacta al administrador.");
            }
            // Verificar también por contenido por compatibilidad
            if (message != null && (message.contains("Credenciales inválidas") || message.contains("CREDENCIALES_INVALIDAS"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
            }
            if (message != null && (message.contains("bloqueada") || message.contains("BLOQUEADA"))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido bloqueada. Contacta al administrador.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message != null ? message : "Error en el login");
        }
    }
    
    @Operation(
        summary = "Login de administrador", 
        description = "Autentica un administrador con email y contraseña. Similar al login de usuario pero específico " +
                      "para administradores. Valida credenciales y genera token JWT con permisos de administrador."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login exitoso. Retorna token JWT y datos del administrador.",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: email o contraseña faltantes o formato inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"El email es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Credenciales inválidas: email o contraseña incorrectos, o usuario no es administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"Credenciales inválidas\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Cuenta bloqueada: el administrador no puede iniciar sesión",
            content = @Content(schema = @Schema(example = "{\"error\": \"Tu cuenta ha sido bloqueada. Contacta al administrador.\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al procesar el login",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error en el login de administrador\"}"))
        )
    })
    @PostMapping("/admin/login")
    public ResponseEntity<EntityModel<AuthResponse>> adminLogin(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            EntityModel<AuthResponse> resource = EntityModel.of(response);
            
            // Agregar enlaces HATEOAS
            resource.add(linkTo(methodOn(AuthController.class).adminLogin(request)).withSelfRel());
            resource.add(linkTo(methodOn(AuthController.class).login(new LoginRequest())).withRel("user-login"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            // Usar códigos específicos en lugar de verificar el contenido del mensaje
            // para evitar problemas de encoding
            if (message != null && message.equals("CREDENCIALES_INVALIDAS")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
            }
            if (message != null && message.equals("BLOQUEADA")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido bloqueada. Contacta al administrador.");
            }
            // Verificar también por contenido por compatibilidad
            if (message != null && (message.contains("Credenciales inválidas") || message.contains("CREDENCIALES_INVALIDAS"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
            }
            if (message != null && (message.contains("bloqueada") || message.contains("BLOQUEADA"))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido bloqueada. Contacta al administrador.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message != null ? message : "Error en el login de administrador");
        }
    }
}

