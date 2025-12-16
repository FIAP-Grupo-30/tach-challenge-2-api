# Build stage
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
WORKDIR /app
FROM eclipse-temurin:21-jre-alpine
# Run stage

RUN gradle bootJar --no-daemon
COPY . .
WORKDIR /app
FROM gradle:8.11-jdk21 AS build

