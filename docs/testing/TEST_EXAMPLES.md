# 테스트 예제 및 베스트 프랙티스

## 📋 목차
1. [단위 테스트 예제](#단위-테스트-예제)
2. [통합 테스트 예제](#통합-테스트-예제)
3. [웹 계층 테스트 예제](#웹-계층-테스트-예제)
4. [도메인 모델 테스트 예제](#도메인-모델-테스트-예제)
5. [예외 처리 테스트 예제](#예외-처리-테스트-예제)
6. [성능 테스트 예제](#성능-테스트-예제)

---

## 🔧 단위 테스트 예제

### 1. 서비스 계층 테스트
```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserCommandPort userCommandPort;
    
    @Mock
    private UserQueryPort userQueryPort;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자 생성")
    class CreateUser {
        
        @Test
        @DisplayName("유효한 데이터로 사용자를 생성한다")
        void createUser_WithValidData_ShouldCreateUser() {
            // given
            UserRequest request = TestDataBuilder.userRequest()
                    .email("test@example.com")
                    .nickname("테스트유저")
                    .password("password123!")
                    .build();
            
            User savedUser = TestDataBuilder.user()
                    .email(request.getEmail())
                    .nickname(request.getNickname())
                    .build();
            
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userCommandPort.save(any(User.class))).thenReturn(savedUser);
            
            // when
            UserResponse response = userService.createUser(request);
            
            // then
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo(request.getEmail());
            assertThat(response.getNickname()).isEqualTo(request.getNickname());
            
            verify(passwordEncoder).encode(request.getPassword());
            verify(userCommandPort).save(any(User.class));
        }
        
        @Test
        @DisplayName("중복 이메일로 사용자 생성 시 예외가 발생한다")
        void createUser_WithDuplicateEmail_ShouldThrowException() {
            // given
            UserRequest request = TestDataBuilder.userRequest()
                    .email("duplicate@example.com")
                    .build();
            
            when(userQueryPort.existsByEmail(request.getEmail())).thenReturn(true);
            
            // when & then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");
        }
    }
}
```

### 2. 도메인 모델 테스트
```java
class UserTest {

    @Test
    @DisplayName("User.of() 정적 팩토리 메서드로 인스턴스를 생성한다")
    void createUserWithOfMethod() {
        // given
        String email = "test@example.com";
        String nickname = "테스트유저";
        String password = "password123!";
        RoleType role = RoleType.ROLE_USER;
        
        // when
        User user = User.of(email, nickname, password, role);
        
        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("사용자 정보 수정 시 업데이트 시간이 변경된다")
    void updateUser_ShouldUpdateTimestamp() {
        // given
        User user = TestDataBuilder.user()
                .email("test@example.com")
                .nickname("기존닉네임")
                .build();
        
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();
        
        // when
        user.updateNickname("새닉네임");
        
        // then
        assertThat(user.getNickname()).isEqualTo("새닉네임");
        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
}
```

---

## 🔗 통합 테스트 예제

### 1. 데이터베이스 통합 테스트
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 생성 및 조회 통합 테스트")
    void createAndFindUser_IntegrationTest() {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email(TestDataBuilder.RandomData.randomEmail())
                .nickname(TestDataBuilder.RandomData.randomNickname())
                .build();
        
        // when
        UserResponse createdUser = userService.createUser(request);
        UserResponse foundUser = userService.getUser(createdUser.getId());
        
        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(foundUser.getNickname()).isEqualTo(request.getNickname());
        
        // 데이터베이스에서 직접 확인
        var savedUser = userJpaRepository.findById(createdUser.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo(request.getEmail());
    }
    
    @Test
    @DisplayName("트랜잭션 롤백 테스트")
    void transactionRollback_WhenExceptionOccurs_ShouldRollback() {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email(TestDataBuilder.RandomData.randomEmail())
                .build();
        
        // when - 예외 발생 시나리오
        assertThatThrownBy(() -> userService.createUserWithException(request))
                .isInstanceOf(RuntimeException.class);
        
        // then - 데이터가 롤백되었는지 확인
        var allUsers = userJpaRepository.findAll();
        assertThat(allUsers).isEmpty();
    }
}
```

---

## 🌐 웹 계층 테스트 예제

### 1. REST Controller 테스트
```java
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @Test
    @DisplayName("사용자 생성 API 테스트")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email("test@example.com")
                .nickname("테스트유저")
                .build();
        
        UserResponse response = TestDataBuilder.userResponse()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();
        
        when(userService.createUser(any(UserRequest.class))).thenReturn(response);
        
        // when & then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.nickname").value(request.getNickname()))
                .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    @DisplayName("잘못된 요청 데이터로 사용자 생성 시 400 에러")
    void createUser_WithInvalidData_ShouldReturn400() throws Exception {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email("invalid-email") // 잘못된 이메일 형식
                .nickname("") // 빈 닉네임
                .build();
        
        // when & then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }
}
```

### 2. Web Mapper 테스트
```java
@ExtendWith(MockitoExtension.class)
class UserWebMapperTest {

    private UserWebMapper userWebMapper;

    @BeforeEach
    void setUp() {
        userWebMapper = new UserWebMapper();
    }

    @Test
    @DisplayName("UserRequest를 User로 변환한다")
    void toUser_WithValidRequest_ShouldReturnUser() {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email("test@example.com")
                .nickname("테스트유저")
                .password("password123!")
                .build();
        
        // when
        User user = userWebMapper.toUser(request);
        
        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getNickname()).isEqualTo(request.getNickname());
        assertThat(user.getPassword()).isEqualTo(request.getPassword());
    }
    
    @Test
    @DisplayName("User를 UserResponse로 변환한다")
    void toResponse_WithValidUser_ShouldReturnResponse() {
        // given
        User user = TestDataBuilder.user()
                .email("test@example.com")
                .nickname("테스트유저")
                .role(RoleType.ROLE_USER)
                .build();
        
        // when
        UserResponse response = userWebMapper.toResponse(user);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getRole()).isEqualTo(user.getRole());
    }
}
```

---

## 🎯 도메인 모델 테스트 예제

### 1. Value Object 테스트
```java
class EmailTest {

    @Test
    @DisplayName("유효한 이메일 형식으로 Email을 생성한다")
    void createEmail_WithValidFormat_ShouldCreateEmail() {
        // given
        String validEmail = "test@example.com";
        
        // when
        Email email = Email.of(validEmail);
        
        // then
        assertThat(email.getValue()).isEqualTo(validEmail);
    }
    
    @Test
    @DisplayName("잘못된 이메일 형식으로 Email 생성 시 예외가 발생한다")
    void createEmail_WithInvalidFormat_ShouldThrowException() {
        // given
        String invalidEmail = "invalid-email";
        
        // when & then
        assertThatThrownBy(() -> Email.of(invalidEmail))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessage("유효하지 않은 이메일 형식입니다.");
    }
    
    @Test
    @DisplayName("동일한 값을 가진 Email은 같다")
    void equals_WithSameValue_ShouldBeEqual() {
        // given
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");
        
        // when & then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}
```

### 2. Enum 테스트
```java
class RoleTypeTest {

    @Test
    @DisplayName("유효한 역할 문자열로 RoleType을 생성한다")
    void of_WithValidRole_ShouldReturnRoleType() {
        // given
        String validRole = "ROLE_USER";
        
        // when
        RoleType roleType = RoleType.of(validRole);
        
        // then
        assertThat(roleType).isEqualTo(RoleType.ROLE_USER);
    }
    
    @Test
    @DisplayName("대소문자 구분 없이 RoleType을 생성한다")
    void of_WithCaseInsensitive_ShouldReturnRoleType() {
        // given
        String lowerCaseRole = "role_user";
        
        // when
        RoleType roleType = RoleType.of(lowerCaseRole);
        
        // then
        assertThat(roleType).isEqualTo(RoleType.ROLE_USER);
    }
    
    @Test
    @DisplayName("잘못된 역할 문자열로 RoleType 생성 시 예외가 발생한다")
    void of_WithInvalidRole_ShouldThrowException() {
        // given
        String invalidRole = "INVALID_ROLE";
        
        // when & then
        assertThatThrownBy(() -> RoleType.of(invalidRole))
                .isInstanceOf(InvalidRoleTypeException.class)
                .hasMessage("유효하지 않은 역할 타입입니다.");
    }
}
```

---

## ⚠️ 예외 처리 테스트 예제

### 1. 비즈니스 예외 테스트
```java
@ExtendWith(MockitoExtension.class)
class UserServiceExceptionTest {

    @Mock
    private UserQueryPort userQueryPort;
    
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 조회 시 사용자가 없으면 예외가 발생한다")
    void getUser_WhenUserNotFound_ShouldThrowException() {
        // given
        Long nonExistentUserId = 999L;
        when(userQueryPort.findById(nonExistentUserId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> userService.getUser(nonExistentUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
    
    @Test
    @DisplayName("중복 이메일로 회원가입 시 예외가 발생한다")
    void signUp_WithDuplicateEmail_ShouldThrowException() {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email("duplicate@example.com")
                .build();
        
        when(userQueryPort.existsByEmail(request.getEmail())).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }
}
```

### 2. 예외 응답 테스트
```java
@WebMvcTest(controllers = UserController.class)
class UserControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("사용자 조회 시 404 에러 응답")
    void getUser_WhenUserNotFound_ShouldReturn404() throws Exception {
        // given
        Long nonExistentUserId = 999L;
        when(userService.getUser(nonExistentUserId))
                .thenThrow(new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        // when & then
        mockMvc.perform(get("/api/v1/users/{id}", nonExistentUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER-002"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }
}
```

---

## ⚡ 성능 테스트 예제

### 1. 메서드 실행 시간 테스트
```java
class UserServicePerformanceTest {

    @Test
    @DisplayName("사용자 생성 성능 테스트 - 1초 이내 완료")
    void createUser_ShouldCompleteWithinOneSecond() {
        // given
        UserRequest request = TestDataBuilder.userRequest()
                .email(TestDataBuilder.RandomData.randomEmail())
                .build();
        
        // when
        long startTime = System.currentTimeMillis();
        UserResponse response = userService.createUser(request);
        long endTime = System.currentTimeMillis();
        
        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(1000); // 1초 이내
        assertThat(response).isNotNull();
    }
}
```

### 2. 대용량 데이터 처리 테스트
```java
@SpringBootTest
@ActiveProfiles("test")
class UserServiceBulkTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("대량 사용자 생성 성능 테스트")
    void createBulkUsers_ShouldCompleteWithinReasonableTime() {
        // given
        List<UserRequest> requests = IntStream.range(0, 1000)
                .mapToObj(i -> TestDataBuilder.userRequest()
                        .email(TestDataBuilder.RandomData.randomEmail())
                        .nickname("유저" + i)
                        .build())
                .collect(Collectors.toList());
        
        // when
        long startTime = System.currentTimeMillis();
        List<UserResponse> responses = userService.createBulkUsers(requests);
        long endTime = System.currentTimeMillis();
        
        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(5000); // 5초 이내
        assertThat(responses).hasSize(1000);
    }
}
```

---

## 📝 테스트 작성 체크리스트

### 단위 테스트 작성 시
- [ ] Given-When-Then 패턴 사용
- [ ] 테스트 메서드명이 의도를 명확히 표현
- [ ] @DisplayName으로 한글 설명 추가
- [ ] Mock 사용으로 의존성 격리
- [ ] 예외 상황도 테스트
- [ ] 테스트 데이터 빌더 사용

### 통합 테스트 작성 시
- [ ] @SpringBootTest 사용
- [ ] @Transactional로 데이터 정리
- [ ] 실제 데이터베이스 사용
- [ ] 트랜잭션 롤백 테스트
- [ ] 실제 비즈니스 시나리오 테스트

### 웹 테스트 작성 시
- [ ] @WebMvcTest 사용
- [ ] MockMvc로 HTTP 요청/응답 테스트
- [ ] JSON 직렬화/역직렬화 테스트
- [ ] HTTP 상태 코드 검증
- [ ] 에러 응답 형식 검증

---

## 🎯 결론

이 예제들을 참고하여 일관되고 품질 높은 테스트를 작성하세요. 

**핵심 원칙:**
1. **명확성**: 테스트의 의도가 명확해야 함
2. **독립성**: 각 테스트는 독립적으로 실행되어야 함
3. **반복성**: 같은 결과를 반복적으로 얻을 수 있어야 함
4. **빠른 피드백**: 테스트 실행이 빠르고 결과가 명확해야 함
5. **유지보수성**: 테스트 코드도 프로덕션 코드만큼 중요함

