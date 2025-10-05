# Spotless 코드 포맷팅 가이드

## 개요

Spotless는 코드 포맷팅을 자동화하는 Gradle 플러그인입니다. Google Java Format, import 정리, 불필요한 공백 제거 등을 자동으로 수행하여 일관된 코드 스타일을 유지합니다.

## 설정

### Gradle 설정

```gradle
// Spotless 플러그인 활성화
plugins {
    id 'com.diffplug.spotless' version '6.23.3'
}

// Spotless 설정
spotless {
    java {
        // Google Java Format 적용 - 일관된 들여쓰기, 괄호 배치
        googleJavaFormat('1.19.2')

        // Import 정리 - 표준 순서로 자동 정렬
        importOrder('java', 'javax', 'org', 'com.dataracy', '')
        removeUnusedImports()  // 사용하지 않는 import 자동 제거

        // 기타 포맷팅 규칙
        trimTrailingWhitespace()  // 줄 끝 공백 자동 제거
        endWithNewline()          // 파일 끝 개행 문자로 통일

        // QueryDSL 생성 파일 제외
        targetExclude('**/Q*.java')
    }

    // XML 파일 포맷팅
    format 'xml', {
        target '*.xml'
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }

    // YAML 파일 포맷팅
    format 'yaml', {
        target '*.yml', '*.yaml'
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }

    // Gradle 파일 포맷팅
    format 'gradle', {
        target '*.gradle', '*.gradle.kts'
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }

    // Markdown 파일 포맷팅
    format 'markdown', {
        target '*.md'
        trimTrailingWhitespace()
        endWithNewline()
    }

    // 기타 파일 포맷팅
    format 'misc', {
        target '*.properties', '*.json', '*.sql'
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }
}

// Spotless 태스크 의존성 설정
tasks.named('spotlessJava') {
    mustRunAfter 'compileJava'
}
```

## 사용법

### 기본 명령어

```bash
# 코드 포맷팅 적용
./gradlew spotlessApply

# 포맷팅 검사 (변경사항 확인)
./gradlew spotlessCheck

# Java 파일만 포맷팅
./gradlew spotlessJavaApply

# 특정 파일 타입만 포맷팅
./gradlew spotlessXmlApply
./gradlew spotlessYamlApply
./gradlew spotlessGradleApply
./gradlew spotlessMarkdownApply
```

### 자동화 설정

```bash
# 빌드 시 자동 포맷팅 적용
./gradlew build spotlessApply

# 테스트 실행 전 포맷팅 검사
./gradlew spotlessCheck test
```

## 포맷팅 규칙

### 1. Java 파일 포맷팅

#### Google Java Format

- **들여쓰기**: 2칸 스페이스
- **라인 길이**: 100자 (기본값)
- **괄호 스타일**: K&R 스타일
- **중괄호**: 같은 라인에 시작, 새 라인에 끝

```java
// Before: 포맷팅 전
public class UserService{
private final UserRepository userRepository;
public UserService(UserRepository userRepository){
this.userRepository=userRepository;
}
public User findUser(Long id){
if(id==null){
throw new IllegalArgumentException("ID cannot be null");
}
return userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found"));
}
}

// After: 포맷팅 후
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User findUser(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null");
    }
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }
}
```

#### Import 정리

- **순서**: java → javax → org → com.dataracy → 기타
- **사용하지 않는 import 자동 제거**
- **와일드카드 import 금지**

```java
// Before: 정리되지 않은 import
import java.util.*;
import javax.persistence.Entity;
import org.springframework.stereotype.Service;
import com.dataracy.domain.User;
import com.other.library.SomeClass;
import java.util.List;
import java.util.ArrayList;

// After: 정리된 import
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import org.springframework.stereotype.Service;

import com.dataracy.domain.User;
```

#### 공백 및 줄바꿈 정리

- **줄 끝 공백 자동 제거**
- **파일 끝 개행 문자 통일**
- **연속된 빈 줄 정리**

```java
// Before: 공백이 있는 코드
public class Example {
    private String name;

    public void method() {
        // 코드...
    }
}

// After: 정리된 코드
public class Example {
  private String name;

  public void method() {
    // 코드...
  }
}
```

### 2. XML 파일 포맷팅

```xml
<!-- Before: 포맷팅 전 -->
<configuration>
<property name="name" value="test"/>
<property name="description" value="example"/>
</configuration>

<!-- After: 포맷팅 후 -->
<configuration>
	<property name="name" value="test" />
	<property name="description" value="example" />
</configuration>
```

### 3. YAML 파일 포맷팅

```yaml
# Before: 포맷팅 전
server:
port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: password

# After: 포맷팅 후
server:
	port: 8080
spring:
	datasource:
		url: jdbc:mysql://localhost:3306/test
		username: root
		password: password
```

### 4. Gradle 파일 포맷팅

```gradle
// Before: 포맷팅 전
plugins {
id 'java'
id 'org.springframework.boot' version '3.3.11'
}

dependencies {
implementation 'org.springframework.boot:spring-boot-starter-web'
testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

// After: 포맷팅 후
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.3.11'
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 5. Markdown 파일 포맷팅

```markdown
<!-- Before: 포맷팅 전 -->

# 제목

## 부제목

