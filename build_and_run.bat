@echo off
chcp 65001 > nul
title Build and Run - Hospital Appointment Scheduling System
echo =========================================================
echo   CarePulse Hospital Appointment Scheduling System
echo =========================================================

echo [1/3] Preparing build environment...
if not exist "target\classes" mkdir "target\classes"

echo [2/3] Compiling Java source files...
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d target\classes -cp "lib\*;src\main\resources" @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Copying resources...
xcopy /E /I /Y src\main\resources target\classes > nul

echo [3/3] Launching Hospital Appointment System GUI...
java -cp "target\classes;lib\*" com.hospital.Main

pause
