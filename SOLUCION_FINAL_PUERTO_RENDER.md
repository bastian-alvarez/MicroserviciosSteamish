# 🔧 Solución Final: Error de Puertos en Render

## ❌ Problema

Render no detecta puertos abiertos incluso después de configurar `0.0.0.0`.

## 🎯 Solución: Usar Maven en lugar de Docker

Render a veces tiene problemas detectando puertos en Docker. La solución más confiable es usar **Maven directamente**.

---

## ✅ Configuración Correcta en Render

### Opción 1: Maven (Recomendado)

1. **Ve a Settings → Build & Deploy**

2. **Cambia la configuración:**
   ```
   Environment: Maven (o "Other")
   Root Directory: api-gateway
   Build Command: mvn clean package -DskipTests
   Start Command: java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
   ```

3. **Variables de entorno:**
   ```
   SPRING_PROFILES_ACTIVE=production
   PORT=8080
   ```

### Opción 2: Docker con Puerto Explícito

Si prefieres seguir con Docker:

1. **En Render Dashboard → Settings → Environment**
   - Agrega variable: `PORT=8080`

2. **Verifica que el Dockerfile tenga:**
   ```dockerfile
   EXPOSE 8080
   ENV PORT=8080
   ```

3. **Actualiza el ENTRYPOINT:**
   ```dockerfile
   ENTRYPOINT ["sh", "-c", "java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
   ```

---

## 🔧 Dockerfile Mejorado

Actualiza el Dockerfile con esta versión:

```dockerfile
# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml* ./
COPY api-gateway/pom.xml* ./api-gateway/
COPY api-gateway/src ./api-gateway/src
RUN mvn clean package -DskipTests -pl api-gateway -am || \
    (cd api-gateway && mvn clean package -DskipTests)

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar curl
RUN apk add --no-cache curl

# Copiar JAR
COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar

# Variables de entorno
ENV PORT=8080
ENV SERVER_ADDRESS=0.0.0.0

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=10s --timeout=5s --start-period=90s --retries=5 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

# Ejecutar con puerto explícito
ENTRYPOINT ["sh", "-c", "java -Dserver.address=${SERVER_ADDRESS} -Dserver.port=${PORT} -jar app.jar"]
```

---

## 📋 Pasos Inmediatos

### Si usas Maven (Más fácil):

1. **En Render Dashboard:**
   - Settings → Build & Deploy
   - Environment: **Maven**
   - Root Directory: `api-gateway`
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar`

2. **Variables de entorno:**
   ```
   SPRING_PROFILES_ACTIVE=production
   PORT=8080
   ```

3. **Guarda y redeploy**

### Si sigues con Docker:

1. **Actualiza el Dockerfile** (versión mejorada arriba)
2. **Agrega variable:** `PORT=8080` en Render
3. **Commit y push**
4. **Redeploy**

---

## 🔍 Verificar Logs

Después del deploy, busca en los logs:

```
Started ApiGatewayApplication
Netty started on port(s): 8080
```

Si ves esto, el puerto está funcionando.

---

## ⚠️ Si Persiste el Error

1. **Revisa los logs completos** - ¿La aplicación inicia?
2. **Prueba Maven** en lugar de Docker
3. **Verifica que el JAR se genera** correctamente
4. **Contacta soporte de Render** con los logs

---

## 💡 Recomendación

**Usa Maven** para empezar. Es más simple y Render lo detecta mejor. Puedes cambiar a Docker después cuando todo funcione.

