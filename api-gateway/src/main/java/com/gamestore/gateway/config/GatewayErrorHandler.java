package com.gamestore.gateway.config;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador de errores global para el API Gateway
 * Proporciona respuestas de error más informativas
 */
@Component
@Order(-2)
public class GatewayErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            status = HttpStatus.resolve(responseStatusException.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            message = responseStatusException.getReason();
        } else if (ex.getMessage() != null) {
            message = ex.getMessage();
            // Detectar errores comunes
            if (ex.getMessage().contains("503") || ex.getMessage().contains("Service Unavailable")) {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                message = "Service temporarily unavailable. Please try again later.";
            } else if (ex.getMessage().contains("timeout") || ex.getMessage().contains("Timeout")) {
                status = HttpStatus.GATEWAY_TIMEOUT;
                message = "Request timeout. The service took too long to respond.";
            } else if (ex.getMessage().contains("Connection refused") || ex.getMessage().contains("Connection")) {
                status = HttpStatus.BAD_GATEWAY;
                message = "Unable to connect to the service. Please check if the service is running.";
            }
        }

        response.setStatusCode(status);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", java.time.Instant.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", exchange.getRequest().getPath().value());
        
        try {
            String json = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

