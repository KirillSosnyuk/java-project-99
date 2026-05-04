# Используем образ с JDK 23
FROM eclipse-temurin:23-jdk

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем все файлы проекта
COPY . .

# Даем права на выполнение Gradle Wrapper (важно для CI/CD и пайплайнов)
RUN chmod +x gradlew

# Собираем проект через Wrapper, чтобы не зависеть от системного gradle.
# Флаг --no-daemon критически важен для Docker, чтобы не оставлять фоновых процессов.
RUN ./gradlew installDist --no-daemon

# Команда запуска.
# Убедитесь, что имя 'app' совпадает с названием проекта в settings.gradle.kts
CMD ./build/install/app/bin/app