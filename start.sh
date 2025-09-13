#!/bin/bash
echo "Starting KPAH Server with Maven..."
mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"