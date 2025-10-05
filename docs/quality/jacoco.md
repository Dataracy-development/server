# JaCoCo 코드 커버리지 가이드

## 개요

JaCoCo(Java Code Coverage)는 Java 애플리케이션의 테스트 커버리지를 측정하는 도구입니다. 코드의 어느 부분이 테스트에 의해 실행되었는지 분석하여 테스트 품질을 평가합니다.

## 설정

### Gradle 설정

```gradle
// JaCoCo 플러그인 활성화
plugins {
    id 'jacoco'
}

// JaCoCo 버전 설정
jacoco {
    toolVersion = "0.8.8"
}

// 테스트 커버리지 리포트 생성
jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }

    // 실무적 설정: 특정 패키지만 커버리지 측정
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/config/**',
                '**/dto/**',
                '**/vo/**',
                '**/entity/**',
                '**/enums/**',
                '**/exception/**',
                '**/DataracyApplication.class'
            ])
        }))
    }

    finalizedBy jacocoTestCoverageVerification
}

// 커버리지 검증 규칙
jacocoTestCoverageVerification {
    dependsOn jacocoTestReport

    violationRules {
        rule {
            limit {
                minimum = 0.70 // 70% 이상 커버리지 요구
            }
        }

        // 경고만 표시, 빌드 실패하지 않음
        rule {
            enabled = false
            limit {
                minimum = 0.75 // 75% 목표 (경고 수준)
            }
        }
    }
}
```

## 사용법

### 기본 명령어

```bash
# 테스트 실행 및 커버리지 리포트 생성
./gradlew jacocoTestReport

# 커버리지 검증
./gradlew jacocoTestCoverageVerification

# 테스트와 커버리지를 함께 실행
./gradlew test jacocoTestReport
```

### 커버리지 리포트 확인

커버리지 리포트는 다음 위치에서 확인할 수 있습니다:

- **HTML 리포트**: `build/reports/jacoco/test/index.html`
- **XML 리포트**: `build/reports/jacoco/test/jacocoTestReport.xml`

## 커버리지 지표

### 1. Line Coverage (라인 커버리지)

- 실행된 코드 라인의 비율
- 가장 기본적인 커버리지 지표

### 2. Branch Coverage (분기 커버리지)

- if문, switch문 등의 분기점이 테스트된 비율
- 조건문의 true/false 경로 모두 테스트되었는지 확인

### 3. Instruction Coverage (명령어 커버리지)

- 실행된 바이트코드 명령어의 비율
- 가장 세밀한 커버리지 지표

### 4. Method Coverage (메서드 커버리지)

- 실행된 메서드의 비율
- 메서드 단위의 테스트 완성도

## 커버리지 기준

### 프로젝트 기준

- **최소 요구사항**: 70%
- **목표 수준**: 75%
- **우수 수준**: 80% 이상

### 제외 대상

다음 패키지/클래스는 커버리지 측정에서 제외됩니다:

```gradle
exclude: [
    '**/config/**',      // 설정 클래스
    '**/dto/**',         // 데이터 전송 객체
    '**/vo/**',          // 값 객체
    '**/entity/**',      // JPA 엔티티
    '**/enums/**',       // 열거형
    '**/exception/**',   // 예외 클래스
    '**/DataracyApplication.class'  // 메인 클래스
]
```

## 커버리지 개선 방법

### 1. 테스트 케이스 추가

```java
@Test
void shouldReturnUserWhenValidId() {
    // Given
    Long userId = 1L;
    User expectedUser = new User(userId, "test@example.com");
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

    // When
    User result = userService.getUser(userId);

    // Then
    assertThat(result).isEqualTo(expectedUser);
    verify(userRepository).findById(userId);
}
```

### 2. 예외 상황 테스트

```java
@Test
void shouldThrowExceptionWhenUserNotFound() {
    // Given
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.getUser(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User not found with id: " + userId);
}
```

### 3. 경계값 테스트

```java
@Test
void shouldHandleEdgeCases() {
    // Given
    String emptyString = "";
    String nullString = null;

    // When & Then
    assertThat(userService.validateEmail(emptyString)).isFalse();
    assertThat(userService.validateEmail(nullString)).isFalse();
}
```

## CI/CD 통합

### GitHub Actions 설정

```yaml
- name: Generate JaCoCo Report
  run: ./gradlew jacocoTestReport

- name: Upload JaCoCo Report
  uses: codecov/codecov-action@v3
  with:
    file: ./build/reports/jacoco/test/jacocoTestReport.xml
    flags: unittests
    name: codecov-umbrella
```

## 모니터링 및 알림

### 커버리지 추적

- 커버리지 변화를 지속적으로 모니터링
- 커버리지가 기준 이하로 떨어지면 알림 설정
- PR별 커버리지 변화 확인

### 품질 게이트

```yaml
# quality-gate.yml
coverage:
  minimum: 70
  target: 80
```

## 문제 해결

### 일반적인 문제

1. **커버리지가 0%로 나오는 경우**

   - 테스트가 실제로 실행되지 않았을 가능성
   - `./gradlew clean test jacocoTestReport` 실행

2. **특정 클래스가 커버리지에 포함되지 않는 경우**

   - exclude 설정 확인
   - 클래스 경로 확인

3. **커버리지가 예상보다 낮은 경우**
   - 테스트 케이스 부족
   - 예외 처리 경로 미테스트
   - 조건문 분기 미테스트

### 디버깅 팁

```bash
# 상세한 로그와 함께 실행
./gradlew test --info

# 특정 테스트만 실행
./gradlew test --tests "com.dataracy.service.UserServiceTest"

# 커버리지 리포트 강제 재생성
./gradlew clean jacocoTestReport
```

## 모범 사례

### 1. 의미 있는 테스트 작성

- 단순히 커버리지를 높이기 위한 테스트보다는 실제 비즈니스 로직을 검증하는 테스트 작성

### 2. 점진적 개선

- 한 번에 모든 커버리지를 높이려 하지 말고 점진적으로 개선

### 3. 팀 기준 설정

- 팀 내에서 합의된 커버리지 기준 설정
- 프로젝트 특성에 맞는 기준 적용

### 4. 정기적 리뷰

- 커버리지 리포트를 정기적으로 리뷰
- 커버리지가 낮은 영역 식별 및 개선 계획 수립

## 관련 도구

- **SonarQube**: JaCoCo 리포트를 통합하여 종합적인 코드 품질 분석
- **Codecov**: 커버리지 리포트 시각화 및 추적
- **IntelliJ IDEA**: IDE 내에서 커버리지 확인 및 개선 제안

## 참고 자료

- [JaCoCo 공식 문서](https://www.jacoco.org/jacoco/)
- [Gradle JaCoCo 플러그인](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [테스트 커버리지 모범 사례](https://martinfowler.com/bliki/TestCoverage.html)
