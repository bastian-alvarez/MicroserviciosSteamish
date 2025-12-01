# 🔌 Puertos de los Microservicios

## 📊 Resumen de Puertos

| Microservicio | Puerto | URL Local | Descripción |
|---------------|--------|-----------|-------------|
| **Eureka Server** | `8761` | `http://localhost:8761` | Service Discovery |
| **API Gateway** | `8080` | `http://localhost:8080` | Gateway principal |
| **Auth Service** | `3001` | `http://localhost:3001` | Autenticación y usuarios |
| **Game Catalog Service** | `3002` | `http://localhost:3002` | Catálogo de juegos |
| **Order Service** | `3003` | `http://localhost:3003` | Órdenes y compras |
| **Library Service** | `3004` | `http://localhost:3004` | Biblioteca de usuario |

---

## 🔍 Detalles por Microservicio

### 1. Eureka Server
- **Puerto**: `8761`
- **URL**: `http://localhost:8761`
- **Dashboard**: `http://localhost:8761` (interfaz web de Eureka)
- **Archivo**: `eureka-server/src/main/resources/application.properties`

### 2. API Gateway
- **Puerto**: `8080` (o `${PORT}` en producción)
- **URL**: `http://localhost:8080`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Archivo**: `api-gateway/src/main/resources/application.properties`
- **Nota**: En Render/producción usa la variable `PORT` automáticamente

### 3. Auth Service
- **Puerto**: `3001`
- **URL**: `http://localhost:3001`
- **Health Check**: `http://localhost:3001/actuator/health`
- **Swagger**: `http://localhost:3001/swagger-ui.html`
- **Archivo**: `auth-service/src/main/resources/application.properties`

### 4. Game Catalog Service
- **Puerto**: `3002`
- **URL**: `http://localhost:3002`
- **Health Check**: `http://localhost:3002/actuator/health`
- **Swagger**: `http://localhost:3002/swagger-ui.html`
- **Archivo**: `game-catalog-service/src/main/resources/application.properties`

### 5. Order Service
- **Puerto**: `3003`
- **URL**: `http://localhost:3003`
- **Health Check**: `http://localhost:3003/actuator/health`
- **Swagger**: `http://localhost:3003/swagger-ui.html`
- **Archivo**: `order-service/src/main/resources/application.properties`

### 6. Library Service
- **Puerto**: `3004`
- **URL**: `http://localhost:3004`
- **Health Check**: `http://localhost:3004/actuator/health`
- **Swagger**: `http://localhost:3004/swagger-ui.html`
- **Archivo**: `library-service/src/main/resources/application.properties`

---

## 🌐 Rutas del API Gateway

El API Gateway (puerto 8080) enruta las peticiones a los microservicios:

| Ruta | Microservicio | Puerto Interno |
|------|---------------|----------------|
| `/api/auth/**` | Auth Service | 3001 |
| `/api/users/**` | Auth Service | 3001 |
| `/api/admin/**` | Auth Service | 3001 |
| `/api/notifications/**` | Auth Service | 3001 |
| `/api/games/**` | Game Catalog Service | 3002 |
| `/api/categories/**` | Game Catalog Service | 3002 |
| `/api/genres/**` | Game Catalog Service | 3002 |
| `/api/comments/**` | Game Catalog Service | 3002 |
| `/api/ratings/**` | Game Catalog Service | 3002 |
| `/api/moderator/**` | Game Catalog Service | 3002 |
| `/api/orders/**` | Order Service | 3003 |
| `/api/library/**` | Library Service | 3004 |

---

## 🔧 Configuración en Producción (Render)

En producción, los puertos pueden cambiar. Configura estas variables de entorno:

### API Gateway
```
PORT=8080
SERVER_ADDRESS=0.0.0.0
```

### Auth Service
```
SERVER_PORT=3001
```

### Game Catalog Service
```
SERVER_PORT=3002
```

### Order Service
```
SERVER_PORT=3003
```

### Library Service
```
SERVER_PORT=3004
```

### Eureka Server
```
SERVER_PORT=8761
```

---

## 📝 Notas Importantes

1. **API Gateway** usa `${PORT:8080}` - en producción Render asigna `PORT` automáticamente
2. **Eureka Server** siempre usa puerto `8761` (estándar)
3. Los microservicios usan puertos fijos (3001-3004) para desarrollo local
4. En producción, Render puede asignar puertos diferentes, pero los servicios se comunican vía Eureka

---

## 🚀 Orden de Inicio Recomendado

1. **Eureka Server** (8761) - Primero
2. **API Gateway** (8080) - Segundo
3. **Auth Service** (3001)
4. **Game Catalog Service** (3002)
5. **Order Service** (3003)
6. **Library Service** (3004)

---

## 🔍 Verificar Puertos en Uso

### Windows (PowerShell)
```powershell
netstat -ano | findstr :8761
netstat -ano | findstr :8080
netstat -ano | findstr :3001
netstat -ano | findstr :3002
netstat -ano | findstr :3003
netstat -ano | findstr :3004
```

### Linux/Mac
```bash
lsof -i :8761
lsof -i :8080
lsof -i :3001
lsof -i :3002
lsof -i :3003
lsof -i :3004
```

---

## ✅ Checklist de Puertos

- [ ] Eureka Server: 8761
- [ ] API Gateway: 8080
- [ ] Auth Service: 3001
- [ ] Game Catalog Service: 3002
- [ ] Order Service: 3003
- [ ] Library Service: 3004

---

## 🆘 Si un Puerto Está en Uso

Si un puerto está ocupado, puedes cambiarlo en el `application.properties` del servicio correspondiente:

```properties
server.port=3005  # Cambia a otro puerto disponible
```

