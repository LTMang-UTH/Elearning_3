# Script helper để build và chạy dự án WebSocket
# Sử dụng: .\build.ps1 [command]
# Commands: clean, install, run, test

# Set JAVA_HOME nếu chưa được set
if (-not $env:JAVA_HOME) {
    $javaPath = "C:\Program Files\Java\jdk-24"
    if (Test-Path $javaPath) {
        $env:JAVA_HOME = $javaPath
        Write-Host "JAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green
    } else {
        Write-Host "Error: JAVA_HOME not set and Java not found at $javaPath" -ForegroundColor Red
        Write-Host "Please set JAVA_HOME environment variable" -ForegroundColor Yellow
        exit 1
    }
}

$command = $args[0]

switch ($command) {
    "clean" {
        Write-Host "Cleaning project..." -ForegroundColor Cyan
        .\mvnw.cmd clean
    }
    "install" {
        Write-Host "Building project..." -ForegroundColor Cyan
        .\mvnw.cmd clean install
    }
    "run" {
        Write-Host "Starting WebSocket application..." -ForegroundColor Cyan
        .\mvnw.cmd spring-boot:run
    }
    "test" {
        Write-Host "Running tests..." -ForegroundColor Cyan
        .\mvnw.cmd test
    }
    default {
        Write-Host "Usage: .\build.ps1 [command]" -ForegroundColor Yellow
        Write-Host "Commands:" -ForegroundColor Yellow
        Write-Host "  clean   - Clean build artifacts"
        Write-Host "  install - Build project"
        Write-Host "  run     - Run application"
        Write-Host "  test    - Run tests"
    }
}
