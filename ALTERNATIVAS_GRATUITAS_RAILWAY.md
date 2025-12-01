# 🆓 Alternativas Gratuitas a Railway para Microservicios

## 📊 Comparación Rápida

| Plataforma | Plan Gratuito | Facilidad | Mejor Para |
|------------|--------------|-----------|------------|
| **Render** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Microservicios individuales |
| **Fly.io** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Contenedores Docker |
| **Oracle Cloud (OCI)** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | Recursos generosos |
| **Google Cloud Run** | ⭐⭐⭐⭐ | ⭐⭐⭐ | Pay-per-use |
| **Koyeb** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Similar a Railway |
| **Render.com** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Muy fácil |

---

## 1. 🎨 Render (Recomendado - Más Fácil)

### Ventajas
- ✅ **750 horas gratis/mes** (suficiente para 1 servicio 24/7)
- ✅ Interfaz muy similar a Railway
- ✅ Auto-deploy desde GitHub
- ✅ SSL automático
- ✅ Muy fácil de configurar

### Límites del Plan Gratuito
- ⚠️ Servicios se duermen después de 15 min de inactividad
- ⚠️ Despiertan automáticamente en la primera petición (puede tardar ~30 seg)
- ⚠️ 1 servicio web gratis

### Configuración para API Gateway

