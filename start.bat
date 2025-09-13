@echo off
chcp 65001
echo Starting KPAH Server with Maven...
mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
pause