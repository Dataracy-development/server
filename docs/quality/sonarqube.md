# SonarQube 정적 분석 가이드

## 개요

SonarQube는 코드 품질, 보안, 유지보수성을 종합적으로 분석하는 정적 분석 도구입니다. 코드 스멜, 버그, 보안 취약점, 중복 코드 등을 자동으로 검출하여 코드 품질을 향상시킵니다.

## 설정

### Gradle 설정

```gradle
// SonarQube 플러그인 (필요시 활성화)
plugins {
    id 'org.sonarqube' version '4.2.1.3168'
}

// SonarQube 설정
sonarqube {
    properties {
        property "sonar.projectKey", "dataracy"
        property "sonar.projectName", "dataracy"
        property "sonar.host.url", "http://localhost:9000"
        property "sonar.login", "admin"
        property "sonar.password", "Juuuunny123@"
        property "sonar.sources", "src/main/java"
        property "sonar.tests", "src/test/java"
        property "sonar.java.binaries", "build/classes/java/main"
        property "sonar.java.test.binaries", "build/classes/java/test"
        property "sonar.java.coveragePlugin", "jacoco"
        property "sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml"

        // 제외할 파일/패키지
        property "sonar.coverage.exclusions", "**/config/**/*,**/exception/**/*,**/dto/**/*,**/entity/**/*,**/vo/**/*"
        property "sonar.exclusions", "**/generated/**/*,**/build/**/*,**/.DS_Store,**/config/**/*,**/dto/**/*,**/exception/**/*,**/entity/**/*,**/vo/**/*,**/domain/model/**/*,**/domain/enums/**/*,**/domain/entity/**/*"
        property "sonar.test.exclusions", "**/generated/**/*,**/build/**/*,**/.DS_Store"

        // 인코딩 및 버전 설정
        property "sonar.sourceEncoding", "UTF-8"
        property "sonar.java.source", "17"
        property "sonar.java.target", "17"

        // 품질 게이트 설정
        property "sonar.qualitygate.wait", "false"
        property "sonar.qualitygate.timeout", "300"
        property "sonar.coverage.minimum", "70"
        property "sonar.coverage.target", "80"
        property "sonar.newCode.coverage.minimum", "0"
        property "sonar.newCode.period", "previous_version"

        // 중복 코드 설정
        property "sonar.duplicated_lines_density.maximum", "5"
        property "sonar.cpd.java.minimumtokens", "100"
        property "sonar.cpd.minimumtokens", "100"

        // 기타 설정
        property "sonar.ncloc.minimum", "100"
        property "sonar.analysis.mode", "publish"
        property "sonar.issuesReport.console.enable", "true"
        property "sonar.verbose", "false"
        property "sonar.security.hotspots", "true"
        property "sonar.vulnerabilities", "true"
    }
}
```

### Docker Compose 설정

```yaml
# infrastructure/sonarqube/docker-compose.sonarqube.yml
version: "3.8"

services:
  sonarqube:
    image: sonarqube:community
    container_name: sonarqube
    ports:
      - "9000:9000"
    environment:
      - SONAR_JDBC_URL=jdbc:postgresql://postgres:5432/sonar
      - SONAR_JDBC_USERNAME=sonar
      - SONAR_JDBC_PASSWORD=sonar
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_logs:/opt/sonarqube/logs
      - sonarqube_extensions:/opt/sonarqube/extensions
    depends_on:
      - postgres
    networks:
      - sonarqube-network

  postgres:
    image: postgres:13
    container_name: sonarqube-postgres
    environment:
      - POSTGRES_USER=sonar
      - POSTGRES_PASSWORD=sonar
      - POSTGRES_DB=sonar
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - sonarqube-network

volumes:
  sonarqube_data:
  sonarqube_logs:
  sonarqube_extensions:
  postgres_data:

networks:
  sonarqube-network:
    driver: bridge
```

## 사용법

### SonarQube 서버 시작

```bash
# Docker Compose로 SonarQube 시작
cd infrastructure/sonarqube
docker-compose -f docker-compose.sonarqube.yml up -d

# 서버 상태 확인
docker-compose -f docker-compose.sonarqube.yml ps
```

### 분석 실행

```bash
# SonarQube 분석 실행
./gradlew sonar

# 테스트와 함께 분석 실행
./gradlew test jacocoTestReport sonar
```

### 웹 인터페이스 접근

- **URL**: http://localhost:9000
- **기본 계정**: admin / admin
- **프로젝트**: dataracy

## 분석 지표

### 1. Reliability (신뢰성)

- **버그**: 코드에서 발견된 버그의 수
- **신뢰성 등급**: A, B, C, D, E

### 2. Security (보안)

- **보안 취약점**: 보안상 위험한 코드 패턴
- **보안 핫스팟**: 잠재적 보안 이슈
- **보안 등급**: A, B, C, D, E

### 3. Maintainability (유지보수성)

- **코드 스멜**: 유지보수를 어렵게 만드는 코드 패턴
- **기술 부채**: 코드 품질 개선이 필요한 영역
- **유지보수성 등급**: A, B, C, D, E

### 4. Coverage (커버리지)

- **라인 커버리지**: 테스트된 코드 라인의 비율
- **분기 커버리지**: 테스트된 분기점의 비율

### 5. Duplications (중복)

- **중복 라인**: 중복된 코드 라인의 수
- **중복 블록**: 중복된 코드 블록의 수

## 품질 게이트

