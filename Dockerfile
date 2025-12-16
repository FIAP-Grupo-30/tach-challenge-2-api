FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/api-financeiro-0.0.1-SNAPSHOT.jar"]
