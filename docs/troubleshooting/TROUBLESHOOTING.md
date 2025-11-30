# 🔧 문제 해결 가이드

## 📋 **목차**

- [빌드 및 컴파일 문제](#빌드-및-컴파일-문제)
- [테스트 실행 문제](#테스트-실행-문제)
- [데이터베이스 연결 문제](#데이터베이스-연결-문제)
- [Kafka 연결 문제](#kafka-연결-문제)
- [Redis 연결 문제](#redis-연결-문제)
- [Elasticsearch 연결 문제](#elasticsearch-연결-문제)
- [배포 관련 문제](#배포-관련-문제)
- [성능 문제](#성능-문제)
  - [실제 개선 사례](#4-실제-개선-사례)
- [메모리 문제](#메모리-문제)
- [로그 분석](#로그-분석)

---

## 🔨 **빌드 및 컴파일 문제**

### **1. Gradle 빌드 실패**

**증상:**

```
BUILD FAILED in 2s
1 actionable task: 1 failed
```

**해결 방법:**

```bash
# 1. 캐시 정리
./gradlew clean

# 2. 의존성 새로고침
./gradlew --refresh-dependencies

# 3. Gradle Wrapper 재생성
./gradlew wrapper --gradle-version 8.14.2

# 4. 전체 재빌드
./gradlew clean build
```

### **2. QueryDSL 컴파일 오류**

**증상:**

```
cannot find symbol: class QUserEntity
```

**해결 방법:**

```bash
# 1. QueryDSL 클래스 재생성
./gradlew clean compileJava

# 2. IDE 캐시 정리 (IntelliJ)
File → Invalidate Caches and Restart

# 3. 수동으로 Q 클래스 생성
./gradlew compileQuerydsl
```

### **3. Lombok 관련 오류**

**증상:**

```
cannot find symbol: method getUserId()
```

**해결 방법:**

```bash
# 1. Lombok 플러그인 확인 (IntelliJ)
# Settings → Plugins → Lombok 설치 확인

# 2. Annotation Processing 활성화
# Settings → Build → Compiler → Annotation Processors → Enable

# 3. 프로젝트 재빌드
./gradlew clean build
```

---

## 🧪 **테스트 실행 문제**

### **1. 테스트 실패**

**증상:**

```
Tests run: 100, Failures: 5, Errors: 2, Skipped: 3
```

**해결 방법:**

```bash
# 1. 상세 로그 확인
./gradlew test --info

# 2. 특정 테스트만 실행
./gradlew test --tests "UserServiceTest"

# 3. 테스트 리포트 확인
open build/reports/tests/test/index.html

# 4. 실패한 테스트 디버깅
./gradlew test --tests "UserServiceTest" --debug
```

### **2. H2 데이터베이스 테이블 없음**

**증상:**

```
Table "DATA_ES_PROJECTION_QUEUE" not found
```

**해결 방법:**

```bash
# 1. 테스트 설정 확인
cat src/test/resources/application-test.yml

# 2. H2 콘솔 확인
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb

# 3. 테스트 데이터베이스 재생성
./gradlew clean test
```

### **3. Mock 관련 오류**

**증상:**

```
NullPointerException in test
```

**해결 방법:**

```java
// 1. @Mock 어노테이션 확인
@Mock
private UserRepository userRepository;

// 2. @InjectMocks 대신 수동 주입
@BeforeEach
void setUp() {
    service = new UserService(userRepository, ...);
}

// 3. Mock 설정 확인
given(userRepository.findById(1L)).willReturn(Optional.of(user));
```

---

## 🗄️ **데이터베이스 연결 문제**

### **1. MySQL 연결 실패**

**증상:**

```
Communications link failure
```

**해결 방법:**

```bash
# 1. MySQL 서비스 상태 확인
docker ps | grep mysql

# 2. MySQL 로그 확인
docker logs mysql-container

# 3. 연결 테스트
mysql -h localhost -P 3306 -u root -p

# 4. 환경 변수 확인
echo $DB_HOST
echo $DB_PORT
echo $DB_NAME
echo $DB_USERNAME
echo $DB_PASSWORD
```

### **2. 데이터베이스 스키마 문제**

**증상:**

```
Table 'dataracy.users' doesn't exist
```

**해결 방법:**

```bash
# 1. 스키마 생성
mysql -u root -p -e "CREATE DATABASE dataracy;"

# 2. Flyway 마이그레이션 실행
./gradlew flywayMigrate

# 3. JPA DDL 자동 생성 확인
# application.yml에서 hibernate.ddl-auto: update
```

### **3. 연결 풀 문제**

**증상:**

```
HikariPool-1 - Connection is not available
```

**해결 방법:**

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

## 📨 **Kafka 연결 문제**

### **1. Kafka 브로커 연결 실패**

**증상:**

```
Connection to node -1 (localhost/127.0.0.1:9092) could not be established
```

**해결 방법:**

```bash
# 1. Kafka 서비스 상태 확인
docker ps | grep kafka

# 2. Kafka 로그 확인
docker logs kafka-container

# 3. 토픽 확인
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list

# 4. Kafka 재시작
docker-compose -f infrastructure/kafka/docker-compose.kafka-local.yml restart
```

### **2. 컨슈머 그룹 문제**

**증상:**

```
Consumer group not found
```

**해결 방법:**

```bash
# 1. 컨슈머 그룹 목록 확인
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# 2. 컨슈머 그룹 상태 확인
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group test-group

# 3. 컨슈머 그룹 리셋
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --reset-offsets --to-earliest --group test-group --topic test-topic --execute
```

### **3. 메시지 처리 오류**

**증상:**

```
SerializationException: Can't deserialize data
```

**해결 방법:**

```java
// 1. 직렬화 설정 확인
@Configuration
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}

// 2. 에러 핸들러 설정
@Bean
public DefaultErrorHandler kafkaErrorHandler() {
    return new DefaultErrorHandler((record, exception) -> {
        log.error("Error processing record: {}", record, exception);
    });
}
```

---

## 🔴 **Redis 연결 문제**

### **1. Redis 연결 실패**

**증상:**

```
Unable to connect to Redis
```

**해결 방법:**

```bash
# 1. Redis 서비스 상태 확인
docker ps | grep redis

# 2. Redis 연결 테스트
redis-cli -h localhost -p 6379 ping

# 3. Redis 로그 확인
docker logs redis-container

# 4. Redis 재시작
docker-compose -f infrastructure/redis/redis-compose-local.yml restart
```

### **2. Redis 메모리 부족**

**증상:**

```
OOM command not allowed when used memory > 'maxmemory'
```

**해결 방법:**

```bash
# 1. Redis 메모리 사용량 확인
redis-cli info memory

# 2. 메모리 정리
redis-cli flushdb

# 3. Redis 설정 확인
redis-cli config get maxmemory

# 4. 메모리 제한 증가
redis-cli config set maxmemory 1gb
```

### **3. 분산락 문제**

**증상:**

```
Could not acquire lock
```

**해결 방법:**

```java
// 1. 락 타임아웃 설정
@DistributedLock(key = "user:#{#userId}", waitTime = 3, leaseTime = 10)
public void processUser(Long userId) {
    // 처리 로직
}

// 2. 락 상태 확인
RLock lock = redissonClient.getLock("user:" + userId);
if (lock.isLocked()) {
    log.warn("Lock is already held by another process");
}
```

---

## 🔍 **Elasticsearch 연결 문제**

### **1. Elasticsearch 연결 실패**

**증상:**

```
Connection refused to host: localhost
```

**해결 방법:**

```bash
# 1. Elasticsearch 서비스 상태 확인
docker ps | grep elasticsearch

# 2. Elasticsearch 헬스체크
curl -X GET "localhost:9200/_cluster/health?pretty"

# 3. Elasticsearch 로그 확인
docker logs elasticsearch-container

# 4. Elasticsearch 재시작
docker-compose -f infrastructure/elasticsearch/docker-compose.elasticsearch.yml restart
```

### **2. 인덱스 문제**

**증상:**

```
Index not found exception
```

**해결 방법:**

```bash
# 1. 인덱스 목록 확인
curl -X GET "localhost:9200/_cat/indices?v"

# 2. 인덱스 생성
curl -X PUT "localhost:9200/projects" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "description": { "type": "text" }
    }
  }
}'

# 3. 인덱스 삭제 후 재생성
curl -X DELETE "localhost:9200/projects"
```

### **3. 검색 성능 문제**

**증상:**

```
Search query takes too long
```

**해결 방법:**

```java
// 1. 쿼리 최적화
SearchRequest searchRequest = new SearchRequest("projects");
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.matchQuery("title", keyword))
             .size(20)
             .timeout(TimeValue.timeValueSeconds(5));

// 2. 인덱스 설정 최적화
PUT /projects/_settings
{
  "index": {
    "number_of_replicas": 0,
    "refresh_interval": "30s"
  }
}
```

---

## 🚀 **배포 관련 문제**

### **1. Docker 빌드 실패**

**증상:**

```
Docker build failed
```

**해결 방법:**

```bash
# 1. Docker 이미지 정리
docker system prune -a

# 2. 빌드 캐시 무시
docker build --no-cache -t dataracy-backend .

# 3. Dockerfile 확인
cat Dockerfile

# 4. 단계별 빌드
docker build --target build -t dataracy-build .
docker build --target runtime -t dataracy-backend .
```

### **2. Blue-Green 배포 실패**

**증상:**

```
Deployment failed during switch
```

**해결 방법:**

```bash
# 1. 현재 상태 확인
~/dataracy-dev/deployment/scripts/status.sh

# 2. 수동 스위치
~/dataracy-dev/deployment/dev/blue-green/switch-dev.sh

# 3. 로그 확인
docker logs backend-blue
docker logs backend-green

# 4. 롤백
~/dataracy-dev/deployment/dev/blue-green/switch-dev.sh  # 다시 실행하여 이전 버전으로 복구
```

### **3. 헬스체크 실패**

**증상:**

```
Health check failed
```

**해결 방법:**

```bash
# 1. 헬스체크 엔드포인트 확인
curl http://localhost:8080/actuator/health

# 2. 애플리케이션 로그 확인
docker logs backend-blue

# 3. 데이터베이스 연결 확인
curl http://localhost:8080/actuator/health/db

# 4. 헬스체크 설정 확인
# application.yml에서 management.endpoints.web.exposure.include=health
```

---

## ⚡ **성능 문제**

### **1. 응답 시간 지연**

**증상:**

```
API response time > 5s
```

**해결 방법:**

```bash
# 1. JVM 메모리 설정 확인
java -XX:+PrintFlagsFinal -version | grep -i heap

# 2. GC 로그 확인
-XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps

# 3. 프로파일링
jstack <pid>
jmap -histo <pid>

# 4. 데이터베이스 쿼리 최적화
# application.yml에서 spring.jpa.show-sql=true
```

### **2. 메모리 사용량 증가**

**증상:**

```
OutOfMemoryError: Java heap space
```

**해결 방법:**

```bash
# 1. 힙 덤프 생성
jmap -dump:format=b,file=heap.hprof <pid>

# 2. 메모리 사용량 분석
jstat -gc <pid> 1s

# 3. JVM 옵션 조정
-Xms2g -Xmx4g -XX:+UseG1GC

# 4. 애플리케이션 재시작
docker restart backend-blue
```

### **3. 데이터베이스 성능 문제**

**증상:**

```
Slow query detected
```

**해결 방법:**

```sql
-- 1. 느린 쿼리 확인
SHOW PROCESSLIST;

-- 2. 인덱스 확인
SHOW INDEX FROM users;

-- 3. 쿼리 실행 계획 확인
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';

-- 4. 인덱스 생성
CREATE INDEX idx_user_email ON users(email);
```

### **4. 실제 개선 사례**

#### **📌 사례 1: 인기 프로젝트 조회 Redis 캐싱으로 응답 속도 15배 개선**

**문제 상황:**

- N+1 문제로 평균 **139.23ms** 소요 (프로젝트 5개 조회 시 **31개 쿼리** 발생)
- 매 요청마다 DB 조회로 서버 부하 증가

**해결 방법:**

1. **N+1 개선**: 배치 쿼리로 31개 → 7개 쿼리 감소 (77.4% 감소)
2. **Redis 캐싱 도입**: 5분 주기 배치 갱신, 10분 TTL
3. **k6 성능 테스트**: Smoke(1 VU) + Load(10 VUs) 단계별 검증

**성과:**

- 🚀 **평균 응답시간 93.4% 개선** (139.23ms → 9.18ms, 15배 빠름)
- 📉 **최대 응답시간 98.1% 개선** (823.68ms → 15.72ms, 52배 빠름)
- 💾 **DB 쿼리 수 100% 제거** (31개 → 0개, 캐시 히트 시)
- 📈 **Load 테스트 4,468회 요청 100% 성공** (10 VUs × 60초)

#### **📌 사례 2: Rate Limiting으로 무차별 대입 공격 100% 차단**

**문제 상황:**

- k6 공격 시뮬레이션에서 **638회 시도 중 8개 계정 뚫림** (약 1.3% 성공률)
- 무차별 대입 공격에 대한 방어 메커니즘 부재
- 모든 시도에 BCrypt 검증 수행 → 서버 리소스 낭비
- 평균 응답시간: **120ms**

**해결 방법:**

1. **1단계 (Memory 기반)**: ConcurrentHashMap으로 프로토타입 검증
   - k6 결과: 819회 시도, avg=37.24ms, 571회 차단, 0개 성공
2. **2단계 (Redis 분산)**: RedisTemplate 기반 카운터 구현
   - k6 결과: 803회 시도, avg=39.12ms, 552회 차단, 0개 성공
3. **정책**: email:IP 조합으로 각 계정당 5회/분 제한
4. **Port & Adapter 패턴**: 테스트 용이성과 확장성 확보

**k6 테스트 결과 비교:**

| 단계               | 평균 응답시간 | 최대 응답시간 | BCrypt 검증 | 429 차단 | 공격 성공 | 총 요청 수 |
| ------------------ | ------------- | ------------- | ----------- | -------- | --------- | ---------- |
| **Before**         | 120ms         | 246ms         | 638회       | 0개      | **8개**   | 638회      |
| **After (Memory)** | 37.24ms       | 152ms         | 248회       | 571개    | **0개**   | 819회      |
| **After (Redis)**  | 39.12ms       | 163ms         | 251회       | 552개    | **0개**   | 803회      |

**성과:**

- 🛡️ **공격 성공 100% 차단** (8개 → 0개)
- ⚡ **응답 시간 67.4% 개선** (120ms → 39.12ms)
- 💾 **BCrypt 검증 60.7% 감소** (638회 → 251회)
- 🚫 **차단 효과 68.7%** (552회 차단)
- 📈 **처리량 25.9% 증가** (638회 → 803회)

**비즈니스 임팩트:**

- 무차별 대입 공격 방어로 계정 탈취 위험 제거
- 서버 리소스 보호로 서비스 안정성 확보
- 빠른 차단으로 정상 사용자 경험 개선

**상세 문서:**

- [인기 프로젝트 조회 성능 최적화](https://velog.io/@juuuunny/%EC%9D%B8%EA%B8%B0-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%A1%B0%ED%9A%8C-API-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94%ED%95%98%EA%B8%B0-N1-%EB%AC%B8%EC%A0%9C%EC%99%80-%EC%BA%90%EC%8B%B1%EC%9C%BC%EB%A1%9C-93-%EA%B0%9C%EC%84%A0)
- [Rate Limiting으로 무차별 대입 공격 방어하기](https://velog.io/@juuuunny/%ED%95%98%EB%A3%A8%EC%97%90-10%EB%A7%8C-%EB%B2%88-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%8B%9C%EB%8F%84%EC%9A%94-Rate-Limiting%EC%9C%BC%EB%A1%9C-%EB%AC%B4%EC%B0%A8%EB%B3%84-%EB%8C%80%EC%9E%85-%EA%B3%B5%EA%B2%A9-%EB%A7%89%EA%B8%B0)

---

## 📊 **로그 분석**

### **1. 애플리케이션 로그**

**로그 위치:**

```bash
# 로컬
tail -f logs/system.log

# Docker
docker logs -f backend-blue

# Kubernetes
kubectl logs -f deployment/dataracy-backend
```

**로그 레벨 설정:**

```yaml
# application.yml
logging:
  level:
    com.dataracy: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### **2. 에러 로그 분석**

**일반적인 에러 패턴:**

```bash
# NullPointerException
grep -n "NullPointerException" logs/system-error.log

# SQL 오류
grep -n "SQLException" logs/system-error.log

# 연결 오류
grep -n "Connection" logs/system-error.log
```

### **3. 성능 로그 분석**

**응답 시간 분석:**

```bash
# 느린 요청 찾기
grep "took.*ms" logs/system.log | sort -k5 -nr | head -10

# 에러율 계산
grep -c "ERROR" logs/system.log
grep -c "INFO" logs/system.log
```

---

## 🆘 **긴급 상황 대응**

### **1. 서비스 다운**

**즉시 조치:**

```bash
# 1. 서비스 상태 확인
~/dataracy-dev/deployment/scripts/status.sh

# 2. 로그 확인
docker logs --tail 100 backend-blue

# 3. 롤백 실행
~/dataracy-dev/deployment/dev/blue-green/switch-dev.sh

# 4. 모니터링 확인
curl http://localhost:8080/actuator/health
```

### **2. 데이터베이스 문제**

**즉시 조치:**

```bash
# 1. 데이터베이스 연결 확인
mysql -h localhost -P 3306 -u root -p

# 2. 백업 확인
ls -la /backup/

# 3. 복구 실행 (필요시)
mysql -u root -p dataracy < /backup/dataracy_backup.sql
```

### **3. 메모리 부족**

**즉시 조치:**

```bash
# 1. 메모리 사용량 확인
free -h
docker stats

# 2. 불필요한 컨테이너 정리
docker system prune -f

# 3. 애플리케이션 재시작
docker restart backend-blue
```

---

## 📞 **지원 및 연락처**

### **개발팀 연락처**

- **슬랙**: #dataracy-dev
- **이메일**: dev-team@dataracy.store
- **긴급**: +82-10-1234-5678

### **모니터링 도구**

- **Prometheus**: http://localhost:9090
- **Kibana**: http://localhost:5601

### **문서 및 리소스**

- **API 문서**: https://api.dataracy.co.kr/swagger-ui.html
- **개발 가이드**: ./docs/development/README.md
- **배포 가이드**: ./docs/deployment/README.md

---

**💡 문제가 지속되면 로그와 함께 개발팀에 문의해주세요!**
