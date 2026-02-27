# Event Booking Frontend

Фронтенд для системы бронирования мероприятий, разработанный на React с TypeScript.

## Описание проекта

Система позволяет:
- Регистрироваться и входить в систему
- Просматривать список мероприятий с пагинацией
- Бронировать билеты на мероприятия
- Управлять своими бронированиями
- Настраивать уведомления
- Администраторам: управлять мероприятиями и бронированиями

## Технологический стек

- **React 18** с TypeScript
- **React Router** для навигации
- **React Hook Form** с Yup для валидации форм
- **Axios** для HTTP-запросов
- **Tailwind CSS** для стилизации
- **Lucide React** для иконок
- **React Hot Toast** для уведомлений

## Быстрый старт

У вас есть 4 варианта запуска приложения:

### 🐳 Вариант 1: Docker с моками (демонстрация)

**Предварительные требования:**
- [Docker](https://docs.docker.com/get-docker/) 
- [Docker Compose](https://docs.docker.com/compose/install/)

**Запуск:**
```bash
# macOS/Linux
npm run docker:mocks

# Windows
npm run docker:mocks
# или запустите scripts/start-docker-mocks.bat
```

**Что происходит:**
- Фронтенд запускается в Docker контейнере
- Используются встроенные моки (демо-данные)
- Приложение доступно на http://localhost:3000

### 🐳 Вариант 2: Docker с реальным бэкендом

**Предварительные требования:**
- [Docker](https://docs.docker.com/get-docker/) 
- [Docker Compose](https://docs.docker.com/compose/install/)
- **Ваш бэкенд должен быть запущен на порту 8080**

**Запуск:**
```bash
# macOS/Linux
npm run docker:backend

# Windows
npm run docker:backend
# или запустите scripts/start-docker-backend.bat
```

**Что происходит:**
- Фронтенд запускается в Docker контейнере
- Подключается к вашему бэкенду на http://localhost:8080
- Приложение доступно на http://localhost:3000
- **Порт 8080 должен быть свободен для вашего бэкенда**

#### Управление Docker контейнерами

**С помощью npm скриптов:**
```bash
# Остановить контейнеры
npm run docker:stop

# Просмотр логов
npm run docker:logs

# Пересборка образов
npm run docker:build
```

**С помощью команд Docker Compose:**
```bash
# Остановить контейнеры
docker-compose down

# Просмотр логов
docker-compose logs -f

# Пересборка образов
docker-compose build --no-cache
```

#### Прямые команды Docker Compose

```bash
# Вариант 1: Docker с моками
docker-compose --profile docker-mocks up --build

# Вариант 2: Docker с реальным бэкендом
docker-compose --profile docker-backend up --build

# Остановка
docker-compose down
```

### 💻 Вариант 3: Локальный запуск с моками

**Предварительные требования:**
- Node.js 16+ 
- npm или yarn

**Установка зависимостей:**
```bash
npm install
```

**Запуск:**
```bash
npm run start:mocks
```

**Что происходит:**
- Фронтенд запускается локально через npm
- Используются встроенные моки (демо-данные)
- Приложение доступно на http://localhost:3000

### 💻 Вариант 4: Локальный запуск с реальным бэкендом

**Предварительные требования:**
- Node.js 16+ 
- npm или yarn
- **Ваш бэкенд должен быть запущен на порту 8080**

**Установка зависимостей:**
```bash
npm install
```

**Запуск:**
```bash
npm run start:backend
```

**Что происходит:**
- Фронтенд запускается локально через npm
- Подключается к вашему бэкенду на http://localhost:8080
- Приложение доступно на http://localhost:3000
- **Порт 8080 должен быть свободен для вашего бэкенда**

### 🔧 Альтернативный способ (через .env файл)

Для вариантов 3 и 4 можно также использовать `.env` файл:

```bash
# Скопируйте пример конфигурации
cp env.example .env
```

Затем отредактируйте `.env` в зависимости от ваших потребностей:

```env
# Для моков (Вариант 3)
REACT_APP_USE_MOCKS=true

# Для реального бэкенда (Вариант 4)
REACT_APP_API_URL=http://localhost:8080
REACT_APP_USE_MOCKS=false
```

И запустите:
```bash
npm start
```

**Примечание:** Использование `.env` файла менее удобно, чем npm скрипты, так как требует ручного редактирования файла при переключении между режимами.

### 🔑 Тестовые аккаунты для демонстрации

**Обычный пользователь:**
- Email: `user@example.com`
- Пароль: `password123`

**Администратор:**
- Email: `admin@example.com`
- Пароль: `password123`

## Инструкции по запуску для разных ОС

### Windows

1. **Установка Node.js:**
   - Скачайте Node.js с официального сайта: https://nodejs.org/
   - Установите, следуя инструкциям установщика
   - Проверьте установку: `node --version` и `npm --version`

2. **Клонирование и запуск:**
   ```cmd
   git clone <repository-url>
   cd event-booking-frontend
   npm install
   npm start
   ```

3. **Возможные проблемы:**
   - Если возникают ошибки с правами доступа, запустите командную строку от имени администратора
   - При проблемах с npm кэшем: `npm cache clean --force`

### macOS

1. **Установка Node.js:**
   - Рекомендуется использовать Homebrew:
     ```bash
     brew install node
     ```
   - Или скачайте с официального сайта: https://nodejs.org/

2. **Клонирование и запуск:**
   ```bash
   git clone <repository-url>
   cd event-booking-frontend
   npm install
   npm start
   ```

3. **Возможные проблемы:**
   - При проблемах с правами доступа: `sudo npm install`
   - Если порт 3000 занят, npm автоматически предложит другой порт

## Структура проекта

```
├── src/                    # Исходный код приложения
│   ├── components/         # React компоненты
│   │   ├── AuthForm.tsx    # Форма аутентификации
│   │   ├── Navigation.tsx  # Навигация
│   │   ├── EventList.tsx   # Список мероприятий
│   │   ├── EventDetail.tsx # Детали мероприятия
│   │   ├── BookingList.tsx # Список бронирований
│   │   ├── AdminBookingList.tsx # Админ: управление бронированиями
│   │   └── NotificationSettings.tsx # Настройки уведомлений
│   ├── contexts/           # React контексты
│   │   └── AuthContext.tsx # Контекст аутентификации
│   ├── services/           # API сервисы
│   │   ├── api.ts         # Основной API сервис
│   │   └── mockApi.ts     # Моки для демонстрации
│   ├── types/              # TypeScript типы
│   │   └── index.ts       # Определения типов
│   ├── App.tsx            # Главный компонент
│   └── index.tsx          # Точка входа
├── scripts/               # Скрипты для запуска
│   ├── start-docker-mocks.sh    # Запуск Docker с моками (Unix)
│   ├── start-docker-backend.sh  # Запуск Docker с бэкендом (Unix)
│   ├── stop-docker.sh           # Остановка Docker (Unix)
│   ├── start-docker-mocks.bat   # Запуск Docker с моками (Windows)
│   ├── start-docker-backend.bat # Запуск Docker с бэкендом (Windows)
│   └── stop-docker.bat          # Остановка Docker (Windows)
├── Dockerfile             # Конфигурация Docker образа
├── docker-compose.yml     # Конфигурация Docker Compose
├── nginx.conf             # Конфигурация Nginx
├── backend-stub.conf      # Заглушка для бэкенда
├── .dockerignore          # Исключения для Docker
├── env.example            # Пример переменных окружения
├── QUICKSTART.md          # Краткая инструкция по запуску
└── README.md              # Документация проекта
```

## API Endpoints

### Аутентификация
- `POST /auth/login` - Вход в систему
- `POST /auth/register` - Регистрация

### Мероприятия
- `GET /events` - Список мероприятий
- `GET /events/{id}` - Детали мероприятия
- `POST /admin/events` - Создание мероприятия (Admin)
- `PUT /admin/events/{id}` - Обновление мероприятия (Admin)
- `DELETE /admin/events/{id}` - Удаление мероприятия (Admin)

### Бронирования
- `GET /bookings` - Мои бронирования
- `POST /bookings` - Создание бронирования
- `DELETE /bookings/{id}` - Отмена бронирования
- `GET /admin/bookings` - Все бронирования (Admin)
- `PUT /admin/bookings/{id}/confirm` - Подтверждение бронирования (Admin)
- `DELETE /admin/bookings/{id}` - Удаление бронирования (Admin)

### Уведомления
- `GET /user/notifications` - Настройки уведомлений
- `PUT /user/notifications` - Обновление настроек
- `DELETE /user/notifications` - Сброс настроек
- `POST /user/telegram/link` - Привязка Telegram

## Конфигурация

### Переменные окружения

| Переменная | Описание | По умолчанию | Примечание |
|------------|----------|--------------|------------|
| `REACT_APP_API_URL` | URL вашего бэкенда | `http://localhost:8080` | Для Docker используйте `http://host.docker.internal:8080` |
| `REACT_APP_USE_MOCKS` | Использовать встроенные моки | `true` | `true` = демо-данные, `false` = реальный API |

### Docker конфигурация

#### Профили Docker Compose

- **`docker-mocks`** - Вариант 1: Фронтенд в Docker с моками
- **`docker-backend`** - Вариант 2: Фронтенд в Docker с реальным бэкендом

#### Настройка для продакшена

Для продакшена создайте файл `.env.production`:

```env
REACT_APP_API_URL=https://your-backend-domain.com
REACT_APP_USE_MOCKS=false
```

И соберите образ:

```bash
docker build -t event-booking-frontend:latest .
```

#### Кастомизация портов

Для изменения портов отредактируйте `docker-compose.yml`:

```yaml
services:
  frontend-mocks:
    ports:
      - "YOUR_PORT:80"  # Измените YOUR_PORT на нужный порт
```

#### Специфика для Docker

При запуске в Docker контейнере используйте `host.docker.internal` для доступа к бэкенду на хост-машине:

```env
REACT_APP_API_URL=http://host.docker.internal:8080
```

## Сборка для продакшена

```bash
npm run build
```

Собранные файлы будут в папке `build/`.

## Тестирование

```bash
npm test
```

## NPM скрипты

### Основные команды
- `npm start` - Запуск в режиме разработки (с настройками из .env)
- `npm run build` - Сборка для продакшена
- `npm run test` - Запуск тестов
- `npm run eject` - Извлечение конфигурации (необратимо)

### Docker команды
- `npm run docker:mocks` - Вариант 1: Docker с моками
- `npm run docker:backend` - Вариант 2: Docker с реальным бэкендом
- `npm run docker:stop` - Остановка Docker контейнеров
- `npm run docker:build` - Сборка Docker образов
- `npm run docker:logs` - Просмотр логов контейнеров

### Локальные команды
- `npm run start:mocks` - Вариант 3: Локальный запуск с моками
- `npm run start:backend` - Вариант 4: Локальный запуск с реальным бэкендом

### Дополнительные команды
- `npm run test:coverage` - Тесты с покрытием

## Особенности демонстрационного режима

При запуске с моками:
- Все данные хранятся в памяти браузера
- При перезагрузке страницы данные сбрасываются
- Имитируются задержки API для реалистичности
- Доступны предустановленные тестовые аккаунты

## Поддержка

### Устранение неполадок

#### Для локального запуска:
1. Проверьте версию Node.js (должна быть 16+)
2. Удалите `node_modules` и `package-lock.json`, затем выполните `npm install`
3. Очистите кэш npm: `npm cache clean --force`
4. Убедитесь, что порт 3000 свободен

#### Для Docker:
1. **Порт занят**: Измените порт в `docker-compose.yml` или остановите процесс на порту 3000
2. **Ошибки сборки**: Очистите Docker кэш: `docker system prune -a`
3. **Проблемы с правами**: Запустите Docker с правами администратора
4. **Контейнер не запускается**: Проверьте логи: `docker-compose logs frontend-mocks`

#### Частые проблемы:

**Docker не установлен:**
- Windows/macOS: Скачайте Docker Desktop с [официального сайта](https://www.docker.com/products/docker-desktop)
- Linux: Следуйте [инструкции по установке](https://docs.docker.com/engine/install/)

**Порт 3000 занят:**
```bash
# Найти процесс на порту 3000
lsof -i :3000  # macOS/Linux
netstat -ano | findstr :3000  # Windows

# Остановить процесс
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows
```

**Ошибки сборки Docker:**
```bash
# Очистить все Docker ресурсы
docker system prune -a --volumes

# Пересобрать образ
docker-compose build --no-cache
```

## Лицензия

MIT
