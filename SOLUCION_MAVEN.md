# 🔧 Solución: Maven no está instalado

## ⚡ Solución Rápida

Maven no está instalado en tu sistema. Tienes **3 opciones**:

### ✅ Opción 1: Instalar Maven (5 minutos)

**Método más rápido usando PowerShell:**

1. Abre PowerShell como **Administrador**
2. Ejecuta este comando para instalar Chocolatey (si no lo tienes):

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

3. Luego instala Maven:

```powershell
choco install maven -y
```

4. **Cierra y vuelve a abrir PowerShell**
5. Verifica: `mvn -version`

**O descarga manualmente:**
- Ve a: https://maven.apache.org/download.cgi
- Descarga: `apache-maven-3.9.x-bin.zip`
- Extrae en: `C:\Program Files\Apache\maven`
- Agrega `C:\Program Files\Apache\maven\bin` al PATH del sistema

### ✅ Opción 2: Usar un IDE (Más fácil)

Si tienes **IntelliJ IDEA**, **Eclipse** o **VS Code con extensión Java**:

#### IntelliJ IDEA:
1. File → Open → Selecciona `C:\Users\bsaka\Desktop\MS-orden-resena-catalogo`
2. IntelliJ detectará automáticamente el proyecto Maven
3. Espera a que descargue las dependencias
4. Ejecuta cada `*Application.java`:
   - `AuthServiceApplication.java` (puerto 3001)
   - `GameCatalogServiceApplication.java` (puerto 3002)
   - `OrderServiceApplication.java` (puerto 3003)
   - `LibraryServiceApplication.java` (puerto 3004)

#### VS Code:
1. Instala la extensión "Extension Pack for Java"
2. Abre la carpeta del proyecto
3. VS Code detectará Maven automáticamente
4. Ejecuta cada `*Application.java`

### ✅ Opción 3: Usar Maven Wrapper (Si está disponible)

Algunos proyectos incluyen `mvnw` (Maven Wrapper) que no requiere instalación:

```powershell
cd auth-service
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

## 🎯 Recomendación

**Para desarrollo:** Usa **IntelliJ IDEA** (Community Edition es gratis)
- Incluye Maven embebido
- Fácil de usar
- Debug integrado
- Descarga: https://www.jetbrains.com/idea/download/

**Para producción/CI:** Instala Maven en el sistema

## 📝 Después de Instalar Maven

Una vez que Maven esté disponible, ejecuta:

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

