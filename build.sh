#!/bin/bash
echo "Building KPAH Server package..."
mvn clean package
echo
echo "Build completed! Check target directory for JAR files."