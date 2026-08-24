# Eventify
 
Платформа для бронирования билетов на мероприятия: каталог событий, бронирование
с подтверждением по email/Telegram, автоматическое снятие брони по истечении
срока и напоминания о предстоящих событиях.
 
Backend построен как набор независимых Spring Boot микросервисов на Java 21 с
общей шиной событий на Kafka.
 
## Содержание
 
- [Архитектура](#архитектура)
- [Сервисы](#сервисы)
- [Технологический стек](#технологический-стек)
- [Взаимодействие сервисов](#взаимодействие-сервисов)
- [Ключевые архитектурные решения](#ключевые-архитектурные-решения)
- [Структура репозитория](#структура-репозитория)
- [Быстрый старт](#быстрый-старт)
- [Переменные окружения](#переменные-окружения)
- [API](#api)
- [Разработка](#разработка)
## Архитектура
 
```
                         ┌────────────────── ──┐
                         │        nginx        │  единая точка входа
                         │   (localhost:8080)  │
                         └──────────┬──────────┘
              ┌─────────────┬────────────┼───────────┐
              ▼             ▼            ▼           ▼
       ┌─────────────┐┌──────────┐┌──────────┐┌────────────┐
       │auth-service ││event-svc ││booking-  ││notification│
       │   :8081     ││  :8082   ││svc :8083 ││  -svc :8084│
       └──────┬──────┘└────┬─────┘└────┬─────┘└─────┬──────┘
              │            │           │            │
              │        ┌───┴───────────┴────────────┴──────┐
              │        │              Kafka                │
              │        │        (localhost:9094 / UI :8090)│
              │        └────┬────────────┬─────────────┬───┘
              │             │            │             │
        ┌─────┴─────┐ ┌─────┴─────┐┌─────┴─────┐ ┌─────┴─────┐
        │auth-pg    │ │event-pg + ││booking-pg+│ │notif-pg + │
        │ :5433     │ │redis      ││redis      │ │redis      │
        └───────────┘ └───────────┘└───────────┘ └───────────┘
 
                    ┌─────────────────────────┐
                    │  telegram (bot-service) │──── Telegram Bot API
                    │  собственные pg + redis │
                    └─────────────────────────┘
```
 
У каждого сервиса своя база PostgreSQL со своими миграциями Liquibase - общей
базы данных нет. Синхронные запросы между сервисами защищены внутренним
API-ключом (см. [Взаимодействие сервисов](#взаимодействие-сервисов)), а
события о бизнес-действиях (регистрация, бронирование, изменение события и
т.д.) публикуются в Kafka по паттерну transactional outbox.
 
## Сервисы
 
| Сервис | Порт | Своя БД | Назначение |
|---|---|---|---|
| **auth-service** | 8081 | `auth_db` (Postgres) | Регистрация, логин, выпуск и валидация JWT, роли пользователей |
| **event-service** | 8082 | `event_db` (Postgres + Redis) | CRUD мероприятий, учёт доступных билетов, резервирование/освобождение мест |
| **booking-service** | 8083 | `booking_db` (Postgres + Redis) | Создание брони, подтверждение, отмена, автоматическая просрочка неподтверждённых броней |
| **notification-service** | 8084 | `notification_db` (Postgres + Redis) | Настройки уведомлений пользователя, подтверждение email, очередь писем, напоминания о событиях, привязка Telegram |
| **telegram** | - (без HTTP-порта наружу) | `telegram_bot_db` (Postgres + Redis) | Telegram-бот: команды, напоминания, уведомления о статусе брони |
| **nginx** | 8080 | - | Единая точка входа, реверс-прокси к сервисам, CORS |
| **kafbat-ui** | 8090 | - | Веб-интерфейс для просмотра топиков и сообщений Kafka |
 
### Общие библиотеки (Gradle-модули)
 
Эти модули не запускаются самостоятельно, а подключаются к сервисам выше:
 
- **common** - общий JWT-фильтр аутентификации (`JwtAuthenticationFilter`,
  `JwtService`), базовая security-конфигурация, единый формат ошибок
  (`GlobalExceptionHandler`, `ErrorResponse`).
- **service-security** — аутентификация сервис-к-сервису по внутреннему
  API-ключу (`InternalApiKeyFilter` на стороне, принимающей запрос,
  `InternalApiKeyExchangeFilter` на стороне, вызывающей другой сервис).
- **kafka-contracts** - общие Java-классы Kafka-сообщений и перечисления
  топиков (`auth.Topics`, `event.Topics`, `booking.Topics`), чтобы продюсер и
  консьюмер использовали одну и ту же схему.
- **outbox-support** - переиспользуемая реализация transactional outbox
  (сущность `OutboxEvent`, `OutboxPublishingScheduler`, `OutboxTypeRegistry`),
  подключается к сервисам, которым нужно публиковать события в Kafka
  атомарно с записью в БД.
- **event-api** - общий DTO мероприятия (`EventDto`) и исключения для
  сервисов, вызывающих event-service.
## Технологический стек
 
- Java 21, Spring Boot 4 (Web, Security, Data JPA, Validation, Kafka)
- PostgreSQL 16 - по отдельной базе на сервис
- Liquibase - версионирование схемы БД
- Redis 7 — дедупликация Kafka-сообщений, очередь напоминаний, отложенное
  снятие брони
- Apache Kafka 3.9 - асинхронная связь между сервисами
- JWT - аутентификация пользователей
- Gradle (Kotlin DSL) - сборка, multi-module проект
- Docker / Docker Compose - контейнеризация и локальный запуск
- nginx - единая точка входа и маршрутизация к сервисам
Telegram-бот (`telegram`) - отдельный Spring Boot сервис с собственной БД,
независимый от доступности notification-service в рантайме.
 
## Взаимодействие сервисов
 
### Синхронные вызовы
 
Сервисы обращаются друг к другу напрямую по HTTP на внутренние эндпоинты
(`/internal/...`), защищённые заголовком с внутренним API-ключом:
 
- `booking-service` → `event-service`: проверка мероприятия и
  резервирование/освобождение билетов (`PUT /events/{id}/book`,
  `PUT /events/{id}/free`, `POST /events/batch`).
- `telegram` → `booking-service`, `event-service`, `notification-service`:
  список броней пользователя, информация о событиях, привязка Telegram и
  время напоминаний.
- `notification-service` ↔ `booking-service`: списки email/подписчиков для
  рассылок и напоминаний (`GET /internal/bookings/emails`,
  `GET /internal/bookings/confirmed`).
Каждый сервис описывает доверенные вызывающие сервисы через
`internal.trusted-services` в своём `application.yml`.
 
### Асинхронные события (Kafka)
 
| Топик | Продюсер | Описание |
|---|---|---|
| `user.registered` | auth-service | Зарегистрирован новый пользователь |
| `event.created` | event-service | Создано новое мероприятие |
| `event.date-changed` | event-service | Изменена дата мероприятия |
| `event.overbooked` | event-service | Превышение вместимости при конкурентном бронировании |
| `event.deleted` | event-service | Мероприятие удалено |
| `booking.book-tickets` | booking-service | Запрос на резервирование билетов |
| `booking.confirmed` | booking-service | Бронь подтверждена администратором |
| `booking.canceled` | booking-service | Бронь отменена пользователем/по истечении срока |
| `booking.canceled-cascade` | booking-service | Массовая отмена броней (например, при удалении события) |
| `booking.deleted-by-admin` | booking-service | Бронь удалена администратором |
| `booking.force-canceled` | booking-service | Принудительная отмена брони |
| `booking.force-cancellation-error` | booking-service | Ошибка при принудительной отмене |
 
Каждый сервис-консьюмер дедуплицирует входящие сообщения через Redis, отличая
повторную доставку одного и того же сообщения от повторного выполнения того
же действия (по `operationId`, если применимо).
 
## Ключевые архитектурные решения
 
**Transactional outbox.** Запись в БД сервиса и публикация Kafka-события
происходят в одной транзакции: событие сначала попадает в таблицу outbox, а
отдельный планировщик (`OutboxPublishingScheduler`) с заданным интервалом
публикует накопленные записи в Kafka. Это гарантирует, что временная
недоступность брокера не приведёт к потере события.
 
**Дедупликация Kafka-сообщений.** Консьюмеры различают «то же сообщение
доставлено повторно» (сетевые ретраи, ребалансировка консьюмер-группы) и «то
же действие выполнено снова» (например, повторная отмена той же брони).
Для второго случая используется `operationId` (UUID), сгенерированный на
источнике в момент действия и сохраняемый в Redis.
 
**Очередь исходящей почты.** Письма не отправляются напрямую из
Kafka-консьюмера. Запись сначала попадает в таблицу `pending_emails`, а
отправку по SMTP выполняет отдельный планировщик (`PendingEmailScheduler`) с
заданным интервалом. Для адресов, которые SMTP-сервер отклоняет сразу,
установлен лимит в 5 попыток - по его достижении отправка прекращается вместо
бесконечных повторов.
 
**Автоматическая просрочка брони.** У неподтверждённой брони есть
`expiry_time`. Основной механизм снятия - очередь с таймером в Redis
(`ExpiryQueueService`); резервный - периодический `BookingExpiryFallbackCleanupScheduler`,
который батчами (`booking.expiry-batch-size`) проверяет базу напрямую на
случай, если запись в Redis была потеряна.
 
**Автономность Telegram-бота.** У бота собственная локальная таблица
подписок (`TelegramSubscription`), не зависящая от доступности
notification-service во время работы. Интервал напоминаний в Telegram
настраивается независимо от интервала для email-напоминаний.
 
## Структура
 
```
eventify-backend/                  # Multi-module Gradle-проект
├── auth-service/                  # Аутентификация и JWT
├── event-service/                 # Каталог мероприятий
├── booking-service/               # Бронирования
├── notification-service/          # Email, Telegram-привязка, напоминания
├── telegram/                      # Telegram-бот
├── common/                        # Общий JWT-фильтр, security, обработка ошибок
├── service-security/              # Аутентификация сервис-к-сервису
├── kafka-contracts/               # DTO и топики Kafka
├── outbox-support/                # Реализация transactional outbox
├── event-api/                     # Общие DTO для вызова event-service
├── docker-compose.yml             # Полный стек: все сервисы + инфраструктура
├── Dockerfile                     # Единый multi-stage Dockerfile для всех сервисов
├── nginx.conf                     # Маршрутизация и CORS для единой точки входа
└── .env.example                   # Пример переменных окружения
```
 
## Быстрый старт
 
Требуются установленные Docker и Docker Compose.
 
```bash
cd eventify-backend
cp .env.example .env
# заполнить .env: JWT_SECRET, внутренние API-ключи, данные SMTP, токен Telegram-бота
docker compose up -d --build
```
 
Поднимутся все пять сервисов, их базы Postgres, Redis-инстансы, Kafka,
kafbat-ui и nginx. Приложение станет доступно через nginx на
`http://localhost:8080`, Kafka UI — на `http://localhost:8090`.
 
К каждому сервису при желании можно обратиться напрямую по своему порту
(8081–8084), минуя nginx - например, для отладки.
 
## Переменные окружения
 
Файл `eventify-backend/.env`, на основе `.env.example`:
 
| Переменная | Назначение |
|---|---|
| `JWT_SECRET` | Секрет для подписи JWT, общий для всех сервисов |
| `AUTH_POSTGRES_USER` / `AUTH_POSTGRES_PASSWORD` | Доступ к БД auth-service |
| `EVENT_POSTGRES_USER` / `EVENT_POSTGRES_PASSWORD` | Доступ к БД event-service |
| `BOOKING_POSTGRES_USER` / `BOOKING_POSTGRES_PASSWORD` | Доступ к БД booking-service |
| `NOTIFICATION_POSTGRES_USER` / `NOTIFICATION_POSTGRES_PASSWORD` | Доступ к БД notification-service |
| `BOT_POSTGRES_USER` / `BOT_POSTGRES_PASSWORD` | Доступ к БД telegram-бота |
| `EVENT_INTERNAL_API_KEY`, `BOOKING_INTERNAL_API_KEY`, `NOTIFICATION_INTERNAL_API_KEY`, `BOT_INTERNAL_API_KEY` | Ключи для аутентификации внутренних вызовов между сервисами (`openssl rand -base64 32`) |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | Настройки SMTP для отправки писем |
| `BOT_TOKEN` | Токен Telegram-бота, выданный BotFather |
| `BOT_NAME` | Username бота (для ссылки привязки `t.me/<BOT_NAME>`) |
 
## API
 
Все запросы, кроме админских и внутренних, проходят через nginx на
`http://localhost:8080`.
 
### Аутентификация (`auth-service`, префикс `/auth`)
 
| Метод | Путь | Описание |
|---|---|---|
| `POST` | `/auth/register` | Регистрация нового пользователя, возвращает JWT |
| `POST` | `/auth/login` | Вход, возвращает JWT |
 
### Мероприятия (`event-service`, префикс `/events`, админские — `/admin/events`)
 
| Метод | Путь | Описание |
|---|---|---|
| `GET` | `/events` | Список мероприятий с пагинацией, фильтрация по диапазону дат (`from`, `to`) |
| `GET` | `/events/{id}` | Детали мероприятия |
| `POST` | `/admin/events` | Создание мероприятия (Admin) |
| `PUT` | `/admin/events/{id}` | Обновление мероприятия (Admin) |
| `DELETE` | `/admin/events/{id}` | Удаление мероприятия (Admin) |
 
### Бронирования (`booking-service`, префикс `/bookings`, админские — `/admin/bookings`)
 
| Метод | Путь | Описание |
|---|---|---|
| `GET` | `/bookings` | Список своих броней |
| `GET` | `/bookings/{id}` | Детали брони |
| `POST` | `/bookings` | Создание брони (резервирует билеты, выставляет срок подтверждения) |
| `PUT` | `/bookings/{id}` | Обновление брони |
| `DELETE` | `/bookings/{id}` | Отмена брони |
| `GET` | `/admin/bookings` | Все брони с пагинацией; фильтры `eventId`, `unconfirmedOnly` (Admin) |
| `PUT` | `/admin/bookings/{id}/confirm` | Подтверждение брони (Admin) |
| `DELETE` | `/admin/bookings/{id}` | Удаление брони (Admin) |
 
### Уведомления (`notification-service`, префикс `/user`)
 
| Метод | Путь | Описание |
|---|---|---|
| `GET` | `/user/notifications` | Настройки уведомлений текущего пользователя |
| `PUT` | `/user/notifications` | Обновление настроек |
| `DELETE` | `/user/notifications` | Сброс настроек |
| `POST` | `/user/notifications/confirm-email?email=&code=` | Подтверждение email по ссылке из письма |
| `POST` | `/user/notifications/resend-confirmation` | Повторная отправка письма с подтверждением |
| `POST` | `/user/telegram/link` | Получить ссылку для привязки Telegram-аккаунта (`t.me/<bot>?start=<code>`) |
 
Аутентификация — заголовок `Authorization: Bearer <JWT>`, полученный от
`auth-service`. Внутренние (`/internal/...`) и админские эндпоинты в
продакшене должны быть закрыты на уровне nginx/сетевой политики от прямого
внешнего доступа.
 
### Telegram-бот
 
Команды, доступные пользователю в чате с ботом:
 
| Команда | Описание |
|---|---|
| `/help` | Список доступных команд |
| `/my_books` | Список своих бронирований |
| `/notification_time` | Настройка времени напоминания о брони |
| `/stop` | Отписаться от уведомлений |
 
Привязка аккаунта происходит по ссылке `t.me/<BOT_NAME>?start=<code>`,
полученной через `POST /user/telegram/link`.
 
## Разработка
 
Каждый сервис - отдельный Gradle-подпроект в общей сборке
(`settings.gradle.kts`), Java 21. Запуск отдельного сервиса локально (без
Docker) требует поднятых для него Postgres/Redis и Kafka - проще всего
поднять только инфраструктуру через `docker compose up` нужных сервисов из
`docker-compose.yml`, а сам сервис — из IDE с профилем, читающим
`application.yml`.
 
```bash
cd eventify-backend
./gradlew :event-service:bootRun
```
