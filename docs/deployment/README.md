# 🚀 배포 가이드

## 📋 **개요**

Dataracy 백엔드 서비스의 배포 프로세스와 환경 설정을 안내합니다.

---

## 🌍 **환경 구성**

### **환경별 설정**

| 환경         | URL                                  | 브랜치      | 목적           |
| ------------ | ------------------------------------ | ----------- | -------------- |
| **로컬**     | `http://localhost:8080`              | `develop`   | 개발 및 테스트 |
| **개발**     | `https://dev-api.dataracy.store`     | `develop`   | 기능 검증      |
| **스테이징** | `https://staging-api.dataracy.store` | `release/*` | 최종 검증      |
| **운영**     | `https://api.dataracy.store`         | `main`      | 실제 서비스    |

### **인프라 구성**

```
┌─────────────────────────────────────────────────────────────┐
│                    Load Balancer (Nginx)                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐              ┌─────────────┐              │
│  │   Backend   │              │   Backend   │              │
│  │   (Blue)    │              │  (Green)    │              │
│  │   :8081     │              │   :8082     │              │
│  └─────────────┘              └─────────────┘              │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                    External Services                        │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │    MySQL    │  │    Redis    │  │Elasticsearch│        │
│  │   :3306     │  │   :6379     │  │   :9200     │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │    Kafka    │  │     S3      │  │  SendGrid   │        │
│  │   :9092     │  │   (AWS)     │  │   (Email)   │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 **Blue-Green 배포**

### **배포 전략**

Blue-Green 배포는 무중단 배포를 위한 전략으로, 두 개의 동일한 환경을 운영합니다.

#### **배포 단계**

1. **Green 환경 준비**

   - 새 버전을 Green 환경에 배포
   - 헬스체크 및 기능 테스트
   - 데이터베이스 마이그레이션 (필요시)

2. **트래픽 전환**

   - Nginx 설정 변경
   - Blue → Green 트래픽 전환
   - 모니터링 및 검증

3. **Blue 환경 정리**
   - Blue 환경 중지
   - 리소스 정리
   - 다음 배포를 위한 준비

### **배포 스크립트**

#### **개발 환경 배포**

```bash
# 1. 배포 실행
./deployment/scripts/deploy-dev.sh

# 2. 상태 확인
./deployment/scripts/status.sh

# 3. 트래픽 전환
cd deployment/dev/blue-green
./switch-dev.sh
```

#### **운영 환경 배포**

```bash
# 1. 배포 실행
./deployment/scripts/deploy-prod.sh

# 2. 상태 확인
./deployment/scripts/status.sh

# 3. 트래픽 전환
cd deployment/prod/blue-green
./switch-prod.sh
```

---

## 🐳 **Docker 배포**

### **Docker 이미지 빌드**

```bash
# 1. 이미지 빌드
docker build -t dataracy-backend:latest .

# 2. 이미지 태그
docker tag dataracy-backend:latest dataracy-backend:v1.2.0

# 3. 이미지 푸시
docker push dataracy-backend:v1.2.0
```

### **Docker Compose 실행**

#### **개발 환경**

```bash
# 개발 환경 실행
docker-compose -f deployment/dev/docker/docker-compose-blue-dev.yml up -d

# Green 환경 실행
docker-compose -f deployment/dev/docker/docker-compose-green-dev.yml up -d
```

#### **운영 환경**

```bash
# 운영 환경 실행
docker-compose -f deployment/prod/docker/docker-compose-blue-prod.yml up -d

# Green 환경 실행
docker-compose -f deployment/prod/docker/docker-compose-green-prod.yml up -d
```

---

## 🔧 **환경 변수**

### **필수 환경 변수**

#### **데이터베이스**

```bash
# MySQL
MYSQL_URL=jdbc:mysql://localhost:3306/dataracy
MYSQL_USERNAME=root
MYSQL_PASSWORD=password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=password
```

#### **외부 서비스**

```bash
# AWS S3
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_S3_BUCKET=dataracy-files

# SendGrid
SENDGRID_API_KEY=your-sendgrid-key

