# 🛠️ 개발 환경 설정

## 📋 **개요**

Dataracy 백엔드 개발을 위한 로컬 개발 환경 설정 가이드입니다.

---

## 🔧 **필수 요구사항**

### **소프트웨어 요구사항**

- **Java**: 17 이상
- **Gradle**: 8.14.2 이상
- **Docker**: 24.0 이상
- **Docker Compose**: 2.0 이상
- **Git**: 2.30 이상

### **하드웨어 요구사항**

- **RAM**: 8GB 이상 (16GB 권장)
- **디스크**: 20GB 이상 여유 공간
- **CPU**: 4코어 이상

---

## 🚀 **환경 설정 단계**

### **1. 프로젝트 클론**

```bash
# 프로젝트 클론
git clone https://github.com/your-org/dataracy-server.git
cd dataracy-server

# 브랜치 확인
git branch -a
```

### **2. 환경 변수 설정**

```bash
# 환경 변수 파일 복사
cp .env.example .env.local

# 환경 변수 편집
vim .env.local
```

**필수 환경 변수**:

```bash
# 서버 설정
SERVER_HOST=localhost
SERVER_PORT=8080

# 데이터베이스
DB_HOST=localhost
DB_PORT=3306
DB_NAME=dataracy_local
DB_USERNAME=root
DB_PASSWORD=password

# Redis
REDIS_PROTOCOL=redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_SERVER_HOST=localhost
KAFKA_SERVER_PORT=9092

# Elasticsearch
ELASTIC_SEARCH_HOST=localhost
ELASTIC_SEARCH_PORT=9200

# JWT
JWT_SECRET=your-jwt-secret-key
ACCESS_TOKEN_EXPIRATION_TIME=3600000
REFRESH_TOKEN_EXPIRATION_TIME=1209600000
REGISTER_TOKEN_EXPIRATION_TIME=600000
RESET_TOKEN_EXPIRATION_TIME=600000

# AWS S3
AWS_S3_BUCKET_NAME=dataracy-bucket-local
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
AWS_REGION=ap-northeast-2
```

### **3. 인프라 서비스 시작**

```bash
# Docker 네트워크 생성 (필요시)
docker network create dataracy-network

# Kafka 시작
docker-compose -f infrastructure/kafka/docker-compose.kafka-local.yml up -d

# Redis 시작
docker-compose -f infrastructure/redis/redis-compose-local.yml up -d

# Elasticsearch 시작
docker-compose -f infrastructure/elasticsearch/docker-compose.elasticsearch.yml up -d

# 서비스 상태 확인
docker ps
```

### **4. 데이터베이스 설정**

```bash
# 로컬 MySQL 사용 (권장)
mysql -u root -p -e "CREATE DATABASE dataracy_local;"

# 또는 Docker MySQL 컨테이너 사용
docker run --name mysql-local -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=dataracy_local -p 3306:3306 -d mysql:8.0
```

### **5. 애플리케이션 실행**

```bash
# 의존성 다운로드
./gradlew dependencies

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'

# 또는 IDE에서 실행
# Run Configuration: --spring.profiles.active=local
```

---

## 🔍 **환경 확인**

### **1. 서비스 상태 확인**

```bash
# Docker 컨테이너 상태
docker ps

# 포트 사용 확인
netstat -an | grep -E "(8080|3306|6379|9092|9200)"

# 서비스 헬스체크
curl http://localhost:8080/actuator/health
```

### **2. 데이터베이스 연결 확인**

```bash
# MySQL 연결 테스트
mysql -h localhost -P 3306 -u root -p dataracy_local

# Redis 연결 테스트
redis-cli -h localhost -p 6379 ping

# Elasticsearch 연결 테스트
curl http://localhost:9200/_cluster/health
```

### **3. Kafka 연결 확인**

