# Script para agregar Maven al PATH temporalmente después de instalación manual

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Configuración de Maven" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$mavenPath = "C:\Program Files\Apache\maven\bin"

if (Test-Path $mavenPath) {
    Write-Host "[OK] Maven encontrado en: $mavenPath" -ForegroundColor Green
    Write-Host ""
    Write-Host "Agregando Maven al PATH de esta sesión..." -ForegroundColor Yellow
    $env:Path += ";$mavenPath"
    Write-Host "[OK] Maven agregado al PATH" -ForegroundColor Green
    Write-Host ""
    Write-Host "Verificando instalación..." -ForegroundColor Yellow
    mvn -version
} else {
    Write-Host "[ERROR] Maven no encontrado en: $mavenPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor:" -ForegroundColor Yellow
    Write-Host "1. Descarga Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor White
    Write-Host "2. Extrae en: C:\Program Files\Apache\maven" -ForegroundColor White
    Write-Host "3. Vuelve a ejecutar este script" -ForegroundColor White
    Write-Host ""
    Write-Host "O ejecuta este comando para agregar al PATH permanentemente:" -ForegroundColor Yellow
    Write-Host '[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\Apache\maven\bin", "User")' -ForegroundColor Gray
}

Write-Host ""
Write-Host "Presiona cualquier tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

