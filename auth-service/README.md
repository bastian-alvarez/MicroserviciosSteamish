# 🔐 Auth Service

Microservicio de autenticación y gestión de usuarios/administradores.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL (XAMPP)
- Base de datos `auth_db` creada

### Configuración

1. **Configurar base de datos:**
   - Editar `src/main/resources/application.properties`
   - Ajustar `spring.datasource.username` y `spring.datasource.password`

2. **Configurar JWT Secret:**
   - Cambiar `jwt.secret` en `application.properties`

### Ejecutar

```bash
mvn spring-boot:run
```

O compilar y ejecutar:
```bash
mvn clean package
java -jar target/auth-service-1.0.0.jar
```

## 📡 Endpoints

### POST /api/auth/register
Registrar nuevo usuario

**Request:**
```json
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+56 9 1234 5678",
  "password": "password123",
  "gender": "M"
}
```

**Response:**
```json
{
  "user": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "+56 9 1234 5678",
    "profilePhotoUri": null,
    "isBlocked": false,
    "gender": "M"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

### POST /api/auth/login
Login de usuario o administrador

**Request:**
```json
{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Response:** Igual que register

### POST /api/auth/admin/login
Login específico para administradores (mismo endpoint que login)

## 🔧 Puerto

Puerto por defecto: **3001**

Cambiar en `application.properties`:
```properties
server.port=3001
```

