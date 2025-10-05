# 🔬 단위 테스트 가이드

## 📋 **개요**

Dataracy 백엔드 프로젝트의 단위 테스트 작성 방법과 모범 사례를 안내합니다.

---

## 🎯 **단위 테스트 원칙**

### **FIRST 원칙**

- **Fast**: 빠른 실행 (수 밀리초)
- **Independent**: 독립적 실행 (다른 테스트에 의존하지 않음)
- **Repeatable**: 반복 가능 (환경에 관계없이 동일한 결과)
- **Self-Validating**: 자체 검증 (성공/실패가 명확)
- **Timely**: 적시에 작성 (코드 작성과 동시에)

### **AAA 패턴**

```java
@Test
void 사용자_생성_성공() {
    // Arrange (Given) - 테스트 데이터 준비
    UserRequest request = createUserRequest();

    // Act (When) - 테스트 대상 실행
    UserResponse response = userService.createUser(request);

    // Assert (Then) - 결과 검증
    assertThat(response.getId()).isNotNull();
    assertThat(response.getEmail()).isEqualTo(request.getEmail());
}
```

---

## 🛠️ **테스트 도구**

### **JUnit 5**

#### **기본 어노테이션**

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전 초기화
    }

    @AfterEach
    void tearDown() {
        // 각 테스트 실행 후 정리
    }

    @Test
    @DisplayName("사용자 생성 성공 테스트")
    void 사용자_생성_성공() {
        // 테스트 구현
    }

    @Test
    @Disabled("임시로 비활성화")
    void 임시_비활성화_테스트() {
        // 구현 예정
    }
}
```

#### **파라미터화 테스트**

```java
@ParameterizedTest
@ValueSource(strings = {"user1@example.com", "user2@example.com", "user3@example.com"})
void 이메일_형식_검증(String email) {
    // Given
    UserRequest request = createUserRequest().email(email);

    // When & Then
    assertThatCode(() -> userService.createUser(request))
        .doesNotThrowAnyException();
}

@ParameterizedTest
@CsvSource({
    "user@example.com, true",
    "invalid-email, false",
    "user@, false",
    "@example.com, false"
})
void 이메일_유효성_검증(String email, boolean expected) {
    // Given
    UserRequest request = createUserRequest().email(email);

    // When & Then
    if (expected) {
        assertThatCode(() -> userService.createUser(request))
            .doesNotThrowAnyException();
    } else {
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(ValidationException.class);
    }
}
```

### **Mockito**

#### **Mock 생성**

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void 사용자_조회_성공() {
        // Given
        User user = createUser();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // When
        UserResponse response = userService.getUser(1L);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }
}
```

#### **Mock 검증**

```java
@Test
void 사용자_생성_시_이메일_발송_검증() {
    // Given
    UserRequest request = createUserRequest();
    given(userRepository.save(any(User.class))).willReturn(createUser());

    // When
    userService.createUser(request);

    // Then
    verify(emailService).sendWelcomeEmail(request.getEmail());
    verify(emailService, times(1)).sendWelcomeEmail(anyString());
    verify(emailService, never()).sendPasswordResetEmail(anyString());
}
```

#### **Argument Captor**

```java
@Test
void 사용자_저장_데이터_검증() {
    // Given
    UserRequest request = createUserRequest();
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    // When
    userService.createUser(request);

    // Then
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
    assertThat(savedUser.getNickname()).isEqualTo(request.getNickname());
}
```

### **AssertJ**

#### **기본 어설션**

```java
@Test
void 사용자_정보_검증() {
    // Given
    UserResponse response = userService.getUser(1L);

    // Then
    assertThat(response)
        .isNotNull()
        .extracting(UserResponse::getId, UserResponse::getEmail)
        .containsExactly(1L, "user@example.com");

    assertThat(response.getEmail())
        .isNotBlank()
        .contains("@")
        .endsWith(".com");
}
```

#### **컬렉션 어설션**

```java
@Test
void 사용자_목록_검증() {
    // Given
    List<UserResponse> users = userService.getUsers();

    // Then
    assertThat(users)
        .hasSize(3)
        .extracting(UserResponse::getEmail)
        .containsExactlyInAnyOrder(
            "user1@example.com",
            "user2@example.com",
            "user3@example.com"
        );

    assertThat(users)
        .filteredOn(user -> user.getRole() == RoleType.ROLE_ADMIN)
        .hasSize(1)
        .extracting(UserResponse::getEmail)
        .containsOnly("admin@example.com");
}
```

#### **예외 어설션**

```java
@Test
void 존재하지_않는_사용자_조회_예외() {
    // Given
    given(userRepository.findById(999L)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.getUser(999L))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("사용자를 찾을 수 없습니다: 999");

    assertThatCode(() -> userService.getUser(1L))
        .doesNotThrowAnyException();
}
```

