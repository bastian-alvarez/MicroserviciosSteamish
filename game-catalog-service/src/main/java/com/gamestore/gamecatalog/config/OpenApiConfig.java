package com.gamestore.gamecatalog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Game Catalog Service API",
        version = "1.0.0",
        description = "API para gestión del catálogo de juegos, categorías, géneros, calificaciones y comentarios del sistema GameStore",
        contact = @Contact(
            name = "GameStore Support",
            email = "support@gamestore.com",
            url = "https://gamestore.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    ),
    servers = {
        @Server(url = "http://localhost:3002", description = "Servidor de desarrollo local"),
        @Server(url = "http://10.0.2.2:3002", description = "Android Emulator"),
        @Server(url = "http://game-catalog-service:3002", description = "Servidor Eureka (producción)")
    }
)
public class OpenApiConfig {
}

