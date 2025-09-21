# 🚀 Dataracy 포트폴리오용 성능 테스트 가이드

## 📋 개요

이 문서는 Dataracy 서비스의 **포트폴리오용 핵심 성능 테스트** 실행 가이드입니다. 실제 문제 해결 경험을 보여주는 20개 핵심 테스트만 선별하여 체계적인 성능, 부하, 보안 테스트를 제공합니다.

## 🏗️ 포트폴리오용 테스트 구조 (총 20개)

```
performance-test/
├── auth/                    # 인증 도메인 (2개)
│   ├── login.test.js        # 로그인 성능 최적화
│   └── login-abuse.test.js  # 보안 시스템 구축
├── comment/                 # 댓글 도메인 (2개)
│   ├── find-comments.test.js # 댓글 페이징 최적화
│   └── modify-basic.test.js  # 실시간 댓글 시스템
├── dataset/                 # 데이터셋 도메인 (4개)
│   ├── dataset-upload.test.js    # 대용량 파일 업로드
│   ├── dataset-detail.test.js    # 복합 쿼리 최적화
│   ├── dataset-popular.test.js   # 실시간 인기도 계산
│   └── dataset-filter.test.js    # 데이터셋 필터링 검색
├── like/                    # 좋아요 도메인 (2개)
│   ├── like-toggle-hotspot.test.js    # 핫스팟 분산 락
│   └── like-distributed-load.test.js  # 분산 처리 최적화
├── project/                 # 프로젝트 도메인 (6개)
│   ├── project-upload.test.js         # 대용량 프로젝트 업로드
│   ├── project-search.test.js         # 프로젝트 실시간 검색
│   ├── project-detail-read.test.js    # 복잡한 도메인 로직
│   ├── project-popular-read.test.js   # 인기 콘텐츠 캐싱
│   ├── project-latest-read.test.js    # 실시간 데이터 신선도
│   └── project-filtered-read.test.js  # 동적 필터링 시스템
├── user/                    # 사용자 도메인 (3개)
│   ├── user-signup.test.js     # 회원가입 프로세스 최적화
│   ├── user-read-me.test.js    # 개인정보 보호 마스킹
│   └── user-modify.test.js     # 이벤트 기반 아키텍처
├── run-all-tests.sh         # 전체 테스트 실행
└── run-{domain}-tests.sh    # 도메인별 테스트 실행
```

## 🎯 포트폴리오용 테스트 시나리오

### 1. Smoke Test (20개 테스트 모두)

- **목적**: CI/CD 파이프라인용 기본 기능 검증
- **VU**: 5명
- **지속시간**: 30초
- **포트폴리오 가치**: 빠른 기능 확인, 기본 동작 검증

### 2. Load Test (6개 핵심 테스트)

- **목적**: 일상적 트래픽 시뮬레이션
- **VU**: 10-100명
- **지속시간**: 8분
- **포트폴리오 가치**: 실제 사용자 패턴 검증, 정상 운영 환경 시뮬레이션

### 3. Stress Test (6개 핵심 테스트)

- **목적**: 시스템 한계점 탐색 및 핫스팟 처리
- **VU**: 50-300명
- **지속시간**: 10분
- **포트폴리오 가치**: 동시성 처리, 핫스팟 문제 해결 경험

### 4. Soak Test (4개 핵심 테스트)

- **목적**: 장시간 안정성 및 메모리 누수 검증
- **VU**: 100명
- **지속시간**: 1시간
- **포트폴리오 가치**: 캐시 효율성, 장시간 안정성 검증

### 5. Spike Test (2개 핵심 테스트)

- **목적**: 갑작스러운 트래픽 폭증 대응
- **VU**: 20-800명
- **지속시간**: 2분 30초
- **포트폴리오 가치**: 시스템 복구력, 트래픽 폭증 대응 경험

### 6. Capacity Test (3개 핵심 테스트)

- **목적**: 최대 처리량 한계 측정
- **RPS**: 50-200 req/s
- **지속시간**: 6분
- **포트폴리오 가치**: 병목 지점 식별, 성능 한계 확인

## 📊 포트폴리오용 측정 메트릭

### 공통 메트릭

- **성공률**: 95% 이상 목표
- **응답시간**: p95 기준 (도메인별 상이)
- **처리량**: RPS (Requests Per Second)
- **에러율**: 5% 이하 목표

### 포트폴리오용 도메인별 특화 메트릭

- **Auth 도메인 (2개)**: 로그인 성공률, 응답시간, 에러율, 동시 사용자 수, 처리량, 레이트 리미팅 효과성, 공격 탐지율
- **User 도메인 (3개)**: 회원가입 성공률, 데이터 마스킹시간, 이벤트 발행시간, 시스템 동기화
- **Dataset 도메인 (4개)**: 파일 처리시간, S3 업로드시간, 복합 쿼리 성능, Elasticsearch 쿼리시간
- **Project 도메인 (6개)**: 업로드 처리시간, 검색 응답시간, 캐시 히트율, 데이터 신선도, 필터링 정확성
- **Like 도메인 (2개)**: 분산 락 획득시간, 핫스팟 충돌 횟수, 데이터 일관성, 분산 처리 성능
- **Comment 도메인 (2개)**: 페이징 성능, 동시성 처리 성능, 이벤트 발행시간, 데이터 일관성

