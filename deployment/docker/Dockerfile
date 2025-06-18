FROM openjdk:17-jdk-slim
WORKDIR /app

COPY build/libs/dataracy-*.jar app.jar

# 📌 application-prod.yml을 jar 외부에서 인식하도록 분리 (선택사항)
COPY src/main/resources/application*.yml ./
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

CMD ["java", "-Dspring.config.additional-location=application-prod.yml", "-jar", "app.jar"]
