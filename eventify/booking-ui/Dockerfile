# Используем официальный Node.js образ для сборки
FROM node:18-alpine AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы зависимостей
COPY package*.json ./

# Устанавливаем зависимости
RUN npm ci --only=production

# Копируем исходный код
COPY . .

ARG REACT_APP_API_URL
ARG REACT_APP_USE_MOCKS
ENV REACT_APP_API_URL=$REACT_APP_API_URL
ENV REACT_APP_USE_MOCKS=$REACT_APP_USE_MOCKS

# Собираем приложение
RUN npm run build

# Используем nginx для раздачи статических файлов
FROM nginx:alpine

# Копируем собранные файлы из builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Копируем конфигурацию nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Открываем порт 80
EXPOSE 80

# Запускаем nginx
CMD ["nginx", "-g", "daemon off;"] 