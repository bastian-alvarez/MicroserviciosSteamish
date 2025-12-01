# 🎯 Solución Definitiva: Error "/api-gateway: not found"

## ❌ Problema Persistente

El error sigue apareciendo incluso después de configurar Build Context = `.`

Esto puede deberse a que Render tiene problemas detectando la estructura del repositorio.

---

## ✅ Solución: Usar Dockerfile Standalone

La solución más confiable es usar el **Dockerfile.standalone** con Build Context en `api-gateway/`.

### Paso 1: Cambiar Configuración en Render

1. **Settings → Build & Deploy**

   ```
   Docker Build Context Directory: api-gateway
   Dockerfile Path: Dockerfile
   ```

2. **Renombrar Dockerfile en el repositorio**

   Necesitamos que el Dockerfile.standalone sea el principal.

### Paso 2: Actualizar el Repositorio

Ejecuta estos comandos:

```bash
# Opción 1: Renombrar (recomendado)
cd api-gateway
mv Dockerfile Dockerfile.multi-module
mv Dockerfile.standalone Dockerfile
cd ..
git add api-gateway/Dockerfile api-gateway/Dockerfile.multi-module
git commit -m "Fix: Usar Dockerfile standalone para Render"
git push origin main
```

O **Opción 2**: Cambiar el path en Render a `api-gateway/Dockerfile.standalone`

---

## 🔧 Configuración Final en Render

### Build & Deploy

```
Environment: Docker
Docker Build Context Directory: api-gateway
Dockerfile Path: Dockerfile
Build Command: (vacío)
Start Command: (vacío)
```

### Environment Variables

```
SPRING_PROFILES_ACTIVE=production
PORT=8080
SERVER_ADDRESS=0.0.0.0
```

---

## 📋 Por Qué Esta Solución Funciona

El Dockerfile.standalone:
- ✅ No necesita el `pom.xml` padre
- ✅ Compila solo el módulo api-gateway
- ✅ Funciona con Build Context = `api-gateway/`
- ✅ Más simple y directo

**Limitación**: No puede usar dependencias del proyecto padre, pero para un API Gateway standalone esto está bien.

---

## 🚀 Pasos Rápidos

1. **En Render**: Cambia Build Context a `api-gateway`
2. **En Render**: Cambia Dockerfile Path a `Dockerfile`
3. **En tu repo**: Renombra los Dockerfiles (comandos arriba)
4. **Commit y push**
5. **Redeploy en Render**

---

## 🔍 Verificación

Después del cambio, en los logs deberías ver:

```
Step 1/8 : FROM maven:3.9-eclipse-temurin-17 AS build
Step 2/8 : WORKDIR /app
Step 3/8 : COPY pom.xml ./
Step 4/8 : COPY src ./src
Step 5/8 : RUN mvn clean package -DskipTests
...
[INFO] Building jar: /app/target/api-gateway-1.0.0.jar
```

---

## ⚠️ Nota sobre pom.xml

El Dockerfile.standalone compila solo con el `pom.xml` de `api-gateway/`. Si ese pom.xml tiene dependencias del proyecto padre, necesitarás:

1. **Opción A**: Asegurarte de que el `api-gateway/pom.xml` tenga todas las dependencias necesarias (sin `<parent>`)

2. **Opción B**: Volver a intentar con Build Context = `.` pero verificar que Render esté clonando correctamente

---

## 🎯 Recomendación Final

**Usa el Dockerfile.standalone** porque:
- ✅ Es más simple
- ✅ Funciona de forma más confiable en Render
- ✅ No depende de la estructura multi-módulo
- ✅ Menos propenso a errores de rutas

