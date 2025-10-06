# 🔗 통합 테스트 가이드

## 📋 **개요**

Dataracy 백엔드 프로젝트의 통합 테스트 작성 방법과 실행 전략을 안내합니다.

---

## 🎯 **통합 테스트 전략**

### **테스트 범위**

#### **컴포넌트 통합**

- **서비스 계층**: 비즈니스 로직과 데이터 접근 계층 통합
- **웹 계층**: 컨트롤러와 서비스 계층 통합
- **데이터 계층**: JPA와 데이터베이스 통합

#### **시스템 통합**

- **API 엔드포인트**: 전체 요청-응답 플로우
- **데이터베이스**: 실제 데이터베이스와의 상호작용
- **외부 서비스**: Redis, Elasticsearch, Kafka 연동

### **테스트 환경**

#### **H2 인메모리 데이터베이스 (실제 사용)**

- **H2**: 인메모리 데이터베이스 (테스트용)
- **Redis**: 캐시 및 세션 저장소 (Mock)
- **Elasticsearch**: 검색 엔진 (Mock)
- **Kafka**: 메시지 브로커 (Mock)

---

## 🛠️ **테스트 도구**

### **Spring Boot Test**

#### **@SpringBootTest (실제 구현 기반)**

```java
// 실제 DataracyApplicationTests.java 기반
@SpringBootTest
class DataracyApplicationTests {
    @Test
    void contextLoads() {
        // Spring 컨텍스트가 정상적으로 로드되는지 확인하는 테스트
        // 별도 로직 없이 애플리케이션 컨텍스트 로딩만으로 충분
    }
}
```

#### **@WebMvcTest (실제 구현 기반)**

```java
// 실제 TopicControllerTest.java 기반
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = TopicController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class TopicControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private FindAllTopicsUseCase findAllTopicsUseCase;
    @MockBean private TopicWebMapper webMapper;
    @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("findAllTopics API: 성공 - 200 OK와 JSON 응답 검증")
    void findAllTopicsSuccess() throws Exception {
        // given
        AllTopicsResponse svc = new AllTopicsResponse(List.of());
        AllTopicsWebResponse web = new AllTopicsWebResponse(
            List.of(
                new TopicWebResponse(1L, "AI", "인공지능"),
                new TopicWebResponse(2L, "DATA", "데이터 분석")));

        given(findAllTopicsUseCase.findAllTopics()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc
            .perform(get("/api/v1/references/topics").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.topics[0].id").value(1))
            .andExpect(jsonPath("$.data.topics[0].value").value("AI"))
            .andExpect(jsonPath("$.data.topics[0].label").value("인공지능"))
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }
}
```

### **H2 인메모리 데이터베이스 (실제 사용)**

#### **테스트 설정**

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
```

#### **실제 테스트 예시**

```java
// 실제 LockTest.java 기반
@SpringBootTest
class LockTest {

