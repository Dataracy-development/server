# 📝 코딩 표준

## 📋 **개요**

Dataracy 백엔드 프로젝트의 코딩 표준 및 컨벤션을 정의합니다.

---

## 🎯 **기본 원칙**

### **가독성 우선**

- 코드는 사람이 읽기 쉬워야 함
- 명확한 변수명과 함수명 사용
- 적절한 주석과 문서화

### **일관성 유지**

- 팀 전체가 동일한 스타일 적용
- 기존 코드와 일관된 패턴 사용
- 자동화된 도구로 스타일 검증

### **유지보수성**

- 단순하고 명확한 구조
- 적절한 추상화와 분리
- 테스트 가능한 코드 작성

---

## 📁 **패키지 구조**

### **모듈별 패키지**

```
com.dataracy.modules.{domain}/
├── domain/                    # 도메인 계층
│   ├── model/                # 도메인 모델
│   ├── enums/                # 열거형
│   ├── exception/            # 도메인 예외
│   └── status/               # 상태 코드
├── application/              # 애플리케이션 계층
│   ├── port/
│   │   ├── in/              # 인바운드 포트
│   │   └── out/             # 아웃바운드 포트
│   ├── service/             # 애플리케이션 서비스
│   └── dto/                 # DTO
└── adapter/                  # 어댑터 계층
    ├── persistence/         # 데이터 영속성
    ├── query/              # 쿼리 어댑터
    └── web/                # 웹 어댑터
```

### **네이밍 컨벤션**

#### **클래스명**

- **도메인 모델**: `User`, `Project`, `Data`, `BehaviorLog`
- **엔티티**: `UserEntity`, `ProjectEntity`, `DataEntity`
- **서비스**: `UserCommandService`, `ProjectQueryService`
- **컨트롤러**: `UserController`, `ProjectController`
- **DTO**: `SelfSignUpWebRequest`, `PopularProjectResponse`, `DataDetailResponse`
- **예외**: `UserException`, `ProjectException`, `CommonException`
- **매퍼**: `UserDtoMapper`, `ProjectReadDtoMapper`
- **유틸리티**: `S3KeyGeneratorUtil`, `LoggerFactory`

#### **메서드명**

- **조회**: `find`, `get`, `search`
- **생성**: `create`, `save`, `upload`
- **수정**: `update`, `modify`, `change`
- **삭제**: `delete`, `remove`, `withdraw`
- **검증**: `validate`, `check`, `verify`

#### **변수명**

- **camelCase** 사용
- **의미 있는 이름** 사용
- **약어 지양**: `userName` (O), `usrNm` (X)
- **불린 변수**: `is`, `has`, `can` 접두사

---

## 🔧 **코드 스타일**

### **들여쓰기**

- **2칸 공백** 사용 (탭 금지)
- **Google Java Style** 적용

### **중괄호**

```java
// 클래스, 메서드
public class UserService {
    public void createUser() {
        // 구현
    }
}

// 조건문
if (condition) {
    // 구현
} else {
    // 구현
}
```

### **import 정리**

```java
// 표준 순서
import java.util.List;           // Java 표준 라이브러리
import jakarta.persistence.Entity; // Jakarta EE
import org.springframework.web.bind.annotation.RestController; // Spring
import com.dataracy.modules.user.domain.model.User; // 프로젝트 내부
```

---

## 📝 **주석 및 문서화**

### **JavaDoc**

```java
/**
 * 사용자 정보를 생성합니다.
 *
 * @param request 사용자 생성 요청 정보
 * @return 생성된 사용자 정보
 * @throws UserException 사용자 생성 실패 시
 */
public UserResponse createUser(UserRequest request) {
    // 구현
}
```

### **인라인 주석**

```java
// 비즈니스 로직: 사용자 중복 검사
if (userRepository.existsByEmail(request.getEmail())) {
    throw new UserException(UserErrorStatus.DUPLICATED_EMAIL);
}

// TODO: 추후 캐싱 적용 예정
// FIXME: 메모리 누수 가능성 있음
```

---

## 🏗️ **아키텍처 패턴**

### **DDD 적용**

- **도메인 모델** 중심 설계
- **어그리거트** 경계 명확히 정의
- **도메인 서비스**로 복잡한 비즈니스 로직 처리

### **헥사고날 아키텍처**

- **포트와 어댑터** 패턴 적용
- **의존성 역전** 원칙 준수
- **테스트 용이성** 확보

### **CQRS 패턴**

- **Command**와 **Query** 분리
- **읽기 전용 모델** 사용
- **성능 최적화** 달성

---

## 🧪 **테스트 코드**

### **테스트 클래스명**

- `{클래스명}Test`
- `UserServiceTest`, `UserControllerTest`

### **테스트 메서드명**

```java
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

### **테스트 구조**

- **Given-When-Then** 패턴 사용
- **@DisplayName**으로 한글 테스트 설명
- **@Nested**로 테스트 그룹화
- **@ExtendWith(MockitoExtension.class)** 사용
- **의미 있는 테스트 데이터** 생성
- **경계값 테스트** 포함

---

## 🔍 **코드 품질**

### **복잡도 관리**

- **메서드 길이**: 20줄 이하
- **클래스 길이**: 300줄 이하
- **순환 복잡도**: 10 이하

### **매직 넘버 금지**

```java
// Bad
if (user.getAge() > 18) {
    // 구현
}

// Good
private static final int ADULT_AGE = 18;

if (user.getAge() > ADULT_AGE) {
    // 구현
}
```

### **예외 처리**

```java
// 구체적인 예외 사용
throw new UserException(UserErrorStatus.USER_NOT_FOUND);

// 로깅 포함
LoggerFactory.service().logError("사용자 조회 실패", "userId={}", userId, e);
throw new UserException(UserErrorStatus.USER_NOT_FOUND);
```

---

## 🛠️ **도구 설정**

### **자동 포맷팅**

- **Spotless** 사용
- **Google Java Format** 적용
- **Import 정리** 자동화

### **코드 검사**

- **Checkstyle** 설정
- **SpotBugs** 정적 분석 (주석 처리됨)
- **SonarQube** 품질 게이트 (주석 처리됨)
- **JaCoCo** 테스트 커버리지

### **IDE 설정**

- **IntelliJ IDEA** 권장
- **코드 스타일** 동기화
- **라이브 템플릿** 활용

---

## 📚 **참고 자료**

### **공식 문서**

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

### **팀 규칙**

- **코드 리뷰** 필수
- **PR 템플릿** 사용
- **컨벤션 위반** 시 수정 요청

---

**💡 코딩 표준 관련 문의사항은 개발팀에 연락해주세요!**
