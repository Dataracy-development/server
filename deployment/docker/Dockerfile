# Dockerfile
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 빌드된 JAR 복사
COPY build/libs/*.jar app.jar

# 설정 파일 복사
COPY src/main/resources/application.yml /app/application.yml
COPY src/main/resources/application-prod.yml /app/application-prod.yml

EXPOSE 8080

CMD ["java", "-Dspring.config.additional-location=/app/application-prod.yml", "-jar", "app.jar"]
