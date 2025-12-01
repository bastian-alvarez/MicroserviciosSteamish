# 🔧 Solución: Error "/api-gateway: not found" en Render

## ❌ Error

```
error: failed to solve: failed to compute cache key: failed to calculate checksum of ref ...
"/api-gateway": not found
```

## 🔍 Causa

El error indica que el **Docker Build Context Directory** está mal configurado en Render. 

Hay dos formas de configurar esto:

### Opción A: Build Context en Raíz (Recomendado)
- Build Context = `.` (punto)
- Dockerfile Path = `api-gateway/Dockerfile`
- Dockerfile actual funciona

### Opción B: Build Context en api-gateway/
- Build Context = `api-gateway/`
- Dockerfile Path = `Dockerfile` (sin ruta)
- Necesita Dockerfile diferente

---

## ✅ Solución: Opción A (Recomendada)

### Configuración en Render

1. **Settings → Build & Deploy**

   ```
   Docker Build Context Directory: . (solo un punto)
   Dockerfile Path: api-gateway/Dockerfile
   ```

2. **Verifica que NO tenga espacios o caracteres extra**

### Verificación

El Build Context debe ser **exactamente**:
```
.
```

NO debe ser:
- `api-gateway/`
- `api-gateway`
- `. ` (punto con espacio)
- ` .` (espacio y punto)

---

## ✅ Solución Alternativa: Opción B

Si prefieres usar Build Context = `api-gateway/`:

### Configuración en Render

1. **Settings → Build & Deploy**

   ```
   Docker Build Context Directory: api-gateway
   Dockerfile Path: Dockerfile
   ```

2. **Usa el Dockerfile standalone**

   He creado `api-gateway/Dockerfile.standalone` para esta configuración.

3. **Renombra o copia**:
   ```bash
   # Opción 1: Renombrar
   mv api-gateway/Dockerfile api-gateway/Dockerfile.multi
   mv api-gateway/Dockerfile.standalone api-gateway/Dockerfile
   
   # Opción 2: Cambiar path en Render
   Dockerfile Path: api-gateway/Dockerfile.standalone
   ```

---

## 🎯 Recomendación: Usar Opción A

**Opción A es mejor** porque:
- ✅ Funciona con proyectos multi-módulo
- ✅ Puede acceder al `pom.xml` padre
- ✅ Más flexible para futuros cambios

**Pasos**:
1. Ve a Render → Settings → Build & Deploy
2. **Docker Build Context Directory**: Borra todo, escribe solo `.` (punto)
3. **Dockerfile Path**: `api-gateway/Dockerfile`
4. Guarda
5. Redeploy

---

## 🔍 Cómo Verificar

### En Render Dashboard

1. Ve a **Settings → Build & Deploy**
2. Verifica que:
   - **Docker Build Context Directory** = `.` (exactamente un punto)
   - **Dockerfile Path** = `api-gateway/Dockerfile`

### En los Logs de Build

Si está bien configurado, verás:
```
Step 1/8 : FROM maven:3.9-eclipse-temurin-17 AS build
Step 2/8 : WORKDIR /app
Step 3/8 : COPY pom.xml ./
Step 4/8 : COPY api-gateway ./api-gateway
```

Si está mal, verás el error "not found".

---

## ⚠️ Errores Comunes

### Error: "/api-gateway: not found"
**Causa**: Build Context no es `.` (raíz)
**Solución**: Cambia Build Context Directory a `.` (punto)

### Error: "pom.xml: not found"
**Causa**: Build Context está en `api-gateway/` pero Dockerfile busca `pom.xml` en raíz
**Solución**: Usa Opción A (Build Context = `.`)

### Error: "Dockerfile not found"
**Causa**: Dockerfile Path incorrecto
**Solución**: Verifica que sea `api-gateway/Dockerfile` (con Build Context = `.`)

---

## 📋 Checklist Final

- [ ] Build Context Directory = `.` (solo punto, sin espacios)
- [ ] Dockerfile Path = `api-gateway/Dockerfile`
- [ ] Docker Command = (vacío)
- [ ] Pre-Deploy Command = (vacío)
- [ ] Variables de entorno configuradas (PORT=8080, etc.)
- [ ] Guardar cambios
- [ ] Redeploy

---

## 🚀 Después de Corregir

1. **Guarda los cambios** en Render
2. **Haz deploy manual** o espera auto-deploy
3. **Revisa logs** - deberías ver el build exitoso
4. **Verifica** que el puerto se detecte

---

## 💡 Tip

Si Render sigue dando errores después de configurar correctamente:
1. **Borra el servicio** y créalo de nuevo
2. O **contacta soporte de Render** con los logs