---

## 🏗️ **테스트 구조**

### **테스트 클래스 구조**

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 서비스 테스트")
class UserServiceTest {

    // 테스트 대상
    @InjectMocks
    private UserService userService;

    // 의존성 Mock
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    // 테스트 데이터
    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = createUserRequest();
        user = createUser();
    }

    @Nested
    @DisplayName("사용자 생성")
    class CreateUser {

        @Test
        @DisplayName("정상적인 사용자 생성")
        void 성공() {
            // Given
            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);

            // When
            UserResponse response = userService.createUser(userRequest);

            // Then
            assertThat(response.getId()).isNotNull();
            verify(emailService).sendWelcomeEmail(userRequest.getEmail());
        }

        @Test
        @DisplayName("중복된 이메일로 사용자 생성 실패")
        void 중복_이메일_실패() {
            // Given
            given(userRepository.existsByEmail(userRequest.getEmail())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(userRequest))
                .isInstanceOf(DuplicateEmailException.class);
        }
    }

    @Nested
    @DisplayName("사용자 조회")
    class GetUser {

        @Test
        @DisplayName("사용자 조회 성공")
        void 성공() {
            // Given
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // When
            UserResponse response = userService.getUser(1L);

            // Then
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 실패")
        void 사용자_없음_실패() {
            // Given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(UserNotFoundException.class);
        }
    }
}
```

### **테스트 데이터 빌더**

```java
public class UserTestDataBuilder {

    public static User.UserBuilder defaultUser() {
        return User.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("테스트사용자")
            .password("password123")
            .role(RoleType.ROLE_USER)
            .createdAt(LocalDateTime.now());
    }

    public static User createUser() {
        return defaultUser().build();
    }

    public static User createUserWithEmail(String email) {
        return defaultUser().email(email).build();
    }

    public static User createAdminUser() {
        return defaultUser()
            .role(RoleType.ROLE_ADMIN)
            .email("admin@example.com")
            .build();
    }

    public static UserRequest.UserRequestBuilder defaultUserRequest() {
        return UserRequest.builder()
            .email("test@example.com")
            .nickname("테스트사용자")
            .password("password123")
            .authorLevelId(1L)
            .occupationId(2L)
            .topicIds(List.of(1L, 2L))
            .visitSourceId(1L);
    }

