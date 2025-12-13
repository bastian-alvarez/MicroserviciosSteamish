package com.gamestore.gamecatalog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Game Catalog Service API - GameStore",
        version = "1.0.0",
        description = "API REST completa para gestión del catálogo de juegos del sistema GameStore. " +
                      "Este servicio proporciona endpoints CRUD para juegos, categorías, géneros, calificaciones y comentarios. " +
                      "Incluye funcionalidades de búsqueda, filtrado, moderación de comentarios y gestión de stock. " +
                      "Todos los endpoints están documentados con ejemplos detallados de request/response y códigos de estado HTTP.",
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
    },
    tags = {
        @Tag(name = "Juegos", description = "CRUD completo de juegos: crear, leer, actualizar y eliminar juegos. Incluye búsqueda, filtrado por categoría/género y gestión de stock."),
        @Tag(name = "Categorías", description = "Gestión de categorías de juegos. Listado de todas las categorías disponibles para filtrado."),
        @Tag(name = "Géneros", description = "Gestión de géneros de juegos. Listado de todos los géneros disponibles para clasificación."),
        @Tag(name = "Comentarios", description = "Sistema completo de comentarios: crear, listar por juego/usuario, eliminar comentarios."),
        @Tag(name = "Calificaciones", description = "Sistema de calificaciones (1-5 estrellas): crear/actualizar calificaciones, obtener promedio y conteo por juego."),
        @Tag(name = "Moderador", description = "Endpoints para moderadores: ocultar/mostrar comentarios, consultar datos de usuarios y gestionar contenido.")
    }
)
public class OpenApiConfig {
}

