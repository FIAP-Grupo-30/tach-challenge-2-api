FROM gradle:8.11-jdk21
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/api-financeiro-0.0.1-SNAPSHOT.jar"]
