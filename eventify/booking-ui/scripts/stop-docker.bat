@echo off
echo 🛑 Остановка Docker контейнеров...

REM Останавливаем все контейнеры
docker-compose down

echo ✅ Контейнеры остановлены!
pause 