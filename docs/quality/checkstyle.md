# Checkstyle 코드 스타일 가이드

## 개요

Checkstyle은 Java 코드의 스타일과 코딩 표준을 검사하는 정적 분석 도구입니다. 일관된 코드 스타일을 유지하고 코딩 표준을 준수하도록 도와줍니다.

## 설정

### Gradle 설정

```gradle
// Checkstyle 플러그인 활성화
plugins {
    id 'checkstyle'
}

// Checkstyle 설정
checkstyle {
    toolVersion = '10.12.4'
    configFile = file('checkstyle.xml')
    ignoreFailures = true
    maxWarnings = 100
}
```

### Checkstyle 설정 파일

현재 프로젝트의 `checkstyle.xml` 설정:

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>
    <property name="fileExtensions" value="java, properties, xml"/>

    <!-- 제외할 파일 패턴 -->
    <module name="BeforeExecutionExclusionFileFilter">
        <property name="fileNamePattern" value="module\-info\.java$"/>
    </module>
    <module name="BeforeExecutionExclusionFileFilter">
        <property name="fileNamePattern" value=".*Q[A-Z].*\.java$"/>
    </module>
    <module name="BeforeExecutionExclusionFileFilter">
        <property name="fileNamePattern" value=".*/generated/.*"/>
    </module>

    <!-- 파일 길이 제한 -->
    <module name="FileLength">
        <property name="max" value="2000"/>
    </module>

    <!-- 라인 길이 제한 -->
    <module name="LineLength">
        <property name="fileExtensions" value="java"/>
        <property name="max" value="200"/>
        <property name="ignorePattern" value="^package.*|^import.*|a href|href|http://|https://|ftp://"/>
    </module>

    <!-- 탭 문자 검사 -->
    <module name="FileTabCharacter">
        <property name="eachLine" value="true"/>
    </module>

    <module name="TreeWalker">
        <!-- 메서드 길이 제한 -->
        <module name="MethodLength">
            <property name="max" value="200"/>
        </module>

        <!-- 공백 관련 규칙 -->
        <module name="GenericWhitespace"/>
        <module name="NoWhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="ParenPad"/>
        <module name="WhitespaceAfter"/>

        <!-- 수정자 관련 규칙 -->
        <module name="ModifierOrder"/>
        <module name="RedundantModifier"/>

        <!-- 블록 관련 규칙 -->
        <module name="AvoidNestedBlocks"/>
        <module name="EmptyBlock"/>

        <!-- 기타 규칙 -->
        <module name="EmptyStatement"/>
        <module name="EqualsHashCode"/>
        <module name="IllegalInstantiation"/>
        <module name="InnerAssignment"/>

        <!-- 매직 넘버 검사 -->
        <module name="MagicNumber">
            <property name="ignoreNumbers" value="-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 20, 21, 25, 27, 28, 30, 31, 32, 36, 40, 41, 50, 55, 60, 99, 100, 101, 102, 120, 123, 150, 200, 201, 202, 204, 300, 400, 401, 404, 409, 500, 999, 1000, 1024, 1500, 2000, 2022, 2023, 2025, 2048, 3000, 4096, 5000, 8192, 10000, 15000, 16000, 16384, 32000, 32768, 60000, 90000, 100000, 120000, 134217728, 3600, 86400, 15000000, 67088640, 1.5, 1.3, 64_000, 30_000, 67_108_864, 1_000_000, 900000, 1_000_000"/>
            <property name="ignoreHashCodeMethod" value="true"/>
            <property name="ignoreAnnotation" value="true"/>
        </module>

        <!-- 불린 표현식 단순화 -->
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>

        <!-- 유틸리티 클래스 생성자 숨김 -->
        <module name="HideUtilityClassConstructor"/>

        <!-- 가시성 수정자 -->
        <module name="VisibilityModifier">
            <property name="protectedAllowed" value="true"/>
        </module>

        <!-- 기타 규칙 -->
        <module name="ArrayTypeStyle"/>
        <module name="TodoComment"/>
        <module name="UpperEll"/>

        <!-- 명명 규칙 -->
        <module name="ConstantName">
            <property name="format" value="^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"/>
        </module>
        <module name="LocalFinalVariableName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="MethodName"/>
        <module name="PackageName"/>
        <module name="ParameterName"/>
        <module name="StaticVariableName"/>
        <module name="TypeName"/>
    </module>
</module>
```

## 사용법

### 기본 명령어

```bash
# Checkstyle 검사 실행
./gradlew checkstyleMain

# 테스트 코드 Checkstyle 검사
./gradlew checkstyleTest

