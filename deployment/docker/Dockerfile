FROM openjdk:17-jdk-slim
WORKDIR /app

COPY build/libs/dataracy-*.jar app.jar

# 📌 설정 파일 복사
COPY src/main/resources/application*.yml ./

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
