# 🚀 Cómo Ejecutar los Microservicios

## ⚠️ Requisitos Previos

1. **Java 17+ instalado** - Verifica con: `java -version`
2. **Maven instalado** - Verifica con: `mvn -version`
3. **Laragon con MySQL corriendo**
4. **Bases de datos creadas** (ejecuta `setup-databases.sql` en phpMyAdmin)

## 🔧 Si Maven no está en el PATH

### Opción 1: Agregar Maven al PATH

1. Encuentra dónde está instalado Maven (normalmente en `C:\Program Files\Apache\maven` o similar)
2. Agrega la carpeta `bin` de Maven al PATH del sistema
3. Reinicia la terminal/CMD

### Opción 2: Usar la ruta completa de Maven

Si Maven está instalado pero no en el PATH, puedes usar la ruta completa:

```bash
"C:\ruta\a\maven\bin\mvn.cmd" clean install
```

### Opción 3: Usar Maven Wrapper (si está disponible)

Algunos proyectos incluyen `mvnw` (Maven Wrapper):

```bash
.\mvnw clean install
```

## 📋 Pasos para Ejecutar los Servicios

### Método 1: Ejecutar todos los servicios (Recomendado)

1. **Abre 4 ventanas de CMD/PowerShell** (una para cada servicio)

2. **En cada ventana, ejecuta:**

**Ventana 1 - Auth Service:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo\auth-service
mvn clean install
mvn spring-boot:run
```

**Ventana 2 - Game Catalog Service:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo\game-catalog-service
mvn clean install
mvn spring-boot:run
```

**Ventana 3 - Order Service:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo\order-service
mvn clean install
mvn spring-boot:run
```

**Ventana 4 - Library Service:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo\library-service
mvn clean install
mvn spring-boot:run
```

### Método 2: Ejecutar desde la raíz del proyecto

**Compilar todo:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
mvn clean install
```

**Luego ejecutar cada servicio en ventanas separadas:**

**Ventana 1:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
mvn spring-boot:run -pl auth-service
```

**Ventana 2:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
mvn spring-boot:run -pl game-catalog-service
```

**Ventana 3:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
mvn spring-boot:run -pl order-service
```

**Ventana 4:**
```bash
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
mvn spring-boot:run -pl library-service
```

### Método 3: Usar un IDE (IntelliJ IDEA, Eclipse, VS Code)

1. Abre el proyecto en tu IDE
2. Importa como proyecto Maven
3. Ejecuta cada `*Application.java`:
   - `AuthServiceApplication.java`
   - `GameCatalogServiceApplication.java`
   - `OrderServiceApplication.java`
   - `LibraryServiceApplication.java`

## ✅ Verificar que los Servicios Están Corriendo

Abre en tu navegador:

- **Auth Service:** http://localhost:3001/swagger-ui.html
- **Game Catalog Service:** http://localhost:3002/swagger-ui.html
- **Order Service:** http://localhost:3003/swagger-ui.html
- **Library Service:** http://localhost:3004/swagger-ui.html

Si ves la documentación de Swagger, ¡los servicios están funcionando! 🎉

## 🐛 Solución de Problemas

### Error: "mvn no se reconoce"
- Instala Maven o agrégalo al PATH
- O usa la ruta completa de mvn.cmd

### Error: "Puerto ya en uso"
- Cierra otros servicios que usen esos puertos
- O cambia el puerto en `application.properties`

### Error: "No se puede conectar a MySQL"
- Verifica que Laragon esté corriendo
- Verifica que MySQL esté activo
- Verifica las credenciales en `application.properties`

### Error: "Base de datos no existe"
- Ejecuta `setup-databases.sql` en phpMyAdmin

## 📝 Notas

- La primera vez que ejecutes, Maven descargará todas las dependencias (puede tardar varios minutos)
- Cada servicio debe correr en su propia ventana/terminal
- Los servicios se comunican entre sí, así que es mejor tenerlos todos corriendo

