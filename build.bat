@echo off
chcp 65001
echo Building KPAH Server package...
mvn clean package
echo.
echo Build completed! Check target directory for JAR files.
pause