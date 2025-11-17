@echo off
echo ========================================
echo   Verificacion de Conexion MySQL
echo ========================================
echo.
echo Este script verificara la conexion a MySQL en Laragon
echo.

REM Verificar si MySQL esta corriendo
echo Verificando si MySQL esta corriendo en el puerto 3306...
netstat -an | findstr ":3306" >nul
if %errorlevel%==0 (
    echo [OK] MySQL parece estar corriendo en el puerto 3306
) else (
    echo [ERROR] MySQL no esta corriendo en el puerto 3306
    echo Por favor inicia MySQL desde Laragon
    pause
    exit /b 1
)

echo.
echo Intentando conectar a MySQL...
echo.

REM Intentar conectar usando mysql command (si esta disponible)
where mysql >nul 2>&1
if %errorlevel%==0 (
    echo Probando conexion con: mysql -u root -p
    echo Si te pide contraseña, presiona Enter si no tienes una
    echo.
    mysql -u root -e "SHOW DATABASES;" 2>nul
    if %errorlevel%==0 (
        echo [OK] Conexion exitosa a MySQL
        echo.
        echo Verificando bases de datos...
        mysql -u root -e "SHOW DATABASES LIKE '%_db';" 2>nul
    ) else (
        echo [ADVERTENCIA] No se pudo conectar automaticamente
        echo Por favor verifica manualmente en phpMyAdmin
    )
) else (
    echo [INFO] MySQL command line no encontrado
    echo Por favor verifica manualmente en phpMyAdmin: http://localhost/phpmyadmin
)

echo.
echo ========================================
echo   Bases de datos requeridas:
echo ========================================
echo   1. auth_db
echo   2. games_db
echo   3. orders_db
echo   4. library_db
echo.
echo Si alguna no existe, ejecuta setup-databases.sql en phpMyAdmin
echo.
pause

