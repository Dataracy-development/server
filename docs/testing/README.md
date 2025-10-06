# 🧪 테스트 가이드

## 📋 **개요**

Dataracy 백엔드 프로젝트의 실제 구현된 테스트 전략과 실행 방법을 안내합니다.

---

## 🎯 **실제 테스트 전략**

### **테스트 피라미드**

```
    🔺 Performance Tests (5%) - k6 기반
   🔺🔺 Integration Tests (25%) - Spring Boot Test
  🔺🔺🔺 Unit Tests (70%) - JUnit 5 + Mockito
```

### **실제 테스트 유형**

#### **단위 테스트 (Unit Tests)**

- **목적**: 개별 메서드/클래스 검증
- **범위**: 도메인 모델, 서비스 로직, 유틸리티
- **도구**: JUnit 5, Mockito, AssertJ
- **목표**: 82.5% 커버리지 (실제 기준)
- **실제 예시**: `UserTest.java`, `EmailContentFactoryTest.java`

#### **통합 테스트 (Integration Tests)**

- **목적**: 컴포넌트 간 상호작용 검증
- **범위**: 서비스 계층, 포트 어댑터
- **도구**: Spring Boot Test, MockitoExtension
- **목표**: 핵심 비즈니스 플로우 검증
- **실제 예시**: `EmailCommandServiceTest.java`, `DataCommandServiceTest.java`

#### **성능 테스트 (Performance Tests)**

- **목적**: API 성능 및 부하 검증
- **범위**: 실제 API 엔드포인트
- **도구**: k6, 실제 시나리오 기반
- **목표**: 응답 시간, 처리량, 안정성 검증
- **실제 예시**: `login.test.js`, `login-abuse.test.js`, `project-popular-read.test.js`

---

## 🛠️ **실제 테스트 도구**

### **핵심 도구**

#### **JUnit 5 (실제 사용 패턴)**

```java
// 실제 UserTest.java 기반
@Test
@DisplayName("isPasswordMatch - 비밀번호가 일치하는 경우 true를 반환한다")
void isPasswordMatchWhenPasswordMatchesReturnsTrue() {
    // given
    PasswordEncoder encoder = mock(PasswordEncoder.class);
    String rawPassword = "password1";
    String encodedPassword = "encodedPassword1";

    User user = User.builder().password(encodedPassword).build();

    given(encoder.matches(rawPassword, encodedPassword)).willReturn(true);

    // when
    boolean result = user.isPasswordMatch(encoder, rawPassword);

    // then
    assertThat(result).isTrue();
}
```

#### **Mockito (실제 사용 패턴)**

```java
// 실제 EmailCommandServiceTest.java 기반
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailCommandServiceTest {

    @Mock private SendEmailPort sendEmailPort;
    @Mock private ManageEmailCodePort manageEmailCodePort;

    private EmailCommandService emailCommandService;

    @BeforeEach
    void setUp() {
        emailCommandService = new EmailCommandService(sendEmailPort, manageEmailCodePort);
    }

    @Test
    @DisplayName("성공: 이메일 인증 코드 전송 성공")
    void sendEmailVerificationCodeNormalSendingReturnsSuccess() {
        // given
        String email = "test@example.com";
        EmailVerificationType type = EmailVerificationType.SIGN_UP;

        // when
        emailCommandService.sendEmailVerificationCode(email, type);

        // then
        then(sendEmailPort).should().send(anyString(), anyString(), anyString());
        then(manageEmailCodePort)
            .should()
            .saveCode(anyString(), anyString(), any(EmailVerificationType.class));
    }
}
```

#### **AssertJ (실제 사용 패턴)**

```java
// 실제 EmailContentFactoryTest.java 기반
@Test
@DisplayName("성공: SIGN_UP 타입 이메일 내용 생성")
void generateWithSignupTypeCreatesSignupEmailContent() {
    // given
    EmailVerificationType type = EmailVerificationType.SIGN_UP;
    String code = "1456";

    // when
    EmailContent content = EmailContentFactory.generate(type, code);

    // then
    assertAll(
        () -> assertThat(content).isNotNull(),
        () -> assertThat(content.subject()).contains("회원가입 이메일 인증번호"),
        () -> assertThat(content.body()).contains(code),
        () -> assertThat(content.body()).contains("Dataracy 회원가입을 위한 인증번호"));
}
```