    private MockMvc mockMvc;
    @Autowired private UserValidateController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // Spring Security 없이 컨트롤러만 등록
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testNicknameLockconcurrentAccess() throws Exception {
        // given
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String json = objectMapper.writeValueAsString(new DuplicateNicknameRequest("주니22"));

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(
                        post("/api/v1/public/nickname/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                        .andDo(print());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // then
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
```

---

## 🗄️ **데이터베이스 테스트**

### **H2 인메모리 데이터베이스 테스트**

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

#### **쿼리 테스트**

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 이메일로_사용자_조회_테스트() {
        // Given
        User user = createUserWithEmail("test@example.com");
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void 닉네임_중복_검사_테스트() {
        // Given
        User user = createUserWithNickname("테스트사용자");
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByNickname("테스트사용자");

        // Then
        assertThat(exists).isTrue();
    }
}
```

### **트랜잭션 테스트**

#### **트랜잭션 롤백**

```java
@SpringBootTest
@Transactional
class UserServiceTransactionTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 트랜잭션_롤백_테스트() {
        // Given
        UserRequest request = createUserRequest();

        // When
        assertThatThrownBy(() -> userService.createUserWithError(request))
            .isInstanceOf(RuntimeException.class);

        // Then - 트랜잭션이 롤백되어 데이터가 저장되지 않음
        assertThat(userRepository.findByEmail(request.getEmail())).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 트랜잭션_없이_테스트() {
        // Given
        User user = createUser();
        userRepository.save(user);

        // When
        userService.updateUserWithoutTransaction(user.getId(), "새닉네임");

        // Then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo("새닉네임");
    }
}
```

---

## 🌐 **웹 계층 테스트**

### **REST API 테스트**

#### **컨트롤러 통합 테스트 (실제 구현 기반)**

```java
// 실제 CommentCommandControllerTest.java 기반
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = CommentCommandController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class CommentCommandControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UploadCommentUseCase uploadCommentUseCase;
    @MockBean private ModifyCommentUseCase modifyCommentUseCase;
    @MockBean private DeleteCommentUseCase deleteCommentUseCase;
    @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean private JwtValidateUseCase jwtValidateUseCase;
    @MockBean private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @BeforeEach
    void setupResolver() {
        // 모든 @CurrentUserId Long 파라미터 → userId=1L 주입
        given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("댓글 생성 API: 성공 - 201 Created와 JSON 응답 검증")
    void uploadCommentSuccess() throws Exception {
        // given
        UploadCommentWebRequest request = new UploadCommentWebRequest(1L, "테스트 댓글");
        UploadCommentResponse response = new UploadCommentResponse(1L);
        given(uploadCommentUseCase.uploadComment(any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/projects/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.httpStatus").value(201))
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.commentId").value(1L));
    }
}
```

#### **인증 테스트**

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 로그인_통합_테스트() throws Exception {
        // Given
        User user = createUserWithPassword("password123");
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
            .email(user.getEmail())
            .password("password123")
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200))
            .andExpect(jsonPath("$.code").value("SUCCESS"));
    }

    @Test
    @WithMockUser
    void 인증_필요_API_테스트() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200));
    }

    @Test
    void 인증_없이_API_접근_실패_테스트() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/user"))
            .andExpect(status().isUnauthorized());
    }
}
```

---

## 🔍 **검색 엔진 테스트**

### **Elasticsearch Mock 테스트 (실제 구현)**

```java
// 실제 구현에서는 Elasticsearch를 Mock으로 처리
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectSearchServiceTest {

    @Mock private ProjectSearchPort projectSearchPort;
    @InjectMocks private ProjectSearchService projectSearchService;

    @Test
    @DisplayName("프로젝트 검색 성공")
    void searchProjectsSuccess() {
        // given
        String keyword = "머신러닝";
        List<ProjectSearchDocument> expectedResults = List.of(
            new ProjectSearchDocument(1L, "머신러닝 프로젝트", "AI", "데이터 분석")
        );
        given(projectSearchPort.searchProjects(keyword)).willReturn(expectedResults);

        // when
        List<ProjectSearchDocument> results = projectSearchService.searchProjects(keyword);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).contains("머신러닝");
        verify(projectSearchPort).searchProjects(keyword);
    }
}
```

---

## 📨 **메시징 테스트**

### **Kafka Mock 테스트 (실제 구현)**

```java
// 실제 구현에서는 Kafka를 Mock으로 처리
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BehaviorLogKafkaProducerAdapterTest {

    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @InjectMocks private BehaviorLogKafkaProducerAdapter adapter;

    @Test
    @DisplayName("행동 로그 Kafka 전송 성공")
    void sendBehaviorLogSuccess() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
            .userId("1")
            .path("/api/v1/projects")
            .action(ActionType.CLICK)
            .build();

        // when
        adapter.send(behaviorLog);

        // then
        verify(kafkaTemplate).send(eq("behavior-log-topic"), anyString());
    }
}
```

---

## 🔧 **테스트 설정**

### **테스트 프로파일**

#### **application-integration-test.yml**

```yaml
spring:
  profiles:
    active: integration-test

  datasource:
    url: jdbc:mysql://localhost:3306/testdb
    username: test
    password: test
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  redis:
    host: localhost
    port: 6379

  elasticsearch:
    hosts: http://localhost:9200

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