## 🚀 포트폴리오용 실행 방법

### 1. 전체 테스트 실행 (20개 핵심 테스트)

```bash
# 모든 도메인 테스트 실행
./performance-test/run-all-tests.sh

# 특정 도메인만 실행
./performance-test/run-all-tests.sh --auth-only
./performance-test/run-all-tests.sh --comment-only
./performance-test/run-all-tests.sh --dataset-only
./performance-test/run-all-tests.sh --like-only
./performance-test/run-all-tests.sh --project-only
./performance-test/run-all-tests.sh --user-only
```

### 2. 포트폴리오용 도메인별 테스트 실행

#### 🔐 인증 도메인 (Auth) - 2개 테스트

**포트폴리오 가치**: 로그인 성능 최적화, 보안 시스템 구축

```bash
# 전체 인증 테스트
./performance-test/run-tests.sh auth

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login.test.js
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse.test.js
```

#### 💬 댓글 도메인 (Comment) - 2개 테스트

**포트폴리오 가치**: 무한 스크롤 최적화, 실시간 댓글 시스템

```bash
# 전체 댓글 테스트
./performance-test/run-comment-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/comment/scenarios/find-comments.test.js
k6 run --env SCENARIO=load performance-test/comment/scenarios/modify-basic.test.js
```

#### 📊 데이터셋 도메인 (Dataset) - 4개 테스트

**포트폴리오 가치**: 대용량 파일 처리, 복합 쿼리 최적화, 실시간 검색 시스템

```bash
# 전체 데이터셋 테스트
./performance-test/run-dataset-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-upload.test.js
k6 run --env SCENARIO=load performance-test/dataset/scenarios/dataset-detail.test.js
k6 run --env SCENARIO=stress performance-test/dataset/scenarios/dataset-popular.test.js
k6 run --env SCENARIO=soak performance-test/dataset/scenarios/dataset-filter.test.js
```

#### ❤️ 좋아요 도메인 (Like) - 2개 테스트

**포트폴리오 가치**: 핫스팟 문제 해결, 분산 처리 최적화

```bash
# 전체 좋아요 테스트
./performance-test/run-like-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/like/scenarios/like-toggle-hotspot.test.js
k6 run --env SCENARIO=load performance-test/like/scenarios/like-distributed-load.test.js
```

#### 🚀 프로젝트 도메인 (Project) - 6개 테스트

**포트폴리오 가치**: 대용량 업로드, AI 검색, 복잡한 도메인 로직, 캐싱 전략

```bash
# 전체 프로젝트 테스트
./performance-test/run-project-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-upload.test.js
k6 run --env SCENARIO=load performance-test/project/scenarios/project-search.test.js
k6 run --env SCENARIO=stress performance-test/project/scenarios/project-detail-read.test.js
k6 run --env SCENARIO=soak performance-test/project/scenarios/project-popular-read.test.js
k6 run --env SCENARIO=spike performance-test/project/scenarios/project-latest-read.test.js
k6 run --env SCENARIO=capacity performance-test/project/scenarios/project-filtered-read.test.js
```

#### 👤 사용자 도메인 (User) - 3개 테스트

**포트폴리오 가치**: 회원가입 최적화, 개인정보 보호, 이벤트 기반 아키텍처

```bash
# 전체 사용자 테스트
./performance-test/run-user-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-signup.test.js
k6 run --env SCENARIO=load performance-test/user/scenarios/user-read-me.test.js
k6 run --env SCENARIO=stress performance-test/user/scenarios/user-modify.test.js
```

### 3. 환경별 실행

```bash
# 개발 환경
k6 run --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js

# 스테이징 환경
k6 run --env BASE_URL=https://staging-api.dataracy.com performance-test/auth/scenarios/login.test.js

# 프로덕션 환경
k6 run --env BASE_URL=https://api.dataracy.com performance-test/auth/scenarios/login.test.js
```

### 4. 인증 모드별 실행

```bash
# 토큰 기반 인증
k6 run --env AUTH_MODE=token --env TOKEN=your-token performance-test/user/scenarios/user-read-me.test.js

# 로그인 기반 인증
k6 run --env AUTH_MODE=login --env EMAIL=test@example.com --env PASSWORD=password performance-test/user/scenarios/user-read-me.test.js
```

## 🔧 환경 변수

### 공통 환경 변수