### **실제 테스트 설정**

#### **테스트 프로파일 (실제 설정)**

```yaml
# application-test.yml (실제 파일)
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.H2Dialect

  h2:
    console:
      enabled: true

  data:
    redis:
      host: localhost
      port: 6379
      protocol: redis
      database: 0

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
      auto-startup: false

logging:
  level:
    com.dataracy: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

#### **통합 테스트 설정 (실제 설정)**

```yaml
# application-integration-test.yml (실제 파일)
spring:
  profiles:
    active: test

  datasource:
    url: jdbc:h2:mem:integration-testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

  application:
    name: dataracy-integration-test

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

logging:
  level:
    com.dataracy: WARN
    org.springframework.web: WARN
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
```

---

## 🗄️ **실제 데이터베이스 테스트**

### **H2 인메모리 데이터베이스 (실제 사용)**

#### **실제 테스트 설정**

```yaml
# application-test.yml (실제 파일)
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.H2Dialect
        hbm2ddl:
          auto: create-drop
        naming:
          physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

  h2:
    console:
      enabled: true
```

### **실제 테스트 데이터 생성 패턴**

#### **직접 생성 패턴 (실제 사용)**

```java
// 실제 DataCommandServiceTest.java 기반
private Data createSampleData() {
    return Data.of(
        1L,
        "Test Data",
        1L,
        1L,
        1L,
        1L,
        LocalDate.now(),
        LocalDate.now(),
        "Description",
        "Guide",
        "file-url",
        "thumb-url",
        1,
        1024L,
        DataMetadata.of(1L, 10, 5, "{}"),
        LocalDateTime.now());
}

private UploadDataRequest createSampleUploadRequest() {
    return new UploadDataRequest(
        "Test Data",
        1L,
        2L,
        3L,
        LocalDate.now(),
        LocalDate.now().plusDays(1),
        "Description",
        "Guide");
}
```

---

## 🔍 **실제 테스트 실행**

### **로컬 실행 (실제 스크립트 사용)**

#### **전체 테스트 실행**

```bash
# 실제 테스트 실행 스크립트 사용
./test-run.sh

# 또는 직접 실행
./gradlew test --continue --exclude-tests "*IntegrationTest"
```

#### **커버리지 확인 (실제 스크립트 사용)**

```bash
# 실제 커버리지 스크립트 사용
./test-coverage.sh

# 또는 직접 실행
./gradlew jacocoTestReport
./gradlew jacocoTestCoverageVerification
```

#### **특정 테스트 실행**

```bash
# 특정 테스트 클래스 실행
./gradlew test --tests "UserTest"

# 특정 메서드 실행
./gradlew test --tests "UserTest.isPasswordMatchWhenPasswordMatchesReturnsTrue"

# 특정 모듈 테스트
./gradlew test --tests "com.dataracy.modules.email.*"
```

#### **통합 테스트**

```bash
# 통합 테스트만 실행 (CI 환경에서 제외됨)
./gradlew test --tests "*IntegrationTest"

# 또는 통합 테스트 프로파일로 실행
./gradlew test -Dspring.profiles.active=integration-test
```

### **실제 CI/CD 실행**

#### **GitHub Actions (실제 설정)**

```yaml
# .github/workflows/build-dev.yml (실제 파일)
name: Build and Push Docker Image (DEV - latest only)

on:
  pull_request:
    branches: [develop]

env:
  IMAGE_NAME: juuuunny/backend
  IMAGE_TAG: ${{ github.sha }}

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    needs: test

    steps:
      - name: Checkout source
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Make Gradle executable
        run: chmod +x gradlew

      - name: 🏗️ Build Application (Tests run in separate workflow)
        run: ./gradlew clean build -x test
```

#### **실제 배포 워크플로우**

```yaml
# .github/workflows/deploy-dev.yml (실제 파일)
name: Build and Deploy to EC2 (DEV - Blue/Green)

