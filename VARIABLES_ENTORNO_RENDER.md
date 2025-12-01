# 🔐 Variables de Entorno para Render

## 📋 Variables para API Gateway

Haz clic en **"Añadir variable de entorno"** y agrega estas variables:

### Variables Esenciales

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SERVER_PORT` | `8080` | Puerto del servidor (Render usa `PORT` automáticamente) |
| `SPRING_PROFILES_ACTIVE` | `production` | Perfil de Spring activo |

### Variables para Eureka (si usas Service Discovery)

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | `http://eureka-server.onrender.com:8761/eureka/` | URL de Eureka Server (ajusta con tu URL) |
| `EUREKA_INSTANCE_PREFER_IP_ADDRESS` | `true` | Usar IP en lugar de hostname |
| `EUREKA_CLIENT_REGISTER_WITH_EUREKA` | `true` | Registrar con Eureka |
| `EUREKA_CLIENT_FETCH_REGISTRY` | `true` | Obtener registro de servicios |

### Variables Opcionales

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_APPLICATION_NAME` | `api-gateway` | Nombre de la aplicación |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | `health,info` | Endpoints de Actuator expuestos |

---

## 🚀 Configuración Rápida

### Opción 1: Mínima (Sin Eureka)

Solo agrega estas 2 variables:

```
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
```

### Opción 2: Completa (Con Eureka)

Agrega todas las variables esenciales + Eureka:

```
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server.onrender.com:8761/eureka/
EUREKA_INSTANCE_PREFER_IP_ADDRESS=true
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
```

---

## ⚠️ Nota Importante sobre PORT

Render asigna automáticamente la variable `PORT`. Tu aplicación debe leerla:

En `application.properties`, asegúrate de tener:

```properties
server.port=${PORT:8080}
```

Esto significa: usa `PORT` si existe, sino usa `8080`.

---

## 📝 Cómo Agregar Variables en Render

1. Ve a tu servicio en Render
2. Click en **"Environment"** (en el menú lateral)
3. Click en **"Añadir variable de entorno"**
4. Ingresa:
   - **Key**: `SERVER_PORT`
   - **Value**: `8080`
5. Click en **"Save Changes"**
6. Repite para cada variable

---

## 🔄 Variables por Servicio

### API Gateway

```
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server.onrender.com:8761/eureka/
```

### Eureka Server (si lo despliegas)

```
SERVER_PORT=8761
SPRING_PROFILES_ACTIVE=production
```

### Auth Service (ejemplo)

```
SERVER_PORT=3001
SPRING_PROFILES_ACTIVE=production
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server.onrender.com:8761/eureka/
SPRING_DATASOURCE_URL=jdbc:postgresql://... (si usas DB)
```

---

## 🔒 Variables Secretas

Si tienes secretos (API keys, passwords, etc.), Render los encripta automáticamente.

**No agregues**:
- ❌ Passwords en texto plano
- ❌ API keys visibles en logs
- ❌ Credenciales de base de datos

**Usa**:
- ✅ Variables de entorno para secretos
- ✅ Render Secrets (si está disponible en tu plan)

---

## ✅ Checklist

- [ ] `SERVER_PORT` configurado
- [ ] `SPRING_PROFILES_ACTIVE=production`
- [ ] Variables de Eureka (si aplica)
- [ ] Variables de base de datos (si aplica)
- [ ] Application.properties usa `${PORT:8080}`

---

## 🐛 Troubleshooting

### Error: "Port already in use"

**Solución**: Render asigna el puerto automáticamente. Asegúrate de usar:
```properties
server.port=${PORT:8080}
```

### Error: "Cannot connect to Eureka"

**Solución**: Verifica que la URL de Eureka sea correcta:
- Debe ser la URL pública de Render (ej: `https://eureka-server.onrender.com`)
- No uses `localhost` o IPs privadas

### Variables no se aplican

**Solución**: 
1. Guarda los cambios
2. Haz un redeploy manual
3. Verifica que no haya espacios en los valores

---

## 📚 Referencias

- [Render Environment Variables](https://render.com/docs/environment-variables)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

