# run_tests.ps1 — Compila y ejecuta las pruebas JUnit de EasySokoban
# Uso: .\run_tests.ps1

$SRC  = "src"
$BIN  = "bin"
$LIB  = "lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar"

Write-Host ""
Write-Host "=== Compilando fuentes del dominio y pruebas ===" -ForegroundColor Cyan

New-Item -ItemType Directory -Force -Path $BIN | Out-Null

javac -encoding UTF-8 -cp $LIB -d $BIN `
    "$SRC\domain\CellType.java" `
    "$SRC\domain\Direction.java" `
    "$SRC\domain\Position.java" `
    "$SRC\domain\EasySokoban.java" `
    "$SRC\domain\EasySokobanTest.java"

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR de compilacion. Revisa los mensajes anteriores." -ForegroundColor Red
    exit 1
}

Write-Host "Compilacion exitosa." -ForegroundColor Green
Write-Host ""
Write-Host "=== Ejecutando pruebas JUnit ===" -ForegroundColor Cyan

java -cp "$BIN;$LIB" org.junit.runner.JUnitCore domain.EasySokobanTest

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Todas las pruebas pasaron." -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "Algunas pruebas fallaron. Revisa el reporte anterior." -ForegroundColor Yellow
}
