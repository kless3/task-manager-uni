# Этап сборки frontend-приложения(используем образ с node)
FROM node:20-alpine AS build

# Рабочая директория внутри build-контейнера
WORKDIR /app

# Копируем зависимости
COPY package*.json ./
RUN npm ci

# Копируем исходники и собираем production-бандл
COPY . .

# Билд-аргумент для адреса backend API
ARG VITE_API_BASE

# Пробрасываем значение в окружение Vite на этапе сборки
ENV VITE_API_BASE=${VITE_API_BASE}
RUN npm run build


# используем образ с nginx
FROM nginx:1.27-alpine

# настраиваем конфигурацию чтобы все корректно работало
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Копируем файлы в рабочую директорию
COPY --from=build /app/dist /usr/share/nginx/html

# Открываем HTTP-порт
EXPOSE 80

# Запускаем Nginx в foreground-режиме
CMD ["nginx", "-g", "daemon off;"]
