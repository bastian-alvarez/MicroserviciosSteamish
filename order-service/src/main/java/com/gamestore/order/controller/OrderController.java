package com.gamestore.order.controller;

import com.gamestore.order.dto.CreateOrderRequest;
import com.gamestore.order.dto.OrderResponse;
import com.gamestore.order.service.OrderService;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Órdenes", description = "API para gestión de órdenes y compras")
public class OrderController {
    private final OrderService orderService;
    
    @Operation(
        summary = "Crear nueva orden", 
        description = "Crea una nueva orden de compra con los juegos especificados. Valida el stock disponible, " +
                      "disminuye el stock de los juegos y agrega los juegos a la biblioteca del usuario. " +
                      "No requiere autenticación JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Orden creada exitosamente. Los juegos han sido agregados a la biblioteca del usuario.",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos: campos requeridos faltantes, stock insuficiente, o juego no encontrado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Stock insuficiente para el juego ID: 10\"}"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Uno o más juegos especificados no fueron encontrados en el catálogo",
            content = @Content(schema = @Schema(example = "{\"error\": \"Juego con ID 10 no encontrado\"}"))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al procesar la orden",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al crear la orden\"}"))
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse order = orderService.createOrder(request);
            EntityModel<OrderResponse> resource = EntityModel.of(order);
            
            resource.add(linkTo(methodOn(OrderController.class).createOrder(request)).withSelfRel());
            resource.add(linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withRel("order"));
            resource.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(order.getUserId())).withRel("user-orders"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            // Mantener el mensaje de error original para que el frontend lo reciba
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @Operation(
        summary = "Obtener órdenes por usuario", 
        description = "Obtiene todas las órdenes realizadas por un usuario específico, ordenadas por fecha de creación " +
                      "(más recientes primero). Incluye detalles completos de cada orden."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de órdenes obtenida exitosamente. Retorna una lista vacía si el usuario no tiene órdenes.",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de usuario inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"ID de usuario inválido\"}"))
        )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getOrdersByUserId(
            @Parameter(description = "ID del usuario", example = "5", required = true)
            @PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        
        List<EntityModel<OrderResponse>> orderResources = orders.stream()
                .map(order -> {
                    EntityModel<OrderResponse> resource = EntityModel.of(order);
                    resource.add(linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel());
                    resource.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(userId)).withRel("user-orders"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<OrderResponse>> collection = CollectionModel.of(orderResources);
        collection.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(userId)).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
    
    @Operation(
        summary = "Obtener orden por ID", 
        description = "Obtiene los detalles completos de una orden específica, incluyendo todos los juegos comprados, " +
                      "precios, cantidades y estado de la orden."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Orden encontrada exitosamente",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Orden no encontrada con el ID especificado",
            content = @Content(schema = @Schema(example = "{\"error\": \"Orden con ID 1 no encontrada\"}"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de orden inválido",
            content = @Content(schema = @Schema(example = "{\"error\": \"ID de orden inválido\"}"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderResponse>> getOrderById(
            @Parameter(description = "ID de la orden", example = "1", required = true)
            @PathVariable Long id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            EntityModel<OrderResponse> resource = EntityModel.of(order);
            
            resource.add(linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel());
            resource.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(order.getUserId())).withRel("user-orders"));
            
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
        summary = "Obtener todas las órdenes", 
        description = "Obtiene todas las órdenes del sistema, ordenadas por fecha de creación. " +
                      "Útil para administradores que necesitan ver todas las transacciones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de todas las órdenes obtenida exitosamente. Retorna lista vacía si no hay órdenes.",
            content = @Content(schema = @Schema(implementation = CollectionModel.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor al obtener las órdenes",
            content = @Content(schema = @Schema(example = "{\"error\": \"Error al obtener las órdenes\"}"))
        )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        
        List<EntityModel<OrderResponse>> orderResources = orders.stream()
                .map(order -> {
                    EntityModel<OrderResponse> resource = EntityModel.of(order);
                    resource.add(linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel());
                    resource.add(linkTo(methodOn(OrderController.class).getOrdersByUserId(order.getUserId())).withRel("user-orders"));
                    return resource;
                })
                .collect(Collectors.toList());
        
        CollectionModel<EntityModel<OrderResponse>> collection = CollectionModel.of(orderResources);
        collection.add(linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel());
        
        return ResponseEntity.ok(collection);
    }
}

