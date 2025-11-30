package com.gamestore.order.service;

import com.gamestore.order.dto.CreateOrderRequest;
import com.gamestore.order.dto.OrderResponse;
import com.gamestore.order.entity.Order;
import com.gamestore.order.entity.OrderDetail;
import com.gamestore.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    
    // URLs de servicios - Eureka resolverá automáticamente, pero tenemos fallback directo
    // IMPORTANTE: Para @LoadBalanced, usar solo el nombre del servicio sin http://
    private static final String LIBRARY_SERVICE_EUREKA = "library-service";
    private static final String GAME_CATALOG_SERVICE_EUREKA = "game-catalog-service";
    // URLs directas como fallback si Eureka no está disponible
    private static final String LIBRARY_SERVICE_DIRECT = "http://localhost:3004";
    private static final String GAME_CATALOG_SERVICE_DIRECT = "http://localhost:3002";
    
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
        
        // Agregar juegos a la biblioteca del usuario
        // Se hace después de crear la orden para que si falla, la orden ya esté guardada
        for (CreateOrderRequest.OrderItem item : request.getItems()) {
            // Agregar cada unidad comprada a la biblioteca
            for (int i = 0; i < item.getCantidad(); i++) {
                addGameToLibrary(request.getUserId(), item.getJuegoId());
            }
        }
        
        return mapToResponse(order);
    }
    
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
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
        private String nombre;
        private String generoNombre;
        
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getGeneroNombre() { return generoNombre; }
        public void setGeneroNombre(String generoNombre) { this.generoNombre = generoNombre; }
    }
    
    // Clase interna para request al Library Service
    private static class AddToLibraryRequest {
        private Long userId;
        private String juegoId;
        private String name;
        private Double price;
        private String status = "Disponible";
        private String genre;
        
        // Getters y setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getJuegoId() { return juegoId; }
        public void setJuegoId(String juegoId) { this.juegoId = juegoId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
    }
    
    private GameResponse getGameInfo(Long juegoId) {
        // Intentar primero con URL directa (más confiable), luego con Eureka como alternativa
        // Esto evita problemas con LoadBalancer cuando Eureka no está completamente configurado
        String[] urls = {GAME_CATALOG_SERVICE_DIRECT, GAME_CATALOG_SERVICE_EUREKA};
        
        for (String baseUrl : urls) {
            try {
                boolean isEureka = !baseUrl.contains("localhost");
                log.info("Obteniendo información del juego {} desde {} (intento con {})", juegoId, baseUrl, 
                        isEureka ? "Eureka" : "URL directa");
                
                // Para Eureka con @LoadBalanced, usar http://service-name (el LoadBalancer resolverá el nombre)
                // Para URL directa, usar la URL completa sin LoadBalancer
                WebClient webClient = isEureka 
                    ? webClientBuilder.baseUrl("http://" + baseUrl).build()
                    : WebClient.builder().baseUrl(baseUrl).build();
                
                GameResponse game = webClient.get()
                        .uri("/api/games/{id}", juegoId)
                        .retrieve()
                        .onStatus(status -> status.is5xxServerError() || status.is4xxClientError(), response -> {
                            log.error("Error HTTP al obtener juego {} desde {}: {}", juegoId, baseUrl, response.statusCode());
                            return response.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Error del servidor: " + response.statusCode() + " - " + body));
                        })
                        .bodyToMono(GameResponse.class)
                        .block();
                
                if (game == null) {
                    log.warn("Juego {} no encontrado (respuesta null) desde {}", juegoId, baseUrl);
                    if (baseUrl.equals(GAME_CATALOG_SERVICE_DIRECT)) {
                        continue; // Intentar con Eureka
                    }
                    throw new RuntimeException("Juego no encontrado con ID: " + juegoId);
                }
                
                log.info("Información del juego {} obtenida exitosamente desde {}", juegoId, baseUrl);
                return game;
            } catch (org.springframework.web.reactive.function.client.WebClientException e) {
                String errorMsg = e.getMessage();
                log.warn("Error al conectar con {}: {}. {}", baseUrl, errorMsg, 
                        baseUrl.equals(GAME_CATALOG_SERVICE_DIRECT) ? "Intentando con Eureka..." : "");
                
                // Si el error es de LoadBalancer y estamos usando URL directa, ya falló, intentar Eureka
                if (baseUrl.equals(GAME_CATALOG_SERVICE_DIRECT)) {
                    continue; // Intentar con Eureka
                }
                
                // Si falló también con Eureka, lanzar error más descriptivo
                if (errorMsg != null && (errorMsg.contains("UNKNOWN") || errorMsg.contains("LoadBalancer"))) {
                    throw new RuntimeException("No se pudo resolver el servicio 'game-catalog-service' a través de Eureka. " +
                            "Usando conexión directa. Verifica que: 1) Eureka Server esté corriendo en http://localhost:8761, " +
                            "2) game-catalog-service esté corriendo en http://localhost:3002.");
                }
                throw new RuntimeException("Error al conectar con el servicio de catálogo: " + errorMsg);
            } catch (RuntimeException e) {
                // Si es un error de "no encontrado" y estamos usando URL directa, intentar Eureka
                if (baseUrl.equals(GAME_CATALOG_SERVICE_DIRECT) && 
                    (e.getMessage().contains("no encontrado") || e.getMessage().contains("503"))) {
                    continue;
                }
                throw e;
            } catch (Exception e) {
                log.error("Error inesperado al obtener juego {} desde {}: {}", juegoId, baseUrl, e.getMessage(), e);
                if (baseUrl.equals(GAME_CATALOG_SERVICE_DIRECT)) {
                    continue; // Intentar con Eureka
                }
                throw new RuntimeException("Error al obtener información del juego " + juegoId + ": " + e.getMessage());
            }
        }
        
        // Si llegamos aquí, ambos intentos fallaron
        throw new RuntimeException("No se pudo conectar con game-catalog-service. " +
                "Verifica que el servicio esté corriendo en http://localhost:3002");
    }
    
    private Double getGamePrice(Long juegoId) {
        return getGameInfo(juegoId).getPrecio();
    }
    
    private void decreaseGameStock(Long juegoId, Integer quantity) {
        // Intentar primero con URL directa, luego con Eureka
        String[] urls = {GAME_CATALOG_SERVICE_DIRECT, GAME_CATALOG_SERVICE_EUREKA};
        
        for (String baseUrl : urls) {
            try {
                boolean isEureka = !baseUrl.contains("localhost");
                // Para Eureka con @LoadBalanced, usar http://service-name (el LoadBalancer resolverá el nombre)
                // Para URL directa, usar la URL completa
                WebClient webClient = isEureka 
                    ? webClientBuilder.baseUrl("http://" + baseUrl).build()
                    : WebClient.builder().baseUrl(baseUrl).build();
                webClient.post()
                        .uri("/api/games/internal/{id}/decrease-stock", juegoId)
                        .bodyValue(Map.of("quantity", quantity))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                log.info("Stock del juego {} disminuido exitosamente desde {}", juegoId, baseUrl);
                return;
            } catch (Exception e) {
                log.warn("Error al actualizar stock desde {}: {}. {}", baseUrl, e.getMessage(),
                        baseUrl.equals(GAME_CATALOG_SERVICE_EUREKA) ? "Intentando con URL directa..." : "");
                if (baseUrl.equals(GAME_CATALOG_SERVICE_EUREKA)) {
                    continue; // Intentar con URL directa
                }
                throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
            }
        }
    }
    
    /**
     * Agrega un juego a la biblioteca del usuario
     * Si falla, solo registra el error pero no revierte la orden
     */
    private void addGameToLibrary(Long userId, Long juegoId) {
        try {
            // Obtener información completa del juego
            GameResponse gameInfo = getGameInfo(juegoId);
            
            // Preparar request para Library Service
            AddToLibraryRequest libraryRequest = new AddToLibraryRequest();
            libraryRequest.setUserId(userId);
            libraryRequest.setJuegoId(juegoId.toString());
            libraryRequest.setName(gameInfo.getNombre() != null ? gameInfo.getNombre() : "Juego " + juegoId);
            libraryRequest.setPrice(gameInfo.getPrecio() != null ? gameInfo.getPrecio() : 0.0);
            libraryRequest.setGenre(gameInfo.getGeneroNombre() != null ? gameInfo.getGeneroNombre() : "Acción");
            libraryRequest.setStatus("Disponible");
            
            log.info("Preparando request para agregar juego a biblioteca: userId={}, juegoId={}, name={}, price={}", 
                    userId, juegoId, libraryRequest.getName(), libraryRequest.getPrice());
            
            // Llamar al Library Service usando endpoint interno (sin autenticación)
            // Intentar primero con URL directa, luego con Eureka como alternativa
            String[] urls = {LIBRARY_SERVICE_DIRECT, LIBRARY_SERVICE_EUREKA};
            
            for (String baseUrl : urls) {
                try {
                    boolean isEureka = !baseUrl.contains("localhost");
                    log.info("Intentando agregar juego {} a biblioteca del usuario {} desde {} (método: {})", 
                            juegoId, userId, baseUrl, isEureka ? "Eureka" : "URL directa");
                    
                    // Para Eureka, usar solo el nombre del servicio. Para URL directa, usar la URL completa
                    WebClient webClient = isEureka 
                        ? webClientBuilder.baseUrl("http://" + baseUrl).build()
                        : WebClient.builder().baseUrl(baseUrl).build();
                    
                    // Enviar request con Content-Type application/json
                    var response = webClient.post()
                            .uri("/api/library/internal/add")
                            .header("Content-Type", "application/json")
                            .bodyValue(libraryRequest)
                            .retrieve()
                            .toEntity(Void.class)
                            .block();
                    
                    if (response != null && response.getStatusCode().is2xxSuccessful()) {
                        log.info("Juego {} agregado exitosamente a la biblioteca del usuario {} desde {}", juegoId, userId, baseUrl);
                        return;
                    } else {
                        log.warn("Respuesta no exitosa al agregar juego {} a biblioteca: {}", juegoId, 
                                response != null ? response.getStatusCode() : "null");
                        if (baseUrl.equals(LIBRARY_SERVICE_DIRECT)) {
                            continue; // Intentar con Eureka
                        }
                        throw new RuntimeException("Error al agregar juego a biblioteca: respuesta no exitosa");
                    }
                } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
                    // Error de respuesta HTTP (4xx, 5xx)
                    String errorBody = e.getResponseBodyAsString();
                    log.error("Error HTTP al agregar juego {} a biblioteca desde {}: {} - {}. Body: {}", 
                            juegoId, baseUrl, e.getStatusCode(), e.getMessage(), errorBody);
                    
                    // Si es un error 409 (conflicto - juego ya existe), no es crítico
                    if (e.getStatusCode().value() == 409) {
                        log.warn("Juego {} ya está en la biblioteca del usuario {}: {}", juegoId, userId, errorBody);
                        return; // No es un error crítico, el juego ya está en la biblioteca
                    }
                    
                    if (baseUrl.equals(LIBRARY_SERVICE_DIRECT)) {
                        continue; // Intentar con Eureka
                    }
                    throw new RuntimeException("Error al agregar juego a biblioteca: " + e.getStatusCode() + " - " + errorBody, e);
                } catch (Exception e) {
                    log.error("Error inesperado al agregar a biblioteca desde {}: {}", baseUrl, e.getMessage(), e);
                    if (baseUrl.equals(LIBRARY_SERVICE_DIRECT)) {
                        continue; // Intentar con Eureka
                    }
                    throw new RuntimeException("Error al agregar juego a biblioteca: " + e.getMessage(), e);
                }
            }
            
            log.info("Juego {} agregado a la biblioteca del usuario {}", juegoId, userId);
        } catch (Exception e) {
            // Si el juego ya está en la biblioteca, no es un error crítico
            if (e.getMessage() != null && e.getMessage().contains("ya está en la biblioteca")) {
                log.warn("Juego {} ya está en la biblioteca del usuario {}: {}", juegoId, userId, e.getMessage());
            } else {
                // Para otros errores, registramos pero no fallamos la orden
                log.error("Error al agregar juego {} a la biblioteca del usuario {}: {}", 
                    juegoId, userId, e.getMessage());
            }
            // No lanzamos excepción para que la orden se mantenga
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

