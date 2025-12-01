# 🐳 Configuración Docker para Render

## ✅ Configuración Actual

Tu configuración está **correcta**:

- **Dockerfile Path**: `api-gateway/Dockerfile` ✅
- **Root Directory**: Debe ser `.` (raíz del repositorio) ✅

---

## 🔍 Verificación

### Root Directory

Asegúrate de que el **Root Directory** esté configurado como:
- `.` (punto) - significa raíz del repositorio
- O **vacío** - Render usa la raíz por defecto

**NO uses** `api-gateway` como Root Directory cuando usas Docker.

---

## 📋 Configuración Completa para Render

### Settings → Build & Deploy

```
Environment: Docker
Dockerfile Path: api-gateway/Dockerfile
Root Directory: . (o vacío)
Build Command: (vacío - Render lo detecta automáticamente)
Start Command: (vacío - Render lo detecta automáticamente)
```

---

## 🚀 Cómo Funciona

1. Render clona tu repositorio completo
2. Lee el Dockerfile desde `api-gateway/Dockerfile`
3. Ejecuta el build desde la **raíz del repositorio** (contexto Docker)
4. El Dockerfile copia los archivos necesarios:
   - `pom.xml` (raíz)
   - `api-gateway/pom.xml`
   - `api-gateway/src`
5. Compila el proyecto
6. Ejecuta el JAR

---

## ⚠️ Errores Comunes

### Error: "Cannot find pom.xml"

**Causa**: Root Directory está en `api-gateway` en lugar de `.`

**Solución**: Cambia Root Directory a `.` (punto) o déjalo vacío

### Error: "Dockerfile not found"

**Causa**: Dockerfile Path incorrecto

**Solución**: Verifica que sea exactamente `api-gateway/Dockerfile`

### Error: "Build failed"

**Causa**: El Dockerfile no puede encontrar los archivos

**Solución**: Asegúrate de que:
- Root Directory = `.`
- Dockerfile Path = `api-gateway/Dockerfile`
- El repositorio tiene la estructura correcta

---

## 🔧 Estructura del Proyecto

Render espera esta estructura:

```
tu-repositorio/
├── pom.xml                    (pom padre)
├── api-gateway/
│   ├── Dockerfile            ← Render busca aquí
│   ├── pom.xml
│   └── src/
└── otros-modulos/
```

---

## ✅ Checklist

- [ ] Environment = **Docker**
- [ ] Dockerfile Path = `api-gateway/Dockerfile`
- [ ] Root Directory = `.` (o vacío)
- [ ] Build Command = (vacío)
- [ ] Start Command = (vacío)
- [ ] Variables de entorno configuradas

---

## 🎯 Próximos Pasos

1. **Verifica Root Directory** = `.`
2. **Guarda los cambios**
3. **Haz deploy manual** (si es necesario)
4. **Revisa los logs** del build

---

## 📊 Logs de Build Exitoso

Deberías ver en los logs:

```
Step 1/10 : FROM maven:3.9-eclipse-temurin-17 AS build
Step 2/10 : WORKDIR /app
Step 3/10 : COPY pom.xml* ./
Step 4/10 : COPY api-gateway/pom.xml* ./api-gateway/
...
[INFO] Building jar: /app/api-gateway/target/api-gateway-1.0.0.jar
...
Step 9/10 : COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar
Step 10/10 : ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🆘 Si el Build Falla

1. **Revisa los logs completos** en Render
2. **Verifica la estructura** del repositorio
3. **Confirma que el Dockerfile** está en `api-gateway/Dockerfile`
4. **Asegúrate de que Root Directory** sea `.`

---

## 💡 Tip

Si Render no detecta automáticamente el Dockerfile, puedes especificar manualmente en el Build Command:

```
docker build -f api-gateway/Dockerfile -t api-gateway .
```

Pero normalmente no es necesario si la configuración está correcta.

