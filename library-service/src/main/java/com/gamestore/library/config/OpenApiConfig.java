package com.gamestore.library.config;

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
        title = "Library Service API - GameStore",
        version = "1.0.0",
        description = "API REST completa para gestión de la biblioteca de juegos de los usuarios del sistema GameStore. " +
                      "Este servicio permite agregar juegos a la biblioteca del usuario, consultar la biblioteca completa, " +
                      "verificar propiedad de juegos y eliminar juegos de la biblioteca. Los juegos se agregan automáticamente " +
                      "cuando se completa una orden de compra. Todos los endpoints están documentados con ejemplos detallados de request/response y códigos de estado HTTP.",
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
        @Server(url = "http://localhost:3004", description = "Servidor de desarrollo local"),
        @Server(url = "http://10.0.2.2:3004", description = "Android Emulator"),
        @Server(url = "http://library-service:3004", description = "Servidor Eureka (producción)")
    },
    tags = {
        @Tag(name = "Biblioteca", description = "CRUD completo de biblioteca de juegos: agregar juegos, consultar biblioteca por usuario, verificar propiedad y eliminar juegos de la biblioteca.")
    }
)
public class OpenApiConfig {
}

