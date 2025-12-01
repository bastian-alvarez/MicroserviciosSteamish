# 🔧 Configuración de URLs en Eureka

## ⚠️ PROBLEMA IDENTIFICADO

El API Gateway usa `lb://` (load balancer de Eureka) para descubrir servicios. Esto significa:

1. ✅ **NO necesita** las URLs directas de los Dev Tunnels de los microservicios
2. ❌ **SÍ necesita** que los microservicios estén registrados correctamente en Eureka
3. ⚠️ **PROBLEMA**: Los microservicios pueden estar registrándose con `localhost`, lo que impide que el API Gateway se conecte

## 🔍 Verificación Necesaria

### 1. Eureka Server
- **URL**: `https://13wfn3bx-8761.brs.devtunnels.ms`
- **Estado**: ✅ Debe estar corriendo
- **Accesible desde**: API Gateway y todos los microservicios

### 2. API Gateway
- **URL**: `https://13wfn3bx-8080.brs.devtunnels.ms`
- **Estado**: ✅ Debe estar corriendo
- **Conectado a**: Eureka Server
- **Descubre servicios**: A través de Eureka (no necesita URLs directas)

### 3. Microservicios
Cada microservicio debe:
- ✅ Estar corriendo
- ✅ Conectarse a Eureka
- ✅ Registrarse con información accesible (NO `localhost` si están en diferentes máquinas)

## 🚨 Problema Potencial

### Configuración Actual:
```properties
eureka.instance.hostname=localhost  # ❌ PROBLEMA si están en diferentes máquinas
eureka.instance.prefer-ip-address=true
```

Si los microservicios están en diferentes máquinas o redes:
- Se registran con `localhost:3002`
- El API Gateway intenta conectarse a `localhost:3002`
- ❌ Falla porque `localhost` del API Gateway ≠ `localhost` del microservicio

## ✅ Solución

### Opción 1: Usar IPs accesibles
Si todos están en la misma red, usar IPs reales:
```properties
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=<IP_ACCESIBLE>
```

### Opción 2: Usar URLs públicas de Dev Tunnels
Configurar los microservicios para que se registren con sus URLs públicas:
```properties
eureka.instance.hostname=13wfn3bx-3002.brs.devtunnels.ms
eureka.instance.non-secure-port-enabled=false
eureka.instance.secure-port-enabled=true
eureka.instance.secure-port=443
```

### Opción 3: Verificar que estén en la misma máquina
Si todos los servicios están corriendo en la misma máquina local:
- ✅ `localhost` funcionará
- ✅ No se necesita configuración adicional

## 🧪 Cómo Verificar

### 1. Abrir Eureka Dashboard
`https://13wfn3bx-8761.brs.devtunnels.ms`

### 2. Verificar Servicios Registrados
Clic en cada servicio para ver:
- **Hostname/IP**: ¿Es accesible desde el API Gateway?
- **Status**: ¿Está "UP"?
- **Port**: ¿Es correcto?

### 3. Verificar desde API Gateway
Si el API Gateway está en la misma máquina que los microservicios:
- ✅ `localhost` debería funcionar
- ✅ No se necesita configuración adicional

Si están en diferentes máquinas:
- ❌ `localhost` NO funcionará
- ✅ Necesitas configurar IPs o hostnames accesibles

## 📋 Resumen

**Para que el frontend funcione:**

1. ✅ **Eureka Server** debe estar corriendo y accesible
2. ✅ **API Gateway** debe estar corriendo y accesible
3. ✅ **Microservicios** deben estar:
   - Corriendo
   - Registrados en Eureka
   - Accesibles desde el API Gateway (misma máquina o IPs/hostnames correctos)

**Los Dev Tunnels de los microservicios individuales NO son necesarios para el frontend**, solo para acceso directo/debugging.

