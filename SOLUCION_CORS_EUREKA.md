# 🔧 Solución: Problemas de CORS y Conexión con Microservicios

## ❌ Problemas Identificados

1. **Error CORS**: El API Gateway no tenía configuración de CORS
2. **Error 502 Bad Gateway**: Los microservicios no podían conectarse a Eureka porque usaban `localhost`
3. **Configuración incorrecta**: El API Gateway no estaba configurado para escuchar en todas las interfaces

## ✅ Soluciones Aplicadas

### 1. Configuración de CORS en API Gateway

**Archivo creado**: `api-gateway/src/main/java/com/gamestore/gateway/config/CorsConfig.java`

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Total-Count", "Access-Control-Allow-Origin"
        ));
        corsConfiguration.setAllowCredentials(false);
        corsConfiguration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsWebFilter(source);
    }
}
```

### 2. Actualización de URLs de Eureka

**Cambios en todos los microservicios**:
- `auth-service/src/main/resources/application.properties`
- `game-catalog-service/src/main/resources/application.properties`
- `order-service/src/main/resources/application.properties`
- `library-service/src/main/resources/application.properties`
- `api-gateway/src/main/resources/application.properties`

**Antes**:
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**Después**:
```properties
eureka.client.service-url.defaultZone=${EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE:https://13wfn3bx-8761.brs.devtunnels.ms/eureka/}
```

### 3. Configuración del Servidor

**API Gateway** ahora escucha en todas las interfaces:
```properties
server.port=${PORT:8080}
server.address=0.0.0.0
```

## 🔄 Pasos para Aplicar los Cambios

1. **Reiniciar Eureka Server** (si está corriendo)
2. **Reiniciar todos los microservicios** en este orden:
   - Eureka Server
   - API Gateway
   - Auth Service
   - Game Catalog Service
   - Order Service
   - Library Service

3. **Verificar conexión**:
   - Abrir `https://13wfn3bx-8761.brs.devtunnels.ms` (Eureka Dashboard)
   - Verificar que todos los servicios estén registrados

## 🧪 Pruebas

### Probar CORS desde el navegador:

```javascript
// En la consola del navegador (http://localhost:3000)
fetch('https://13wfn3bx-8080.brs.devtunnels.ms/api/games')
  .then(r => r.json())
  .then(console.log)
  .catch(console.error);
```

### Verificar que el API Gateway responde:

```bash
curl -X OPTIONS https://13wfn3bx-8080.brs.devtunnels.ms/api/games \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

Deberías ver headers como:
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD
```

## ⚠️ Notas Importantes

1. **CORS en Producción**: Actualmente permite todos los orígenes (`*`). En producción, deberías especificar dominios específicos:
   ```java
   corsConfiguration.setAllowedOrigins(Arrays.asList(
       "https://tu-dominio.vercel.app",
       "https://www.tu-dominio.com"
   ));
   ```

2. **Eureka URL**: Asegúrate de que la URL de Eureka sea accesible desde todos los microservicios.

3. **HTTPS**: Dev Tunnels usa HTTPS, por lo que todos los servicios deben usar `https://` en las URLs.

## 🔍 Troubleshooting

### Si sigue apareciendo error CORS:

1. Verifica que el API Gateway esté corriendo
2. Verifica que el CorsConfig esté siendo cargado (revisa logs)
3. Limpia la caché del navegador
4. Verifica que la URL del API Gateway sea correcta

### Si aparece error 502 Bad Gateway:

1. Verifica que Eureka Server esté corriendo y accesible
2. Verifica que los microservicios se hayan registrado en Eureka
3. Revisa los logs del API Gateway para ver errores de conexión
4. Verifica que las rutas en `application.properties` sean correctas

### Si los microservicios no se registran en Eureka:

1. Verifica que la URL de Eureka sea accesible desde cada microservicio
2. Revisa los logs de cada microservicio para ver errores de conexión
3. Verifica que `eureka.client.register-with-eureka=true` esté configurado

