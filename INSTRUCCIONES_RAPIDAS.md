# 🚀 Instrucciones Rápidas para Ejecutar Microservicios

## ⚡ Inicio Rápido

### 1. Verificar MySQL
```bash
verificar-conexion.bat
```

### 2. Ejecutar todos los servicios (Recomendado)
```bash
ejecutar-todos-servicios.bat
```

Este script:
- ✅ Compila todos los servicios automáticamente
- ✅ Abre 4 ventanas (una por cada servicio)
- ✅ Cada servicio corre en su puerto correspondiente

### 3. Ejecutar un servicio individual
```bash
ejecutar-servicios.bat
```

Selecciona el número del servicio que quieres ejecutar.

## 📡 URLs de los Servicios

Una vez ejecutados, los servicios estarán disponibles en:

| Servicio | URL Base | Swagger UI |
|----------|----------|------------|
| Auth Service | http://localhost:3001 | http://localhost:3001/swagger-ui.html |
| Game Catalog Service | http://localhost:3002 | http://localhost:3002/swagger-ui.html |
| Order Service | http://localhost:3003 | http://localhost:3003/swagger-ui.html |
| Library Service | http://localhost:3004 | http://localhost:3004/swagger-ui.html |

## 🔍 Verificar que están corriendo

1. Abre cada URL de Swagger UI en tu navegador
2. Deberías ver la documentación interactiva de la API
3. Puedes probar los endpoints directamente desde Swagger

## ⚠️ Solución de Problemas

### Error: "Puerto ya en uso"
- Cierra la ventana del servicio que está usando ese puerto
- O cambia el puerto en `application.properties`

### Error: "No se puede conectar a MySQL"
- Verifica que Laragon esté corriendo
- Verifica que MySQL esté activo (debe aparecer en verde)
- Ejecuta `verificar-conexion.bat`

### Error: "Base de datos no existe"
- Abre phpMyAdmin: http://localhost/phpmyadmin
- Ejecuta el script `setup-databases.sql`

## 🛑 Detener los Servicios

Para detener los servicios:
- Cierra cada ventana de CMD donde está corriendo el servicio
- O presiona `Ctrl+C` en cada ventana

