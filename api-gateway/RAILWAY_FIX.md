# 🔧 Solución para Error "Unable to access jarfile" en Railway

## Problema
Railway está intentando ejecutar `java -jar target/*jar` pero no puede encontrar el archivo JAR.

## Solución Implementada

Se han creado los siguientes archivos de configuración:

1. **`start.sh`** - Script que busca y ejecuta el JAR correctamente
2. **`nixpacks.toml`** - Configuración de Nixpacks para Railway
3. **`railway.json`** - Configuración de Railway
4. **`Procfile`** - Archivo de proceso para Railway

## Configuración en Railway

### Opción 1: Usar el script (Recomendado)

Railway debería detectar automáticamente el `Procfile` y usar el script `start.sh`.

### Opción 2: Configuración Manual

Si Railway no detecta automáticamente, configura manualmente:

1. Ve a tu servicio en Railway
2. Settings > Deploy
3. **Build Command**: `mvn clean package -DskipTests`
4. **Start Command**: `chmod +x start.sh && ./start.sh`

### Opción 3: Comando Directo (Alternativa)

Si el script no funciona, puedes usar:

**Start Command**: 
```bash
JAR_FILE=$(find target -name "api-gateway-*.jar" -type f | head -n 1) && java -jar "$JAR_FILE"
```

## Verificar el Nombre del JAR

Si el error persiste, verifica el nombre exacto del JAR:

1. En Railway, ve a "Build Logs"
2. Busca la línea que dice algo como:
   ```
   [INFO] Building jar: /app/target/api-gateway-1.0.0.jar
   ```
3. Usa ese nombre exacto en el Start Command:
   ```bash
   java -jar target/api-gateway-1.0.0.jar
   ```

## Solución Alternativa: Verificar pom.xml

Si el api-gateway no tiene su propio `pom.xml`, necesitas:

1. Verificar que el api-gateway esté en los módulos del `pom.xml` padre
2. O crear un `pom.xml` independiente para el api-gateway

## Pasos para Aplicar la Solución

1. Los archivos ya están creados en el repositorio
2. Haz commit y push:
   ```bash
   git add api-gateway/
   git commit -m "Fix: Configuración para Railway - Agregados archivos de configuración para Railway"
   git push origin main
   ```
3. Railway debería detectar los cambios y redeployar automáticamente
4. Si no, haz un redeploy manual en Railway

## Verificar que Funciona

Después del redeploy, verifica:
1. Los logs de Railway deberían mostrar: "Iniciando API Gateway con: target/api-gateway-*.jar"
2. El servicio debería estar "Active" (no "Crashed")
3. La URL del servicio debería responder

