#!/bin/bash
# Script para iniciar el API Gateway en Railway
# Busca el JAR generado y lo ejecuta

# Buscar el JAR en el directorio target
JAR_FILE=$(find target -name "api-gateway-*.jar" -type f | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Error: No se encontró el archivo JAR. Compilando..."
    mvn clean package -DskipTests
    JAR_FILE=$(find target -name "api-gateway-*.jar" -type f | head -n 1)
fi

if [ -z "$JAR_FILE" ]; then
    echo "Error: No se pudo encontrar el archivo JAR después de compilar."
    echo "Archivos en target:"
    ls -la target/ || echo "Directorio target no existe"
    exit 1
fi

echo "Iniciando API Gateway con: $JAR_FILE"
java -jar "$JAR_FILE"

