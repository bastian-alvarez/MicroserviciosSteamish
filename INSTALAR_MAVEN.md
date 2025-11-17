# 📦 Instalación Rápida de Maven

## Opción 1: Instalación Manual (Recomendado)

### Paso 1: Descargar Maven

1. Ve a: https://maven.apache.org/download.cgi
2. Descarga: **apache-maven-3.9.x-bin.zip** (la versión más reciente)
3. Extrae el ZIP en una carpeta, por ejemplo:
   - `C:\Program Files\Apache\maven`
   - O `C:\apache-maven`

### Paso 2: Agregar Maven al PATH

#### Método A: Desde PowerShell (Temporal - solo para esta sesión)

```powershell
$env:PATH += ";C:\Program Files\Apache\maven\bin"
```

Reemplaza la ruta con donde extrajiste Maven.

#### Método B: Permanente (Recomendado)

1. Presiona `Win + R`, escribe `sysdm.cpl` y presiona Enter
2. Ve a la pestaña **"Opciones avanzadas"**
3. Haz clic en **"Variables de entorno"**
4. En **"Variables del sistema"**, busca `Path` y haz clic en **"Editar"**
5. Haz clic en **"Nuevo"** y agrega: `C:\Program Files\Apache\maven\bin`
6. Haz clic en **"Aceptar"** en todas las ventanas
7. **Cierra y vuelve a abrir PowerShell/CMD**

### Paso 3: Verificar Instalación

Abre una **nueva** ventana de PowerShell y ejecuta:

```powershell
mvn -version
```

Deberías ver algo como:
```
Apache Maven 3.9.x
Maven home: C:\Program Files\Apache\maven
Java version: 21.0.9
```

## Opción 2: Usar Chocolatey (Si lo tienes instalado)

```powershell
choco install maven
```

## Opción 3: Usar Scoop (Si lo tienes instalado)

```powershell
scoop install maven
```

## Opción 4: Usar un IDE con Maven Embebido

Si tienes **IntelliJ IDEA** o **Eclipse**, estos incluyen Maven embebido:

### IntelliJ IDEA:
1. Abre el proyecto
2. File → Open → Selecciona la carpeta `MS-orden-resena-catalogo`
3. IntelliJ detectará automáticamente el proyecto Maven
4. Ejecuta cada `*Application.java` desde el IDE

### Eclipse:
1. File → Import → Maven → Existing Maven Projects
2. Selecciona la carpeta `MS-orden-resena-catalogo`
3. Ejecuta cada `*Application.java` desde el IDE

## ✅ Después de Instalar Maven

Una vez que Maven esté instalado, puedes ejecutar:

```powershell
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
.\ejecutar-todos-servicios.bat
```

O manualmente:

```powershell
cd auth-service
mvn clean install
mvn spring-boot:run
```