### 프로젝트 품질 게이트 설정

```yaml
# quality-gate.yml
quality_gate:
  # 코드 커버리지 최소 기준
  coverage:
    minimum: 70
    target: 80

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

## 주요 규칙 및 개선 방법

### 1. 코드 스멜 개선

#### 복잡한 메서드 분해

```java
// Before: 복잡한 메서드
public void processUserData(User user) {
    if (user != null) {
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            if (user.getAge() > 0 && user.getAge() < 150) {
                // 복잡한 로직...
            }
        }
    }
}

// After: 메서드 분해
public void processUserData(User user) {
    if (!isValidUser(user)) {
        return;
    }

    validateUserData(user);
    saveUserData(user);
    sendWelcomeEmail(user);
}

private boolean isValidUser(User user) {
    return user != null && isValidEmail(user.getEmail()) && isValidAge(user.getAge());
}
```

#### 매직 넘버 제거

```java
// Before: 매직 넘버 사용
if (user.getAge() < 18) {
    throw new IllegalArgumentException("User must be at least 18 years old");
}

// After: 상수 사용
private static final int MINIMUM_AGE = 18;

if (user.getAge() < MINIMUM_AGE) {
    throw new IllegalArgumentException("User must be at least " + MINIMUM_AGE + " years old");
}
```

### 2. 보안 취약점 해결

#### SQL 인젝션 방지

```java
// Before: SQL 인젝션 위험
@Query("SELECT u FROM User u WHERE u.name = '" + name + "'")
List<User> findUsersByName(String name);

// After: 파라미터 바인딩 사용
@Query("SELECT u FROM User u WHERE u.name = :name")
List<User> findUsersByName(@Param("name") String name);
```

#### 하드코딩된 비밀번호 제거

```java
// Before: 하드코딩된 비밀번호
private static final String PASSWORD = "admin123";

// After: 환경 변수 사용
@Value("${app.admin.password}")
private String adminPassword;
```

### 3. 중복 코드 제거

#### 공통 유틸리티 메서드 생성

```java
// Before: 중복된 검증 로직
public void createUser(User user) {
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        throw new IllegalArgumentException("Email cannot be empty");
    }
    // ...
}

public void updateUser(User user) {
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        throw new IllegalArgumentException("Email cannot be empty");
    }
    // ...
}

// After: 공통 유틸리티 메서드
public void createUser(User user) {
    validateUserEmail(user.getEmail());
    // ...
}

public void updateUser(User user) {
    validateUserEmail(user.getEmail());
    // ...
}

private void validateUserEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException("Email cannot be empty");
    }
}
```

## CI/CD 통합

### GitHub Actions 설정

```yaml
name: SonarQube Analysis

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  sonarqube:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3
        with:
          fetch-depth: 0

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      - name: Run tests with coverage
        run: ./gradlew test jacocoTestReport

      - name: SonarQube Scan
        uses: sonarqube-quality-gate-action@master
        with:
          scanMetadataReportFile: target/sonar/report-task.txt
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

## 모니터링 및 알림

### 품질 메트릭 추적

- 코드 품질 변화 추이 모니터링
- 새로운 이슈 발생 시 알림 설정
- 품질 게이트 실패 시 자동 알림

### 대시보드 설정

- 프로젝트별 품질 대시보드 구성
- 팀별 품질 지표 비교
- 시간별 품질 변화 그래프

## 문제 해결

### 일반적인 문제

1. **SonarQube 서버 연결 실패**

   ```bash
   # 서버 상태 확인
   curl http://localhost:9000/api/system/status

   # 로그 확인
   docker-compose -f docker-compose.sonarqube.yml logs sonarqube
   ```

2. **분석 실패**

   - Java 버전 호환성 확인
   - 메모리 부족 시 JVM 옵션 조정
   - 네트워크 연결 상태 확인

3. **품질 게이트 실패**
   - 커버리지 부족 시 테스트 추가
   - 코드 스멜 해결
   - 보안 취약점 수정

### 디버깅 팁

```bash
# 상세한 로그와 함께 분석 실행
./gradlew sonar --info --stacktrace

# 특정 프로젝트만 분석
./gradlew :module-name:sonar

# 캐시 클리어 후 재분석
./gradlew clean sonar
```

## 모범 사례

### 1. 점진적 개선

- 한 번에 모든 이슈를 해결하려 하지 말고 우선순위에 따라 점진적으로 개선

### 2. 팀 교육

- SonarQube 규칙과 모범 사례에 대한 팀 교육 실시
- 코드 리뷰 시 SonarQube 이슈 함께 검토

### 3. 자동화

- CI/CD 파이프라인에 SonarQube 분석 자동화
- 품질 게이트 실패 시 빌드 중단 설정

### 4. 정기적 리뷰

- 주간/월간 품질 메트릭 리뷰
- 품질 개선 계획 수립 및 실행

## 관련 도구

- **JaCoCo**: 테스트 커버리지 측정
- **Checkstyle**: 코드 스타일 검사
- **SpotBugs**: 정적 버그 검출
- **Spotless**: 코드 포맷팅

## 참고 자료

- [SonarQube 공식 문서](https://docs.sonarqube.org/)
- [SonarQube 규칙 가이드](https://rules.sonarsource.com/)
- [Gradle SonarQube 플러그인](https://docs.gradle.org/current/userguide/sonar_plugin.html)
- [코드 품질 모범 사례](https://www.sonarqube.org/resources/whitepapers/)
