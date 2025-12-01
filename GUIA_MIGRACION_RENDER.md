# 🚀 Guía Rápida: Migrar de Railway a Render

## ⚡ Pasos Rápidos (5 minutos)

### 1. Crear Cuenta en Render
- Ve a [render.com](https://render.com)
- Crea cuenta con GitHub
- Conecta tu repositorio

### 2. Crear Nuevo Web Service

1. Click en **"New +"** → **"Web Service"**
2. Conecta tu repositorio de GitHub
3. Configura:
   - **Name**: `api-gateway`
   - **Environment**: `Maven` (o `Docker` si prefieres)
   - **Root Directory**: `api-gateway`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: 
     ```bash
     JAR_FILE=$(find target -name "api-gateway-*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" | head -n 1) && java -jar "$JAR_FILE"
     ```

### 3. Variables de Entorno

Agrega estas variables en **Environment**:

```
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
```

### 4. Deploy!

Click en **"Create Web Service"** y espera el deploy.

---

## 🔧 Opción con Docker (Recomendado)

Si prefieres usar Docker (más confiable):

1. **Build Command**: (dejar vacío, Render detectará Dockerfile)
2. **Start Command**: (dejar vacío)
3. **Dockerfile Path**: `api-gateway/Dockerfile`

Render automáticamente:
- Detectará el Dockerfile
- Hará build
- Ejecutará el contenedor

---

## ⚙️ Configuración Avanzada

### Usar `render.yaml` (Opcional)

Si quieres configurar todo desde código:

1. El archivo `render.yaml` ya está creado en la raíz
2. En Render, al crear el servicio, selecciona **"Apply Render YAML"**
3. Render leerá el archivo y configurará todo automáticamente

### Health Checks

Render automáticamente verifica:
- `GET /` 
- `GET /actuator/health` (si usas Spring Actuator)

Asegúrate de tener Actuator en tu `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Y en `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

---

## 🐛 Troubleshooting

### Error: "No se encuentra el JAR"

**Solución**: Verifica que el Build Command compile correctamente:
```bash
mvn clean package -DskipTests
```

Revisa los logs de build en Render.

### Error: "Puerto no disponible"

**Solución**: Asegúrate de que tu aplicación escuche en el puerto que Render asigna:
```properties
server.port=${PORT:8080}
```

Render asigna el puerto automáticamente via variable `PORT`.

### El servicio se duerme

**Normal**: Los servicios gratuitos se duermen después de 15 min de inactividad.

**Solución**: 
- La primera petición despertará el servicio (puede tardar ~30 seg)
- Para producción, considera el plan de pago ($7/mes)

---

## 📊 Comparación Railway vs Render

| Característica | Railway | Render |
|----------------|---------|--------|
| Plan Gratuito | ❌ Ya no disponible | ✅ 750 horas/mes |
| Auto-deploy | ✅ | ✅ |
| SSL | ✅ | ✅ |
| Se duerme | ❌ | ✅ (15 min inactivo) |
| Facilidad | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔄 Migrar Otros Servicios

Para migrar otros microservicios (auth-service, game-catalog-service, etc.):

1. Repite los pasos para cada servicio
2. Cambia el **Root Directory** al directorio del servicio
3. Ajusta el **Start Command** para buscar el JAR correcto

Ejemplo para `auth-service`:
- **Root Directory**: `auth-service`
- **Start Command**: 
  ```bash
  JAR_FILE=$(find target -name "auth-service-*.jar" -type f ! -name "*-sources.jar" ! -name "*-javadoc.jar" ! -name "*.original" | head -n 1) && java -jar "$JAR_FILE"
  ```

---

## 💡 Tips

1. **Usa Docker**: Más confiable y portable
2. **Health Checks**: Configura Actuator para monitoreo
3. **Logs**: Render muestra logs en tiempo real
4. **Variables de Entorno**: Úsalas para configuración sensible
5. **Custom Domains**: Render permite dominios personalizados gratis

---

## 🆘 Soporte

- [Render Docs](https://render.com/docs)
- [Render Community](https://community.render.com)
- [Render Status](https://status.render.com)

---

## ✅ Checklist de Migración

- [ ] Cuenta creada en Render
- [ ] Repositorio conectado
- [ ] Web Service creado
- [ ] Variables de entorno configuradas
- [ ] Build exitoso
- [ ] Servicio corriendo
- [ ] Health check funcionando
- [ ] URL pública accesible

¡Listo! 🎉

