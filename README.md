# Guia de Configuracion - Steamish

## Requisitos Previos

- Laragon con MySQL funcionando
- Java JDK 19+
- Node.js 18+
- Maven instalado

## Paso 1: Bases de Datos

1. Abre Laragon y asegurate de que MySQL este iniciado
2. Abre phpMyAdmin (http://localhost/phpmyadmin)
3. Copia y ejecuta el contenido de `MS-orden-resena-catalogo/setup-databases.sql` en la pestaña SQL

Esto crea 4 bases de datos: auth_db, games_db, orders_db, library_db e inserta datos de prueba.

## Paso 2: Microservicios

### Compilar

```bash
cd MS-orden-resena-catalogo
mvn clean install
```

### Configurar Base de Datos (si es necesario)

Si tu MySQL tiene contrasena, edita `spring.datasource.password=` en:
- auth-service/src/main/resources/application.properties
- game-catalog-service/src/main/resources/application.properties
- order-service/src/main/resources/application.properties
- library-service/src/main/resources/application.properties

### Ejecutar

Abre 4 terminales y ejecuta en cada una:

```bash
# Terminal 1
cd MS-orden-resena-catalogo/auth-service
mvn spring-boot:run

# Terminal 2
cd MS-orden-resena-catalogo/game-catalog-service
mvn spring-boot:run

# Terminal 3
cd MS-orden-resena-catalogo/order-service
mvn spring-boot:run

# Terminal 4
cd MS-orden-resena-catalogo/library-service
mvn spring-boot:run
```

Verifica que esten funcionando:
- http://localhost:3001/actuator/health
- http://localhost:3002/actuator/health
- http://localhost:3003/actuator/health
- http://localhost:3004/actuator/health

## Paso 3: Aplicacion React

```bash
cd steamish-react-app
npm install
npm start
```

La aplicacion se conecta automaticamente a localhost (puertos 3001-3004) en desarrollo. NO usa Dev Tunnels a menos que configures `REACT_APP_USE_DEV_TUNNELS=true`.

Se abre en http://localhost:3000

## Verificacion

1. Abre http://localhost:3000
2. Inicia sesion con: admin@steamish.com / Admin123!
3. Verifica que puedas ver productos y acceder al Panel Admin

## Despliegue en Vercel

1. Ve a https://vercel.com y conecta tu repositorio
2. Configura las variables de entorno:
   - REACT_APP_AUTH_SERVICE_URL=https://tu-url-auth-service
   - REACT_APP_GAME_CATALOG_SERVICE_URL=https://tu-url-game-catalog-service
   - REACT_APP_ORDER_SERVICE_URL=https://tu-url-order-service
   - REACT_APP_LIBRARY_SERVICE_URL=https://tu-url-library-service
3. Haz clic en Deploy

El link de produccion estara en el dashboard de Vercel.

## Credenciales de Prueba

- Admin: admin@steamish.com / Admin123!
- Moderador: moderador@steamish.com / password123
- Usuario: usuario@test.com / password123

## Solucion de Problemas

- Microservicios no inician: Verifica MySQL y que los puertos 3001-3004 esten libres
- No se conecta: Verifica que todos los microservicios esten corriendo y revisa la consola del navegador (F12)
- Error CORS: Los microservicios ya tienen CORS configurado, verifica CorsConfig.java si persiste

## Notas

- Los microservicios deben estar corriendo antes de usar la app React
- En produccion, cambia el JWT secret en todos los microservicios
- La app usa localhost por defecto en desarrollo, no requiere configuracion adicional