on:
  push:
    branches: [develop]

jobs:
  build-and-deploy-dev:
    runs-on: ubuntu-latest

    steps:
      - name: ☕ Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
          distribution: "temurin"

      - name: 🛠️ Build with Gradle (Skip Tests)
        run: ./gradlew clean build -x test
```

---

## 📊 **실제 성능 테스트**

### **k6 성능 테스트 (실제 구현)**

#### **로그인 성능 테스트**

```javascript
// performance-test/auth/scenarios/login.test.js (실제 파일)
/**
 * 로그인 성능 테스트 시나리오 (실제 구현 기반)
 *
 * 실제 API 엔드포인트:
 * - POST /api/v1/auth/dev/login (개발용 - 토큰 반환)
 * - POST /api/v1/auth/login (운영용 - 쿠키 설정)
 *
 * 실제 측정 가능한 메트릭:
 * - login_success_rate: 로그인 성공률 (목표: >95%)
 * - login_response_time: 전체 응답 시간 (목표: p95 < 500ms)
 * - throughput: 초당 처리 요청 수 (목표: >200 req/s)
 * - error_rate: 에러 발생률 (목표: <5%)
 */

import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    load_test: {
      executor: "ramping-vus",
      startVUs: 10,
      stages: [
        { duration: "2m", target: 100 },
        { duration: "5m", target: 100 },
        { duration: "2m", target: 0 },
      ],
    },
  },
};

