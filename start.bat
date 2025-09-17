@echo off
chcp 65001
echo Starting KPAH Server with Maven...
@REM mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
@REM pause
java -jar kpah-server-jar-with-dependencies.jar