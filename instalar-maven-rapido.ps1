# Script para instalar Maven rápidamente usando Chocolatey o descarga directa

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Instalador Rápido de Maven" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si Chocolatey está instalado
$chocoInstalled = Get-Command choco -ErrorAction SilentlyContinue

if ($chocoInstalled) {
    Write-Host "[INFO] Chocolatey detectado. Instalando Maven..." -ForegroundColor Yellow
    choco install maven -y
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Maven instalado exitosamente!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Cierra y vuelve a abrir PowerShell, luego ejecuta: mvn -version" -ForegroundColor Yellow
    } else {
        Write-Host "[ERROR] La instalación falló." -ForegroundColor Red
    }
} else {
    Write-Host "[INFO] Chocolatey no está instalado." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Opciones:" -ForegroundColor Cyan
    Write-Host "1. Instalar Chocolatey primero:" -ForegroundColor White
    Write-Host "   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Instalar Maven manualmente:" -ForegroundColor White
    Write-Host "   - Descarga desde: https://maven.apache.org/download.cgi" -ForegroundColor Gray
    Write-Host "   - Extrae en: C:\Program Files\Apache\maven" -ForegroundColor Gray
    Write-Host "   - Agrega C:\Program Files\Apache\maven\bin al PATH" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. Usar un IDE (IntelliJ IDEA, Eclipse) que incluye Maven" -ForegroundColor White
    Write-Host ""
}

Write-Host "Presiona cualquier tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

