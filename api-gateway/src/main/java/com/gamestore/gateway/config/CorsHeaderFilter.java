package com.gamestore.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para remover headers CORS de las respuestas de los microservicios.
 * El API Gateway manejará CORS, por lo que no necesitamos los headers de los servicios downstream.
 * 
 * Este filtro se ejecuta DESPUÉS de recibir la respuesta del microservicio
 * pero ANTES de que CorsWebFilter agregue sus headers.
 */
@Component
public class CorsHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        
        // Crear un decorator que intercepta los headers cuando se escriben
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = super.getHeaders();
                
                // Remover TODOS los valores de headers CORS que puedan venir de los microservicios
                // Esto previene duplicados cuando CorsWebFilter agregue los suyos
                if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                }
                if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                }
                if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS)) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                }
                if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
                }
                if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                }
                if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_MAX_AGE)) {
                    headers.remove(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
                }
                
                return headers;
            }
        };
        
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        // Ejecutar DESPUÉS de recibir la respuesta del downstream service
        // pero ANTES del CorsWebFilter (que tiene orden -1 por defecto)
        // Usar un orden más bajo (mayor número) para ejecutarse después
        return -50;
    }
}

