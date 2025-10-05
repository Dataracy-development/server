# 코드 품질 도구 가이드

## 개요

이 문서는 프로젝트에서 사용하는 코드 품질 도구들에 대한 종합적인 가이드를 제공합니다. 각 도구의 설정, 사용법, 모범 사례를 다룹니다.

## 도구 목록

### 1. [JaCoCo](jacoco.md) - 코드 커버리지

- **목적**: 테스트 커버리지 측정 및 분석
- **상태**: ✅ 활성화
- **사용법**: `./gradlew jacocoTestReport`

### 2. [SonarQube](sonarqube.md) - 정적 분석

- **목적**: 종합적인 코드 품질 분석
- **상태**: 🔄 필요시 활성화
- **사용법**: `./gradlew sonar`

### 3. [Checkstyle](checkstyle.md) - 코드 스타일 검사

- **목적**: 코딩 표준 및 스타일 검사
- **상태**: ✅ 활성화
- **사용법**: `./gradlew checkstyleMain`

### 4. [Spotless](spotless.md) - 코드 포맷팅

- **목적**: 자동 코드 포맷팅 및 스타일 통일
- **상태**: ✅ 활성화
- **사용법**: `./gradlew spotlessApply`

### 5. [SpotBugs](spotbugs.md) - 버그 검출

- **목적**: 잠재적 버그 패턴 검출
- **상태**: 🔄 필요시 활성화
- **사용법**: `./gradlew spotbugsMain`

## 통합 워크플로우

### 개발 단계별 품질 검사

```bash
# 1. 코드 포맷팅 (개발 중)
./gradlew spotlessApply

# 2. 코드 스타일 검사 (커밋 전)
./gradlew checkstyleMain checkstyleTest

# 3. 테스트 실행 및 커버리지 측정 (CI/CD)
./gradlew test jacocoTestReport

# 4. 버그 검출 (필요시)
./gradlew spotbugsMain spotbugsTest

# 5. 종합 품질 분석 (필요시)
./gradlew sonar
```

### CI/CD 파이프라인 통합

```yaml
# .github/workflows/quality-check.yml
name: Code Quality Check

on: [push, pull_request]

jobs:
  quality-check:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Apply Code Formatting
        run: ./gradlew spotlessApply

      - name: Check Code Style
        run: ./gradlew checkstyleMain checkstyleTest

      - name: Run Tests with Coverage
        run: ./gradlew test jacocoTestReport

      - name: Bug Detection
        run: ./gradlew spotbugsMain spotbugsTest
        continue-on-error: true

      - name: Upload Reports
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: quality-reports
          path: |
            build/reports/jacoco/
            build/reports/checkstyle/
            build/reports/spotbugs/
```

## 품질 기준

### 커버리지 기준

- **최소 요구사항**: 70%
- **목표 수준**: 75%
- **우수 수준**: 80% 이상

### 코드 스타일 기준

- **Checkstyle**: 경고 100개 이하
- **Spotless**: 모든 파일 포맷팅 적용
- **라인 길이**: 최대 200자
- **파일 길이**: 최대 2000라인

### 버그 검출 기준

- **심각한 버그**: 0개
- **중요한 버그**: 5개 이하
- **일반적인 버그**: 10개 이하

## 도구별 설정 파일

### 핵심 설정 파일

- `build.gradle` - 모든 도구의 Gradle 설정
- `checkstyle.xml` - Checkstyle 규칙 설정
- `spotbugs-exclude.xml` - SpotBugs 제외 규칙
- `quality-gate.yml` - 품질 게이트 기준

### 리포트 위치

- **JaCoCo**: `build/reports/jacoco/test/`
- **Checkstyle**: `build/reports/checkstyle/`
- **SpotBugs**: `build/reports/spotbugs/`
- **SonarQube**: 웹 인터페이스 (http://localhost:9000)

## 모범 사례

### 1. 개발 워크플로우

```bash
# 개발 시작 전
./gradlew spotlessApply

# 코드 작성 중
# IDE에서 실시간 검사 활용

# 커밋 전
./gradlew checkstyleMain spotlessCheck

# PR 생성 전
./gradlew test jacocoTestReport
```

### 2. 팀 협업

- 모든 팀원이 동일한 도구 설정 사용
- 코드 리뷰 시 품질 리포트 함께 검토
- 정기적인 품질 메트릭 리뷰

### 3. 지속적 개선

- 주간 품질 메트릭 모니터링
- 월간 품질 기준 검토 및 조정
- 분기별 도구 설정 최적화

## 문제 해결

### 일반적인 문제

1. **도구 간 충돌**

   - 설정 파일의 제외 패턴 통일
   - 도구별 실행 순서 조정

2. **성능 문제**

   - 불필요한 파일 제외 설정
   - 병렬 처리 옵션 활용

3. **False Positive**
   - 제외 규칙 적절히 설정
   - 팀 내 규칙 합의

### 지원 및 문의

- 각 도구별 상세 가이드는 해당 문서 참조
- 팀 내 코드 품질 담당자에게 문의
- 정기적인 도구 업데이트 및 설정 검토

## 관련 문서

- [개발 가이드](../development/README.md)
- [테스트 가이드](../testing/README.md)
- [배포 가이드](../deployment/README.md)
- [API 문서](../api/README.md)
