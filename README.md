### Hexlet tests and linter status:
[![Actions Status](https://github.com/KirillSosnyuk/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/KirillSosnyuk/java-project-99/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=KirillSosnyuk_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=KirillSosnyuk_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=KirillSosnyuk_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=KirillSosnyuk_java-project-99)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=KirillSosnyuk_java-project-99&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=KirillSosnyuk_java-project-99)

https://java-project-99-p2lf.onrender.com

### Деплой на Render

В репозитории не задаётся активный Spring-профиль: по умолчанию Boot использует профиль `default`, а фрагменты `application-prod.yml` / `application-dev.yml` подключаются **только** если включён соответствующий профиль.

Для продакшена на Render в переменных окружения сервиса укажите **`SPRING_PROFILES_ACTIVE=prod`**, чтобы подтянулись настройки из `application-prod.yml` (в т.ч. `spring.datasource.*` из `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, `JDBC_DATABASE_PASSWORD`, которые Render выдаёт для подключённой PostgreSQL).

Для локальной разработки с H2 включите профиль **`dev`** (например, `SPRING_PROFILES_ACTIVE=dev` в Run Configuration или в `.env`), чтобы использовался `application-dev.yml`.

### Docker

Multi-stage Dockerfile: **stage build** — JDK + `./gradlew bootJar`; **stage runtime** — только JRE и `app.jar` (без Gradle и исходников). См. [multi-stage builds](https://docs.docker.com/build/building/multi-stage/).

```bash
docker build -t java-project-99 .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -e JDBC_DATABASE_URL=... java-project-99
```

### Observability (traceId в логах)

Подключены Micrometer Tracing + OpenTelemetry (OTLP). В логах каждый запрос помечается `traceId` / `spanId` (см. `logging.pattern.level` в `application.yml`). Для экспорта в Sentry/коллектор задайте `OTEL_EXPORTER_OTLP_ENDPOINT` на Render. Уже настроен Sentry DSN для ошибок.