# 모든 Checkstyle 검사 실행
./gradlew checkstyle

# HTML 리포트 생성
./gradlew checkstyleMain --info
```

### 리포트 확인

Checkstyle 리포트는 다음 위치에서 확인할 수 있습니다:

- **HTML 리포트**: `build/reports/checkstyle/main.html`
- **XML 리포트**: `build/reports/checkstyle/main.xml`

## 주요 규칙 및 개선 방법

### 1. 파일 및 라인 관련 규칙

#### FileLength (파일 길이)

- **규칙**: 파일당 최대 2000라인
- **개선 방법**: 큰 파일을 여러 개의 작은 파일로 분리

```java
// Before: 너무 긴 파일
public class UserService {
    // 2000라인 이상의 코드...
}

// After: 책임별로 분리
public class UserService {
    // 핵심 비즈니스 로직만
}

public class UserValidationService {
    // 검증 로직
}

public class UserNotificationService {
    // 알림 로직
}
```

#### LineLength (라인 길이)

- **규칙**: 라인당 최대 200자
- **개선 방법**: 긴 라인을 여러 라인으로 분리

```java
// Before: 너무 긴 라인
String result = userService.findByEmailAndStatusAndCreatedDateBetween(email, status, startDate, endDate);

// After: 여러 라인으로 분리
String result = userService.findByEmailAndStatusAndCreatedDateBetween(
    email,
    status,
    startDate,
    endDate
);
```

### 2. 메서드 관련 규칙

#### MethodLength (메서드 길이)

- **규칙**: 메서드당 최대 200라인
- **개선 방법**: 메서드를 작은 단위로 분리

```java
// Before: 너무 긴 메서드
public void processUserRegistration(User user) {
    // 200라인 이상의 코드...
    validateUser(user);
    saveUser(user);
    sendWelcomeEmail(user);
    createUserProfile(user);
    // ... 더 많은 로직
}

// After: 메서드 분리
public void processUserRegistration(User user) {
    validateAndSaveUser(user);
    setupUserProfile(user);
    sendNotifications(user);
}

private void validateAndSaveUser(User user) {
    validateUser(user);
    saveUser(user);
}

private void setupUserProfile(User user) {
    createUserProfile(user);
    setDefaultPreferences(user);
}

private void sendNotifications(User user) {
    sendWelcomeEmail(user);
    sendVerificationEmail(user);
}
```

### 3. 명명 규칙

#### ConstantName (상수 명명)

- **규칙**: `UPPER_SNAKE_CASE` 형식
- **예시**: `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`

```java
// Before: 잘못된 상수 명명
private static final int maxRetryCount = 3;
private static final String defaultTimeout = "30s";

// After: 올바른 상수 명명
private static final int MAX_RETRY_COUNT = 3;
private static final String DEFAULT_TIMEOUT = "30s";
```

#### MethodName (메서드 명명)

- **규칙**: `camelCase` 형식, 동사로 시작
- **예시**: `getUserById`, `validateEmail`, `sendNotification`

```java
// Before: 잘못된 메서드 명명
public void user_data_processing() { }
public void EmailValidation() { }

// After: 올바른 메서드 명명
public void processUserData() { }
public void validateEmail() { }
```

### 4. 매직 넘버 규칙

#### MagicNumber (매직 넘버)

- **규칙**: 의미 있는 상수로 대체
- **개선 방법**: 매직 넘버를 상수로 정의

```java
// Before: 매직 넘버 사용
if (user.getAge() < 18) {
    throw new IllegalArgumentException("User must be at least 18 years old");
}

if (password.length() < 8) {
    throw new IllegalArgumentException("Password must be at least 8 characters");
}

// After: 상수 사용
private static final int MINIMUM_AGE = 18;
private static final int MINIMUM_PASSWORD_LENGTH = 8;

if (user.getAge() < MINIMUM_AGE) {
    throw new IllegalArgumentException("User must be at least " + MINIMUM_AGE + " years old");
}

if (password.length() < MINIMUM_PASSWORD_LENGTH) {
    throw new IllegalArgumentException("Password must be at least " + MINIMUM_PASSWORD_LENGTH + " characters");
}
```

### 5. 불린 표현식 단순화

#### SimplifyBooleanExpression (불린 표현식 단순화)

```java
// Before: 복잡한 불린 표현식
if (isValid == true) { }
if (isEnabled == false) { }
if (user != null && user.isActive() == true) { }

// After: 단순화된 표현식
if (isValid) { }
if (!isEnabled) { }
if (user != null && user.isActive()) { }
```

#### SimplifyBooleanReturn (불린 반환 단순화)

```java
// Before: 복잡한 불린 반환
public boolean isValid(String value) {
    if (value != null && !value.isEmpty()) {
        return true;
    } else {
        return false;
    }
}

