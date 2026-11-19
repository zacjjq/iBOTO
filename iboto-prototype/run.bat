@echo off
REM Compiles and starts iBOTO! (needs a JDK 8 or newer: javac -version)
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 (pause & exit /b 1)
del sources.txt
java -cp out iboto.ui.IbotoApp
