@echo off
chcp 65001 >nul
echo ========================================
echo   🚀 Iniciando GameStore - Sistema Completo
echo ========================================
echo.

echo [1/4] ⚠️  Verificando MySQL...
echo       Asegúrate de que MySQL esté corriendo en Laragon/XAMPP
timeout /t 3
echo.

echo [2/4] 🔍 Iniciando Eureka Server (puerto 8761)...
echo       ⚠️  IMPORTANTE: Espera a que Eureka esté listo antes de continuar
start "Eureka Server" cmd /k "title Eureka Server && mvn spring-boot:run -pl eureka-server"
timeout /t 20
echo       ✅ Eureka Server iniciado. Verifica: http://localhost:8761
echo.

echo [3/4] 📦 Iniciando Microservicios...
start "Auth Service (3001)" cmd /k "title Auth Service && mvn spring-boot:run -pl auth-service"
timeout /t 5
start "Game Catalog Service (3002)" cmd /k "title Game Catalog Service && mvn spring-boot:run -pl game-catalog-service"
timeout /t 5
start "Order Service (3003)" cmd /k "title Order Service && mvn spring-boot:run -pl order-service"
timeout /t 5
start "Library Service (3004)" cmd /k "title Library Service && mvn spring-boot:run -pl library-service"
timeout /t 10
echo       ✅ Microservicios iniciando...
echo.

echo [4/4] 🌐 Iniciando React App (puerto 3000)...
cd ..\steamish-react-app
start "React App" cmd /k "title React App && npm start"
cd ..\MS-orden-resena-catalogo
echo       ✅ React App iniciando...
echo.

echo ========================================
echo   ✅ Proceso de inicio completado
echo ========================================
echo.
echo 📊 Verificaciones:
echo    - Eureka Dashboard: http://localhost:8761
echo    - React App: http://localhost:3000
echo    - Auth Service: http://localhost:3001/swagger-ui.html
echo    - Game Catalog: http://localhost:3002/swagger-ui.html
echo    - Order Service: http://localhost:3003/swagger-ui.html
echo    - Library Service: http://localhost:3004/swagger-ui.html
echo.
echo ⏱️  Espera 30-60 segundos para que todos los servicios estén listos
echo.
pause