1. **Crear cuenta en [render.com](https://render.com)**

2. **Nuevo Web Service:**
   - **Name**: `api-gateway`
   - **Environment**: `Docker` o `Maven`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/api-gateway-1.0.0.jar`
   - **Root Directory**: `api-gateway`

3. **Variables de Entorno:**
   ```
   SERVER_PORT=8080
   SPRING_PROFILES_ACTIVE=production
   EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
   ```

4. **Auto-Deploy**: Conecta tu repositorio de GitHub

### Archivo `render.yaml` (Opcional)

Crea `render.yaml` en la raíz del proyecto:

```yaml
services:
  - type: web
    name: api-gateway
    env: docker
    buildCommand: cd api-gateway && mvn clean package -DskipTests
    startCommand: cd api-gateway && java -jar target/api-gateway-1.0.0.jar
    envVars:
      - key: SERVER_PORT
        value: 8080
      - key: SPRING_PROFILES_ACTIVE
        value: production
```

---

## 2. 🚀 Fly.io (Mejor para Contenedores)

### Ventajas
- ✅ **3 VMs compartidas gratis** (256MB RAM cada una)
- ✅ No se duermen
- ✅ Muy rápido
- ✅ Excelente para Docker

### Límites
- ⚠️ 3GB de transferencia/mes
- ⚠️ Recursos limitados (256MB RAM)

### Configuración

1. **Instalar Fly CLI:**
   ```bash
   # Windows (PowerShell)
   powershell -Command "iwr https://fly.io/install.ps1 -useb | iex"
   ```

2. **Login:**
   ```bash
   fly auth login
   ```

3. **Crear `Dockerfile` en `api-gateway/`:**
   ```dockerfile
   FROM maven:3.9-eclipse-temurin-17 AS build
   WORKDIR /app
   COPY pom.xml .
   COPY src ./src
   RUN mvn clean package -DskipTests

   FROM eclipse-temurin:17-jre-alpine
   WORKDIR /app
   COPY --from=build /app/target/api-gateway-1.0.0.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

4. **Inicializar app:**
   ```bash
   cd api-gateway
   fly launch
   ```

5. **Desplegar:**
   ```bash
   fly deploy
   ```

---

## 3. ☁️ Oracle Cloud (OCI) - Siempre Gratis

### Ventajas
- ✅ **Siempre gratis** (no expira)
- ✅ **2 VMs con 1GB RAM cada una**
- ✅ **200GB de almacenamiento**
- ✅ **10TB de transferencia/mes**
- ✅ No se duermen

### Desventajas
- ⚠️ Requiere tarjeta de crédito (no se cobra)
- ⚠️ Configuración más compleja
- ⚠️ Puede tardar en aprobar la cuenta

### Configuración

1. **Crear cuenta en [cloud.oracle.com](https://cloud.oracle.com)**
   - Selecciona "Always Free"

2. **Crear instancia compute:**
   - Shape: VM.Standard.E2.1.Micro (Always Free)
   - OS: Ubuntu 22.04
   - 1GB RAM, 1 OCPU

3. **Conectar por SSH:**
   ```bash
   ssh ubuntu@<IP_PUBLICA>
   ```

4. **Instalar Java y Maven:**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk maven -y
   ```

5. **Clonar repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/tu-repo.git
   cd tu-repo/api-gateway
   ```

6. **Compilar y ejecutar:**
   ```bash
   mvn clean package -DskipTests
   java -jar target/api-gateway-1.0.0.jar
   ```

7. **Usar systemd para que corra siempre:**
   ```bash
   sudo nano /etc/systemd/system/api-gateway.service
   ```
   
   Contenido:
   ```ini
   [Unit]
   Description=API Gateway Service
   After=network.target

   [Service]
   Type=simple
   User=ubuntu
   WorkingDirectory=/home/ubuntu/tu-repo/api-gateway
   ExecStart=/usr/bin/java -jar target/api-gateway-1.0.0.jar
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

   Activar:
   ```bash
   sudo systemctl enable api-gateway
   sudo systemctl start api-gateway
   ```

---

## 4. 🌐 Google Cloud Run (Pay-per-Use)

### Ventajas
- ✅ **2 millones de requests gratis/mes**
- ✅ **400,000 GB-segundos gratis/mes**
- ✅ **200,000 GiB-segundos gratis/mes**
- ✅ Solo pagas por lo que usas
- ✅ Auto-scaling

### Configuración

1. **Instalar Google Cloud SDK:**
   ```bash
   # Windows
   # Descargar desde: https://cloud.google.com/sdk/docs/install
   ```

2. **Login:**
   ```bash
   gcloud auth login
   gcloud config set project TU_PROJECT_ID
   ```

3. **Crear `Dockerfile`** (igual que Fly.io)

4. **Build y deploy:**
   ```bash
   cd api-gateway
   gcloud builds submit --tag gcr.io/TU_PROJECT_ID/api-gateway
   gcloud run deploy api-gateway \
     --image gcr.io/TU_PROJECT_ID/api-gateway \
     --platform managed \
     --region us-central1 \
     --allow-unauthenticated
   ```

---

## 5. 🎯 Koyeb (Similar a Railway)

### Ventajas
- ✅ **Plan gratuito generoso**
- ✅ Muy similar a Railway
- ✅ Auto-deploy desde GitHub
- ✅ No se duermen

### Configuración

1. **Crear cuenta en [koyeb.com](https://koyeb.com)**

2. **Nuevo App:**
   - Source: GitHub
   - Build: Maven
   - Run: `java -jar target/api-gateway-1.0.0.jar`
   - Root: `api-gateway`

---

## 6. 🐳 DigitalOcean App Platform

### Ventajas
- ✅ **$5 crédito gratis/mes** (suficiente para 1 app básica)
- ✅ Muy fácil de usar
- ✅ Auto-deploy

### Configuración

1. **Crear cuenta en [digitalocean.com](https://digitalocean.com)**

2. **Nuevo App:**
   - Source: GitHub
   - Build: Maven
   - Run: `java -jar target/api-gateway-1.0.0.jar`

---

## 📝 Recomendación por Caso de Uso

### Para Desarrollo/Pruebas
**→ Render.com** (más fácil, se duerme pero despierta rápido)

### Para Producción con Presupuesto Cero
**→ Oracle Cloud (OCI)** (siempre gratis, no se duerme)

### Para Máximo Control
**→ Fly.io** (Docker, muy rápido)

### Para Pay-per-Use
**→ Google Cloud Run** (solo pagas por requests)

---

## 🔧 Script de Migración Rápida

### Para Render

Crea `render.yaml` en la raíz:

```yaml
services:
  - type: web
    name: api-gateway
    env: docker
    dockerfilePath: ./api-gateway/Dockerfile
    envVars:
      - key: SERVER_PORT
        value: 8080
      - key: SPRING_PROFILES_ACTIVE
        value: production
```

### Para Fly.io

Crea `api-gateway/Dockerfile`:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🚀 Pasos Rápidos para Migrar

1. **Elige una plataforma** (Recomiendo **Render** para empezar)

2. **Crea cuenta y conecta GitHub**

3. **Configura el servicio:**
   - Root Directory: `api-gateway`
   - Build: `mvn clean package -DskipTests`
   - Start: `java -jar target/api-gateway-1.0.0.jar`

4. **Agrega variables de entorno**

5. **Deploy!**

---

## ⚠️ Notas Importantes

- **Render**: Los servicios se duermen, pero despiertan automáticamente
- **OCI**: Requiere tarjeta pero no se cobra (solo verificación)
- **Fly.io**: Mejor con Docker
- **Cloud Run**: Ideal si tienes poco tráfico

---

## 📚 Recursos

- [Render Docs](https://render.com/docs)
- [Fly.io Docs](https://fly.io/docs)
- [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/)
- [Google Cloud Run](https://cloud.google.com/run)

