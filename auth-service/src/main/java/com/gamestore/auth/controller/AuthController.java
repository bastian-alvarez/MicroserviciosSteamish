package com.gamestore.auth.controller;

import com.gamestore.auth.dto.AuthResponse;
import com.gamestore.auth.dto.LoginRequest;
import com.gamestore.auth.dto.RegisterRequest;
import com.gamestore.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "API para registro, login de usuarios y administradores")
public class AuthController {
    private final AuthService authService;
    
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
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
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Login de usuario", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
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
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Login de administrador", description = "Autentica un administrador y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
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
            throw new RuntimeException(e.getMessage());
        }
    }
}

