# 🔄 Cambiar de Docker a Maven en Render

## 🎯 Solución Recomendada

Docker está dando problemas con el Build Context. **Cambia a Maven** que es más simple y confiable en Render.

---

## ✅ Pasos para Cambiar a Maven

### 1. En Render Dashboard

Ve a **Settings → Build & Deploy**

### 2. Cambiar Environment

**ANTES (Docker)**:
```
Environment: Docker
Docker Build Context Directory: .
Dockerfile Path: api-gateway/Dockerfile
```

**AHORA (Maven)**:
```
Environment: Maven (o "Other")
Root Directory: api-gateway
Build Command: mvn clean package -DskipTests
Start Command: java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
```

### 3. Configuración Completa

#### Build & Deploy
```
Environment: Maven
Root Directory: api-gateway
Build Command: mvn clean package -DskipTests
Start Command: java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
```

#### Environment Variables
```
SPRING_PROFILES_ACTIVE=production
PORT=8080
SERVER_ADDRESS=0.0.0.0
```

---

## 🔧 Si "Maven" no está disponible

Si Render no muestra "Maven" como opción:

1. Selecciona **"Other"** como Environment
2. Configura manualmente:
   - Root Directory: `api-gateway`
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar`

---

## ⚠️ Problema con pom.xml Parent

El `api-gateway/pom.xml` tiene un `<parent>` que referencia al `pom.xml` padre. Con Root Directory = `api-gateway`, Maven no encontrará el padre.

### Solución: Actualizar Build Command

Usa este Build Command que compila desde la raíz:

```
cd .. && mvn clean package -DskipTests -pl api-gateway -am && cd api-gateway
```

O más simple, si Render permite:

```
mvn clean package -DskipTests -f ../pom.xml -pl api-gateway -am
```

---

## 🎯 Solución Más Simple: Script de Build

Crea un script que Render pueda ejecutar:

### 1. Crear `api-gateway/build.sh`

```bash
#!/bin/bash
# Script de build para Render
cd ..
mvn clean package -DskipTests -pl api-gateway -am
cd api-gateway
```

### 2. En Render

```
Build Command: bash build.sh
```

O directamente:

```
Build Command: cd .. && mvn clean package -DskipTests -pl api-gateway -am && cd api-gateway
Start Command: java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
```

---

## 📋 Configuración Final Recomendada

### Opción 1: Root Directory = . (Raíz)

```
Environment: Maven (o Other)
Root Directory: . (punto)
Build Command: mvn clean package -DskipTests -pl api-gateway -am
Start Command: cd api-gateway && java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
```

### Opción 2: Root Directory = api-gateway (con script)

```
Environment: Maven (o Other)
Root Directory: api-gateway
Build Command: cd .. && mvn clean package -DskipTests -pl api-gateway -am && cd api-gateway
Start Command: java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar
```

---

## ✅ Ventajas de Maven

- ✅ Render lo detecta mejor
- ✅ No hay problemas de Build Context
- ✅ Más simple de configurar
- ✅ Logs más claros
- ✅ Menos propenso a errores

---

## 🚀 Pasos Inmediatos

1. **Ve a Render → Settings → Build & Deploy**
2. **Cambia Environment** de "Docker" a **"Maven"** o **"Other"**
3. **Configura**:
   - Root Directory: `.` (punto)
   - Build Command: `mvn clean package -DskipTests -pl api-gateway -am`
   - Start Command: `cd api-gateway && java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar target/api-gateway-1.0.0.jar`
4. **Guarda**
5. **Redeploy**

---

## 🔍 Verificación

En los logs deberías ver:
```
[INFO] Building api-gateway 1.0.0
[INFO] Building jar: .../api-gateway/target/api-gateway-1.0.0.jar
[INFO] BUILD SUCCESS
...
Started ApiGatewayApplication
```

---

## 💡 Por Qué Funciona Mejor

Maven:
- ✅ Render detecta automáticamente proyectos Maven
- ✅ No necesita configuración de Build Context
- ✅ Compila directamente desde el repositorio
- ✅ Menos capas de abstracción

Docker:
- ❌ Requiere Build Context correcto
- ❌ Más complejo de configurar
- ❌ Más propenso a errores de rutas

