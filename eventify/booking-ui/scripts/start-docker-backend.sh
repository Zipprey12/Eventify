#!/bin/bash

echo "🚀 Запуск Event Booking Frontend в Docker с бэкендом..."

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

# Запускаем с бэкендом
echo "🐳 Запуск с бэкендом..."
docker-compose --profile docker-backend up --build

echo "✅ Приложение запущено!"
echo "🌐 Фронтенд: http://localhost:3000"
echo "🔧 Бэкенд: http://localhost:8080 (ожидается запуск вашего бэкенда)" 