```bash
# Kafka 토픽 목록 확인
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list

# Kafka 컨슈머 그룹 확인
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

---

## 🛠️ **IDE 설정**

### **IntelliJ IDEA 설정**

1. **프로젝트 열기**: `File → Open → build.gradle`
2. **Lombok 플러그인**: `Settings → Plugins → Lombok`
3. **Annotation Processing**: `Settings → Build → Compiler → Annotation Processors → Enable`
4. **코드 스타일**: `Settings → Editor → Code Style → Java → Google Style`

### **VS Code 설정**

1. **Java Extension Pack** 설치
2. **Spring Boot Extension Pack** 설치
3. **Lombok Annotations Support** 설치

---

## 🧪 **테스트 환경 설정**

### **1. 테스트 실행**

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "UserServiceTest"

# 커버리지 포함 테스트
./test-coverage.sh
```

### **2. 테스트 데이터베이스**

- **H2 인메모리**: 테스트용 자동 설정 (기본값)
- **MySQL**: 통합 테스트용 (선택사항)

### **3. 테스트 프로파일**

```yaml
# src/test/resources/application-test.yml
spring:
  profiles:
    active: test
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
```

---

## 🔧 **개발 도구 설정**

### **1. 코드 품질 도구**

```bash
# 코드 스타일 검사
./gradlew checkstyleMain checkstyleTest

# 코드 포맷팅
./gradlew spotlessApply

# 커버리지 리포트
./gradlew jacocoTestReport
```

### **2. Git Hooks 설정**

```bash
# pre-commit hook 설정
cp scripts/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit
```

### **3. 디버깅 설정**

```bash
# 디버그 모드로 실행
./gradlew bootRun --debug-jvm

# 원격 디버깅
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

---

## 🚨 **문제 해결**

### **1. 포트 충돌**

```bash
# 포트 사용 확인
lsof -i :8080
lsof -i :3306
lsof -i :6379

# 프로세스 종료
kill -9 <PID>
```

### **2. Docker 문제**

```bash
# Docker 재시작
sudo systemctl restart docker

# 컨테이너 정리
docker system prune -a

# 볼륨 정리
docker volume prune
```

### **3. 의존성 문제**

```bash
# Gradle 캐시 정리
./gradlew clean

# 의존성 새로고침
./gradlew --refresh-dependencies

# Gradle Wrapper 재생성
./gradlew wrapper --gradle-version 8.14.2
```

### **4. 메모리 부족**

```bash
# JVM 메모리 설정
export GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=512m"

# 또는 gradle.properties에 추가
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
```

---

## 📊 **성능 최적화**

### **1. 빌드 성능**

```bash
# Gradle 데몬 사용
./gradlew --daemon

# 병렬 빌드
./gradlew --parallel

# 빌드 캐시 사용
./gradlew --build-cache
```

### **2. 테스트 성능**

```bash
# 병렬 테스트
./gradlew test --parallel

# 테스트 분산 실행
./gradlew test --max-workers=4
```

### **3. IDE 성능**

```bash
# IntelliJ IDEA 메모리 설정
# Help → Edit Custom VM Options
-Xmx4g
-XX:MaxMetaspaceSize=512m
```

---

## 🔄 **개발 워크플로우**

### **1. 일상 개발**

```bash
# 코드 작성
# 테스트 실행
./gradlew test

# 코드 품질 검사
./gradlew checkstyleMain spotlessCheck

# 커밋
git add .
git commit -m "feat: 새로운 기능 추가"
```

### **2. PR 전 확인**

```bash
# 전체 테스트 실행
./test-coverage.sh

# 코드 품질 검사
./gradlew checkstyleMain checkstyleTest spotlessCheck

# 빌드 확인
./gradlew clean build
```

---

## 📞 **지원 및 문의**

### **개발팀 연락처**

- **슬랙**: #dataracy-dev
- **이메일**: dev-team@dataracy.store
- **긴급**: +82-10-1234-5678

### **유용한 링크**

- **API 문서**: http://localhost:8080/swagger-ui.html
- **모니터링**: http://localhost:8080/actuator
- **Kibana**: http://localhost:5601 (Elasticsearch 대시보드)

---

**💡 환경 설정 관련 문제가 발생하면 개발팀에 문의하세요!**
