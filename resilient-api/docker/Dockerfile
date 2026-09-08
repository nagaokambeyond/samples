FROM gradle:9.4.1-jdk21 AS build
WORKDIR /workspace
COPY build.gradle settings.gradle .
COPY src src
RUN gradle --no-daemon test bootJar

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/build/libs/resilient-api-local-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
