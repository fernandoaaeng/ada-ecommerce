@echo off
echo ========================================
echo    ADA TECH E-COMMERCE SYSTEM
echo ========================================
echo Compilando e iniciando o sistema...

REM Criar diretorio build se nao existir
if not exist build mkdir build

REM Limpar compilacoes anteriores
echo Limpando compilacoes anteriores...
del /q build\* >nul 2>&1

REM Compilar todas as classes Java
echo Compilando o projeto...
javac -d build -cp src/main/java src/main/java/br/com/ada/ecommerce/Main.java src/main/java/br/com/ada/ecommerce/models/*.java src/main/java/br/com/ada/ecommerce/utils/*.java src/main/java/br/com/ada/ecommerce/repositories/*.java src/main/java/br/com/ada/ecommerce/services/*.java src/main/java/br/com/ada/ecommerce/cli/*.java

REM Verificar se a compilacao foi bem-sucedida
if %ERRORLEVEL% EQU 0 (
    echo.
    echo SUCESSO: Compilacao concluida!
    echo.
    echo Iniciando o sistema Ada Tech E-Commerce...
    echo ==============================================
    echo.
    
    REM Executar o sistema
    java -cp build br.com.ada.ecommerce.Main
) else (
    echo.
    echo ERRO: Falha na compilacao. Verifique os erros acima.
    echo.
)

echo.
pause
