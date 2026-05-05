# Используем образ с мавен для сборки через Maven
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Рабочая директория внутри контейнера для сборки
WORKDIR /app

# Копируем файлы, необходимые для сборки
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src

# Собираем jar (тесты пропускаем для ускорения)
RUN mvn -B -Dmaven.test.skip=true package

# Финальный образ только с JRE
FROM eclipse-temurin:21-jre

# Рабочая директория финального контейнера
WORKDIR /app

# Копируем собранный jar из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Запуск Spring Boot приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
