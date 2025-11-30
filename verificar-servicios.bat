@echo off
chcp 65001 >nul
echo ========================================
echo   🔍 Verificación de Microservicios
echo ========================================
echo.

echo Verificando servicios...
echo.

echo [1/5] Verificando Eureka Server (8761)...
curl -s http://localhost:8761 >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ Eureka Server está corriendo
) else (
    echo    ❌ Eureka Server NO está corriendo
    echo    Ejecuta: mvn spring-boot:run -pl eureka-server
)
echo.

echo [2/5] Verificando game-catalog-service (3002)...
curl -s http://localhost:3002/api/games >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ game-catalog-service está corriendo
) else (
    echo    ❌ game-catalog-service NO está corriendo
    echo    Ejecuta: mvn spring-boot:run -pl game-catalog-service
)
echo.

echo [3/5] Verificando order-service (3003)...
curl -s http://localhost:3003/api/orders >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ order-service está corriendo
) else (
    echo    ❌ order-service NO está corriendo
    echo    Ejecuta: mvn spring-boot:run -pl order-service
)
echo.

echo [4/5] Verificando auth-service (3001)...
curl -s http://localhost:3001/api/auth/login >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ auth-service está corriendo
) else (
    echo    ❌ auth-service NO está corriendo
    echo    Ejecuta: mvn spring-boot:run -pl auth-service
)
echo.

echo [5/5] Verificando library-service (3004)...
curl -s http://localhost:3004/api/library >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✅ library-service está corriendo
) else (
    echo    ❌ library-service NO está corriendo
    echo    Ejecuta: mvn spring-boot:run -pl library-service
)
echo.

echo ========================================
echo   📊 Resumen
echo ========================================
echo.
echo Abre en el navegador:
echo   - Eureka Dashboard: http://localhost:8761
echo   - game-catalog-service: http://localhost:3002/api/games
echo   - order-service: http://localhost:3003/api/orders
echo   - auth-service: http://localhost:3001/api/auth/login
echo   - library-service: http://localhost:3004/api/library
echo.
echo Si algún servicio no está corriendo, inícialo en el orden correcto:
echo   1. Eureka Server (PRIMERO)
echo   2. game-catalog-service
echo   3. order-service
echo   4. auth-service
echo   5. library-service
echo.
pause

