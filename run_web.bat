@echo off
chcp 65001 > nul
title CarePulse Hospital Appointment Scheduling System - Spring Boot Web
echo ==================================================================
echo   CarePulse Hospital Appointment Scheduling Web App
echo   Spring Boot Backend + Responsive Mobile & Desktop Web UI
echo ==================================================================
echo.

if exist "tools\apache-maven-3.9.6\bin\mvn.cmd" (
    set "MVN_CMD=tools\apache-maven-3.9.6\bin\mvn.cmd"
) else (
    set "MVN_CMD=mvn"
)

echo Starting Spring Boot Web Server on port 8080...
echo.
echo Desktop URL:   http://localhost:8080
echo Mobile Access: Connect phone to same Wi-Fi and open http://^<your-pc-ip^>:8080
echo.

%MVN_CMD% spring-boot:run

pause
