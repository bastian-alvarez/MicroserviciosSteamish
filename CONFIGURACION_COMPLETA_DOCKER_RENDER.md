# 🐳 Configuración Completa Docker para Render

## ✅ Configuración Correcta Paso a Paso

### 1. Settings → Build & Deploy

#### Environment
```
Environment: Docker
```

#### Root Directory
```
Root Directory: . (punto) o VACÍO
```
**⚠️ IMPORTANTE**: NO uses `api-gateway` aquí cuando usas Docker. Debe ser la raíz del repositorio.

#### Dockerfile Path
```
Dockerfile Path: api-gateway/Dockerfile
```

#### Build Command
```
(vacío - Render detecta Dockerfile automáticamente)
```
O si necesitas especificar:
```
docker build -f api-gateway/Dockerfile -t api-gateway .
```

#### Start Command
```
(vacío - Render usa CMD del Dockerfile)
```
O si necesitas especificar:
```
docker run api-gateway
```

---

### 2. Settings → Environment

Agrega estas variables de entorno:

```
SPRING_PROFILES_ACTIVE=production
PORT=8080
SERVER_ADDRESS=0.0.0.0
```

**Opcional** (si usas Eureka):
```
EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
EUREKA_CLIENT_FETCH_REGISTRY=false
```

---

### 3. Build Filters (Opcional)

Si quieres que solo se despliegue cuando cambies archivos del API Gateway:

#### Caminos incluidos (Included Paths):
```
api-gateway/**
```

#### Caminos ignorados (Ignored Paths):
```
*.md
.git/**
.vscode/**
```

---

## 📋 Checklist Completo

### Build & Deploy
- [ ] Environment = **Docker**
- [ ] Root Directory = **.** (punto) o **vacío**
- [ ] Dockerfile Path = **api-gateway/Dockerfile**
- [ ] Build Command = **(vacío)** o docker build command
- [ ] Start Command = **(vacío)** o docker run command

### Environment Variables
- [ ] `SPRING_PROFILES_ACTIVE=production`
- [ ] `PORT=8080`
- [ ] `SERVER_ADDRESS=0.0.0.0`

### Dockerfile
- [ ] `EXPOSE 8080` presente
- [ ] `ENV PORT=8080` presente
- [ ] `ENV SERVER_ADDRESS=0.0.0.0` presente
- [ ] ENTRYPOINT usa `${PORT}` y `${SERVER_ADDRESS}`

---

## 🔍 Verificación

### En los Logs de Build

Deberías ver:
```
Step 1/10 : FROM maven:3.9-eclipse-temurin-17 AS build
...
Step 9/10 : COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar
Step 10/10 : ENTRYPOINT ["sh", "-c", "java -Dserver.address=${SERVER_ADDRESS} -Dserver.port=${PORT} -jar app.jar"]
```

### En los Logs de Runtime

Deberías ver:
```
Started ApiGatewayApplication in X.XXX seconds
Netty started on port(s): 8080
```

---

## ⚠️ Errores Comunes

### Error: "Dockerfile not found"
**Causa**: Dockerfile Path incorrecto
**Solución**: Verifica que sea exactamente `api-gateway/Dockerfile`

### Error: "Cannot find pom.xml"
**Causa**: Root Directory está en `api-gateway` en lugar de `.`
**Solución**: Cambia Root Directory a `.` (punto)

### Error: "No ports detected"
**Causa**: Variables de entorno faltantes o aplicación no inicia
**Solución**: 
1. Agrega `PORT=8080` en Environment
2. Verifica que el Dockerfile tenga `EXPOSE 8080`
3. Revisa logs para ver si la app inicia

---

## 🚀 Pasos para Configurar

1. **Ve a Settings → Build & Deploy**
2. **Configura:**
   - Environment: Docker
   - Root Directory: . (o vacío)
   - Dockerfile Path: api-gateway/Dockerfile
   - Build/Start Commands: vacíos
3. **Ve a Settings → Environment**
4. **Agrega variables:**
   - `SPRING_PROFILES_ACTIVE=production`
   - `PORT=8080`
   - `SERVER_ADDRESS=0.0.0.0`
5. **Guarda cambios**
6. **Haz deploy manual** o espera auto-deploy

---

## 📸 Configuración Visual Esperada

### Build & Deploy
```
Environment:        [Docker ▼]
Root Directory:     [.                    ] [Editar]
Dockerfile Path:    [api-gateway/Dockerfile]
Build Command:      [(vacío)]
Start Command:      [(vacío)]
```

### Environment
```
SPRING_PROFILES_ACTIVE = production
PORT                  = 8080
SERVER_ADDRESS        = 0.0.0.0
```

---

## 🆘 Si Sigue Fallando

1. **Revisa logs completos** del build y runtime
2. **Verifica que el Dockerfile** esté en el repositorio
3. **Prueba build localmente**:
   ```bash
   docker build -f api-gateway/Dockerfile -t api-gateway .
   docker run -p 8080:8080 -e PORT=8080 api-gateway
   ```
4. **Contacta soporte de Render** con los logs

