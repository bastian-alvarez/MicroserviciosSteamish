# 📚 Library Service

Microservicio de biblioteca de juegos del usuario.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL (XAMPP)
- Base de datos `library_db` creada

### Ejecutar

```bash
mvn spring-boot:run
```

## 📡 Endpoints

### POST /api/library
Agregar juego a la biblioteca

**Request:**
```json
{
  "userId": 1,
  "juegoId": "1",
  "name": "Doom Eternal",
  "price": 59.99,
  "status": "Disponible",
  "genre": "Acción"
}
```

### GET /api/library/user/{userId}
Obtener biblioteca del usuario

### GET /api/library/user/{userId}/game/{juegoId}
Verificar si el usuario tiene un juego

**Response:**
```json
{
  "owns": true
}
```

### DELETE /api/library/user/{userId}/game/{juegoId}
Eliminar juego de la biblioteca

## 🔧 Puerto

Puerto por defecto: **3004**