- `BASE_URL`: API 서버 URL (기본값: http://localhost:8080)
- `SCENARIO`: 테스트 시나리오 (smoke, load, stress, soak, spike, capacity)
- `AUTH_MODE`: 인증 모드 (token, login)

### 인증 관련

- `TOKEN`: 액세스 토큰
- `EMAIL`: 로그인 이메일
- `PASSWORD`: 로그인 비밀번호

### 도메인별 특화 변수

- `PROJECT_ID`: 프로젝트 ID (프로젝트 도메인)
- `USER_ID`: 사용자 ID (사용자 도메인)
- `DATASET_ID`: 데이터셋 ID (데이터셋 도메인)
- `COMMENT_ID`: 댓글 ID (댓글 도메인)

## 📈 성능 기준

### 응답 시간 목표

- **인증 API**: p95 < 500ms (로그인 성능 최적화 완료)
- **조회 API**: p95 < 500ms
- **수정 API**: p95 < 2000ms
- **업로드 API**: p95 < 5000ms
- **보안 API**: p95 < 800ms

### 처리량 목표

- **인증**: 200 req/s
- **조회**: 500 req/s
- **수정**: 100 req/s
- **업로드**: 50 req/s
- **보안**: 300 req/s

### 성공률 목표

- **모든 API**: 95% 이상
- **보안 API**: 99% 이상
- **업로드 API**: 90% 이상

## 🛡️ 보안 테스트

### 공통 보안 시나리오

- **브루트 포스 공격**: 무차별 대입 공격 시뮬레이션
- **레이트 리미팅**: 요청 제한 기능 검증
- **인증 우회**: 토큰 검증 우회 시도
- **입력 검증**: XSS, SQL Injection 공격 시뮬레이션

### 도메인별 보안 테스트

- **인증**: 로그인 공격, 토큰 탈취 시뮬레이션
- **사용자**: 계정 탈취, 권한 상승 시도
- **데이터**: 파일 업로드 악용, 데이터 유출 시도
- **프로젝트**: 무단 접근, 데이터 조작 시도

## 📊 결과 분석

### 주요 지표

1. **성공률**: API 호출 성공 비율
2. **응답시간**: 평균, 중간값, 95백분위수
3. **처리량**: 초당 요청 수 (RPS)
4. **에러율**: 실패한 요청 비율
5. **캐시 효율성**: 캐시 히트율

### 성능 병목 지점

1. **데이터베이스**: 쿼리 최적화 필요
2. **캐시**: Redis 활용도 개선
3. **네트워크**: 대역폭 및 지연시간
4. **메모리**: 가비지 컬렉션 최적화
5. **CPU**: 알고리즘 효율성 개선

## 🔍 문제 해결

### 일반적인 문제

1. **연결 거부**: 서버가 실행 중인지 확인
2. **인증 실패**: 토큰 유효성 및 권한 확인
3. **타임아웃**: 네트워크 설정 및 서버 성능 확인
4. **메모리 부족**: VU 수 조정 및 시스템 리소스 확인

### 성능 최적화

1. **인덱스 최적화**: 데이터베이스 쿼리 성능 개선
2. **캐시 전략**: Redis 활용도 극대화
3. **비동기 처리**: 큐 시스템 도입
4. **CDN 활용**: 정적 리소스 최적화
5. **로드 밸런싱**: 트래픽 분산 처리

## 📝 보고서 생성

### 자동 보고서

```bash
# HTML 보고서 생성
k6 run --out json=results.json performance-test/auth/scenarios/login.test.js
k6 run --out influxdb=http://localhost:8086/dataracy performance-test/auth/scenarios/login.test.js
```

### 수동 분석

1. **로그 분석**: 서버 로그와 k6 결과 비교
2. **메트릭 수집**: Prometheus, Grafana 활용
3. **알림 설정**: 임계값 초과 시 알림
4. **트렌드 분석**: 시간별 성능 변화 추적

## 🚀 CI/CD 통합

### GitHub Actions 예시

```yaml
name: Performance Tests
on: [push, pull_request]
jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run k6 tests
        uses: grafana/k6-action@v0.3.0
        with:
          filename: performance-test/auth/scenarios/login.test.js
          options: "--env SCENARIO=smoke"
```

### Jenkins Pipeline 예시

```groovy
pipeline {
    agent any
    stages {
        stage('Performance Test') {
            steps {
                sh 'k6 run --env SCENARIO=load performance-test/auth/scenarios/login.test.js'
            }
        }
    }
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'results',
                reportFiles: 'index.html',
                reportName: 'Performance Test Report'
            ])
        }
    }
}
```

## 📚 추가 자료

- [k6 공식 문서](https://k6.io/docs/)
- [성능 테스트 모범 사례](https://k6.io/docs/testing-guides/)
- [메트릭 및 모니터링](https://k6.io/docs/results-visualization/)
- [CI/CD 통합](https://k6.io/docs/integrations/)

---

**⚠️ 주의사항**: 프로덕션 환경에서 성능 테스트를 실행할 때는 충분한 주의를 기울이고, 테스트 전에 백업을 수행하세요.
