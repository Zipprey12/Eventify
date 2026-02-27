#!/bin/bash

echo "🚀 Запуск Event Booking Frontend в Docker с моками..."

# Проверяем, установлен ли Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен. Установите Docker с https://docs.docker.com/get-docker/"
    exit 1
fi

# Проверяем, установлен ли Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose не установлен. Установите Docker Compose с https://docs.docker.com/compose/install/"
    exit 1
fi

# Останавливаем существующие контейнеры
echo "🛑 Остановка существующих контейнеров..."
docker-compose down

# Запускаем с моками
echo "🐳 Запуск с моками..."
docker-compose --profile docker-mocks up --build

echo "✅ Приложение запущено! Откройте http://localhost:3000" 