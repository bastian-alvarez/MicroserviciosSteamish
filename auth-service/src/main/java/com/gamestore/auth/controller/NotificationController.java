package com.gamestore.auth.controller;

import com.gamestore.auth.dto.CreateNotificationRequest;
import com.gamestore.auth.dto.NotificationResponse;
import com.gamestore.auth.service.NotificationService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Notificaciones", description = "API para gestión de notificaciones de usuarios")
public class NotificationController {
    private final NotificationService notificationService;
    
    @Operation(
        summary = "Obtener todas las notificaciones", 
        description = "Obtiene todas las notificaciones de un usuario, incluyendo leídas y no leídas, " +
                      "ordenadas por fecha de creación (más recientes primero)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Notificaciones obtenidas exitosamente. Retorna lista vacía si no hay notificaciones.",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<NotificationResponse>>> getAllNotifications(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            List<NotificationResponse> notifications = notificationService.getAllNotifications(userId);
            
            List<EntityModel<NotificationResponse>> notificationResources = notifications.stream()
                    .map(notification -> {
                        EntityModel<NotificationResponse> resource = EntityModel.of(notification);
                        resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withSelfRel());
                        resource.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withRel("unread"));
                        resource.add(linkTo(methodOn(NotificationController.class).getUnreadCount(userId)).withRel("unread-count"));
                        if (notification.getId() != null) {
                            resource.add(linkTo(methodOn(NotificationController.class).markAsRead(notification.getId(), userId)).withRel("mark-read"));
                            resource.add(linkTo(methodOn(NotificationController.class).deleteNotification(notification.getId(), userId)).withRel("delete"));
                        }
                        return resource;
                    })
                    .collect(Collectors.toList());
            
