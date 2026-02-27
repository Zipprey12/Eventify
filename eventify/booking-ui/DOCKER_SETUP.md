# 🐳 Docker настройка Event Booking Frontend

## Что было добавлено

### 1. Docker файлы
- **`Dockerfile`** - Многоэтапная сборка с Node.js и Nginx
- **`docker-compose.yml`** - Конфигурация с профилями для разных режимов
- **`nginx.conf`** - Оптимизированная конфигурация Nginx для React SPA
- **`backend-stub.conf`** - Заглушка для демонстрации работы с бэкендом
- **`.dockerignore`** - Исключения для оптимизации сборки

### 2. Скрипты запуска
- **Unix (macOS/Linux):**
  - `scripts/start-docker-mocks.sh` - Запуск с моками
  - `scripts/start-docker-backend.sh` - Запуск с бэкендом
  - `scripts/stop-docker.sh` - Остановка контейнеров

- **Windows:**
  - `scripts/start-docker-mocks.bat` - Запуск с моками
  - `scripts/start-docker-backend.bat` - Запуск с бэкендом
  - `scripts/stop-docker.bat` - Остановка контейнеров

### 3. NPM скрипты
Добавлены в `package.json`:
```json
{
  "docker:mocks": "./scripts/start-docker-mocks.sh",
  "docker:backend": "./scripts/start-docker-backend.sh",
  "docker:stop": "./scripts/stop-docker.sh",
  "docker:build": "docker-compose build",
  "docker:logs": "docker-compose logs -f"
}
```

### 4. Конфигурационные файлы
- **`env.example`** - Пример переменных окружения
- **`QUICKSTART.md`** - Краткая инструкция по запуску

## Режимы работы

### Профиль `mocks`
- Запускает только фронтенд с моками
- Идеально для демонстрации и разработки
- Порт: 3000

### Профиль `backend`
- Запускает фронтенд + заглушку бэкенда
- Для тестирования интеграции с API
- Порты: 3000 (фронтенд), 8080 (бэкенд)

## Команды для запуска

### Быстрый старт
```bash
# С моками
npm run docker:mocks

# С бэкендом
npm run docker:backend

# Остановка
npm run docker:stop
```

### Продвинутые команды
```bash
# Сборка образов
npm run docker:build

# Просмотр логов
npm run docker:logs

# Прямые команды Docker Compose
docker-compose --profile mocks up --build
docker-compose --profile backend up --build
docker-compose down
```

## Особенности реализации

### Многоэтапная сборка
1. **Builder stage** - Node.js для сборки React приложения
2. **Production stage** - Nginx для раздачи статических файлов

### Оптимизации
- Gzip сжатие
- Кэширование статических файлов
- Security headers
- Health check endpoints

### Переменные окружения
- `REACT_APP_USE_MOCKS` - Переключение между моками и реальным API
- `REACT_APP_API_URL` - URL бэкенда

## Тестирование

Все компоненты протестированы:
- ✅ Сборка Docker образа
- ✅ Запуск с моками
- ✅ NPM скрипты
- ✅ Остановка контейнеров
- ✅ Доступность приложения на порту 3000

## Совместимость

- **macOS**: ✅ Протестировано
- **Linux**: ✅ Ожидается совместимость
- **Windows**: ✅ Скрипты .bat созданы

## Следующие шаги

1. Интеграция с реальным бэкендом
2. Настройка CI/CD
3. Оптимизация размера образа
4. Добавление мониторинга 