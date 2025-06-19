# deployment/docker/Dockerfile

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY build/libs/*.jar app.jar
COPY src/main/resources/application.yml /app/application.yml
COPY src/main/resources/application-prod.yml /app/application-prod.yml

EXPOSE 8080

CMD ["java", "-Dspring.config.additional-location=/app/application-prod.yml", "-jar", "app.jar"]
