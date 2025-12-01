package com.gamestore.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para manejar CORS manualmente
 * Se ejecuta antes que otros filtros para asegurar que los headers CORS estén presentes
 */
@Component
public class CorsGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // Obtener el origen de la petición
        String origin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
        
        // Si hay un origen, permitirlo específicamente, sino permitir todos
        if (origin != null && !origin.isEmpty()) {
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        } else {
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }
        
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Content-Type, X-Total-Count");
        headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "false");

        // Manejar preflight requests (OPTIONS)
        if (CorsUtils.isPreFlightRequest(request)) {
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Ejecutar muy temprano, antes que otros filtros
        return -1000;
    }
}