    public static UserRequest createUserRequest() {
        return defaultUserRequest().build();
    }
}
```

---

## 🎭 **Mock 전략**

### **Mock 사용 가이드**

#### **외부 의존성 Mock**

```java
@Test
void 사용자_생성_시_외부_서비스_호출() {
    // Given
    UserRequest request = createUserRequest();
    given(userRepository.save(any(User.class))).willReturn(createUser());

    // When
    userService.createUser(request);

    // Then
    verify(emailService).sendWelcomeEmail(request.getEmail());
    verify(fileStorageService).createUserFolder(anyString());
    verify(analyticsService).trackUserSignup(anyString());
}
```

#### **정적 메서드 Mock**

```java
@Test
void 현재_시간_기반_테스트() {
    // Given
    LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 15, 10, 0);
    try (MockedStatic<LocalDateTime> mockedDateTime = mockStatic(LocalDateTime.class)) {
        mockedDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

        // When
        User user = userService.createUser(createUserRequest());

        // Then
        assertThat(user.getCreatedAt()).isEqualTo(fixedTime);
    }
}
```

#### **Spy 사용**

```java
@Test
void 부분적_Mock_테스트() {
    // Given
    UserService spyUserService = spy(userService);
    doReturn(createUser()).when(spyUserService).getUserFromCache(anyLong());

    // When
    UserResponse response = spyUserService.getUser(1L);

    // Then
    assertThat(response.getId()).isEqualTo(1L);
    verify(spyUserService).getUserFromCache(1L);
    verify(spyUserService, never()).getUserFromDatabase(anyLong());
}
```

---

## 🔍 **테스트 케이스 설계**

### **경계값 테스트**

```java
@ParameterizedTest
@ValueSource(ints = {0, 1, 2, 10, 19, 20, 21, 100})
void 페이지_크기_경계값_테스트(int pageSize) {
    // Given
    PageRequest pageRequest = PageRequest.of(0, pageSize);

    if (pageSize < 1 || pageSize > 100) {
        // When & Then
        assertThatThrownBy(() -> userService.getUsers(pageRequest))
            .isInstanceOf(IllegalArgumentException.class);
    } else {
        // When & Then
        assertThatCode(() -> userService.getUsers(pageRequest))
            .doesNotThrowAnyException();
    }
}
```

### **상태 기반 테스트**

```java
@Test
void 사용자_상태_변경_테스트() {
    // Given
    User user = createUser();
    given(userRepository.findById(1L)).willReturn(Optional.of(user));

    // When
    userService.deactivateUser(1L);

    // Then
    assertThat(user.isActive()).isFalse();
    assertThat(user.getDeactivatedAt()).isNotNull();
    verify(userRepository).save(user);
}
```

### **상호작용 테스트**

```java
@Test
void 사용자_삭제_시_관련_데이터_정리() {
    // Given
    User user = createUser();
    given(userRepository.findById(1L)).willReturn(Optional.of(user));

    // When
    userService.deleteUser(1L);

    // Then
    verify(userRepository).delete(user);
    verify(projectService).deleteUserProjects(1L);
    verify(commentService).deleteUserComments(1L);
    verify(fileStorageService).deleteUserFiles(1L);
}
```

---

## 📊 **테스트 커버리지**

### **커버리지 측정**

#### **JaCoCo 설정**

```gradle
// build.gradle
jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/config/**',
                '**/dto/**',
                '**/vo/**',
                '**/entity/**',
                '**/enums/**',
                '**/exception/**'
            ])
        }))
    }
}
```

#### **커버리지 확인**

```bash
# 커버리지 리포트 생성
./gradlew jacocoTestReport

# HTML 리포트 확인
open build/reports/jacoco/test/html/index.html

# 커버리지 검증
./gradlew jacocoTestCoverageVerification
```

### **커버리지 목표**

- **전체 커버리지**: 70% 이상
- **핵심 비즈니스 로직**: 90% 이상
- **서비스 계층**: 80% 이상
- **유틸리티 클래스**: 95% 이상

---

## 🚫 **테스트 안티패턴**

### **피해야 할 패턴**

#### **테스트 간 의존성**

```java
// ❌ Bad: 테스트 간 의존성
class BadTest {
    private static int counter = 0;

    @Test
    void test1() {
        counter++;
        assertThat(counter).isEqualTo(1);
    }

    @Test
    void test2() {
        counter++;
        assertThat(counter).isEqualTo(2); // test1에 의존
    }
}

// ✅ Good: 독립적인 테스트
class GoodTest {
    @Test
    void test1() {
        int result = service.process(1);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void test2() {
        int result = service.process(2);
        assertThat(result).isEqualTo(2);
    }
}
```

#### **과도한 Mock**

```java
// ❌ Bad: 모든 것을 Mock
@Test
void 과도한_Mock_사용() {
    given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
    given(emailService.sendEmail(anyString())).willReturn(mock(EmailResult.class));
    given(fileStorageService.upload(any())).willReturn(mock(FileResult.class));

    // 실제 로직이 거의 테스트되지 않음
}

// ✅ Good: 필요한 것만 Mock
@Test
void 적절한_Mock_사용() {
    given(userRepository.findById(1L)).willReturn(Optional.of(createUser()));

    UserResponse response = userService.getUser(1L);

    assertThat(response.getId()).isEqualTo(1L);
}
```

#### **불안정한 테스트**

```java
// ❌ Bad: 시간에 의존하는 테스트
@Test
void 시간_의존_테스트() {
    User user = userService.createUser(createUserRequest());

    // 현재 시간에 따라 실패할 수 있음
    assertThat(user.getCreatedAt()).isEqualTo(LocalDateTime.now());
}

// ✅ Good: 고정된 시간 사용
@Test
void 고정_시간_테스트() {
    LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 15, 10, 0);
    try (MockedStatic<LocalDateTime> mockedDateTime = mockStatic(LocalDateTime.class)) {
        mockedDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

        User user = userService.createUser(createUserRequest());

        assertThat(user.getCreatedAt()).isEqualTo(fixedTime);
    }
}
```

---

## 📈 **성능 테스트**

### **단위 성능 테스트**

```java
@Test
void 사용자_목록_조회_성능_테스트() {
    // Given
    List<User> users = IntStream.range(0, 1000)
        .mapToObj(i -> createUserWithId((long) i))
        .collect(Collectors.toList());
    given(userRepository.findAll()).willReturn(users);

    // When & Then
    assertThatCode(() -> {
        long startTime = System.currentTimeMillis();
        userService.getUsers();
        long endTime = System.currentTimeMillis();

        assertThat(endTime - startTime).isLessThan(100); // 100ms 이내
    }).doesNotThrowAnyException();
}
```

---

## 📞 **지원 및 연락처**

### **단위 테스트 관련 문의**

- **슬랙**: #dataracy-unit-testing
- **이메일**: testing@dataracy.co.kr
- **문서**: [테스트 가이드](./README.md)

### **참고 자료**

- **JUnit 5**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **AssertJ**: https://assertj.github.io/doc/

---

**💡 단위 테스트 관련 문제가 발생하면 개발팀에 문의하세요!**
