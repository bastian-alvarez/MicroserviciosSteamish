# Guia de Configuracion Completa - Steamish

Este documento explica paso a paso como configurar y ejecutar todo el proyecto Steamish, desde la creacion de las bases de datos hasta la ejecucion de la aplicacion React.

## Requisitos Previos

- Laragon instalado y funcionando
- Java JDK 19 o superior
- Node.js 18 o superior
- Maven instalado
- Git instalado

## Paso 1: Configuracion de Bases de Datos en Laragon

### 1.1 Iniciar Laragon

1. Abre Laragon
2. Asegurate de que MySQL este iniciado (debe aparecer en verde)
3. Abre phpMyAdmin desde el menu de Laragon o accede a http://localhost/phpmyadmin

### 1.2 Crear las Bases de Datos

1. Abre el archivo setup-databases.sql ubicado en MS-orden-resena-catalogo/setup-databases.sql
2. Copia todo el contenido del archivo
3. En phpMyAdmin, ve a la pestaña SQL
4. Pega el contenido completo del script
5. Haz clic en Ejecutar

Este script creara las siguientes bases de datos:

- auth_db: Base de datos para autenticacion y usuarios
- games_db: Base de datos para el catalogo de juegos
- orders_db: Base de datos para ordenes de compra
- library_db: Base de datos para la biblioteca de usuarios

El script tambien inserta datos de prueba:
- Usuario administrador: admin@steamish.com / Admin123!
- Usuario moderador: moderador@steamish.com / password123
- Usuario de prueba: usuario@test.com / password123
- 10 juegos de ejemplo
- Categorias y generos iniciales

### 1.3 Verificar las Bases de Datos

En phpMyAdmin, verifica que se hayan creado las 4 bases de datos:
- auth_db
- games_db
- orders_db
- library_db

## Paso 2: Configuracion de Microservicios

### 2.1 Ubicacion de los Microservicios

Los microservicios se encuentran en la carpeta MS-orden-resena-catalogo. Cada microservicio es un proyecto Spring Boot independiente:

- auth-service (Puerto 3001)
- game-catalog-service (Puerto 3002)
- order-service (Puerto 3003)
- library-service (Puerto 3004)

### 2.2 Configuracion de Base de Datos en Microservicios

Cada microservicio tiene un archivo application.properties que debe configurarse. Por defecto, estan configurados para:

- Host: localhost
- Puerto: 3306
- Usuario: root
- Contrasena: (vacio, ajustar si tu MySQL tiene contrasena)

Si necesitas cambiar la contrasena de MySQL, edita los archivos:
- MS-orden-resena-catalogo/auth-service/src/main/resources/application.properties
- MS-orden-resena-catalogo/game-catalog-service/src/main/resources/application.properties
- MS-orden-resena-catalogo/order-service/src/main/resources/application.properties
- MS-orden-resena-catalogo/library-service/src/main/resources/application.properties

Busca la linea:
spring.datasource.password=

Y cambiala por tu contrasena de MySQL.

### 2.3 Compilar los Microservicios

Abre una terminal en la carpeta MS-orden-resena-catalogo y ejecuta:

```
mvn clean install
```

Este comando compilara todos los microservicios. Asegurate de tener conexion a internet ya que Maven descargara las dependencias necesarias.

### 2.4 Ejecutar los Microservicios

Debes ejecutar cada microservicio en una terminal separada. Abre 4 terminales diferentes y ejecuta en cada una:

Terminal 1 - Auth Service:
```
cd MS-orden-resena-catalogo/auth-service
mvn spring-boot:run
```

Terminal 2 - Game Catalog Service:
```
cd MS-orden-resena-catalogo/game-catalog-service
mvn spring-boot:run
```

Terminal 3 - Order Service:
```
cd MS-orden-resena-catalogo/order-service
mvn spring-boot:run
```

Terminal 4 - Library Service:
```
cd MS-orden-resena-catalogo/library-service
mvn spring-boot:run
```

Espera a que cada microservicio inicie completamente. Veras mensajes como "Started AuthServiceApplication" cuando cada uno este listo.

### 2.5 Verificar que los Microservicios Estan Funcionando

Abre tu navegador y verifica que cada microservicio responda:

- Auth Service: http://localhost:3001/actuator/health
- Game Catalog Service: http://localhost:3002/actuator/health
- Order Service: http://localhost:3003/actuator/health
- Library Service: http://localhost:3004/actuator/health

Cada uno debe responder con un JSON que indique "status":"UP"

## Paso 3: Configuracion de la Aplicacion React

### 3.1 Instalar Dependencias

Abre una terminal en la carpeta steamish-react-app y ejecuta:

```
npm install
```

Si tienes problemas con las dependencias, intenta:

```
npm install --legacy-peer-deps
```

### 3.2 Configuracion de Variables de Entorno

La aplicacion React esta configurada para conectarse a los microservicios en localhost por defecto. Si necesitas cambiar las URLs, puedes crear un archivo .env en la carpeta steamish-react-app con:

```
REACT_APP_AUTH_SERVICE_URL=http://localhost:3001
REACT_APP_GAME_CATALOG_SERVICE_URL=http://localhost:3002
REACT_APP_ORDER_SERVICE_URL=http://localhost:3003
REACT_APP_LIBRARY_SERVICE_URL=http://localhost:3004
```

