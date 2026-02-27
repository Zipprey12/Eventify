@echo off
echo 🚀 Запуск Event Booking Frontend в Docker с бэкендом...

REM Проверяем, установлен ли Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker не установлен. Установите Docker с https://docs.docker.com/get-docker/
    pause
    exit /b 1
)

REM Проверяем, установлен ли Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose не установлен. Установите Docker Compose с https://docs.docker.com/compose/install/
    pause
    exit /b 1
)

REM Останавливаем существующие контейнеры
echo 🛑 Остановка существующих контейнеров...
docker-compose down

REM Запускаем с бэкендом
echo 🐳 Запуск с бэкендом...
docker-compose --profile docker-backend up --build

echo ✅ Приложение запущено!
echo 🌐 Фронтенд: http://localhost:3000
echo 🔧 Бэкенд: http://localhost:8080
pause 