# 테스트 가이드라인 및 베스트 프랙티스

## 📋 목차
1. [테스트 전략](#테스트-전략)
2. [테스트 구조](#테스트-구조)
3. [테스트 작성 규칙](#테스트-작성-규칙)
4. [테스트 데이터 관리](#테스트-데이터-관리)
5. [테스트 성능 최적화](#테스트-성능-최적화)
6. [코드 커버리지](#코드-커버리지)
7. [테스트 실행 및 유지보수](#테스트-실행-및-유지보수)

---

## 🎯 테스트 전략

### 테스트 피라미드
```
        /\
       /  \
      / E2E \     ← 소수 (5%)
     /______\
    /        \
   /Integration\  ← 적당 (15%)
  /____________\
 /              \
/    Unit Tests   \  ← 다수 (80%)
/__________________\
```

### 테스트 유형별 역할

#### 1. Unit Tests (단위 테스트)
- **목적**: 개별 클래스/메서드의 로직 검증
- **범위**: 단일 클래스 내부 로직
- **의존성**: Mock 사용
- **실행 속도**: 빠름 (< 1ms)

#### 2. Integration Tests (통합 테스트)
- **목적**: 여러 컴포넌트 간의 상호작용 검증
- **범위**: 실제 데이터베이스, 외부 서비스
- **의존성**: 실제 인프라 사용
- **실행 속도**: 보통 (10-100ms)

#### 3. E2E Tests (종단간 테스트)
- **목적**: 전체 시스템의 사용자 시나리오 검증
- **범위**: 전체 애플리케이션
- **의존성**: 실제 모든 인프라
- **실행 속도**: 느림 (> 1s)

---

## 🏗️ 테스트 구조

### 디렉토리 구조
```
src/test/java/com/dataracy/modules/
├── {module}/
│   ├── adapter/
│   │   ├── web/
│   │   │   ├── api/
│   │   │   │   └── {Controller}Test.java
│   │   │   └── mapper/
│   │   │       └── {Mapper}Test.java
│   │   ├── jpa/
│   │   │   └── {Repository}Test.java
│   │   └── kafka/
│   │       └── {Consumer}Test.java
│   ├── application/
│   │   ├── service/
│   │   │   ├── command/
│   │   │   │   └── {Service}Test.java
│   │   │   └── query/
│   │   │       └── {Service}Test.java
│   │   └── integration/
│   │       └── {Service}IntegrationTest.java
│   └── domain/
│       ├── model/
│       │   └── {Model}Test.java
│       ├── enums/
│       │   └── {Enum}Test.java
│       └── exception/
│           └── {Exception}Test.java
└── common/
    └── test/
        └── support/
            └── TestDataBuilder.java
```

### 테스트 클래스 네이밍 규칙
- **단위 테스트**: `{ClassName}Test`
- **통합 테스트**: `{ClassName}IntegrationTest`
- **테스트 유틸리티**: `{Purpose}TestSupport`

---

## ✍️ 테스트 작성 규칙

### 1. 테스트 메서드 네이밍
```java
@Test
@DisplayName("메서드명_상황_예상결과")
void methodName_WhenCondition_ShouldExpectedResult() {
    // 테스트 구현
}
```

**예시:**
```java
@Test
@DisplayName("사용자 생성 - 유효한 데이터로 사용자를 생성한다")
void createUser_WhenValidData_ShouldCreateUser() {
    // given
    UserRequest request = TestDataBuilder.userRequest()
            .email("test@example.com")
            .nickname("테스트유저")
            .build();
    
    // when
    UserResponse response = userService.createUser(request);
    
    // then
    assertThat(response.getEmail()).isEqualTo("test@example.com");
    assertThat(response.getNickname()).isEqualTo("테스트유저");
}
```

### 2. Given-When-Then 패턴
```java
@Test
@DisplayName("비즈니스 로직 설명")
void testMethod() {
    // Given: 테스트 데이터 준비
    // - 테스트에 필요한 모든 데이터 설정
    // - Mock 객체 설정
    // - 예상 결과 정의
    
    // When: 테스트 대상 실행
    // - 실제 테스트할 메서드 호출
    
    // Then: 결과 검증
    // - 반환값 검증
    // - 상태 변경 검증
    // - Mock 상호작용 검증
}
```

### 3. AssertJ 사용
```java
// ✅ 좋은 예
assertThat(result)
    .isNotNull()
    .hasSize(2)
    .extracting("name")
    .containsExactly("user1", "user2");

// ❌ 나쁜 예
assertNotNull(result);
assertEquals(2, result.size());
assertEquals("user1", result.get(0).getName());
```

### 4. Mock 사용 규칙
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    
    @Mock
    private DependencyPort dependencyPort;
    
    @InjectMocks
    private Service service;
    
    @Test
    void testMethod() {
        // given
        when(dependencyPort.find(any())).thenReturn(expectedResult);
        
        // when
        Result result = service.method();
        
        // then
        verify(dependencyPort).find(any());
        assertThat(result).isEqualTo(expectedResult);
    }
}
```

---

## 🗃️ 테스트 데이터 관리

### 1. TestDataBuilder 사용
```java
// ✅ 좋은 예 - 빌더 패턴 사용
User user = TestDataBuilder.user()
    .email("test@example.com")
    .nickname("테스트유저")
    .role(RoleType.ROLE_USER)
    .build();

// ❌ 나쁜 예 - 하드코딩
User user = User.builder()
    .id(1L)
    .email("test@example.com")
    .nickname("테스트유저")
    .role(RoleType.ROLE_USER)
    .build();
```

### 2. 랜덤 데이터 사용
```java
// ✅ 좋은 예 - 랜덤 데이터로 테스트 격리
User user = TestDataBuilder.user()
    .email(TestDataBuilder.RandomData.randomEmail())
    .nickname(TestDataBuilder.RandomData.randomNickname())
    .build();

// ❌ 나쁜 예 - 고정 데이터로 테스트 간 충돌 가능
User user = TestDataBuilder.user()
    .email("test@example.com")
    .nickname("테스트유저")
    .build();
```

### 3. 테스트 데이터 정리
```java
@SpringBootTest
@Transactional
class IntegrationTest {
    
    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 정리
        userRepository.deleteAll();
    }
    
    @AfterEach
    void tearDown() {
        // 테스트 후 정리 (필요한 경우)
    }
}
```

---

## ⚡ 테스트 성능 최적화

### 1. Spring Context 재사용
```java
// ✅ 좋은 예 - @DirtiesContext 최소화
@SpringBootTest
@ActiveProfiles("test")
class ServiceIntegrationTest {
    // Spring Context가 재사용됨
}

// ❌ 나쁜 예 - 불필요한 Context 재생성
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ServiceIntegrationTest {
    // 매 테스트마다 Context 재생성
}
```

### 2. 테스트 그룹화
```java
@Nested
@DisplayName("사용자 생성")
class CreateUser {
    
    @Test
    void withValidData() { }
    
    @Test
    void withInvalidEmail() { }
    
    @Test
    void withDuplicateEmail() { }
}
```

### 3. 병렬 테스트 실행
```properties
# application-test.yml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
```

---

## 📊 코드 커버리지

### 목표 커버리지
- **전체 커버리지**: 80% 이상
- **핵심 비즈니스 로직**: 100%
- **예외 처리**: 90% 이상
- **도메인 모델**: 100%

### 커버리지 확인 방법
```bash
# 전체 테스트 실행 및 커버리지 리포트 생성
./gradlew clean test jacocoTestReport

# 커버리지 리포트 확인
open build/reports/jacoco/test/html/index.html
```

### 커버리지 제외 규칙
```xml
<!-- build.gradle -->
jacocoTestReport {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/dto/**',
                '**/entity/**',
                '**/config/**',
                '**/exception/**'
            ])
        }))
    }
}
```

---

## 🔧 테스트 실행 및 유지보수

### 테스트 실행 명령어
```bash
# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew test --tests "com.dataracy.modules.user.*"

# 통합 테스트만 실행
./gradlew test --tests "*IntegrationTest"

# 커버리지 포함 실행
./gradlew clean test jacocoTestReport
```

### 테스트 유지보수 체크리스트

#### 매주 확인사항
- [ ] 모든 테스트가 통과하는지 확인
- [ ] 새로운 기능에 대한 테스트 추가
- [ ] 깨진 테스트 수정

#### 매월 확인사항
- [ ] 테스트 커버리지 리뷰
- [ ] 느린 테스트 성능 개선
- [ ] 중복 테스트 코드 정리

#### 분기별 확인사항
- [ ] 테스트 전략 검토
- [ ] 테스트 도구 업데이트
- [ ] 테스트 가이드라인 업데이트

---

## 🚨 주의사항

### 1. 테스트 격리
- 각 테스트는 독립적으로 실행되어야 함
- 테스트 간 데이터 공유 금지
- 외부 의존성은 Mock 사용

### 2. 테스트 안정성
- 시간에 의존적인 테스트 지양
- 랜덤 데이터 사용 시 충분한 범위 고려
- 네트워크 의존성 최소화

### 3. 테스트 가독성
- 테스트 이름은 의도를 명확히 표현
- 복잡한 테스트는 여러 개로 분리
- 주석보다는 코드로 의도 표현

---

## 📚 참고 자료

- [JUnit 5 공식 문서](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 공식 문서](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ 공식 문서](https://assertj.github.io/doc/)
- [Spring Boot Test 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)