- 리스트 항목 1
- 리스트 항목 2

**굵은 글씨**

<!-- After: 포맷팅 후 -->

# 제목

## 부제목

- 리스트 항목 1
- 리스트 항목 2

**굵은 글씨**
```

## CI/CD 통합

### GitHub Actions 설정

```yaml
name: Spotless Check

on: [push, pull_request]

jobs:
  spotless:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Run Spotless Check
        run: ./gradlew spotlessCheck

      - name: Apply Spotless (if needed)
        run: ./gradlew spotlessApply
        continue-on-error: true

      - name: Check for changes
        run: |
          if [ -n "$(git status --porcelain)" ]; then
            echo "Code formatting issues found. Please run './gradlew spotlessApply' to fix them."
            git diff
            exit 1
          fi
```

### Pre-commit Hook 설정

```bash
#!/bin/sh
# .git/hooks/pre-commit

echo "Running Spotless formatting check..."
./gradlew spotlessCheck

if [ $? -ne 0 ]; then
    echo "Code formatting issues found. Applying fixes..."
    ./gradlew spotlessApply
    echo "Please review the changes and commit again."
    exit 1
fi

echo "Code formatting check passed."
```

## IDE 통합

### IntelliJ IDEA 설정

1. **Google Java Format 플러그인 설치**

   - File → Settings → Plugins → Google Java Format 설치

2. **자동 포맷팅 설정**

   - File → Settings → Tools → Actions on Save
   - "Reformat code" 체크
   - "Optimize imports" 체크

3. **코드 스타일 설정**
   - File → Settings → Editor → Code Style → Java
   - Scheme을 "GoogleStyle"로 설정

### VS Code 설정

```json
{
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
  "java.format.settings.profile": "GoogleStyle",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true
  }
}
```

## 커스텀 규칙 설정

### 프로젝트별 포맷팅 규칙

```gradle
spotless {
    java {
        googleJavaFormat('1.19.2')

        // 커스텀 import 순서
        importOrder('java', 'javax', 'jakarta', 'org', 'com.dataracy', '')

        // 특정 패키지 제외
        targetExclude('**/generated/**', '**/Q*.java')

        // 커스텀 포맷팅 규칙
        custom 'Remove unused imports', {
            it.replaceAll(/(?m)^\s*import\s+[^\s]+\s*;\s*$/, '')
        }

        // 라이센스 헤더 추가
        licenseHeader '/* Licensed under Apache-2.0 */\n'
    }

    // 커스텀 포맷터 추가
    format 'custom', {
        target '**/*.custom'
        custom 'Custom formatter', { it.toLowerCase() }
    }
}
```

## 문제 해결

### 일반적인 문제

1. **포맷팅 실패**

   ```bash
   # 상세한 로그와 함께 실행
   ./gradlew spotlessApply --info --stacktrace

   # 특정 파일만 포맷팅
   ./gradlew spotlessJavaApply -Pspotless.includes="**/UserService.java"
   ```

2. **Import 순서 문제**

   - `importOrder` 설정 확인
   - IDE의 import 정리 설정과 충돌 확인

3. **파일 인코딩 문제**
   - 프로젝트 인코딩을 UTF-8로 설정
   - IDE 인코딩 설정 확인

### 디버깅 팁

```bash
# Spotless 버전 확인
./gradlew spotlessJava --version

# 포맷팅 전후 비교
./gradlew spotlessCheck --info

# 특정 규칙만 적용
./gradlew spotlessJavaApply -Pspotless.java.googleJavaFormat.version=1.19.2
```

## 모범 사례

### 1. 자동화

- CI/CD 파이프라인에 Spotless 검사 자동화
- 커밋 전 자동 포맷팅 적용

### 2. 팀 표준

- 팀 내에서 일관된 포맷팅 규칙 설정
- Google Java Format 등 표준 스타일 사용

### 3. 점진적 적용

- 기존 프로젝트에 적용할 때는 점진적으로 진행
- 큰 변경사항은 별도 브랜치에서 작업

### 4. 정기적 실행

- 정기적으로 `spotlessApply` 실행
- 코드 리뷰 시 포맷팅 상태 확인

## 성능 최적화

### 병렬 처리 설정

```gradle
spotless {
    java {
        // 병렬 처리 활성화
        targetExclude('**/generated/**')

        // 캐시 활용
        ratchetFrom 'origin/main'
    }
}
```

### 제외 패턴 최적화

```gradle
spotless {
    java {
        // 불필요한 파일 제외로 성능 향상
        targetExclude(
            '**/generated/**',
            '**/Q*.java',
            '**/build/**',
            '**/target/**'
        )
    }
}
```

## 관련 도구

- **Checkstyle**: 코드 스타일 검사
- **Google Java Format**: Java 코드 포맷팅 표준
- **Prettier**: 다른 언어의 코드 포맷팅
- **EditorConfig**: 에디터별 일관된 설정

## 참고 자료

- [Spotless 공식 문서](https://github.com/diffplug/spotless)
- [Google Java Format](https://github.com/google/google-java-format)
- [Gradle Spotless 플러그인](https://plugins.gradle.org/plugin/com.diffplug.spotless)
- [Java 코딩 스타일 가이드](https://google.github.io/styleguide/javaguide.html)
