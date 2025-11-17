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
- **Laragon** con MySQL corriendo (recomendado) o XAMPP
- Puerto 3306 disponible para MySQL

### 1. Configurar Bases de Datos

#### Opción A: Usando Laragon (Recomendado)

1. Inicia Laragon y asegúrate de que MySQL esté corriendo
2. Abre phpMyAdmin desde Laragon: `http://localhost/phpmyadmin`
3. Ve a la pestaña **SQL**
4. Copia y pega el contenido completo del archivo `setup-databases.sql`
5. Haz clic en **Ejecutar**

**📖 Ver guía completa:** [GUIA_LARAGON.md](GUIA_LARAGON.md)

#### Opción B: Usando XAMPP

Ejecutar el script SQL en MySQL (phpMyAdmin o línea de comandos):

```bash
# Abrir MySQL en XAMPP y ejecutar:
source setup-databases.sql
```

### 2. Configurar Conexión a Base de Datos

Los archivos `application.properties` ya están configurados para Laragon por defecto:

- **Host:** `localhost`
- **Puerto:** `3306`
- **Usuario:** `root`
- **Contraseña:** (vacía por defecto en Laragon)

Si tu MySQL tiene contraseña, edita los archivos `application.properties` de cada servicio:

```properties
spring.datasource.password=tu_password_mysql
```

**Archivos a editar:**
- `auth-service/src/main/resources/application.properties`
- `game-catalog-service/src/main/resources/application.properties`
- `order-service/src/main/resources/application.properties`
- `library-service/src/main/resources/application.properties`

### 3. Compilar y ejecutar los servicios

El proyecto tiene un **POM padre** que gestiona todos los microservicios como módulos Maven.

#### Opción A: Compilar todos los servicios desde la raíz

```bash
# Desde la raíz del proyecto
mvn clean install
```

Esto compilará todos los microservicios en un solo comando.

#### Opción B: Ejecutar cada servicio individualmente

**Auth Service (Puerto 3001)**
```bash
cd auth-service
mvn spring-boot:run
```

**Game Catalog Service (Puerto 3002)**
```bash
cd game-catalog-service
mvn spring-boot:run
```

**Order Service (Puerto 3003)**
```bash
cd order-service
mvn spring-boot:run
```

**Library Service (Puerto 3004)**
```bash
cd library-service
mvn spring-boot:run
```

#### Opción C: Ejecutar un servicio específico desde la raíz

```bash
# Ejecutar auth-service
mvn spring-boot:run -pl auth-service

# Ejecutar game-catalog-service
mvn spring-boot:run -pl game-catalog-service

# Ejecutar order-service
mvn spring-boot:run -pl order-service

# Ejecutar library-service
mvn spring-boot:run -pl library-service
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

## 📚 Documentación

- **Guía de Laragon:** [GUIA_LARAGON.md](GUIA_LARAGON.md) - Configuración paso a paso con Laragon
- **Swagger UI:** Cada servicio tiene documentación interactiva en `/swagger-ui.html`
- **OpenAPI:** Especificaciones disponibles en `/api-docs` de cada servicio
- Cada servicio tiene su propio README.md con documentación detallada

## 🚀 Scripts Útiles

- **`ejecutar-servicios.bat`** - Script interactivo para ejecutar los servicios en Windows
- **`verificar-conexion.bat`** - Verifica la conexión a MySQL antes de iniciar los servicios
