# Eureka Server

Servidor de descubrimiento de servicios Eureka para la arquitectura de microservicios de GameStore.

## 🚀 Descripción

Eureka Server es el componente central que permite que los microservicios se registren y descubran entre sí automáticamente. Esto elimina la necesidad de URLs hardcodeadas y facilita la escalabilidad y el balanceo de carga.

## 📡 Puerto

- **Puerto:** 8761
- **Dashboard:** http://localhost:8761

## 🔧 Configuración

El servidor Eureka está configurado para:
- No registrarse a sí mismo (standalone mode)
- No obtener registros de otros servidores Eureka (standalone mode)
- Auto-preservación deshabilitada para desarrollo
- Intervalo de evicción de 5 segundos

## 🚀 Ejecución

### Desde la raíz del proyecto:
```bash
mvn spring-boot:run -pl eureka-server
```

### Desde el directorio del servicio:
```bash
cd eureka-server
mvn spring-boot:run
```

## 📋 Orden de Inicio

1. **Primero:** Iniciar Eureka Server (puerto 8761)
2. **Segundo:** Iniciar los microservicios (se registrarán automáticamente en Eureka)

## 🔍 Verificar Servicios Registrados

Una vez que los microservicios estén corriendo, puedes verlos en el dashboard de Eureka:
- Abre: http://localhost:8761
- Verás todos los servicios registrados con su estado y metadatos

## 📝 Notas

- Eureka Server debe estar corriendo antes que los microservicios
- Los microservicios se registran automáticamente al iniciar
- Si un servicio se cae, Eureka lo detectará y lo removerá del registro después del intervalo de evicción

