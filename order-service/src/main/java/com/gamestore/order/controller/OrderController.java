package com.gamestore.order.controller;

import com.gamestore.order.dto.CreateOrderRequest;
import com.gamestore.order.dto.OrderResponse;
import com.gamestore.order.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Órdenes", description = "API para gestión de órdenes y compras")
public class OrderController {
    private final OrderService orderService;
    
    @Operation(summary = "Crear nueva orden", description = "Crea una nueva orden de compra con los juegos especificados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden creada exitosamente",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente")
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
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @Operation(summary = "Obtener órdenes por usuario", description = "Obtiene todas las órdenes de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<OrderResponse>>> getOrdersByUserId(@PathVariable Long userId) {
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
    
    @Operation(summary = "Obtener orden por ID", description = "Obtiene los detalles de una orden específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrderResponse>> getOrderById(@PathVariable Long id) {
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
    
    @Operation(summary = "Obtener todas las órdenes", description = "Obtiene todas las órdenes del sistema (solo administradores)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente")
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

