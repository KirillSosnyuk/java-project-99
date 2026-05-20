# Stage 1: сборка (JDK + Gradle wrapper). Gradle в финальный образ не попадает.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q || true

COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon \
    -Porg.gradle.java.installations.auto-download=false

# Stage 2: runtime (только JRE + fat-jar), по образцу multi-stage из Docker docs / project-5
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/build/libs/app-0.0.1-SNAPSHOT.jar ./app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
