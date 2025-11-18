package com.gamestore.auth.controller;

import com.gamestore.auth.dto.UpdateUserRequest;
import com.gamestore.auth.dto.UserResponse;
import com.gamestore.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Administración de Usuarios", description = "API para gestión de usuarios (solo administradores)")
public class AdminController {
    private final AuthService authService;
    
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> getAllUsers() {
        List<UserResponse> users = authService.getAllUsers();
        
        List<EntityModel<UserResponse>> userResources = users.stream()
                .map(user -> {
                    EntityModel<UserResponse> resource = EntityModel.of(user);
                    resource.add(linkTo(methodOn(AdminController.class).getAllUsers()).withSelfRel());
                    resource.add(linkTo(methodOn(AdminController.class).getUserById(user.getId())).withRel("user"));
                    resource.add(linkTo(methodOn(AdminController.class).updateUser(user.getId(), new UpdateUserRequest())).withRel("update"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<UserResponse>> collection = CollectionModel.of(userResources);
        collection.add(linkTo(methodOn(AdminController.class).getAllUsers()).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse user = authService.getUserById(id);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            resource.add(linkTo(methodOn(AdminController.class).getUserById(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).getAllUsers()).withRel("users"));
            resource.add(linkTo(methodOn(AdminController.class).updateUser(id, new UpdateUserRequest())).withRel("update"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserResponse user = authService.updateUser(id, request);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            
            resource.add(linkTo(methodOn(AdminController.class).updateUser(id, request)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).getUserById(id)).withRel("user"));
            resource.add(linkTo(methodOn(AdminController.class).getAllUsers()).withRel("users"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> deleteUser(@PathVariable Long id) {
        try {
            authService.deleteUser(id);
            Map<String, String> response = Map.of("message", "Usuario eliminado exitosamente");
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(AdminController.class).getAllUsers()).withRel("users"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Bloquear usuario", description = "Bloquea un usuario para que no pueda iniciar sesión")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario bloqueado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<EntityModel<UserResponse>> blockUser(@PathVariable Long id) {
        try {
            UserResponse user = authService.blockUser(id);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            
            resource.add(linkTo(methodOn(AdminController.class).blockUser(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).getUserById(id)).withRel("user"));
            resource.add(linkTo(methodOn(AdminController.class).unblockUser(id)).withRel("unblock"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Desbloquear usuario", description = "Desbloquea un usuario para que pueda iniciar sesión")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario desbloqueado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{id}/unblock")
    public ResponseEntity<EntityModel<UserResponse>> unblockUser(@PathVariable Long id) {
        try {
            UserResponse user = authService.unblockUser(id);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            
            resource.add(linkTo(methodOn(AdminController.class).unblockUser(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).getUserById(id)).withRel("user"));
            resource.add(linkTo(methodOn(AdminController.class).blockUser(id)).withRel("block"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

