# 테스트 실행 가이드

## 📋 목차
1. [테스트 실행 명령어](#테스트-실행-명령어)
2. [테스트 환경 설정](#테스트-환경-설정)
3. [테스트 결과 분석](#테스트-결과-분석)
4. [문제 해결 가이드](#문제-해결-가이드)
5. [CI/CD 통합](#cicd-통합)

---

## 🚀 테스트 실행 명령어

### 기본 테스트 실행
```bash
# 전체 테스트 실행
./gradlew test

# 테스트 실행 (상세 로그)
./gradlew test --info

# 테스트 실행 (병렬 처리)
./gradlew test --parallel

# 테스트 실행 (특정 JVM 옵션)
./gradlew test -Dorg.gradle.jvmargs="-Xmx2g"
```

### 특정 테스트 실행
```bash
# 특정 클래스 테스트
./gradlew test --tests "com.dataracy.modules.user.UserServiceTest"

# 특정 메서드 테스트
./gradlew test --tests "com.dataracy.modules.user.UserServiceTest.createUser"

# 패턴 매칭 테스트
./gradlew test --tests "*UserService*"

# 통합 테스트만 실행
./gradlew test --tests "*IntegrationTest"

# 단위 테스트만 실행 (통합 테스트 제외)
./gradlew test --tests "*Test" --exclude-task "*IntegrationTest"
```

### 커버리지 포함 실행
```bash
# 테스트 + 커버리지 리포트 생성
./gradlew clean test jacocoTestReport

# 커버리지 리포트 확인
open build/reports/jacoco/test/html/index.html

# 커버리지 임계값 설정
./gradlew test jacocoTestReport -Pjacoco.minimum.coverage=80
```

### 성능 테스트 실행
```bash
# 성능 테스트 실행
./gradlew test --tests "*PerformanceTest"

# 메모리 사용량 모니터링
./gradlew test -Dorg.gradle.jvmargs="-Xmx1g -XX:+PrintGCDetails"

# 테스트 실행 시간 측정
time ./gradlew test
```

---

## ⚙️ 테스트 환경 설정

### 1. 테스트 프로파일 설정
```yaml
# src/test/resources/application-test.yml
spring:
  profiles:
    active: test
  
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
  
  redis:
    host: localhost
    port: 6379
    database: 1
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

logging:
  level:
    com.dataracy: DEBUG
    org.springframework.test: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 2. 테스트 데이터베이스 설정
```sql
-- src/test/resources/schema.sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- src/test/resources/data.sql
INSERT INTO users (email, nickname, password, role) VALUES
('admin@example.com', '관리자', 'encoded_password', 'ROLE_ADMIN'),
('user@example.com', '일반사용자', 'encoded_password', 'ROLE_USER');
```

### 3. 테스트 설정 클래스
```java
@TestConfiguration
@EnableJpaRepositories(basePackages = "com.dataracy.modules")
@EntityScan(basePackages = "com.dataracy.modules")
public class TestConfig {
    
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
    }
}
```

---

## 📊 테스트 결과 분석

### 1. 테스트 리포트 확인
```bash
# HTML 테스트 리포트 생성
./gradlew test --info

# 리포트 위치
open build/reports/tests/test/index.html
```

### 2. 커버리지 리포트 분석
```bash
# 커버리지 리포트 생성
./gradlew jacocoTestReport

# 커버리지 리포트 확인
open build/reports/jacoco/test/html/index.html

# 커버리지 요약 확인
cat build/reports/jacoco/test/jacocoTestReport.xml
```

### 3. 테스트 성능 분석
```bash
# 테스트 실행 시간 측정
./gradlew test --profile

# 느린 테스트 찾기
./gradlew test --info | grep "took"
```

### 4. 메모리 사용량 분석
```bash
# 메모리 사용량 모니터링
./gradlew test -Dorg.gradle.jvmargs="-Xmx1g -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# 힙 덤프 생성 (문제 발생 시)
./gradlew test -Dorg.gradle.jvmargs="-Xmx1g -XX:+HeapDumpOnOutOfMemoryError"
```

---

## 🔧 문제 해결 가이드

### 1. 테스트 실패 문제

#### 테스트가 간헐적으로 실패하는 경우
```bash
# 테스트를 여러 번 실행하여 재현성 확인
for i in {1..10}; do
  echo "Test run $i"
  ./gradlew test --tests "ProblematicTest"
done
```

**해결 방법:**
- 테스트 격리 확인
- 시간 의존성 제거
- 랜덤 데이터 사용
- Mock 설정 검토

#### 메모리 부족 오류
```bash
# JVM 힙 크기 증가
./gradlew test -Dorg.gradle.jvmargs="-Xmx2g -XX:MaxMetaspaceSize=512m"

# GC 옵션 조정
./gradlew test -Dorg.gradle.jvmargs="-Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### 데이터베이스 연결 오류
```bash
# 테스트 데이터베이스 상태 확인
./gradlew test --debug

# H2 데이터베이스 웹 콘솔 활성화
# application-test.yml에 추가:
# spring.h2.console.enabled=true
```

### 2. 성능 문제

#### 테스트 실행이 느린 경우
```bash
# 병렬 테스트 실행
./gradlew test --parallel --max-workers=4

# 특정 테스트만 실행
./gradlew test --tests "*UnitTest"

# 테스트 결과 캐시 활용
./gradlew test --build-cache
```

#### 메모리 누수 문제
```bash
# 메모리 사용량 모니터링
./gradlew test -Dorg.gradle.jvmargs="-Xmx1g -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# 힙 덤프 분석
jhat build/heap-dump.hprof
```

### 3. 환경 문제

#### 포트 충돌 문제
```bash
# 사용 중인 포트 확인
lsof -i :8080
lsof -i :9092

# 다른 포트 사용
./gradlew test -Dserver.port=8081
```

#### 파일 권한 문제
```bash
# 테스트 디렉토리 권한 확인
ls -la build/test-results/
chmod -R 755 build/
```

---

## 🔄 CI/CD 통합

### 1. GitHub Actions 설정
```yaml
# .github/workflows/test.yml
name: Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Generate coverage report
      run: ./gradlew jacocoTestReport
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: build/reports/jacoco/test/jacocoTestReport.xml
```

### 2. Jenkins Pipeline 설정
```groovy
// Jenkinsfile
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh './gradlew clean test'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'build/test-results/test/*.xml'
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }
        
        stage('Coverage') {
            steps {
                sh './gradlew jacocoTestReport'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'Coverage Report'
                    ])
                }
            }
        }
    }
}
```

### 3. 테스트 결과 알림
```bash
# Slack 알림 설정
curl -X POST -H 'Content-type: application/json' \
--data '{"text":"테스트 실행 완료: '$(./gradlew test --quiet | tail -1)'"}' \
https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK

# 이메일 알림 설정
echo "테스트 실행 완료" | mail -s "Test Results" team@company.com
```

---

## 📈 테스트 모니터링

### 1. 테스트 실행 통계
```bash
# 테스트 실행 시간 통계
./gradlew test --profile | grep "took"

# 테스트 성공/실패 통계
./gradlew test | grep -E "(PASSED|FAILED|SKIPPED)"

# 커버리지 통계
./gradlew jacocoTestReport && cat build/reports/jacoco/test/jacocoTestReport.xml | grep -o 'line-rate="[^"]*"'
```

### 2. 테스트 트렌드 분석
```bash
# 일별 테스트 실행 로그
echo "$(date): $(./gradlew test --quiet | tail -1)" >> test-results.log

# 주간 테스트 리포트 생성
./gradlew test --info > test-results-$(date +%Y-%m-%d).log
```

### 3. 성능 벤치마크
```bash
# 테스트 실행 시간 벤치마크
time ./gradlew test > benchmark-$(date +%Y%m%d-%H%M%S).log 2>&1

# 메모리 사용량 벤치마크
./gradlew test -Dorg.gradle.jvmargs="-Xmx1g -XX:+PrintGCDetails" > memory-$(date +%Y%m%d-%H%M%S).log 2>&1
```

---

## 🎯 결론

이 가이드를 따라 테스트를 실행하고 문제를 해결하세요.

**핵심 포인트:**
1. **정기적인 테스트 실행**: CI/CD 파이프라인에 통합
2. **성능 모니터링**: 테스트 실행 시간과 메모리 사용량 추적
3. **문제 신속 해결**: 실패한 테스트를 즉시 수정
4. **커버리지 유지**: 최소 80% 이상의 코드 커버리지 유지
5. **문서화**: 테스트 실행 결과와 문제 해결 과정 기록

