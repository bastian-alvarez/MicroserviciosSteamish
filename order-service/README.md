# 🛒 Order Service

Microservicio de compras, carrito y órdenes.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL (XAMPP)
- Base de datos `orders_db` creada
- Game Catalog Service corriendo en puerto 3002

### Ejecutar

```bash
mvn spring-boot:run
```

## 📡 Endpoints

### POST /api/orders
Crear una nueva orden

**Request:**
```json
{
  "userId": 1,
  "items": [
    {
      "juegoId": 1,
      "cantidad": 2
    },
    {
      "juegoId": 3,
      "cantidad": 1
    }
  ],
  "metodoPago": "Tarjeta",
  "direccionEnvio": "Calle 123"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "fechaOrden": "2024-01-15T10:30:00",
  "total": 149.98,
  "estadoId": 1,
  "estadoNombre": "Pendiente",
  "metodoPago": "Tarjeta",
  "direccionEnvio": "Calle 123",
  "detalles": [...]
}
```

### GET /api/orders/user/{userId}
Obtener todas las órdenes de un usuario

### GET /api/orders/{id}
Obtener orden por ID

## 🔧 Puerto

Puerto por defecto: **3003**

## 🔗 Comunicación con otros servicios

Este servicio se comunica con:
- **Game Catalog Service** (puerto 3002) para obtener precios y actualizar stock

