# 🎮 Game Catalog Service

Microservicio de catálogo de juegos, categorías y géneros.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL (XAMPP)
- Base de datos `games_db` creada

### Ejecutar

```bash
mvn spring-boot:run
```

## 📡 Endpoints

### GET /api/games
Obtener todos los juegos activos

**Query Parameters:**
- `categoria` - Filtrar por categoría ID
- `genero` - Filtrar por género ID
- `descuento` - `true` para solo juegos con descuento
- `search` - Buscar por nombre

**Ejemplo:**
```
GET /api/games?descuento=true
GET /api/games?categoria=1
GET /api/games?search=Doom
```

### GET /api/games/{id}
Obtener juego por ID

### PUT /api/games/{id}/stock
Actualizar stock de un juego

**Request:**
```json
{
  "stock": 50
}
```

### POST /api/games/{id}/decrease-stock
Disminuir stock (para compras)

**Request:**
```json
{
  "quantity": 2
}
```

### GET /api/categories
Obtener todas las categorías

### GET /api/genres
Obtener todos los géneros

## 🔧 Puerto

Puerto por defecto: **3002**

