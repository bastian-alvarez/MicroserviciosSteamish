package com.gamestore.auth.controller;

import com.gamestore.auth.dto.AdminResponse;
import com.gamestore.auth.dto.UpdateUserRequest;
import com.gamestore.auth.dto.UserResponse;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    
    @Operation(
        summary = "Listar todos los usuarios", 
        description = "Obtiene la lista completa de usuarios registrados en el sistema. Incluye información de " +
                      "perfil, estado de bloqueo y datos de contacto. Solo administradores pueden acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de usuarios obtenida exitosamente. Retorna lista vacía si no hay usuarios.",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al obtener usuarios",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al obtener usuarios\"}"))
        )
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
    
    @Operation(summary = "Listar todos los usuarios (formato simple)", description = "Obtiene la lista completa de usuarios en formato array simple para aplicaciones móviles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping("/simple")
    public ResponseEntity<List<UserResponse>> getAllUsersSimple() {
        try {
            List<UserResponse> users = authService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener usuarios");
        }
    }
    
    @Operation(
        summary = "Obtener usuario por ID", 
        description = "Obtiene los detalles completos de un usuario específico, incluyendo estado de bloqueo, " +
                      "foto de perfil y datos de contacto. Solo administradores pueden acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long id) {
        try {
            UserResponse user = authService.getUserById(id);
            EntityModel<UserResponse> resource = EntityModel.of(user);
            resource.add(linkTo(methodOn(AdminController.class).getUserById(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).getAllUsers()).withRel("users"));
            resource.add(linkTo(methodOn(AdminController.class).updateUser(id, new UpdateUserRequest())).withRel("update"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Actualizar usuario", 
        description = "Actualiza los datos de un usuario. Permite modificar nombre, email, teléfono y otros datos. " +
                      "Valida que el email no esté en uso por otro usuario. Solo administradores pueden actualizar usuarios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario actualizado exitosamente. Retorna el usuario con los datos actualizados.",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: formato de email inválido o campos requeridos faltantes",
            content = @Content(schema = @Schema(example = "{\"error\": \"El email debe ser válido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflicto: el email ya está en uso por otro usuario",
            content = @Content(schema = @Schema(example = "{\"error\": \"El email ya está en uso\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
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
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            if (e.getMessage().contains("ya está en uso") || e.getMessage().contains("ya está registrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Eliminar usuario", 
        description = "Elimina un usuario del sistema permanentemente. Esta acción no se puede deshacer y elimina " +
                      "todos los datos asociados al usuario. Solo administradores pueden eliminar usuarios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario eliminado exitosamente",
            content = @Content(schema = @Schema(example = "{\"message\": \"Usuario eliminado exitosamente\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
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
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Bloquear usuario", 
        description = "Bloquea un usuario para que no pueda iniciar sesión. El usuario bloqueado no podrá " +
                      "autenticarse hasta que sea desbloqueado. Solo administradores pueden bloquear usuarios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario bloqueado exitosamente. Retorna el usuario con isBlocked=true.",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
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
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Desbloquear usuario", 
        description = "Desbloquea un usuario para que pueda iniciar sesión nuevamente. Restaura el acceso " +
                      "completo del usuario al sistema. Solo administradores pueden desbloquear usuarios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario desbloqueado exitosamente. Retorna el usuario con isBlocked=false.",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "No autorizado: se requiere rol de administrador",
            content = @Content(schema = @Schema(example = "{\"error\": \"No autorizado\"}"))
        )
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
            if (e.getMessage().contains("no encontrado")) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(summary = "Listar todos los administradores", description = "Obtiene la lista completa de administradores (solo super administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de administradores obtenida exitosamente")
    })
    @GetMapping("/admins")
    public ResponseEntity<CollectionModel<EntityModel<AdminResponse>>> getAllAdmins() {
        List<AdminResponse> admins = authService.getAllAdmins();
        
        List<EntityModel<AdminResponse>> adminResources = admins.stream()
                .map(admin -> {
                    EntityModel<AdminResponse> resource = EntityModel.of(admin);
                    resource.add(linkTo(methodOn(AdminController.class).getAllAdmins()).withSelfRel());
                    resource.add(linkTo(methodOn(AdminController.class).getAdminById(admin.getId())).withRel("admin"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<AdminResponse>> collection = CollectionModel.of(adminResources);
        collection.add(linkTo(methodOn(AdminController.class).getAllAdmins()).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(summary = "Obtener administrador por ID", description = "Obtiene los detalles de un administrador específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Administrador encontrado",
                content = @Content(schema = @Schema(implementation = AdminResponse.class))),
        @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @GetMapping("/admins/{id}")
    public ResponseEntity<EntityModel<AdminResponse>> getAdminById(@PathVariable Long id) {
        try {
            AdminResponse admin = authService.getAdminById(id);
            EntityModel<AdminResponse> resource = EntityModel.of(admin);
            resource.add(linkTo(methodOn(AdminController.class).getAdminById(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).getAllAdmins()).withRel("admins"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

