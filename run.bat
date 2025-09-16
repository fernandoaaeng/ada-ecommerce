@echo off
echo ========================================
echo    ADA TECH E-COMMERCE
echo ========================================
echo.
echo Compilando o projeto...
call mvn clean compile
if %errorlevel% neq 0 (
    echo.
    echo ERRO: Falha na compilacao!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Compilacao concluida com sucesso!
echo ========================================
echo.
echo Iniciando a aplicacao...
echo.
call mvn exec:java

echo.
echo ========================================
echo Aplicacao finalizada.
echo ========================================
pause