// After: 단순화된 반환
public boolean isValid(String value) {
    return value != null && !value.isEmpty();
}
```

### 6. 공백 및 포맷팅 규칙

#### WhitespaceAfter (공백 후)

```java
// Before: 공백 누락
for(int i=0;i<10;i++) { }
if(condition) { }

// After: 적절한 공백
for (int i = 0; i < 10; i++) { }
if (condition) { }
```

#### NoWhitespaceBefore (공백 전 금지)

```java
// Before: 불필요한 공백
int x = 1 ;
String name = "test" ;

// After: 공백 제거
int x = 1;
String name = "test";
```

## CI/CD 통합

### GitHub Actions 설정

```yaml
name: Checkstyle

on: [push, pull_request]

jobs:
  checkstyle:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Run Checkstyle
        run: ./gradlew checkstyleMain checkstyleTest

      - name: Upload Checkstyle Report
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: checkstyle-report
          path: build/reports/checkstyle/
```

## IDE 통합

### IntelliJ IDEA 설정

1. **Checkstyle 플러그인 설치**

   - File → Settings → Plugins → Checkstyle-IDEA 설치

2. **설정 파일 연결**

   - File → Settings → Tools → Checkstyle
   - Configuration File에 `checkstyle.xml` 경로 설정

3. **실시간 검사 활성화**
   - File → Settings → Tools → Checkstyle
   - "Checkstyle active" 체크

### Eclipse 설정

1. **Checkstyle 플러그인 설치**

   - Help → Eclipse Marketplace → Checkstyle 검색 후 설치

2. **설정 파일 연결**
   - Window → Preferences → Checkstyle
   - New → External Configuration File 선택
   - `checkstyle.xml` 파일 경로 설정

## 문제 해결

### 일반적인 문제

1. **Checkstyle 검사 실패**

   ```bash
   # 상세한 로그와 함께 실행
   ./gradlew checkstyleMain --info --stacktrace

   # 특정 파일만 검사
   ./gradlew checkstyleMain -Pcheckstyle.includes="**/UserService.java"
   ```

2. **설정 파일 인식 실패**

   - `checkstyle.xml` 파일 경로 확인
   - XML 문법 오류 확인
   - 파일 인코딩 확인 (UTF-8)

3. **너무 많은 경고**
   - `maxWarnings` 설정 조정
   - `ignoreFailures = true` 설정으로 빌드 실패 방지

### 디버깅 팁

```bash
# Checkstyle 버전 확인
./gradlew checkstyleMain --version

# 특정 규칙만 실행
./gradlew checkstyleMain -Pcheckstyle.config.location=checkstyle-custom.xml

# 리포트 상세 확인
open build/reports/checkstyle/main.html
```

## 모범 사례

### 1. 점진적 적용

- 기존 프로젝트에 Checkstyle을 적용할 때는 점진적으로 규칙을 추가
- `ignoreFailures = true`로 설정하여 빌드 실패 방지

### 2. 팀 표준 설정

- 팀 내에서 합의된 코딩 표준 설정
- 프로젝트 특성에 맞는 규칙 선택

### 3. 자동화

- CI/CD 파이프라인에 Checkstyle 검사 자동화
- 커밋 전 자동 검사 설정

### 4. 정기적 리뷰

- Checkstyle 리포트를 정기적으로 리뷰
- 새로운 규칙 추가 및 기존 규칙 조정

## 커스텀 규칙 설정

### 프로젝트별 규칙 추가

```xml
<!-- 커스텀 규칙 예시 -->
<module name="RegexpSingleline">
    <property name="format" value="System\.out\.println"/>
    <property name="message" value="System.out.println 사용 금지"/>
</module>

<module name="RegexpSingleline">
    <property name="format" value="TODO|FIXME"/>
    <property name="message" value="TODO/FIXME 주석 제거 필요"/>
</module>
```

## 관련 도구

- **Spotless**: 코드 포맷팅 자동화
- **SonarQube**: 종합적인 코드 품질 분석
- **PMD**: 추가적인 코드 품질 규칙
- **SpotBugs**: 버그 패턴 검출

## 참고 자료

- [Checkstyle 공식 문서](https://checkstyle.sourceforge.io/)
- [Checkstyle 규칙 가이드](https://checkstyle.sourceforge.io/checks.html)
- [Gradle Checkstyle 플러그인](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [Java 코딩 표준 가이드](https://google.github.io/styleguide/javaguide.html)
