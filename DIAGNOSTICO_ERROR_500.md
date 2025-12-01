# 🔍 Diagnóstico: Error 500 en API Gateway

## ❌ Problema

El API Gateway devuelve error 500 al intentar acceder a `/api/games`:
```
GET https://13wfn3bx-8080.brs.devtunnels.ms/api/games 500 (Internal Server Error)
```

## 🔍 Posibles Causas

### 1. Microservicio no registrado en Eureka

El API Gateway usa `lb://game-catalog-service` (load balancer de Eureka). Si el servicio no está registrado, fallará.

**Solución:**
1. Verificar que Eureka Server esté corriendo: `https://13wfn3bx-8761.brs.devtunnels.ms`
2. Verificar que `game-catalog-service` aparezca en el dashboard de Eureka
3. Si no aparece, reiniciar el microservicio y verificar logs

### 2. Problema de conexión con Eureka

El API Gateway no puede conectarse a Eureka para obtener la lista de servicios.

**Solución:**
1. Verificar que la URL de Eureka sea correcta en `application.properties`:
   ```properties
   eureka.client.service-url.defaultZone=https://13wfn3bx-8761.brs.devtunnels.ms/eureka/
   ```
2. Verificar que Eureka Server esté accesible desde donde corre el API Gateway
3. Revisar logs del API Gateway para errores de conexión a Eureka

### 3. Problema con HTTPS/HTTP

Si Eureka usa HTTPS pero el API Gateway intenta HTTP (o viceversa), puede fallar.

**Solución:**
- Asegurar que todas las URLs usen el mismo protocolo (HTTPS para Dev Tunnels)

### 4. Timeout

El microservicio tarda demasiado en responder.

**Solución:**
- Ya configurado: `spring.cloud.gateway.httpclient.response-timeout=30000`
- Si persiste, aumentar el timeout

## ✅ Pasos para Diagnosticar

### Paso 1: Verificar Eureka Dashboard

1. Abrir: `https://13wfn3bx-8761.brs.devtunnels.ms`
2. Buscar `game-catalog-service` en la lista de servicios registrados
3. Verificar que el estado sea "UP"

### Paso 2: Verificar Logs del API Gateway

Buscar en los logs:
- Errores de conexión a Eureka
- Errores de resolución de servicios
- Timeouts

### Paso 3: Probar Acceso Directo al Microservicio

Probar directamente el microservicio (sin API Gateway):
```
GET https://13wfn3bx-3002.brs.devtunnels.ms/api/games
```

Si funciona directamente pero no a través del API Gateway, el problema es la configuración del Gateway.

### Paso 4: Verificar Configuración de Rutas

En `api-gateway/src/main/resources/application.properties`:
```properties
spring.cloud.gateway.routes[1].id=game-catalog-service
spring.cloud.gateway.routes[1].uri=lb://game-catalog-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/games/**,...
```

Verificar que:
- El `uri` use `lb://` (load balancer)
- El nombre del servicio coincida con `spring.application.name` del microservicio
- Las rutas estén correctamente configuradas

## 🔧 Soluciones Aplicadas

### 1. Configuración de Timeout
```properties
spring.cloud.gateway.httpclient.connect-timeout=10000
spring.cloud.gateway.httpclient.response-timeout=30000
```

### 2. Manejador de Errores Mejorado
- Proporciona mensajes de error más informativos
- Detecta errores comunes (timeout, conexión, etc.)

### 3. Logging Mejorado
```properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.com.netflix.eureka=DEBUG
```

## 🚀 Próximos Pasos

1. **Reiniciar todos los servicios** en este orden:
   - Eureka Server
   - Game Catalog Service
   - API Gateway

2. **Verificar registro en Eureka**:
   - Abrir dashboard de Eureka
   - Confirmar que `game-catalog-service` está registrado

3. **Revisar logs del API Gateway**:
   - Buscar errores de conexión
   - Verificar que puede resolver servicios desde Eureka

4. **Probar nuevamente**:
   - Desde el frontend: `https://13wfn3bx-8080.brs.devtunnels.ms/api/games`
   - Verificar respuesta

## ⚠️ Nota Importante

Si el microservicio no está registrado en Eureka, el API Gateway no podrá enrutar las peticiones. Asegúrate de que:

1. Eureka Server esté corriendo y accesible
2. El microservicio tenga la configuración correcta de Eureka
3. El microservicio se haya registrado exitosamente

## 🔍 Comandos Útiles

### Verificar salud del microservicio:
```bash
curl https://13wfn3bx-3002.brs.devtunnels.ms/actuator/health
```

### Verificar registro en Eureka:
```bash
curl https://13wfn3bx-8761.brs.devtunnels.ms/eureka/apps/GAME-CATALOG-SERVICE
```

### Probar API Gateway directamente:
```bash
curl https://13wfn3bx-8080.brs.devtunnels.ms/api/games
```

