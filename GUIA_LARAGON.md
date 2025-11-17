# 🚀 Guía de Configuración con Laragon

Esta guía te ayudará a conectar los microservicios a MySQL usando Laragon.

## 📋 Prerrequisitos

1. **Laragon instalado y funcionando**
2. **MySQL corriendo en Laragon** (puerto 3306)
3. **Java 17+ instalado**
4. **Maven 3.6+ instalado**

## 🔧 Paso 1: Iniciar Laragon y MySQL

1. Abre **Laragon**
2. Haz clic en **"Start All"** o solo inicia **MySQL**
3. Verifica que MySQL esté corriendo (debería aparecer en verde)

## 📊 Paso 2: Crear las Bases de Datos

Tienes dos opciones para ejecutar el script SQL:

### Opción A: Usando phpMyAdmin (Recomendado)

1. En Laragon, haz clic en **"Database"** o abre `http://localhost/phpmyadmin`
2. Ve a la pestaña **"SQL"**
3. Copia y pega el contenido completo del archivo `setup-databases.sql`
4. Haz clic en **"Ejecutar"** o presiona `Ctrl + Enter`
5. Verifica que se hayan creado las 4 bases de datos:
   - `auth_db`
   - `games_db`
   - `orders_db`
   - `library_db`

### Opción B: Usando MySQL Command Line

1. Abre la terminal de Laragon o CMD
2. Navega a la carpeta del proyecto:
   ```bash
   cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
   ```
3. Ejecuta MySQL (ajusta la ruta si es necesario):
   ```bash
   mysql -u root -p < setup-databases.sql
   ```
   - Si no tienes contraseña, presiona Enter cuando te la pida
   - Si tienes contraseña, ingrésala cuando te la pida

## ⚙️ Paso 3: Verificar Configuración de Conexión

Los archivos `application.properties` ya están configurados para Laragon:

- **Host:** `localhost`
- **Puerto:** `3306`
- **Usuario:** `root`
- **Contraseña:** (vacía por defecto en Laragon)

### Si tu MySQL tiene contraseña:

Edita los archivos `application.properties` de cada servicio y cambia:

```properties
spring.datasource.password=tu_contraseña_aqui
```

Archivos a editar:
- `auth-service/src/main/resources/application.properties`
- `game-catalog-service/src/main/resources/application.properties`
- `order-service/src/main/resources/application.properties`
- `library-service/src/main/resources/application.properties`

## 🗄️ Paso 4: Verificar las Bases de Datos

En phpMyAdmin, verifica que existan las siguientes bases de datos:

1. **auth_db** - Con tablas: `users`, `admins`
2. **games_db** - Con tablas: `categorias`, `generos`, `juegos`
3. **orders_db** - Con tablas: `estados`, `ordenes_compra`, `detalles_orden`
4. **library_db** - Con tabla: `biblioteca`

## 🏃 Paso 5: Compilar y Ejecutar los Microservicios

Tienes **3 opciones** para ejecutar los microservicios:

### Opción A: Script para ejecutar todos los servicios (Recomendado)

Ejecuta el script que inicia todos los servicios en ventanas separadas:

```bash
ejecutar-todos-servicios.bat
```

Este script:
- Compila todos los servicios
- Abre 4 ventanas, una para cada servicio
- Cada servicio corre en su propio puerto

### Opción B: Script interactivo para ejecutar un servicio

Ejecuta el script y selecciona qué servicio ejecutar:

```bash
ejecutar-servicios.bat
```

O el script mejorado:

```bash
ejecutar-servicio.bat
```

### Opción C: Ejecutar manualmente cada servicio

**Auth Service (Puerto 3001)**

```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

**Game Catalog Service (Puerto 3002)**

```bash
cd game-catalog-service
mvn clean install
mvn spring-boot:run
```

**Order Service (Puerto 3003)**

```bash
cd order-service
mvn clean install
mvn spring-boot:run
```

**Library Service (Puerto 3004)**

```bash
cd library-service
mvn clean install
mvn spring-boot:run
```

### Opción D: Compilar todo desde la raíz (con POM padre)

```bash
# Compilar todos los servicios
mvn clean install

# Ejecutar un servicio específico desde la raíz
mvn spring-boot:run -pl auth-service
mvn spring-boot:run -pl game-catalog-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl library-service
```

## ✅ Paso 6: Verificar la Conexión

1. Revisa los logs de cada servicio al iniciar
2. Deberías ver mensajes como:
   ```
   HikariPool-1 - Starting...
   HikariPool-1 - Start completed.
   ```
3. Si hay errores de conexión, verifica:
   - Que MySQL esté corriendo en Laragon
   - Que las bases de datos existan
   - Que el usuario y contraseña sean correctos

## 🔍 Verificar en Swagger

Una vez que los servicios estén corriendo, puedes verificar la conexión probando los endpoints:

- **Auth Service:** http://localhost:3001/swagger-ui.html
- **Game Catalog:** http://localhost:3002/swagger-ui.html
- **Order Service:** http://localhost:3003/swagger-ui.html
- **Library Service:** http://localhost:3004/swagger-ui.html

## 🐛 Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"

**Solución:** Verifica la contraseña de MySQL en Laragon y actualiza los archivos `application.properties`

### Error: "Unknown database 'auth_db'"

**Solución:** Ejecuta el script `setup-databases.sql` nuevamente

### Error: "Connection refused"

**Solución:** 
- Verifica que MySQL esté corriendo en Laragon
- Verifica que el puerto 3306 esté disponible
- Revisa la configuración de firewall

### Error: "Table doesn't exist"

**Solución:** 
- Verifica que el script SQL se ejecutó correctamente
- Revisa que `spring.jpa.hibernate.ddl-auto=update` esté configurado
- Hibernate creará las tablas automáticamente si no existen

## 📝 Notas Importantes

1. **JPA Auto-DDL:** Los servicios están configurados con `spring.jpa.hibernate.ddl-auto=update`, lo que significa que Hibernate creará/actualizará las tablas automáticamente basándose en las entidades Java.

2. **Datos Iniciales:** El script SQL incluye datos iniciales (categorías, géneros, estados, admins). Si los servicios crean las tablas automáticamente, estos datos no se insertarán. Puedes ejecutar solo los INSERT después de que las tablas se creen.

3. **Puertos:** Asegúrate de que los puertos 3001-3004 estén libres antes de ejecutar los servicios.

## 🎯 Siguiente Paso

Una vez que todo esté funcionando, puedes:
- Probar los endpoints desde Swagger UI
- Importar las APIs a Postman desde `/api-docs`
- Conectar tu aplicación móvil UINavegacion

