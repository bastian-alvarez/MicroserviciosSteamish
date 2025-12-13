package com.gamestore.auth.config;

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
        title = "Auth Service API - GameStore",
        version = "1.0.0",
        description = "API REST completa para gestión de autenticación, usuarios, administradores y notificaciones del sistema GameStore. " +
                      "Este servicio proporciona endpoints para registro de usuarios, login, gestión de perfiles, administración de usuarios " +
                      "y sistema de notificaciones. Todos los endpoints están documentados con ejemplos de request/response y códigos de estado HTTP.",
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
        @Server(url = "http://localhost:3001", description = "Servidor de desarrollo local"),
        @Server(url = "http://10.0.2.2:3001", description = "Android Emulator"),
        @Server(url = "http://auth-service:3001", description = "Servidor Eureka (producción)")
    },
    tags = {
        @Tag(name = "Autenticación", description = "Endpoints para registro, login de usuarios y administradores. Incluye validación de credenciales y generación de tokens JWT."),
        @Tag(name = "Perfil de Usuario", description = "Gestión del perfil del usuario: consulta, actualización de foto de perfil y cambio de contraseña."),
        @Tag(name = "Administración de Usuarios", description = "CRUD completo de usuarios. Solo administradores pueden acceder. Incluye bloqueo/desbloqueo de usuarios."),
        @Tag(name = "Administración de Juegos", description = "Gestión de juegos desde el servicio de autenticación. CRUD completo y gestión de stock."),
        @Tag(name = "Notificaciones", description = "Sistema completo de notificaciones: crear, listar, marcar como leídas y eliminar notificaciones de usuarios.")
    }
)
public class OpenApiConfig {
}

