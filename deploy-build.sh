#!/bin/bash
set -e

echo "🪏 Building Web Server..."
cd datenow
./mvnw clean package -DskipTests || exit 1
docker build -t aronmindev/datenow-web:latest .
docker push aronmindev/datenow-web:latest

echo "🪏 Building Mail Server..."
cd ../mail
./gradlew clean build -x test || exit 1
docker build -t aronmindev/datenow-mail:latest .
docker push aronmindev/datenow-mail:latest

echo "✅ Build & Pushed"