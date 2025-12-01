# 🎯 Instrucciones Finales para Render

## ⚠️ El Error Persiste

Si sigues viendo el error `/api-gateway: not found`, sigue estos pasos **EXACTAMENTE**:

---

## ✅ Solución Paso a Paso

### 1. En Render Dashboard

Ve a **Settings → Build & Deploy** y configura **EXACTAMENTE** así:

```
Environment: Docker
Docker Build Context Directory: . (solo un punto, sin espacios)
Dockerfile Path: api-gateway/Dockerfile
Build Command: (dejar completamente vacío)
Start Command: (dejar completamente vacío)
Pre-Deploy Command: (dejar completamente vacío)
```

### 2. Verificar que NO Haya Espacios

**CRÍTICO**: El Build Context Directory debe ser **exactamente**:
```
.
```

**NO debe tener**:
- Espacios antes o después
- Caracteres ocultos
- Barras o rutas

### 3. Verificar Variables de Entorno

En **Settings → Environment**, agrega:

```
SPRING_PROFILES_ACTIVE=production
PORT=8080
SERVER_ADDRESS=0.0.0.0
```

### 4. Si Sigue Fallando: Eliminar y Recrear

1. **Elimina el servicio** en Render
2. **Crea uno nuevo** desde cero
3. **Configura TODO** desde el principio:
   - Conecta GitHub
   - Selecciona el repositorio
   - Selecciona branch `main`
   - Environment: **Docker**
   - Build Context: **.** (punto)
   - Dockerfile Path: **api-gateway/Dockerfile**
   - Agrega variables de entorno
   - Crea el servicio

---

## 🔍 Verificación

### En los Logs de Build

Deberías ver:
```
Step 1/8 : FROM maven:3.9-eclipse-temurin-17 AS build
Step 2/8 : WORKDIR /app
Step 3/8 : COPY pom.xml ./
Step 4/8 : COPY api-gateway ./api-gateway
```

Si ves esto, el Build Context está correcto.

### Si Ves Error "/api-gateway: not found"

El Build Context **NO está en `.`**. Verifica:
1. Click en "Edit" del Build Context
2. **Borra TODO** el contenido
3. Escribe **solo** un punto: `.`
4. Guarda
5. Redeploy

---

## 🆘 Última Opción: Usar Maven

Si Docker sigue fallando, **cambia a Maven**:

1. **Settings → Build & Deploy**
   ```
   Environment: Maven (o "Other")
   Root Directory: api-gateway
   Build Command: mvn clean package -DskipTests
   Start Command: java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
   ```

2. **Variables de entorno** (igual que antes)

Maven es más simple y Render lo detecta mejor.

---

## 📞 Contactar Soporte de Render

Si nada funciona:
1. Toma capturas de pantalla de tu configuración
2. Copia los logs completos del build
3. Contacta soporte de Render explicando:
   - El error exacto
   - Tu configuración
   - Que el Build Context está en `.`

---

## ✅ Checklist Final

- [ ] Build Context Directory = `.` (exactamente un punto)
- [ ] Dockerfile Path = `api-gateway/Dockerfile`
- [ ] Todos los comandos están vacíos
- [ ] Variables de entorno configuradas
- [ ] Servicio recreado (si es necesario)
- [ ] Logs revisados