### 3.3 Ejecutar la Aplicacion React

En la terminal dentro de steamish-react-app, ejecuta:

```
npm start
```

La aplicacion se abrira automaticamente en http://localhost:3000

## Paso 4: Verificacion Final

### 4.1 Verificar que Todo Funciona

1. Abre http://localhost:3000 en tu navegador
2. Deberias ver la pagina principal de Steamish
3. Intenta registrarte o iniciar sesion con:
   - Email: admin@steamish.com
   - Contrasena: Admin123!
4. Deberias poder navegar por los productos y ver el catalogo de juegos

### 4.2 Verificar el Panel de Administracion

1. Inicia sesion con el usuario administrador (admin@steamish.com)
2. Haz clic en tu nombre de usuario en la parte superior derecha
3. Deberias ver la opcion "Panel Admin"
4. Al hacer clic, deberias acceder al panel de administracion donde puedes gestionar usuarios, juegos y ordenes

## Paso 5: Despliegue en Vercel

### 5.1 Preparacion para Produccion

Antes de desplegar en Vercel, asegurate de que:

1. Todos los cambios estan subidos a GitHub
2. Los microservicios estan desplegados y accesibles publicamente (puedes usar Dev Tunnels o un servicio de hosting)
3. Las variables de entorno estan configuradas en Vercel

### 5.2 Configuracion en Vercel

1. Ve a https://vercel.com y inicia sesion con tu cuenta de GitHub
2. Haz clic en "New Project"
3. Selecciona el repositorio steamish-react-app
4. Vercel detectara automaticamente que es un proyecto React
5. En la seccion de Environment Variables, agrega las siguientes variables:

```
REACT_APP_AUTH_SERVICE_URL=https://tu-url-auth-service
REACT_APP_GAME_CATALOG_SERVICE_URL=https://tu-url-game-catalog-service
REACT_APP_ORDER_SERVICE_URL=https://tu-url-order-service
REACT_APP_LIBRARY_SERVICE_URL=https://tu-url-library-service
```

Reemplaza las URLs con las URLs publicas de tus microservicios.

6. Haz clic en "Deploy"

### 5.3 Link de Vercel

Una vez desplegado, Vercel te proporcionara un link. Para encontrar el link de tu aplicacion:

1. Ve al dashboard de Vercel: https://vercel.com/dashboard
2. Selecciona tu proyecto steamish-react-app
3. En la seccion "Deployments", encontraras el link de tu aplicacion
4. El link tendra el formato: https://steamish-react-app.vercel.app o https://steamish-react-app-[hash].vercel.app

Tambien puedes verificar el link en la configuracion del repositorio de GitHub si esta conectado con Vercel.

Link de produccion (ejemplo):
https://steamish-react-app.vercel.app

Nota: Reemplaza este link con el link real de tu despliegue en Vercel.

## Solucion de Problemas

### Los microservicios no inician

- Verifica que MySQL este corriendo en Laragon
- Verifica que los puertos 3001, 3002, 3003 y 3004 no esten siendo usados por otros programas
- Revisa los logs de cada microservicio para ver errores especificos

### La aplicacion React no se conecta a los microservicios

- Verifica que todos los microservicios esten corriendo
- Verifica las URLs en src/config/constants.ts
- Abre la consola del navegador (F12) para ver errores de conexion

### Error de CORS

- Los microservicios ya tienen configuracion CORS que permite todas las origenes
- Si aun tienes problemas, verifica los archivos CorsConfig.java en cada microservicio

### Error al crear ordenes

- Verifica que el usuario este autenticado
- Verifica que el order-service este corriendo
- Verifica que la base de datos orders_db tenga la tabla estados con los datos iniciales

## Credenciales de Prueba

Administrador:
- Email: admin@steamish.com
- Contrasena: Admin123!

Moderador:
- Email: moderador@steamish.com
- Contrasena: password123

Usuario Regular:
- Email: usuario@test.com
- Contrasena: password123

## Estructura del Proyecto

```
steamish_react_typescript/
├── steamish-react-app/          # Aplicacion React frontend
│   ├── src/
│   │   ├── components/          # Componentes React
│   │   ├── pages/              # Paginas de la aplicacion
│   │   ├── services/           # Servicios para comunicacion con backend
│   │   ├── context/            # Contextos de React
│   │   └── config/             # Configuracion
│   └── package.json
│
└── MS-orden-resena-catalogo/    # Microservicios Spring Boot
    ├── auth-service/            # Puerto 3001
    ├── game-catalog-service/    # Puerto 3002
    ├── order-service/           # Puerto 3003
    ├── library-service/         # Puerto 3004
    └── setup-databases.sql      # Script de creacion de bases de datos
```

## Notas Importantes

- Los microservicios deben estar corriendo antes de usar la aplicacion React
- Las bases de datos deben estar creadas antes de iniciar los microservicios
- El usuario administrador se crea automaticamente al ejecutar el script SQL
- Todos los microservicios usan el mismo JWT secret para validar tokens
- En produccion, cambia el JWT secret en todos los microservicios

## Soporte

Si encuentras problemas, revisa los logs de cada microservicio y la consola del navegador para identificar el error especifico.

