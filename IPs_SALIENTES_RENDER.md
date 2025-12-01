# 🌐 Direcciones IP Salientes de Render

## 📋 IPs Públicas

Las solicitudes de red de tu servicio a internet público provendrán de:

```
74.220.48.0/24
74.220.56.0/24
```

**Nota importante**: Estas IPs son **compartidas** por otros servicios de Render en la misma región. No son exclusivas de tu servicio.

---

## 🎯 Cuándo Necesitas Estas IPs

### 1. Whitelisting en Bases de Datos Externas

Si tu API Gateway se conecta a una base de datos externa (como MongoDB Atlas, AWS RDS, etc.) que requiere whitelisting de IPs:

**Agrega estos rangos** en la configuración de firewall de tu base de datos:
- `74.220.48.0/24`
- `74.220.56.0/24`

### 2. APIs Externas con Restricción de IP

Si necesitas llamar a APIs externas que solo aceptan solicitudes de IPs específicas, estas son las IPs que usarás.

### 3. Servicios Privados

Si tienes servicios privados que solo aceptan conexiones de IPs conocidas.

---

## 🔧 Configuración en Bases de Datos

### MongoDB Atlas

1. Ve a **Network Access** en MongoDB Atlas
2. Click en **"Add IP Address"**
3. Agrega:
   - `74.220.48.0/24`
   - `74.220.56.0/24`
4. O usa **"Allow Access from Anywhere"** para desarrollo: `0.0.0.0/0`

### AWS RDS

1. Ve a tu **Security Group** en AWS
2. Agrega reglas de entrada:
   - Type: PostgreSQL/MySQL (según tu DB)
   - Source: `74.220.48.0/24`
   - Source: `74.220.56.0/24`

### Google Cloud SQL

1. Ve a **Connections** en Cloud SQL
2. Agrega red autorizada:
   - `74.220.48.0/24`
   - `74.220.56.0/24`

### PostgreSQL/MySQL en Otros Proveedores

Agrega estos rangos en la configuración de firewall/whitelist.

---

## ⚠️ Limitaciones

### IPs Compartidas

- ❌ **No son exclusivas** de tu servicio
- ⚠️ Otros servicios de Render usan las mismas IPs
- ✅ Esto es normal y seguro

### Cambios de IP

- ⚠️ Render **puede cambiar** estas IPs en el futuro
- ✅ Si cambian, Render notificará o actualizará la documentación
- 💡 Considera usar **rangos más amplios** si es posible

### Región Específica

- ⚠️ Estas IPs son para la región de **Oregon (US West)**
- ⚠️ Si cambias de región, las IPs serán diferentes
- ✅ Verifica las IPs en Render Dashboard si cambias de región

---

## 🔍 Verificar IP Actual

### Desde tu Aplicación

Puedes verificar la IP saliente desde tu código:

```java
// Java/Spring Boot
@RestController
public class IpController {
    
    @GetMapping("/api/ip")
    public Map<String, String> getIp() {
        try {
            String ip = new java.net.URL("https://api.ipify.org")
                .openConnection()
                .getInputStream()
                .toString();
            return Map.of("outgoingIp", ip);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
```

O desde la terminal de Render (si está disponible):
```bash
curl https://api.ipify.org
```

---

## 📝 Ejemplo de Configuración

### Variables de Entorno en Render

Si necesitas pasar estas IPs a tu aplicación:

```
RENDER_OUTGOING_IP_RANGE_1=74.220.48.0/24
RENDER_OUTGOING_IP_RANGE_2=74.220.56.0/24
```

### application.properties

```properties
# IPs salientes de Render (para referencia)
render.outgoing.ip.range.1=74.220.48.0/24
render.outgoing.ip.range.2=74.220.56.0/24
```

---

## 🚨 Seguridad

### ✅ Buenas Prácticas

- ✅ Usa **rangos de IP** en lugar de IPs individuales
- ✅ Combina con **autenticación** (no confíes solo en IPs)
- ✅ Usa **HTTPS/TLS** para todas las conexiones
- ✅ Revisa logs regularmente

### ❌ Evitar

- ❌ No confíes solo en whitelisting de IPs
- ❌ No uses estas IPs para autenticación única
- ❌ No compartas credenciales solo porque la IP está whitelisted

---

## 🔄 Actualización de IPs

Si Render cambia las IPs:

1. Render actualizará la documentación
2. Recibirás notificación (si está configurada)
3. Actualiza la whitelist en tus servicios externos
4. Verifica que las conexiones sigan funcionando

---

## 📚 Referencias

- [Render Outbound IPs](https://render.com/docs/outbound-ips)
- [Render Regions](https://render.com/docs/regions)

---

## ✅ Checklist

- [ ] IPs documentadas: `74.220.48.0/24` y `74.220.56.0/24`
- [ ] Whitelist configurado en base de datos (si aplica)
- [ ] Firewall configurado en servicios externos (si aplica)
- [ ] Verificada conectividad después de configurar whitelist
- [ ] Documentación actualizada si cambias de región

---

## 💡 Tip

Si no necesitas restricción por IP, puedes usar:
- **0.0.0.0/0** para permitir desde cualquier IP (solo para desarrollo)
- **Autenticación por token/API key** en lugar de IP whitelisting

