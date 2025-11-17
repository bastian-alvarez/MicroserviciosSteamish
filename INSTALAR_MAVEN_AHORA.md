# 🚀 Instalar Maven Ahora - Instrucciones

Chocolatey está instalado pero necesita ejecutarse desde tu propia sesión de PowerShell.

## ⚡ Pasos Rápidos

### 1. Abre una NUEVA ventana de PowerShell como Administrador

- Presiona `Win + X`
- Selecciona **"Windows PowerShell (Administrador)"** o **"Terminal (Administrador)"**

### 2. Ejecuta este comando:

```powershell
choco install maven -y
```

### 3. Espera a que termine la instalación

Verás mensajes como:
```
Chocolatey vX.X.X
Installing the following packages:
maven
...
```

### 4. Cierra y vuelve a abrir PowerShell

**IMPORTANTE:** Debes cerrar y abrir una nueva ventana de PowerShell para que Maven esté disponible.

### 5. Verifica la instalación:

```powershell
mvn -version
```

Deberías ver algo como:
```
Apache Maven 3.9.x
Maven home: C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.x
Java version: 21.0.9
```

## ✅ Después de Instalar Maven

Una vez que Maven esté instalado, puedes ejecutar los microservicios:

### Opción A: Usar el script automático

```powershell
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo
.\ejecutar-todos-servicios.bat
```

### Opción B: Ejecutar manualmente

```powershell
cd C:\Users\bsaka\Desktop\MS-orden-resena-catalogo\auth-service
mvn clean install
mvn spring-boot:run
```

## 🔄 Si Chocolatey no funciona

Si `choco` no se reconoce, puedes:

1. **Reiniciar tu computadora** (esto refresca el PATH)
2. **O instalar Maven manualmente:**
   - Descarga: https://maven.apache.org/download.cgi
   - Extrae en: `C:\Program Files\Apache\maven`
   - Agrega `C:\Program Files\Apache\maven\bin` al PATH del sistema

## 📝 Nota

Si después de instalar Maven aún no funciona, cierra **todas** las ventanas de PowerShell y abre una nueva. El PATH se actualiza solo en nuevas sesiones.