            CollectionModel<EntityModel<NotificationResponse>> collection = CollectionModel.of(notificationResources);
            collection.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withSelfRel());
            collection.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withRel("unread"));
            collection.add(linkTo(methodOn(NotificationController.class).getUnreadCount(userId)).withRel("unread-count"));
            collection.add(linkTo(methodOn(NotificationController.class).createNotification(new CreateNotificationRequest())).withRel("create"));
            collection.add(linkTo(methodOn(NotificationController.class).markAllAsRead(userId)).withRel("mark-all-read"));
            collection.add(linkTo(methodOn(NotificationController.class).deleteAllNotifications(userId)).withRel("delete-all"));
            
            return ResponseEntity.ok(collection);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Obtener notificaciones no leídas", 
        description = "Obtiene solo las notificaciones no leídas de un usuario. Útil para mostrar un badge " +
                      "con el número de notificaciones pendientes en la UI."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Notificaciones no leídas obtenidas exitosamente. Retorna lista vacía si no hay notificaciones no leídas.",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @GetMapping("/unread")
    public ResponseEntity<CollectionModel<EntityModel<NotificationResponse>>> getUnreadNotifications(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
            
            List<EntityModel<NotificationResponse>> notificationResources = notifications.stream()
                    .map(notification -> {
                        EntityModel<NotificationResponse> resource = EntityModel.of(notification);
                        resource.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withSelfRel());
                        if (notification.getId() != null) {
                            resource.add(linkTo(methodOn(NotificationController.class).markAsRead(notification.getId(), userId)).withRel("mark-read"));
                            resource.add(linkTo(methodOn(NotificationController.class).deleteNotification(notification.getId(), userId)).withRel("delete"));
                        }
                        return resource;
                    })
                    .collect(Collectors.toList());
            
            CollectionModel<EntityModel<NotificationResponse>> collection = CollectionModel.of(notificationResources);
            collection.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withSelfRel());
            collection.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withRel("all"));
            collection.add(linkTo(methodOn(NotificationController.class).getUnreadCount(userId)).withRel("unread-count"));
            collection.add(linkTo(methodOn(NotificationController.class).markAllAsRead(userId)).withRel("mark-all-read"));
            
            return ResponseEntity.ok(collection);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Contar notificaciones no leídas", 
        description = "Obtiene el conteo de notificaciones no leídas de un usuario. Útil para mostrar un badge " +
                      "numérico en la UI sin necesidad de cargar todas las notificaciones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteo obtenido exitosamente",
            content = @Content(schema = @Schema(example = "{\"count\": 5}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @GetMapping("/unread/count")
    public ResponseEntity<EntityModel<Map<String, Long>>> getUnreadCount(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            long count = notificationService.getUnreadCount(userId);
            Map<String, Long> response = Map.of("count", count);
            EntityModel<Map<String, Long>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(NotificationController.class).getUnreadCount(userId)).withSelfRel());
            resource.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withRel("unread"));
            resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withRel("all"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Crear notificación", 
        description = "Crea una nueva notificación para un usuario. Las notificaciones se crean automáticamente " +
                      "para eventos como compras completadas, cambios de estado de órdenes, etc."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Notificación creada exitosamente. Retorna la notificación con su ID asignado.",
            content = @Content(schema = @Schema(implementation = NotificationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes o userId inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"El título es requerido\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Usuario con ID 5 no encontrado\"}"))
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<NotificationResponse>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        try {
            NotificationResponse notification = notificationService.createNotification(request);
            EntityModel<NotificationResponse> resource = EntityModel.of(notification);
            
            resource.add(linkTo(methodOn(NotificationController.class).createNotification(request)).withSelfRel());
            if (notification.getId() != null) {
                resource.add(linkTo(methodOn(NotificationController.class).markAsRead(notification.getId(), notification.getUserId())).withRel("mark-read"));
                resource.add(linkTo(methodOn(NotificationController.class).deleteNotification(notification.getId(), notification.getUserId())).withRel("delete"));
            }
            resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(notification.getUserId())).withRel("all"));
            resource.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(notification.getUserId())).withRel("unread"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Marcar notificación como leída", 
        description = "Marca una notificación específica como leída. Una vez marcada como leída, la notificación " +
                      "ya no aparecerá en la lista de notificaciones no leídas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Notificación marcada como leída exitosamente",
            content = @Content(schema = @Schema(implementation = NotificationResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Notificación no encontrada o no pertenece al usuario",
            content = @Content(schema = @Schema(example = "{\"error\": \"Notificación no encontrada\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<EntityModel<NotificationResponse>> markAsRead(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            NotificationResponse notification = notificationService.markAsRead(id, userId);
            EntityModel<NotificationResponse> resource = EntityModel.of(notification);
            
            resource.add(linkTo(methodOn(NotificationController.class).markAsRead(id, userId)).withSelfRel());
            resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withRel("all"));
            resource.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withRel("unread"));
            resource.add(linkTo(methodOn(NotificationController.class).deleteNotification(id, userId)).withRel("delete"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("No tienes permiso")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Marcar todas las notificaciones como leídas", 
        description = "Marca todas las notificaciones de un usuario como leídas de una vez. Útil para " +
                      "implementar un botón 'Marcar todas como leídas' en la UI."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Todas las notificaciones marcadas como leídas exitosamente",
            content = @Content(schema = @Schema(example = "{\"message\": \"Todas las notificaciones han sido marcadas como leídas\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @PutMapping("/read-all")
    public ResponseEntity<EntityModel<Map<String, String>>> markAllAsRead(
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            notificationService.markAllAsRead(userId);
            Map<String, String> response = Map.of("message", "Todas las notificaciones han sido marcadas como leídas");
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(NotificationController.class).markAllAsRead(userId)).withSelfRel());
            resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withRel("all"));
            resource.add(linkTo(methodOn(NotificationController.class).getUnreadNotifications(userId)).withRel("unread"));
            resource.add(linkTo(methodOn(NotificationController.class).getUnreadCount(userId)).withRel("unread-count"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Eliminar notificación", 
        description = "Elimina una notificación específica del usuario. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Notificación eliminada exitosamente",
            content = @Content(schema = @Schema(example = "{\"message\": \"Notificación eliminada exitosamente\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Notificación no encontrada o no pertenece al usuario",
            content = @Content(schema = @Schema(example = "{\"error\": \"Notificación no encontrada\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> deleteNotification(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            notificationService.deleteNotification(id, userId);
            Map<String, String> response = Map.of("message", "Notificación eliminada exitosamente");
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(NotificationController.class).deleteNotification(id, userId)).withSelfRel());
            resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withRel("all"));
            resource.add(linkTo(methodOn(NotificationController.class).deleteAllNotifications(userId)).withRel("delete-all"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("No tienes permiso")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    @Operation(
        summary = "Eliminar todas las notificaciones", 
        description = "Elimina todas las notificaciones de un usuario de una vez. Esta acción no se puede deshacer. " +
                      "Útil para implementar un botón 'Limpiar todas' en la UI."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Todas las notificaciones eliminadas exitosamente",
            content = @Content(schema = @Schema(example = "{\"message\": \"Todas las notificaciones han sido eliminadas\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "userId es requerido",
            content = @Content(schema = @Schema(example = "{\"error\": \"userId es requerido\"}"))
        )
    })
    @DeleteMapping
    public ResponseEntity<EntityModel<Map<String, String>>> deleteAllNotifications(
            @RequestParam(required = false) Long userId) {
        try {
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es requerido");
            }
            
            notificationService.deleteAllNotifications(userId);
            Map<String, String> response = Map.of("message", "Todas las notificaciones han sido eliminadas");
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            
            resource.add(linkTo(methodOn(NotificationController.class).deleteAllNotifications(userId)).withSelfRel());
            resource.add(linkTo(methodOn(NotificationController.class).getAllNotifications(userId)).withRel("all"));
            
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