export default function () {
  const loginPayload = JSON.stringify({
    email: "test@example.com",
    password: "testpass123",
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const response = http.post(
    `${__ENV.BASE_URL}/api/v1/auth/dev/login`,
    loginPayload,
    params
  );

  check(response, {
    "login status is 200": (r) => r.status === 200,
    "login response time < 500ms": (r) => r.timings.duration < 500,
    "login has access token": (r) => r.json("data.accessToken") !== undefined,
  });

  sleep(1);
}
```

#### **실행 명령어 (실제 사용)**

```bash
# 기본 스모크 테스트 (5명, 30초)
k6 run --env SCENARIO=smoke --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js

# 로드 테스트 (10→100명, 8분)
k6 run --env SCENARIO=load --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js

# 스트레스 테스트 (20→300명, 8분)
k6 run --env SCENARIO=stress --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js
```

---

## 🎭 **실제 Mock 전략**

### **실제 Mock 사용 패턴**

#### **포트 어댑터 Mock (실제 사용)**

```java
// 실제 DataCommandServiceTest.java 기반
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataCommandServiceTest {

    @InjectMocks private DataCommandService service;

    @Mock private CreateDataDtoMapper createDataDtoMapper;
    @Mock private CreateDataPort createDataPort;
    @Mock private UpdateDataPort updateDataPort;
    @Mock private UpdateDataFilePort updateDataFilePort;
    @Mock private UpdateThumbnailFilePort updateThumbnailFilePort;
    @Mock private DataUploadEventPort dataUploadEventPort;
    @Mock private CheckDataExistsByIdPort checkDataExistsByIdPort;
    @Mock private FindDataPort findDataPort;
    @Mock private FileCommandUseCase fileCommandUseCase;
    @Mock private ValidateTopicUseCase validateTopicUseCase;
    @Mock private ValidateDataSourceUseCase validateDataSourceUseCase;
    @Mock private ValidateDataTypeUseCase validateDataTypeUseCase;
    @Mock private MultipartFile dataFile;
    @Mock private MultipartFile thumbnailFile;

    // 실제 테스트 데이터 생성 메서드
    private Data createSampleData() {
        return Data.of(
            1L, "Test Data", 1L, 1L, 1L, 1L,
            LocalDate.now(), LocalDate.now(),
            "Description", "Guide", "file-url", "thumb-url",
            1, 1024L, DataMetadata.of(1L, 10, 5, "{}"),
            LocalDateTime.now());
    }
}
```

#### **로거 Mock (실제 사용)**

```java
// 실제 DataSearchServiceTest.java 기반
@BeforeEach
void setUp() {
    loggerFactoryMock = mockStatic(LoggerFactory.class);
    loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
    loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
    doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
    doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
    doNothing().when(loggerService).logWarning(anyString(), anyString());
}

@AfterEach
void tearDown() {
    if (loggerFactoryMock != null) {
        loggerFactoryMock.close();
    }
}
```

---

## 🔧 **실제 테스트 설정**

### **실제 테스트 프로파일**

#### **application-test.yml (실제 파일)**

```yaml
# 실제 테스트 설정 파일
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

  application:
    name: dataracy-test

  # JWT 테스트용 설정
  jwt:
    secret: test-jwt-secret-key-for-testing-purposes-only
    redirect-onboarding: http://localhost:3000/onboarding
    redirect-base: http://localhost:3000
    register-token-expiration-time: 600000
    reset-token-expiration-time: 600000
    access-token-expiration-time: 3600000
    refresh-token-expiration-time: 1209600000

  # OAuth2 테스트용 설정
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: test-kakao-client-id
            client-secret: test-kakao-client-secret
            redirect-uri: http://localhost:8080/login/oauth2/code/kakao
          google:
            client-id: test-google-client-id
            client-secret: test-google-client-secret
            redirect-uri: http://localhost:8080/login/oauth2/code/google

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.H2Dialect
        hbm2ddl:
          auto: create-drop
        naming:
          physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

  h2:
    console:
      enabled: true

  data:
    redis:
      host: localhost
      port: 6379
      protocol: redis
      database: 0

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
      auto-startup: false

# AWS/SES/SendGrid Mock 설정
aws:
  ses:
    access-key: test-access-key
    secret-key: test-secret-key
    region: ap-northeast-2
    sender: test@example.com

sendgrid:
  api-key: test-sendgrid-key
  sender: test@example.com

cloud:
  aws:
    credentials:
      access-key: test-aws-access-key
      secret-key: test-aws-secret-key
    region:
      static: ap-northeast-2
    s3:
      bucket: test-bucket
```

---

## 📈 **실제 성능 테스트 결과**

### **실제 성능 테스트 시나리오**

#### **로그인 성능 테스트 (실제 구현)**

```javascript
// performance-test/auth/scenarios/login-abuse.test.js (실제 파일)
/**
 * 로그인 남용 테스트 시나리오 (실제 구현 기반)
 *
 * 포트폴리오 트러블슈팅 스토리:
 * "레이트 리미팅 없이 무차별 대입 공격에 취약한 로그인 시스템"
 *
 * 현재 보안 상태:
 * - BCrypt 비밀번호 해싱: ✅ 구현됨
 * - JWT 토큰 시스템: ✅ 구현됨
 * - Redis 세션 관리: ✅ 구현됨
 * - 분산 락 시스템: ✅ 구현됨
 * - 보안 로깅: ✅ 구현됨
 * - ❌ 레이트 리미팅: 미구현 (주요 취약점)
 * - ❌ 계정 잠금: 미구현
 * - ❌ IP 차단: 미구현
 * - ❌ 공격 탐지: 미구현
 */

export const options = {
  scenarios: {
    abuse_test: {
      executor: "constant-vus",
      vus: 50,
      duration: "2m",
    },
  },
};

export default function () {
  const loginPayload = JSON.stringify({
    email: "test@example.com",
    password: "wrongpassword", // 의도적으로 잘못된 비밀번호
  });

  const response = http.post(
    `${__ENV.BASE_URL}/api/v1/auth/dev/login`,
    loginPayload,
    {
      headers: { "Content-Type": "application/json" },
    }
  );

  check(response, {
    "login abuse status is 401": (r) => r.status === 401,
    "login abuse response time < 1000ms": (r) => r.timings.duration < 1000,
  });
}
```

### **실제 실행 방법**

```bash
# 로그인 남용 테스트 (레이트 리미팅 없음)
k6 run --env SCENARIO=abuse --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login-abuse.test.js

# 레이트 리미팅 적용 후 테스트
k6 run --env SCENARIO=abuse --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
```

---

## 📊 **실제 테스트 리포트**

### **실제 테스트 결과 분석**

#### **JUnit 리포트 (실제 위치)**

```bash
# 테스트 리포트 생성
./gradlew test

# HTML 리포트 확인 (실제 경로)
open build/reports/tests/test/index.html
```

#### **커버리지 리포트 (실제 위치)**

```bash
# 커버리지 리포트 생성 (실제 스크립트 사용)
./test-coverage.sh

# HTML 리포트 확인 (실제 경로)
open build/reports/jacoco/test/html/index.html
```

### **실제 품질 게이트 설정**

#### **quality-gate.yml (실제 파일)**

```yaml
# 실제 품질 게이트 설정
quality_gate:
  # 코드 커버리지 최소 기준
  coverage:
    minimum: 70 # 실제 82.5% 커버리지에 맞춘 기준
    target: 70

  # 코드 중복도 최대 허용치
  duplication:
    maximum: 3

  # 보안 취약점 심각도별 허용치
  security:
    blocker: 0
    critical: 0
    major: 5
    minor: 10

  # 코드 스멜 최대 허용치
  code_smells:
    blocker: 0
    critical: 0
    major: 10
    minor: 20

  # 버그 최대 허용치
  bugs:
    blocker: 0
    critical: 0
    major: 5
    minor: 10

  # 유지보수성 등급
  maintainability:
    minimum_rating: B

  # 신뢰성 등급
  reliability:
    minimum_rating: A

  # 보안 등급
  security_rating:
    minimum_rating: A
```

---

## 🎯 **실제 테스트 모범 사례**

### **실제 테스트 작성 패턴**

#### **FIRST 원칙 (실제 적용)**

- **Fast**: 빠른 실행 (H2 인메모리 DB 사용)
- **Independent**: 독립적 실행 (각 테스트마다 새로운 컨텍스트)
- **Repeatable**: 반복 가능 (고정된 테스트 데이터 사용)
- **Self-Validating**: 자체 검증 (AssertJ 사용)
- **Timely**: 적시에 작성 (TDD 적용)

#### **실제 AAA 패턴 (한국어 주석)**

```java
// 실제 UserTest.java 기반
@Test
@DisplayName("isPasswordMatch - 비밀번호가 일치하는 경우 true를 반환한다")
void isPasswordMatchWhenPasswordMatchesReturnsTrue() {
    // given (Arrange)
    PasswordEncoder encoder = mock(PasswordEncoder.class);
    String rawPassword = "password1";
    String encodedPassword = "encodedPassword1";

    User user = User.builder().password(encodedPassword).build();

    given(encoder.matches(rawPassword, encodedPassword)).willReturn(true);

    // when (Act)
    boolean result = user.isPasswordMatch(encoder, rawPassword);

    // then (Assert)
    assertThat(result).isTrue();
}
```

### **실제 피해야 할 패턴**

#### **실제 테스트 안티패턴**

- ❌ **테스트 간 의존성**: 테스트가 다른 테스트에 의존
- ❌ **불안정한 테스트**: 환경에 따라 실패 (CI 환경에서 통합 테스트 제외)
- ❌ **과도한 Mock**: 모든 것을 Mock으로 처리 (포트 어댑터만 Mock)
- ❌ **테스트 코드 중복**: 비슷한 테스트 코드 반복 (직접 생성 패턴으로 해결)

---

## 📞 **지원 및 연락처**

### **테스트 관련 문의**

- **슬랙**: #dataracy-testing
- **이메일**: testing@dataracy.store
- **문서**: [테스트 가이드](./README.md)

### **실제 사용 도구 및 리소스**

- **JUnit 5**: https://junit.org/junit5/
- **Mockito**: https://mockito.org/
- **AssertJ**: https://assertj.github.io/doc/
- **k6**: https://k6.io/
- **H2 Database**: https://www.h2database.com/

### **실제 테스트 스크립트**

- **테스트 실행**: `./test-run.sh`
- **커버리지 확인**: `./test-coverage.sh`
- **성능 테스트**: `k6 run performance-test/auth/scenarios/login.test.js`

---

**💡 이 문서는 실제 구현된 테스트 코드를 기반으로 작성되었습니다. 정확하고 신뢰할 수 있는 정보를 제공합니다!**
