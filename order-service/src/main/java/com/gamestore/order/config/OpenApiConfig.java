package com.gamestore.order.config;

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
        title = "Order Service API - GameStore",
        version = "1.0.0",
        description = "API REST completa para gestión de órdenes y compras del sistema GameStore. " +
                      "Este servicio permite crear órdenes de compra, validar stock disponible, disminuir inventario automáticamente " +
                      "y agregar juegos a la biblioteca del usuario. Incluye consulta de historial de compras por usuario y gestión completa de órdenes. " +
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
        @Server(url = "http://localhost:3003", description = "Servidor de desarrollo local"),
        @Server(url = "http://10.0.2.2:3003", description = "Android Emulator"),
        @Server(url = "http://order-service:3003", description = "Servidor Eureka (producción)")
    },
    tags = {
        @Tag(name = "Órdenes", description = "CRUD completo de órdenes: crear órdenes de compra, consultar por usuario, obtener por ID y listar todas las órdenes. Incluye validación de stock y gestión automática de inventario.")
    }
)
public class OpenApiConfig {
}

