#!/bin/bash

echo "🛑 Остановка Docker контейнеров..."

# Останавливаем все контейнеры
docker-compose down

echo "✅ Контейнеры остановлены!" 