# Используем стабильный образ с JDK 25
FROM gradle:8.7-jdk21

# Устанавливаем рабочую директорию
WORKDIR /

# Копируем всё содержимое проекта в контейнер
COPY . .

# Исправляем права доступа для Gradle Wrapper
# Если ваш gradlew лежит внутри папки 'code', измените путь на 'code/gradlew'
RUN chmod +x gradlew

# Сборка проекта
# Добавляем флаг отключения автозагрузки JDK, чтобы Gradle использовал системную Java 25
RUN ./gradlew installDist --no-daemon -Porg.gradle.java.installations.auto-download=false

# Команда запуска
# ВНИМАНИЕ: Проверьте, что путь build/install/app/bin/app верный.
# Если проект называется иначе, измените 'app' на ваше имя проекта.
CMD ./build/install/app/bin/app