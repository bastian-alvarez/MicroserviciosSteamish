# ✅ Configuración Correcta para Render

## ⚠️ Error Común: Language = "Node"

Si ves que Render detectó **"Node"** como lenguaje, **cámbialo** porque este es un proyecto **Java/Spring Boot**.

---

## 🔧 Configuración Correcta

### Opción 1: Maven (Recomendado para empezar)

1. **Language**: Cambia de "Node" a **"Maven"** o **"Other"**
2. **Root Directory**: `api-gateway`
3. **Build Command**: 
   ```bash
   mvn clean package -DskipTests
   ```
4. **Start Command**: 
   ```bash
   JAR_FILE=$(find target -name "api-gateway-*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" | head -n 1) && java -jar "$JAR_FILE"
   ```

### Opción 2: Docker (Más confiable)

1. **Language**: Selecciona **"Docker"**
2. **Dockerfile Path**: `api-gateway/Dockerfile`
3. **Root Directory**: (dejar vacío o `.`)
4. **Build Command**: (dejar vacío, Render lo detecta automáticamente)
5. **Start Command**: (dejar vacío, Render lo detecta automáticamente)

---

## 📋 Pasos Detallados

### Si ya creaste el servicio con "Node":

1. Ve a **Settings** del servicio
2. Busca la sección **"Build & Deploy"**
3. Cambia **"Environment"** de "Node" a:
   - **"Maven"** (si está disponible)
   - **"Docker"** (recomendado)
   - **"Other"** (si no hay Maven)

### Configuración Manual:

#### Para Maven:

```
Environment: Maven
Root Directory: api-gateway
Build Command: mvn clean package -DskipTests
Start Command: JAR_FILE=$(find target -name "api-gateway-*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" | head -n 1) && java -jar "$JAR_FILE"
```

#### Para Docker:

```
Environment: Docker
Dockerfile Path: api-gateway/Dockerfile
Root Directory: . (o dejar vacío)
Build Command: (vacío)
Start Command: (vacío)
```

---

## 🔍 Verificar Configuración

Después de cambiar, verifica:

1. ✅ **Environment** no es "Node"
2. ✅ **Root Directory** apunta a `api-gateway` (o `.` si usas Docker)
3. ✅ **Build Command** compila con Maven
4. ✅ **Start Command** ejecuta el JAR

---

## 🚀 Variables de Entorno

No olvides agregar estas variables en **Environment**:

```
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
```

---

## ⚡ Quick Fix

Si Render sigue detectando "Node":

1. **Elimina el servicio actual**
2. **Crea uno nuevo** y selecciona manualmente:
   - **"Docker"** como Environment
   - O **"Other"** y configura manualmente

---

## 📝 Nota sobre Root Directory

- **Con Maven**: `api-gateway` (apunta al módulo)
- **Con Docker**: `.` o vacío (desde la raíz del proyecto para acceder al Dockerfile)

---

## ✅ Checklist

- [ ] Language/Environment ≠ "Node"
- [ ] Root Directory configurado correctamente
- [ ] Build Command compila con Maven
- [ ] Start Command ejecuta el JAR
- [ ] Variables de entorno agregadas
- [ ] Deploy exitoso

