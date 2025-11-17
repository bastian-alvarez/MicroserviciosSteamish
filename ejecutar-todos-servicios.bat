@echo off
echo ========================================
echo   Ejecutando Todos los Microservicios
echo ========================================
echo.
echo Este script ejecutara los 4 microservicios en ventanas separadas
echo.
echo IMPORTANTE: Asegurate de que:
echo   1. Laragon este corriendo con MySQL activo
echo   2. Las bases de datos esten creadas
echo   3. Java 17+ y Maven esten instalados
echo.
pause

echo.
echo Compilando todos los servicios...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] La compilacion fallo. Revisa los errores arriba.
    pause
    exit /b 1
)

echo.
echo [OK] Compilacion exitosa!
echo.
echo Iniciando microservicios en ventanas separadas...
echo.

REM Iniciar Auth Service en nueva ventana
start "Auth Service (3001)" cmd /k "cd auth-service && mvn spring-boot:run"

REM Esperar un poco antes de iniciar el siguiente
timeout /t 3 /nobreak >nul

REM Iniciar Game Catalog Service en nueva ventana
start "Game Catalog Service (3002)" cmd /k "cd game-catalog-service && mvn spring-boot:run"

timeout /t 3 /nobreak >nul

REM Iniciar Order Service en nueva ventana
start "Order Service (3003)" cmd /k "cd order-service && mvn spring-boot:run"

timeout /t 3 /nobreak >nul

REM Iniciar Library Service en nueva ventana
start "Library Service (3004)" cmd /k "cd library-service && mvn spring-boot:run"

echo.
echo ========================================
echo   Servicios iniciados!
echo ========================================
echo.
echo Se han abierto 4 ventanas, una para cada servicio:
echo   - Auth Service: http://localhost:3001
echo   - Game Catalog Service: http://localhost:3002
echo   - Order Service: http://localhost:3003
echo   - Library Service: http://localhost:3004
echo.
echo Swagger UI disponible en:
echo   - http://localhost:3001/swagger-ui.html
echo   - http://localhost:3002/swagger-ui.html
echo   - http://localhost:3003/swagger-ui.html
echo   - http://localhost:3004/swagger-ui.html
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
pause >nul

