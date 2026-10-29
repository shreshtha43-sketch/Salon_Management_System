
@echo off
setlocal

echo ===================================================
echo Compiling The Groom Room - Salon Management System
echo ===================================================

if not exist bin mkdir bin

if not exist lib\vlcj-4.12.1.jar (
    echo ERROR: VLCJ JAR not found in lib folder.
    pause
    exit /b 1
)

dir /s /B src\*.java > sources.txt

javac -encoding UTF-8 -cp "lib\*;bin" -d bin @sources.txt
set COMPILE_RESULT=%ERRORLEVEL%

del sources.txt

if %COMPILE_RESULT% equ 0 (
    echo.
    echo Compilation successful! Binaries created in bin\
) else (
    echo.
    echo Compilation failed. Check the errors above.
)

pause
exit /b %COMPILE_RESULT%
