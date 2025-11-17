package com.gamestore.order.service;

import com.gamestore.order.dto.CreateOrderRequest;
import com.gamestore.order.dto.OrderResponse;
import com.gamestore.order.entity.Order;
import com.gamestore.order.entity.OrderDetail;
import com.gamestore.order.entity.OrderStatus;
import com.gamestore.order.repository.OrderRepository;
import com.gamestore.order.repository.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final WebClient.Builder webClientBuilder;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Calcular total
        double total = 0.0;
        for (CreateOrderRequest.OrderItem item : request.getItems()) {
            // Obtener precio del juego desde Game Catalog Service
            Double precio = getGamePrice(item.getJuegoId());
            total += precio * item.getCantidad();
        }
        
        // Crear orden
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setFechaOrden(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        order.setTotal(total);
        order.setEstadoId(1L); // Pendiente
        order.setMetodoPago(request.getMetodoPago());
        order.setDireccionEnvio(request.getDireccionEnvio());
        
        order = orderRepository.save(order);
        
        // Guardar el ID en una variable final para usar en el lambda
        final Long ordenId = order.getId();
        
        // Crear detalles
        List<OrderDetail> detalles = request.getItems().stream()
                .map(item -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrdenId(ordenId);
                    detail.setJuegoId(item.getJuegoId());
                    detail.setCantidad(item.getCantidad());
                    Double precio = getGamePrice(item.getJuegoId());
                    detail.setPrecioUnitario(precio);
                    detail.setSubtotal(precio * item.getCantidad());
                    return detail;
                })
                .collect(Collectors.toList());
        
        order.setDetalles(detalles);
        order = orderRepository.save(order);
        
        // Actualizar stock en Game Catalog Service
        for (CreateOrderRequest.OrderItem item : request.getItems()) {
            decreaseGameStock(item.getJuegoId(), item.getCantidad());
        }
        
        return mapToResponse(order);
    }
    
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return mapToResponse(order);
    }
    
    // Clase interna para respuesta del Game Catalog Service
    private static class GameResponse {
        private Double precio;
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
    }
    
    private Double getGamePrice(Long juegoId) {
        try {
            WebClient webClient = webClientBuilder.build();
            GameResponse game = webClient.get()
                    .uri("http://localhost:3002/api/games/{id}", juegoId)
                    .retrieve()
                    .bodyToMono(GameResponse.class)
                    .block();
            return game != null ? game.getPrecio() : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener precio del juego: " + e.getMessage());
        }
    }
    
    private void decreaseGameStock(Long juegoId, Integer quantity) {
        try {
            WebClient webClient = webClientBuilder.build();
            webClient.post()
                    .uri("http://localhost:3002/api/games/{id}/decrease-stock", juegoId)
                    .bodyValue(Map.of("quantity", quantity))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
        }
    }
    
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setFechaOrden(order.getFechaOrden());
        response.setTotal(order.getTotal());
        response.setEstadoId(order.getEstadoId());
        response.setMetodoPago(order.getMetodoPago());
        response.setDireccionEnvio(order.getDireccionEnvio());
        
        if (order.getEstado() != null) {
            response.setEstadoNombre(order.getEstado().getNombre());
        }
        
        if (order.getDetalles() != null) {
            response.setDetalles(order.getDetalles().stream()
                    .map(detail -> {
                        OrderResponse.OrderDetailResponse detailResponse = new OrderResponse.OrderDetailResponse();
                        detailResponse.setId(detail.getId());
                        detailResponse.setJuegoId(detail.getJuegoId());
                        detailResponse.setCantidad(detail.getCantidad());
                        detailResponse.setPrecioUnitario(detail.getPrecioUnitario());
                        detailResponse.setSubtotal(detail.getSubtotal());
                        return detailResponse;
                    })
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
}

