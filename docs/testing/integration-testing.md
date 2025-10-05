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

#### **TestContainers**

- **MySQL**: 실제 데이터베이스 환경
- **Redis**: 캐시 및 세션 저장소
- **Elasticsearch**: 검색 엔진
- **Kafka**: 메시지 브로커

---

## 🛠️ **테스트 도구**

### **Spring Boot Test**

#### **@SpringBootTest**

```java
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_생성_통합_테스트() {
        // Given
        UserRequest request = createUserRequest();

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertThat(response.getId()).isNotNull();

        User savedUser = userRepository.findById(response.getId()).orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(savedUser.getNickname()).isEqualTo(request.getNickname());
    }
}
```

#### **@WebMvcTest**

```java
@WebMvcTest(UserController.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void 사용자_생성_API_테스트() throws Exception {
        // Given
        UserRequest request = createUserRequest();
        UserResponse response = createUserResponse();
        given(userService.createUser(any(UserRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.httpStatus").value(201))
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }
}
```

### **TestContainers**

#### **MySQL 컨테이너**

```java
@Testcontainers
@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_저장_및_조회_테스트() {
        // Given
        User user = createUser();

        // When
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        // Then
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }
}
```

#### **Redis 컨테이너**

```java
@Testcontainers
@SpringBootTest
class CacheServiceIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CacheService cacheService;

    @Test
    void 캐시_저장_및_조회_테스트() {
        // Given
        String key = "user:1";
        User user = createUser();

        // When
        cacheService.put(key, user, Duration.ofMinutes(10));
        Optional<User> cachedUser = cacheService.get(key, User.class);

        // Then
        assertThat(cachedUser).isPresent();
        assertThat(cachedUser.get().getEmail()).isEqualTo(user.getEmail());
    }
}
```

---

## 🗄️ **데이터베이스 테스트**

### **JPA 통합 테스트**

#### **엔티티 테스트**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_엔티티_저장_테스트() {
        // Given
        User user = createUser();

        // When
        User savedUser = entityManager.persistAndFlush(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void 사용자_이메일_중복_검사_테스트() {
        // Given
        User user1 = createUserWithEmail("test@example.com");
        User user2 = createUserWithEmail("test@example.com");

        entityManager.persistAndFlush(user1);

        // When & Then
        assertThatThrownBy(() -> entityManager.persistAndFlush(user2))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
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

#### **컨트롤러 통합 테스트**

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_생성_API_통합_테스트() throws Exception {
        // Given
        UserRequest request = createUserRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.httpStatus").value(201))
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.email").value(request.getEmail()));

        // 데이터베이스에 실제로 저장되었는지 확인
        assertThat(userRepository.findByEmail(request.getEmail())).isPresent();
    }

    @Test
    void 사용자_조회_API_통합_테스트() throws Exception {
        // Given
        User user = createUser();
        userRepository.save(user);

        // When & Then
        mockMvc.perform(get("/api/v1/user/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200))
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(user.getId()))
            .andExpect(jsonPath("$.data.email").value(user.getEmail()));
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

### **Elasticsearch 통합 테스트**

```java
@Testcontainers
@SpringBootTest
class ProjectSearchIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.13.4")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.hosts",
            () -> "http://" + elasticsearch.getHttpHostAddress());
    }

    @Autowired
    private ProjectSearchService projectSearchService;

    @Test
    void 프로젝트_검색_통합_테스트() {
        // Given
        ProjectDocument project = createProjectDocument();
        projectSearchService.indexProject(project);

        // When
        List<ProjectDocument> results = projectSearchService.searchProjects("머신러닝");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).contains("머신러닝");
    }
}
```

---

## 📨 **메시징 테스트**

### **Kafka 통합 테스트**

```java
@Testcontainers
@SpringBootTest
class EventPublishingIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TestEventConsumer testEventConsumer;

    @Test
    void 이벤트_발행_및_소비_테스트() throws InterruptedException {
        // Given
        String eventMessage = "{\"userId\":1,\"action\":\"LOGIN\"}";

        // When
        kafkaTemplate.send("user-events", eventMessage);

        // Then
        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                assertThat(testEventConsumer.getReceivedMessages())
                    .hasSize(1)
                    .contains(eventMessage);
            });
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

### **테스트 전용 Bean**

#### **@TestConfiguration**

```java
@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @Primary
    public EmailService testEmailService() {
        return new TestEmailService();
    }

    @Bean
    @Primary
    public FileStorageService testFileStorageService() {
        return new TestFileStorageService();
    }

    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"), ZoneId.systemDefault());
    }
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

### **로컬 실행**

#### **통합 테스트만 실행**

```bash
# 통합 테스트 실행
./gradlew integrationTest

# 특정 통합 테스트 실행
./gradlew integrationTest --tests "*IntegrationTest"

# 특정 클래스 실행
./gradlew integrationTest --tests "UserServiceIntegrationTest"
```

#### **TestContainers 실행**

```bash
# Docker가 실행 중인지 확인
docker ps

# 통합 테스트 실행 (컨테이너 자동 시작)
./gradlew integrationTest
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
