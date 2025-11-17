@echo off
echo ========================================
echo   Ejecutar Microservicio Individual
echo ========================================
echo.
echo Selecciona el servicio a ejecutar:
echo.
echo 1. Auth Service (Puerto 3001)
echo 2. Game Catalog Service (Puerto 3002)
echo 3. Order Service (Puerto 3003)
echo 4. Library Service (Puerto 3004)
echo 5. Salir
echo.
set /p opcion="Ingresa el numero (1-5): "

if "%opcion%"=="1" goto auth
if "%opcion%"=="2" goto game
if "%opcion%"=="3" goto order
if "%opcion%"=="4" goto library
if "%opcion%"=="5" goto end
goto invalid

:auth
echo.
echo Compilando Auth Service...
call mvn clean install -pl auth-service -am -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] La compilacion fallo.
    pause
    exit /b 1
)
echo.
echo Iniciando Auth Service en http://localhost:3001
echo Swagger UI: http://localhost:3001/swagger-ui.html
echo.
cd auth-service
call mvn spring-boot:run
goto end

:game
echo.
echo Compilando Game Catalog Service...
call mvn clean install -pl game-catalog-service -am -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] La compilacion fallo.
    pause
    exit /b 1
)
echo.
echo Iniciando Game Catalog Service en http://localhost:3002
echo Swagger UI: http://localhost:3002/swagger-ui.html
echo.
cd game-catalog-service
call mvn spring-boot:run
goto end

:order
echo.
echo Compilando Order Service...
call mvn clean install -pl order-service -am -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] La compilacion fallo.
    pause
    exit /b 1
)
echo.
echo Iniciando Order Service en http://localhost:3003
echo Swagger UI: http://localhost:3003/swagger-ui.html
echo.
cd order-service
call mvn spring-boot:run
goto end

:library
echo.
echo Compilando Library Service...
call mvn clean install -pl library-service -am -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] La compilacion fallo.
    pause
    exit /b 1
)
echo.
echo Iniciando Library Service en http://localhost:3004
echo Swagger UI: http://localhost:3004/swagger-ui.html
echo.
cd library-service
call mvn spring-boot:run
goto end

:invalid
echo.
echo Opcion invalida. Por favor selecciona 1-5.
pause
goto end

:end
echo.
echo Presiona cualquier tecla para salir...
pause >nul

