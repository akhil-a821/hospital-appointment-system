@echo off
title Hospital Appointment Scheduling System
echo =========================================================
echo   Starting CarePulse Hospital Appointment System...
echo =========================================================

REM Run the compiled Java Swing application
java -cp "target\classes;lib\*" com.hospital.Main

pause
