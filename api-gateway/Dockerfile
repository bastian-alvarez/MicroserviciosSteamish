# Dockerfile para API Gateway
# Compatible con Render, Fly.io, Google Cloud Run, etc.
# 
# Uso:
# - Desde la raíz del proyecto: docker build -f api-gateway/Dockerfile -t api-gateway .
# - Desde api-gateway/: docker build -t api-gateway .

# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml del proyecto padre (si existe)
COPY pom.xml* ./
# Copiar pom.xml del módulo
COPY api-gateway/pom.xml* ./api-gateway/
# Copiar código fuente del módulo
COPY api-gateway/src ./api-gateway/src

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

# Exponer puerto (Render usa variable PORT, pero exponemos 8080 como fallback)
EXPOSE 8080

# Health check mejorado
HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Ejecutar - asegurar que escucha en 0.0.0.0
ENTRYPOINT ["sh", "-c", "java -Dserver.address=0.0.0.0 -jar app.jar"]

