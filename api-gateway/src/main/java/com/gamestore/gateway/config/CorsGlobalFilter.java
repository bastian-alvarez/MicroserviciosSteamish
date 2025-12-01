package com.gamestore.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para manejar CORS manualmente
 * Se ejecuta después de recibir la respuesta del microservicio para agregar headers CORS
 */
@Component
public class CorsGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();
        
        // Obtener el origen de la petición
        String origin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
        
        // Manejar preflight requests (OPTIONS) inmediatamente
        if (CorsUtils.isPreFlightRequest(request)) {
            HttpHeaders headers = originalResponse.getHeaders();
            if (origin != null && !origin.isEmpty()) {
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            } else {
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            }
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "false");
            originalResponse.setStatusCode(HttpStatus.OK);
            return originalResponse.setComplete();
        }
        
        // Para peticiones normales, decorar la respuesta para agregar headers CORS
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders(super.getHeaders());
                
                // Agregar headers CORS si no están presentes
                if (!headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)) {
                    if (origin != null && !origin.isEmpty()) {
                        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    } else {
                        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    }
                }
                
                if (!headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)) {
                    headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                }
                
                if (!headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS)) {
                    headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                }
                
                if (!headers.containsKey(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)) {
                    headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Content-Type, X-Total-Count");
                }
                
                if (!headers.containsKey(HttpHeaders.ACCESS_CONTROL_MAX_AGE)) {
                    headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                }
                
                if (!headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
                    headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "false");
                }
                
                return headers;
            }
        };
        
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        // Ejecutar DESPUÉS de recibir la respuesta del microservicio
        // pero ANTES de enviarla al cliente
        // Orden más alto (número menor) para ejecutarse antes
        return -100;
    }
}