# JWT
JWT_SECRET=your-jwt-secret
JWT_ACCESS_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=1209600
```

#### **Elasticsearch**

```bash
# Elasticsearch
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200
ELASTICSEARCH_USERNAME=elastic
ELASTICSEARCH_PASSWORD=password
```

### **환경별 설정 파일**

#### **개발 환경**

```yaml
# application-dev.yml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://dev-mysql:3306/dataracy_dev
  redis:
    host: dev-redis
  elasticsearch:
    host: dev-elasticsearch
```

#### **운영 환경**

```yaml
# application-prod.yml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://prod-mysql:3306/dataracy_prod
  redis:
    host: prod-redis
  elasticsearch:
    host: prod-elasticsearch
```

---

## 📊 **모니터링**

### **헬스체크**

#### **애플리케이션 헬스체크**

```bash
# 전체 헬스체크
curl http://localhost:8080/actuator/health

# 데이터베이스 헬스체크
curl http://localhost:8080/actuator/health/db

# Redis 헬스체크
curl http://localhost:8080/actuator/health/redis
```

#### **상세 메트릭**

```bash
# 애플리케이션 메트릭
curl http://localhost:8080/actuator/metrics

# JVM 메트릭
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# HTTP 메트릭
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### **로그 모니터링**

#### **로그 확인**

```bash
# 애플리케이션 로그
docker logs -f dataracy-backend-blue

# 에러 로그 필터링
docker logs dataracy-backend-blue 2>&1 | grep ERROR

# 실시간 로그 모니터링
tail -f logs/system.log
```

#### **로그 레벨 설정**

```yaml
# application.yml
logging:
  level:
    com.dataracy: INFO
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## 🚨 **롤백 전략**

### **자동 롤백**

#### **헬스체크 실패 시**

```bash
# 헬스체크 실패 감지
if ! curl -f http://localhost:8080/actuator/health; then
    echo "Health check failed, rolling back..."
    ./deployment/scripts/rollback.sh
fi
```

#### **에러율 임계값 초과 시**

```bash
# 에러율 모니터링
ERROR_RATE=$(curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq '.measurements[0].value')
if (( $(echo "$ERROR_RATE > 0.05" | bc -l) )); then
    echo "Error rate too high, rolling back..."
    ./deployment/scripts/rollback.sh
fi
```

### **수동 롤백**

#### **즉시 롤백**

```bash
# Blue-Green 전환 (이전 버전으로)
cd deployment/dev/blue-green
./switch-dev.sh

# 또는 운영 환경
cd deployment/prod/blue-green
./switch-prod.sh
```

#### **데이터베이스 롤백**

```bash
# 마이그레이션 롤백
./gradlew flywayUndo

# 또는 특정 버전으로
./gradlew flywayMigrate -Dflyway.target=1.1.0
```

---

## 🔒 **보안**

### **SSL/TLS 설정**

#### **Nginx SSL 설정**

```nginx
server {
    listen 443 ssl http2;
    server_name api.dataracy.store;

    ssl_certificate /etc/ssl/certs/dataracy.crt;
    ssl_certificate_key /etc/ssl/private/dataracy.key;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
}
```

### **방화벽 설정**

#### **포트 제한**

```bash
# 필요한 포트만 개방
ufw allow 22    # SSH
ufw allow 80    # HTTP
ufw allow 443   # HTTPS
ufw allow 8080  # Application (내부)
ufw allow 3306  # MySQL (내부)
ufw allow 6379  # Redis (내부)
ufw allow 9200  # Elasticsearch (내부)
```

### **환경 변수 보안**

#### **시크릿 관리**

```bash
# Docker Secrets 사용
echo "your-secret" | docker secret create jwt_secret -

# 또는 환경 변수 파일
echo "JWT_SECRET=your-secret" > .env
docker-compose --env-file .env up -d
```

---

## 📈 **성능 최적화**

### **JVM 튜닝**

#### **메모리 설정**

```bash
# JVM 옵션
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### **GC 로깅**

```bash
# GC 로그 활성화
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
```

### **데이터베이스 최적화**

#### **연결 풀 설정**

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 📞 **지원 및 연락처**

### **배포 관련 문의**

- **슬랙**: #dataracy-deployment
- **이메일**: deployment@dataracy.store
- **긴급**: +82-10-1234-5678

### **모니터링 도구**

- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090
- **Kibana**: http://localhost:5601

---

**💡 배포 관련 문제가 발생하면 즉시 개발팀에 연락해주세요!**
