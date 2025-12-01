# Dockerfile para API Gateway
# Compatible con Render, Fly.io, Google Cloud Run, etc.
# 
# IMPORTANTE: Este Dockerfile requiere Build Context = . (raíz del repositorio)
# 
# Configuración en Render:
# - Docker Build Context Directory: . (punto)
# - Dockerfile Path: api-gateway/Dockerfile
#
# Uso:
# - Desde la raíz del proyecto: docker build -f api-gateway/Dockerfile -t api-gateway .

# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml del proyecto padre primero
COPY pom.xml ./

# Copiar todo el directorio api-gateway (incluye pom.xml y src)
COPY api-gateway ./api-gateway

# Compilar desde la raíz del proyecto multi-módulo
RUN mvn clean package -DskipTests -pl api-gateway -am || \
    (cd api-gateway && mvn clean package -DskipTests)

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar curl para health checks
RUN apk add --no-cache curl

# Copiar JAR compilado (buscar en ambas ubicaciones posibles)
COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar

# Variables de entorno para Render
ENV PORT=8080
ENV SERVER_ADDRESS=0.0.0.0

# Exponer puerto
EXPOSE 8080

# Health check mejorado (más tiempo para iniciar)
HEALTHCHECK --interval=10s --timeout=5s --start-period=90s --retries=5 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

# Ejecutar con puerto y dirección explícitos
ENTRYPOINT ["sh", "-c", "java -Dserver.address=${SERVER_ADDRESS} -Dserver.port=${PORT} -jar app.jar"]