logging:
  level:
    com.dataracy: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### **테스트 전용 Bean (실제 구현)**

#### **@TestConfiguration**

```java
// 실제 구현에서는 MockBean을 주로 사용
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CommentCommandController.class)
class CommentCommandControllerTest {

    @MockBean private UploadCommentUseCase uploadCommentUseCase;
    @MockBean private ModifyCommentUseCase modifyCommentUseCase;
    @MockBean private DeleteCommentUseCase deleteCommentUseCase;
    @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean private JwtValidateUseCase jwtValidateUseCase;
    @MockBean private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    // 실제 테스트에서는 외부 서비스를 Mock으로 처리
}
```

---

## 📊 **테스트 데이터 관리**

### **@Sql 어노테이션**

```java
@Test
@Sql("/test-data/users.sql")
@Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
void 사용자_목록_조회_테스트() {
    // Given & When
    List<UserResponse> users = userService.getUsers();

    // Then
    assertThat(users).hasSize(3);
    assertThat(users)
        .extracting(UserResponse::getEmail)
        .containsExactlyInAnyOrder(
            "user1@example.com",
            "user2@example.com",
            "user3@example.com"
        );
}
```

### **테스트 데이터 파일**

#### **users.sql**

```sql
-- test-data/users.sql
INSERT INTO users (id, email, nickname, password, role, created_at, updated_at) VALUES
(1, 'user1@example.com', '사용자1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', 'ROLE_USER', NOW(), NOW()),
(2, 'user2@example.com', '사용자2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', 'ROLE_USER', NOW(), NOW()),
(3, 'user3@example.com', '사용자3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', 'ROLE_USER', NOW(), NOW());

INSERT INTO projects (id, title, user_id, created_at, updated_at) VALUES
(1, '프로젝트1', 1, NOW(), NOW()),
(2, '프로젝트2', 2, NOW(), NOW()),
(3, '프로젝트3', 3, NOW(), NOW());
```

#### **cleanup.sql**

```sql
-- test-data/cleanup.sql
DELETE FROM projects;
DELETE FROM users;
```

---

## 🚀 **테스트 실행**

### **로컬 실행 (실제 구현)**

#### **통합 테스트 실행**

```bash
# 실제 테스트 실행 스크립트 사용
./test-run.sh

# 또는 직접 실행
./gradlew test --continue --exclude-tests "*IntegrationTest"

# 통합 테스트만 실행 (CI 환경에서 제외됨)
./gradlew test --tests "*IntegrationTest"

# 특정 클래스 실행
./gradlew test --tests "DataracyApplicationTests"
```

#### **H2 데이터베이스 실행**

```bash
# H2 콘솔 접속 (테스트용)
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (비어있음)
```

### **CI/CD 실행**

#### **GitHub Actions**

```yaml
name: Integration Tests

on: [push, pull_request]

jobs:
  integration-test:
    runs-on: ubuntu-latest

    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: testdb
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Run integration tests
        run: ./gradlew integrationTest
        env:
          SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/testdb
          SPRING_DATASOURCE_USERNAME: root
          SPRING_DATASOURCE_PASSWORD: root
```

---

## 📈 **성능 테스트**

### **API 성능 테스트**

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApiPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 사용자_목록_조회_성능_테스트() throws Exception {
        // Given - 대량 데이터 준비
        for (int i = 0; i < 1000; i++) {
            User user = createUserWithEmail("user" + i + "@example.com");
            userRepository.save(user);
        }

        // When & Then
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/api/v1/users?page=0&size=20"))
            .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertThat(duration).isLessThan(500); // 500ms 이내
    }
}
```

---

## 📞 **지원 및 연락처**

### **통합 테스트 관련 문의**

- **슬랙**: #dataracy-integration-testing
- **이메일**: testing@dataracy.store
- **문서**: [테스트 가이드](./README.md)

### **참고 자료**

- **Spring Boot Test**: https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing
- **TestContainers**: https://www.testcontainers.org/
- **JUnit 5**: https://junit.org/junit5/docs/current/user-guide/

---

**💡 통합 테스트 관련 문제가 발생하면 개발팀에 문의하세요!**
