# 📦 Instalación Manual de Maven (Sin Chocolatey)

Como Chocolatey no está funcionando correctamente, vamos a instalar Maven manualmente.

## 🚀 Pasos para Instalar Maven

### Paso 1: Descargar Maven

1. Ve a: **https://maven.apache.org/download.cgi**
2. En la sección **"Files"**, busca **"apache-maven-3.9.6-bin.zip"** (o la versión más reciente)
3. Haz clic para descargar el archivo ZIP

### Paso 2: Extraer Maven

1. Una vez descargado, extrae el ZIP
2. **Renombra la carpeta** de `apache-maven-3.9.6` a simplemente `maven` (más fácil)
3. **Mueve la carpeta** a: `C:\Program Files\Apache\maven`
   - Si no existe la carpeta `Apache`, créala primero

**Estructura final debería ser:**
```
C:\Program Files\Apache\maven\
  ├── bin\
  │   └── mvn.cmd
  ├── conf\
  ├── lib\
  └── ...
```

### Paso 3: Agregar Maven al PATH

#### Método A: Desde PowerShell (Temporal - solo esta sesión)

```powershell
$env:Path += ";C:\Program Files\Apache\maven\bin"
```

Luego verifica:
```powershell
mvn -version
```

#### Método B: Permanente (Recomendado)

1. Presiona `Win + R`
2. Escribe: `sysdm.cpl` y presiona Enter
3. Ve a la pestaña **"Opciones avanzadas"**
4. Haz clic en **"Variables de entorno"**
5. En la sección **"Variables del sistema"**, busca `Path` y haz clic en **"Editar"**
6. Haz clic en **"Nuevo"**
7. Agrega: `C:\Program Files\Apache\maven\bin`
8. Haz clic en **"Aceptar"** en todas las ventanas
9. **Cierra TODAS las ventanas de PowerShell/CMD**
10. Abre una **nueva** ventana de PowerShell

### Paso 4: Verificar Instalación

Abre una **nueva** ventana de PowerShell y ejecuta:

```powershell
mvn -version
```

Deberías ver:
```
Apache Maven 3.9.6
Maven home: C:\Program Files\Apache\maven
Java version: 21.0.9
```

## ✅ Después de Instalar Maven

Una vez que Maven esté instalado, ejecuta los microservicios:

```powershell
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
.\ejecutar-todos-servicios.bat
```

## 🔄 Alternativa: Usar un IDE

Si prefieres no instalar Maven, puedes usar:

- **IntelliJ IDEA Community** (gratis): https://www.jetbrains.com/idea/download/
  - Incluye Maven embebido
  - Solo abre el proyecto y ejecuta

- **Eclipse**: https://www.eclipse.org/downloads/
  - También incluye Maven

## 📝 Nota Importante

Después de agregar Maven al PATH, **debes cerrar y volver a abrir PowerShell** para que los cambios surtan efecto.

