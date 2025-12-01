# 🔧 Solución: Error "api-gateway/src: not found"

## ❌ Error

```
error: failed to solve: failed to compute cache key: failed to calculate checksum of ref ...
"/api-gateway/src": not found
```

## 🔍 Causa

El Dockerfile intenta copiar `api-gateway/src` pero Docker no lo encuentra en el Build Context. Esto puede deberse a:

1. **Build Context incorrecto** en Render
2. **Rutas incorrectas** en el Dockerfile
3. **Archivos no incluidos** en el repositorio

---

## ✅ Solución Aplicada

He actualizado el Dockerfile para copiar todo el directorio `api-gateway/` de una vez, en lugar de copiar archivos individuales.

### Cambio en Dockerfile

**ANTES**:
```dockerfile
COPY pom.xml* ./
COPY api-gateway/pom.xml* ./api-gateway/
COPY api-gateway/src ./api-gateway/src
```

**DESPUÉS**:
```dockerfile
COPY pom.xml ./
COPY api-gateway ./api-gateway
```

Esto copia todo el directorio `api-gateway/` completo, incluyendo:
- `api-gateway/pom.xml`
- `api-gateway/src/`
- Todos los demás archivos

---

## 🔧 Verificar Configuración en Render

### Build Context Directory

Debe estar configurado como:
```
.
```
(Solo un punto - raíz del repositorio)

### Dockerfile Path

Debe estar configurado como:
```
api-gateway/Dockerfile
```

---

## 📋 Pasos para Resolver

1. **El Dockerfile ya está actualizado** ✅

2. **Verifica en Render**:
   - Settings → Build & Deploy
   - Docker Build Context Directory = `.` (punto)
   - Dockerfile Path = `api-gateway/Dockerfile`

3. **Haz commit y push** (si no lo has hecho):
   ```bash
   git add api-gateway/Dockerfile
   git commit -m "Fix: Simplificar COPY en Dockerfile para evitar errores de rutas"
   git push origin main
   ```

4. **Redeploy en Render**:
   - Render detectará el nuevo commit
   - O haz "Despliegue manual"

---

## 🔍 Verificar que Funciona

En los logs de build deberías ver:

```
Step 1/8 : FROM maven:3.9-eclipse-temurin-17 AS build
Step 2/8 : WORKDIR /app
Step 3/8 : COPY pom.xml ./
Step 4/8 : COPY api-gateway ./api-gateway
Step 5/8 : RUN mvn clean package -DskipTests -pl api-gateway -am
...
[INFO] Building jar: /app/api-gateway/target/api-gateway-1.0.0.jar
```

Si ves esto, el build está funcionando correctamente.

---

## ⚠️ Si Persiste el Error

### Verificar que los archivos están en Git

```bash
git ls-files api-gateway/src/
```

Si no aparecen archivos, necesitas agregarlos:
```bash
git add api-gateway/src/
git commit -m "Add api-gateway source files"
git push origin main
```

### Verificar Build Context

Asegúrate de que en Render:
- **Docker Build Context Directory** = `.` (punto)
- **NO** = `api-gateway/` o cualquier otra cosa

### Probar Localmente

Prueba el build localmente para verificar:
```bash
docker build -f api-gateway/Dockerfile -t api-gateway .
```

Si funciona localmente pero no en Render, el problema es la configuración del Build Context en Render.

---

## ✅ Checklist

- [x] Dockerfile actualizado (copia `api-gateway/` completo)
- [ ] Build Context Directory = `.` en Render
- [ ] Dockerfile Path = `api-gateway/Dockerfile` en Render
- [ ] Archivos pusheados a GitHub
- [ ] Redeploy en Render
- [ ] Logs muestran build exitoso

---

## 💡 Por Qué Esta Solución Funciona

Copiar todo el directorio `api-gateway/` es más robusto porque:
- ✅ No depende de rutas específicas
- ✅ Copia todo lo necesario de una vez
- ✅ Funciona independientemente de la estructura exacta
- ✅ Menos propenso a errores de rutas

