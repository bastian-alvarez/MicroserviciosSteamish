package com.gamestore.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para remover headers CORS de las respuestas de los microservicios.
 * El API Gateway manejará CORS, por lo que no necesitamos los headers de los servicios downstream.
 */
@Component
public class CorsHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders(super.getHeaders());
                
                // Remover headers CORS que puedan venir de los microservicios
                // El CorsWebFilter agregará los suyos después
                headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                headers.remove(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
                headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                headers.remove(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
                
                return headers;
            }
        };
        
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        // Ejecutar antes del CorsWebFilter (que tiene orden -1 por defecto)
        // pero después de otros filtros importantes
        return -100;
    }
}

