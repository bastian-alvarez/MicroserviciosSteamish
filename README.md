# 🎮 GameStore - Microservicios Spring Boot

Arquitectura de microservicios para la aplicación GameStore usando Java Spring Boot.

## 📦 Microservicios

1. **auth-service** (Puerto 3001) - Autenticación y gestión de usuarios/administradores
2. **game-catalog-service** (Puerto 3002) - Catálogo de juegos, categorías y géneros
3. **order-service** (Puerto 3003) - Compras, carrito y órdenes
4. **library-service** (Puerto 3004) - Biblioteca de juegos del usuario

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+ instalado
- Maven 3.6+ instalado
- XAMPP con MySQL corriendo
- Puerto 3306 disponible para MySQL

### 1. Configurar Bases de Datos

Ejecutar el script SQL en MySQL (phpMyAdmin o línea de comandos):

```bash
# Abrir MySQL en XAMPP y ejecutar:
source setup-databases.sql
```

O ejecutar manualmente cada script SQL desde cada carpeta de servicio.

### 2. Configurar cada servicio

Editar `src/main/resources/application.properties` en cada servicio:

```properties
spring.datasource.username=root
spring.datasource.password=tu_password_mysql
```

### 3. Compilar y ejecutar cada servicio

#### Auth Service
```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

#### Game Catalog Service
```bash
cd game-catalog-service
mvn clean install
mvn spring-boot:run
```

#### Order Service
```bash
cd order-service
mvn clean install
mvn spring-boot:run
```

#### Library Service
```bash
cd library-service
mvn clean install
mvn spring-boot:run
```

## 📡 Puertos y Endpoints

### Auth Service (3001)
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Login usuario/admin
- `POST /api/auth/admin/login` - Login admin

### Game Catalog Service (3002)
- `GET /api/games` - Listar juegos
- `GET /api/games/{id}` - Obtener juego
- `GET /api/categories` - Listar categorías
- `GET /api/genres` - Listar géneros
- `PUT /api/games/{id}/stock` - Actualizar stock
- `POST /api/games/{id}/decrease-stock` - Disminuir stock

### Order Service (3003)
- `POST /api/orders` - Crear orden
- `GET /api/orders/user/{userId}` - Órdenes del usuario
- `GET /api/orders/{id}` - Obtener orden

### Library Service (3004)
- `POST /api/library` - Agregar a biblioteca
- `GET /api/library/user/{userId}` - Biblioteca del usuario
- `GET /api/library/user/{userId}/game/{juegoId}` - Verificar si tiene juego
- `DELETE /api/library/user/{userId}/game/{juegoId}` - Eliminar de biblioteca

## 🔗 Comunicación entre Servicios

- **Order Service** → **Game Catalog Service**: Obtiene precios y actualiza stock
- Todos los servicios pueden comunicarse vía HTTP REST

## 🗄️ Bases de Datos

- `auth_db` - Usuarios y administradores
- `games_db` - Juegos, categorías y géneros
- `orders_db` - Órdenes y detalles
- `library_db` - Biblioteca de usuarios

## 📝 Notas

- Cada servicio es independiente y puede ejecutarse por separado
- Los servicios usan CORS habilitado para desarrollo
- JWT se usa para autenticación (configurar secret en auth-service)
- Los servicios se comunican vía WebClient (Spring WebFlux)

## 🛠️ Tecnologías

- Spring Boot 3.1.5
- Spring Data JPA
- MySQL 8
- Lombok
- Spring WebFlux (para comunicación entre servicios)

## 📚 Documentación Individual

Cada servicio tiene su propio README.md con documentación detallada.
