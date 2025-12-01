#!/bin/bash
# Script para iniciar el API Gateway en Railway
# Busca el JAR generado y lo ejecuta

set -e  # Salir si hay algún error

echo "=== Iniciando API Gateway ==="
echo "Directorio actual: $(pwd)"
echo "Contenido del directorio:"
ls -la

# Buscar el JAR en el directorio target (múltiples patrones posibles)
echo "Buscando archivo JAR..."
JAR_FILE=$(find target -name "*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "No se encontró JAR. Listando archivos en target:"
    if [ -d "target" ]; then
        ls -la target/
        echo "Buscando JARs con diferentes patrones..."
        JAR_FILE=$(find target -name "*.jar" -type f ! -name "*.original" 2>/dev/null | head -n 1)
    else
        echo "Directorio target no existe. Compilando..."
        mvn clean package -DskipTests
        JAR_FILE=$(find target -name "*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" 2>/dev/null | head -n 1)
    fi
fi

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: No se pudo encontrar el archivo JAR."
    echo "Intentando compilar nuevamente..."
    mvn clean package -DskipTests
    echo "Archivos después de compilar:"
    ls -la target/ 2>/dev/null || echo "target/ no existe"
    JAR_FILE=$(find target -name "*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" 2>/dev/null | head -n 1)
fi

if [ -z "$JAR_FILE" ]; then
    echo "ERROR CRÍTICO: No se puede encontrar el JAR después de múltiples intentos."
    echo "Verificando estructura del proyecto..."
    find . -name "pom.xml" -type f 2>/dev/null
    find . -name "*.jar" -type f 2>/dev/null
    exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: El archivo JAR no existe: $JAR_FILE"
    exit 1
fi

echo "✓ JAR encontrado: $JAR_FILE"
echo "Tamaño del JAR: $(ls -lh "$JAR_FILE" | awk '{print $5}')"
echo "Iniciando API Gateway..."
exec java -jar "$JAR_FILE"

