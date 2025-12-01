# ⚠️ Problema: API Gateway sin código fuente

## Diagnóstico

El directorio `api-gateway` existe pero **no tiene código fuente**:
- ❌ No hay directorio `src/`
- ❌ No hay `pom.xml` en `api-gateway/`
- ❌ No está listado en los módulos del `pom.xml` padre

## Soluciones

### Opción 1: Verificar si el API Gateway existe en GitHub

El API Gateway puede existir en el repositorio remoto pero no estar sincronizado localmente.

**Solución:**
```bash
git pull origin main
# O si hay cambios locales:
git fetch origin
git checkout origin/main -- api-gateway/
```

### Opción 2: El API Gateway fue eliminado

Si el API Gateway fue eliminado del proyecto, tienes dos opciones:

#### A) Desplegar un microservicio individual en Railway

En lugar de desplegar el API Gateway, puedes desplegar uno de los microservicios directamente:

1. En Railway, selecciona el servicio que quieres desplegar (ej: `game-catalog-service`)
2. Root Directory: `game-catalog-service`
3. Build Command: `mvn clean package -DskipTests`
4. Start Command: `java -jar target/game-catalog-service-*.jar`

#### B) Usar ngrok para desarrollo

Para pruebas rápidas, usa ngrok para exponer tu API Gateway local:

```bash
# En tu máquina local, con el API Gateway corriendo en puerto 8080:
ngrok http 8080
```

Luego usa la URL de ngrok en Vercel.

### Opción 3: Crear el API Gateway desde cero

Si necesitas el API Gateway, puedes crearlo siguiendo la estructura de los otros microservicios.

## Configuración Recomendada para Railway

Si decides desplegar microservicios individuales:

### Para game-catalog-service:

1. **Root Directory**: `game-catalog-service`
2. **Build Command**: `mvn clean package -DskipTests`
3. **Start Command**: 
   ```bash
   JAR_FILE=$(find target -name "game-catalog-service-*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n 1) && java -jar "$JAR_FILE"
   ```
4. **Variables de entorno**:
   - `SERVER_PORT=3002`
   - `SPRING_DATASOURCE_URL=jdbc:postgresql://...` (si usas base de datos)

### Para auth-service:

1. **Root Directory**: `auth-service`
2. **Build Command**: `mvn clean package -DskipTests`
3. **Start Command**: Similar al anterior pero con `auth-service-*.jar`

## Próximos Pasos

1. **Verifica en GitHub** si el `api-gateway` tiene código fuente
2. Si no existe, decide:
   - ¿Desplegar microservicios individuales?
   - ¿Usar ngrok para desarrollo?
   - ¿Crear el API Gateway desde cero?

## Nota Importante

Si el API Gateway no existe, tu aplicación en Vercel necesitará conectarse directamente a los microservicios individuales en lugar de usar un API Gateway centralizado.

