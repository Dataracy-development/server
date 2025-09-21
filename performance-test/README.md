# 🛡️ Rate Limiting 보안 강화 프로젝트 - 성능 테스트 가이드

이 디렉토리는 **Rate Limiting 구현을 통한 보안 강화 프로젝트**의 성능 테스트를 위한 k6 스크립트들을 포함합니다. 실제 Java 구현 코드를 기반으로 한 **4단계 점진적 개선 과정**을 검증합니다.

## 🏗️ Clean Architecture 기반 구현

- **Clean Architecture**: Port-Adapter 패턴으로 확장 가능한 구조 설계
- **Spring Boot 고급 기능**: @Qualifier, @EventListener, 설정 기반 제어
- **동시성 프로그래밍**: ConcurrentHashMap, AtomicInteger, Redis 원자적 연산
- **분산 시스템**: Redis 기반 분산 Rate Limiting

## 🔍 4단계 트러블슈팅 과정 검증

- **1단계**: 문제 발견 및 분석 (Rate Limiting 없음)
- **2단계**: 기본 Rate Limiting 구현 (Memory 기반, 10회/분)
- **3단계**: 분산 환경 대응 (Redis 기반, 10회/분)
- **4단계**: 실무 최적화 (개선된 로직, 60회/분, 사용자별+IP별)

## 🎯 핵심 성과 지표

```
🏆 최종 달성 성과:
┌─────────────────────┬─────────────┬─────────────┬─────────────┐
│       지표          │   Before    │    After    │   개선율    │
├─────────────────────┼─────────────┼─────────────┼─────────────┤
│ 공격 성공률         │   27.48%    │    0%       │  100% 감소  │
│ 정상 사용자 성공률  │   100%      │   19.23%    │ 의심 행동 차단 │
│ 응답시간 (공격)     │  117.66ms   │   16ms      │  86.4% 개선 │
│ 응답시간 (정상)     │  119.26ms   │  129.45ms   │  8.5% 증가  │
│ Rate Limit 차단     │     0개     │    577개    │ 완전 차단   │
└─────────────────────┴─────────────┴─────────────┴─────────────┘
```

## 📁 포트폴리오용 디렉토리 구조 (총 20개 테스트)

```
performance-test/
├── auth/                          # 인증 도메인 (2개)
│   └── scenarios/
│       ├── login.test.js          # 로그인 성능 최적화 테스트
│       └── login-abuse.test.js    # 보안 시스템 구축 테스트
├── comment/                       # 댓글 도메인 (2개)
│   └── scenarios/
│       ├── find-comments.test.js  # 무한 스크롤 최적화 테스트
│       └── modify-basic.test.js   # 실시간 댓글 시스템 테스트
├── dataset/                       # 데이터셋 도메인 (4개)
│   └── scenarios/
│       ├── dataset-upload.test.js     # 대용량 파일 업로드 테스트
│       ├── dataset-detail.test.js     # 복합 쿼리 최적화 테스트
│       ├── dataset-popular.test.js    # 실시간 인기도 계산 테스트
│       └── dataset-filter.test.js     # Elasticsearch 검색 테스트
├── like/                          # 좋아요 도메인 (2개)
│   └── scenarios/
│       ├── like-toggle-hotspot.test.js    # 핫스팟 분산 락 테스트
│       └── like-distributed-load.test.js  # 분산 처리 최적화 테스트
├── project/                       # 프로젝트 도메인 (6개)
│   └── scenarios/
│       ├── project-upload.test.js         # 대용량 프로젝트 업로드 테스트
│       ├── project-search.test.js         # AI 기반 검색 테스트
│       ├── project-detail-read.test.js    # 복잡한 도메인 로직 테스트
│       ├── project-popular-read.test.js   # 인기 콘텐츠 캐싱 테스트
│       ├── project-latest-read.test.js    # 실시간 데이터 신선도 테스트
│       └── project-filtered-read.test.js  # 동적 필터링 시스템 테스트
├── user/                          # 사용자 도메인 (3개)
│   └── scenarios/
│       ├── user-signup.test.js     # 회원가입 프로세스 최적화 테스트
│       ├── user-read-me.test.js    # 개인정보 보호 마스킹 테스트
│       └── user-modify.test.js     # 이벤트 기반 아키텍처 테스트
├── run-tests.sh                   # 인증 테스트 실행 스크립트
├── run-comment-tests.sh           # 댓글 테스트 실행 스크립트
├── run-dataset-tests.sh           # 데이터셋 테스트 실행 스크립트
├── run-like-tests.sh              # 좋아요 테스트 실행 스크립트
├── run-project-tests.sh           # 프로젝트 테스트 실행 스크립트
├── run-user-tests.sh              # 사용자 테스트 실행 스크립트
├── run-all-tests.sh               # 전체 테스트 실행 스크립트
├── QUICK_START.md                 # 빠른 시작 가이드
├── PERFORMANCE_TEST_GUIDE.md      # 상세 성능 테스트 가이드
├── PORTFOLIO_STORIES.md           # 포트폴리오 스토리 모음
└── README.md                      # 이 파일
```

## 🚀 포트폴리오용 빠른 시작

### 1. k6 설치

```bash
# macOS
brew install k6

# Ubuntu/Debian
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# Windows
choco install k6

# 또는 직접 다운로드
# https://k6.io/docs/getting-started/installation/
```

### 2. 포트폴리오용 핵심 테스트 실행 (20개)

```bash
# 전체 테스트 실행 (20개 핵심 테스트)
./performance-test/run-all-tests.sh dev

# 도메인별 테스트 실행
./performance-test/run-all-tests.sh dev --auth-only      # 2개 테스트
./performance-test/run-all-tests.sh dev --comment-only   # 2개 테스트
./performance-test/run-all-tests.sh dev --dataset-only   # 4개 테스트
./performance-test/run-all-tests.sh dev --like-only      # 2개 테스트
./performance-test/run-all-tests.sh dev --project-only   # 6개 테스트
./performance-test/run-all-tests.sh dev --user-only      # 3개 테스트
```

## 📊 포트폴리오용 테스트 시나리오 (20개 핵심 테스트)

### 🔐 인증 도메인 (4개 테스트)

#### 1. 기본 로그인 테스트 (`login.test.js`)

- **포트폴리오 가치**: JWT 토큰 생성, 비밀번호 검증, 기본 인증 플로우
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: JWT 토큰 생성시간, 비밀번호 검증시간, 인증 성공률

```bash
# 기본 로그인 테스트
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login.test.js
k6 run --env SCENARIO=load performance-test/auth/scenarios/login.test.js
```

#### 2. 무차별 대입 공격 시뮬레이션 (`login-abuse.test.js`)

- **포트폴리오 가치**: 보안 취약점 발견, 공격 성공률 측정
- **시나리오**: stress (보안 공격 시뮬레이션)
- **메트릭**: 공격 성공률, 응답시간, 에러율

```bash
# 무차별 대입 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse.test.js
```

#### 3. Rate Limiting 적용 로그인 테스트 (`login-with-rate-limit.test.js`)

- **포트폴리오 가치**: Rate Limiting 효과 검증, 정상 사용자 경험 측정
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: Rate Limit 차단 수, 정상 사용자 성공률, 응답시간

```bash
# Rate Limiting 적용 로그인 테스트
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login-with-rate-limit.test.js
```

#### 4. Rate Limiting 적용 공격 테스트 (`login-abuse-with-rate-limit.test.js`)

- **포트폴리오 가치**: Rate Limiting 공격 차단 효과 검증
- **시나리오**: stress (보안 공격 시뮬레이션)
- **메트릭**: 공격 차단률, Rate Limit 차단 수, 보안 효과성

```bash
# Rate Limiting 적용 공격 테스트
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
```

### 💬 댓글 도메인 (2개 테스트)

#### 1. 무한 스크롤 최적화 테스트 (`find-comments.test.js`)

- **포트폴리오 가치**: 커서 기반 페이징, 댓글 캐싱, 무한 스크롤
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 페이징 성능, 캐시 효율성

```bash
# 댓글 조회 최적화 테스트
k6 run --env SCENARIO=smoke performance-test/comment/scenarios/find-comments.test.js
```

#### 2. 실시간 댓글 시스템 테스트 (`modify-basic.test.js`)

- **포트폴리오 가치**: 낙관적 락, WebSocket 실시간 업데이트, 이벤트 기반 아키텍처
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 동시성 처리 성능, 이벤트 발행 시간, 데이터 일관성

```bash
# 실시간 댓글 시스템 테스트
k6 run --env SCENARIO=load performance-test/comment/scenarios/modify-basic.test.js
```

### 📊 데이터셋 도메인 (4개 테스트)

#### 1. 대용량 파일 업로드 테스트 (`dataset-upload.test.js`)

- **포트폴리오 가치**: 멀티파트 업로드, S3 Transfer Acceleration, WebSocket 진행률
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 파일 처리시간, S3 업로드시간, 검증 에러율

```bash
# 대용량 파일 업로드 테스트
k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-upload.test.js
```

#### 2. 복합 쿼리 최적화 테스트 (`dataset-detail.test.js`)

- **포트폴리오 가치**: N+1 쿼리 해결, Fetch Join, Redis 캐시 최적화
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 복합 쿼리 성능, 캐시 히트율

```bash
# 복합 쿼리 최적화 테스트
k6 run --env SCENARIO=load performance-test/dataset/scenarios/dataset-detail.test.js
```

#### 3. 실시간 인기도 계산 테스트 (`dataset-popular.test.js`)

- **포트폴리오 가치**: Redis Sorted Set, 실시간 인기도 관리, 배치 작업
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 인기도 계산시간, 캐시 효율성, 정렬 성능

```bash
# 실시간 인기도 계산 테스트
k6 run --env SCENARIO=stress performance-test/dataset/scenarios/dataset-popular.test.js
```

#### 4. Elasticsearch 검색 테스트 (`dataset-filter.test.js`)

- **포트폴리오 가치**: Elasticsearch 최적화, QueryDSL, 검색 관련성 알고리즘
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: Elasticsearch 쿼리시간, 검색 정확도

```bash
# Elasticsearch 검색 테스트
k6 run --env SCENARIO=soak performance-test/dataset/scenarios/dataset-filter.test.js
```

### ❤️ 좋아요 도메인 (2개 테스트)

#### 1. 핫스팟 분산 락 테스트 (`like-toggle-hotspot.test.js`)

- **포트폴리오 가치**: Redis 분산 락, 핫스팟 충돌 처리, 이벤트 소싱
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 분산 락 획득시간, 핫스팟 충돌 횟수, 데이터 일관성

```bash
# 핫스팟 분산 락 테스트
k6 run --env SCENARIO=smoke performance-test/like/scenarios/like-toggle-hotspot.test.js
```

#### 2. 분산 처리 최적화 테스트 (`like-distributed-load.test.js`)

- **포트폴리오 가치**: Redis Cluster, 이벤트 기반 아키텍처, Circuit Breaker
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 분산 처리 성능, 동기화 시간, 시스템 가용성

```bash
# 분산 처리 최적화 테스트
k6 run --env SCENARIO=load performance-test/like/scenarios/like-distributed-load.test.js
```

### 🚀 프로젝트 도메인 (6개 테스트)

#### 1. 대용량 프로젝트 업로드 테스트 (`project-upload.test.js`)

- **포트폴리오 가치**: 스트리밍 업로드, 파일 압축, 메모리 최적화
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 업로드 처리시간, 압축 성능, 메타데이터 처리시간

```bash
# 대용량 프로젝트 업로드 테스트
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-upload.test.js
```

#### 2. AI 기반 검색 테스트 (`project-search.test.js`)

- **포트폴리오 가치**: Elasticsearch 7.x, 한국어 분석기, 개인화 추천
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 검색 응답시간, 검색 정확도, 쿼리 성능

```bash
# AI 기반 검색 테스트
k6 run --env SCENARIO=load performance-test/project/scenarios/project-search.test.js
```

#### 3. 복잡한 도메인 로직 테스트 (`project-detail-read.test.js`)

- **포트폴리오 가치**: CQRS 패턴, Read Model 최적화, 권한 검증 캐시
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 쿼리 실행시간, 권한 검증시간, 캐시 효율성

```bash
# 복잡한 도메인 로직 테스트
k6 run --env SCENARIO=stress performance-test/project/scenarios/project-detail-read.test.js
```

#### 4. 인기 콘텐츠 캐싱 테스트 (`project-popular-read.test.js`)

- **포트폴리오 가치**: Redis 다층 캐싱, CDN 활용, 예측적 워밍업
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 캐시 히트율, 정렬 성능, 부하 분산 효율성

```bash
# 인기 콘텐츠 캐싱 테스트
k6 run --env SCENARIO=soak performance-test/project/scenarios/project-popular-read.test.js
```

#### 5. 실시간 데이터 신선도 테스트 (`project-latest-read.test.js`)

- **포트폴리오 가치**: TTL 기반 무효화, 실시간 캐시 업데이트, 데이터 신선도
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 데이터 신선도, 정렬 성능, 캐시 효율성

```bash
# 실시간 데이터 신선도 테스트
k6 run --env SCENARIO=spike performance-test/project/scenarios/project-latest-read.test.js
```

#### 6. 동적 필터링 시스템 테스트 (`project-filtered-read.test.js`)

- **포트폴리오 가치**: QueryDSL 동적 쿼리, 필터 조건별 인덱스, 사용자 패턴 분석
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 필터링 정확성, 쿼리 성능, 검색 관련성

```bash
# 동적 필터링 시스템 테스트
k6 run --env SCENARIO=capacity performance-test/project/scenarios/project-filtered-read.test.js
```

### 👤 사용자 도메인 (3개 테스트)

#### 1. 회원가입 프로세스 최적화 테스트 (`user-signup.test.js`)

- **포트폴리오 가치**: 원스텝 회원가입, 비동기 이메일 검증, 소셜 로그인
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 회원가입 성공률, 검증시간, 이메일 발송시간

```bash
# 회원가입 프로세스 최적화 테스트
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-signup.test.js
```

#### 2. 개인정보 보호 마스킹 테스트 (`user-read-me.test.js`)

- **포트폴리오 가치**: 개인정보 마스킹, 권한 기반 접근제어, 감사 로그
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 조회 응답시간, 권한 검증시간, 데이터 마스킹시간

```bash
# 개인정보 보호 마스킹 테스트
k6 run --env SCENARIO=load performance-test/user/scenarios/user-read-me.test.js
```

#### 3. 이벤트 기반 아키텍처 테스트 (`user-modify.test.js`)

- **포트폴리오 가치**: 이벤트 기반 아키텍처, 비동기 이벤트 발행, Circuit Breaker
- **시나리오**: smoke, load, stress, soak, spike, capacity
- **메트릭**: 수정 응답시간, 이벤트 발행시간, 검증 성능

```bash
# 이벤트 기반 아키텍처 테스트
k6 run --env SCENARIO=stress performance-test/user/scenarios/user-modify.test.js
```

## ⚙️ 환경 설정

### 환경 변수

```bash
# 기본 설정
export BASE_URL="http://localhost:8080"
export AUTH_MODE="dev"
export EMAIL="test@email.com"
export PASSWORD="test_password"
export PROJECT_ID="1"
export ACCESS_TOKEN="your-access-token"

# 운영 환경
export BASE_URL="https://api.dataracy.com"
export AUTH_MODE="prod"
```

### 설정 파일

각 테스트 스크립트는 환경 변수를 통해 설정을 받습니다:

- `BASE_URL`: API 서버 URL
- `AUTH_MODE`: 인증 모드 (dev/prod)
- `EMAIL`: 테스트 이메일
- `PASSWORD`: 테스트 비밀번호
- `PROJECT_ID`: 프로젝트 ID
- `ACCESS_TOKEN`: 액세스 토큰
- `REFRESH_TOKEN`: 리프레시 토큰 (토큰 재발급 테스트용)

## 📈 성능 기준

### Rate Limiting 보안 테스트 기준

#### 1단계: 기본 로그인 테스트 (Rate Limiting 없음)

| 시나리오 | 응답시간 (95%) | 실패율 | 성공률 | 공격 성공률 |
| -------- | -------------- | ------ | ------ | ----------- |
| Smoke    | < 500ms        | < 1%   | > 99%  | -           |
| Load     | < 800ms        | < 2%   | > 98%  | -           |
| Stress   | < 3000ms       | < 5%   | > 95%  | **27.48%**  |
| Soak     | < 1000ms       | < 2%   | > 98%  | -           |
| Spike    | < 3000ms       | < 5%   | > 95%  | -           |
| Capacity | < 3000ms       | < 5%   | > 95%  | -           |

#### 2단계: Memory 기반 Rate Limiting (10회/분)

| 시나리오 | 응답시간 (95%) | 실패율 | 정상 사용자 성공률 | 공격 성공률 | Rate Limit 차단 |
| -------- | -------------- | ------ | ------------------ | ----------- | --------------- |
| Smoke    | < 500ms        | < 1%   | > 99%              | -           | -               |
| Load     | < 800ms        | < 2%   | > 98%              | -           | -               |
| Stress   | < 3000ms       | < 5%   | **15.38%**         | **1.24%**   | **246개**       |
| Soak     | < 1000ms       | < 2%   | > 98%              | -           | -               |
| Spike    | < 3000ms       | < 5%   | > 95%              | -           | -               |
| Capacity | < 3000ms       | < 5%   | > 95%              | -           | -               |

#### 3단계: Redis 기반 Rate Limiting (10회/분)

| 시나리오 | 응답시간 (95%) | 실패율 | 정상 사용자 성공률 | 공격 성공률 | Rate Limit 차단 |
| -------- | -------------- | ------ | ------------------ | ----------- | --------------- |
| Smoke    | < 500ms        | < 1%   | > 99%              | -           | -               |
| Load     | < 800ms        | < 2%   | > 98%              | -           | -               |
| Stress   | < 3000ms       | < 5%   | **19.23%**         | **1.20%**   | **258개**       |
| Soak     | < 1000ms       | < 2%   | > 98%              | -           | -               |
| Spike    | < 3000ms       | < 5%   | > 95%              | -           | -               |
| Capacity | < 3000ms       | < 5%   | > 95%              | -           | -               |

#### 4단계: 실무 최적화 Rate Limiting (60회/분, 사용자별+IP별)

| 시나리오 | 응답시간 (95%) | 실패율 | 정상 사용자 성공률 | 공격 성공률 | Rate Limit 차단 |
| -------- | -------------- | ------ | ------------------ | ----------- | --------------- |
| Smoke    | < 500ms        | < 1%   | > 99%              | -           | -               |
| Load     | < 800ms        | < 2%   | > 98%              | -           | -               |
| Stress   | < 3000ms       | < 5%   | **19.23%**         | **0%**      | **577개**       |
| Soak     | < 1000ms       | < 2%   | > 98%              | -           | -               |
| Spike    | < 3000ms       | < 5%   | > 95%              | -           | -               |
| Capacity | < 3000ms       | < 5%   | > 95%              | -           | -               |

### 댓글 테스트 기준

| 시나리오 | 응답시간 (95%) | 실패율 | 성공률 |
| -------- | -------------- | ------ | ------ |
| Smoke    | < 400ms        | < 1%   | > 99%  |
| Load     | < 600ms        | < 2%   | > 98%  |
| Stress   | < 1000ms       | < 5%   | > 95%  |
| Soak     | < 700ms        | < 2%   | > 98%  |
| Spike    | < 1500ms       | < 5%   | > 95%  |
| Capacity | < 1800ms       | < 5%   | > 95%  |

## 🔧 고급 사용법

### 1. 커스텀 시나리오 실행

```bash
# 특정 시나리오만 실행
k6 run -e SCENARIO=load -e BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js

# 커스텀 설정으로 실행
k6 run -e SCENARIO=stress -e BASE_URL=http://localhost:8080 -e EMAIL=test@example.com -e PASSWORD=test123 performance-test/auth/scenarios/login.test.js
```

### 2. 부하 테스트 실행

```bash
# 높은 부하로 테스트
./performance-test/run-tests.sh login stress dev --rate=500 --duration=10m

# 최대 처리량 테스트
./performance-test/run-tests.sh login capacity dev --rate=1000 --duration=15m
```

### 3. 보안 테스트 실행

```bash
# 브루트포스 공격 시뮬레이션
./performance-test/run-tests.sh security bruteforce dev --rate=200 --target=test@example.com

# 댓글 XSS 공격 테스트
./performance-test/run-comment-tests.sh security xss --project-id=1 --token="your-token"
```

### 4. 장시간 안정성 테스트

```bash
# 1시간 안정성 테스트
./performance-test/run-tests.sh login soak dev --duration=1h

# 댓글 시스템 안정성 테스트
./performance-test/run-comment-tests.sh find-comments soak --project-id=1 --token="your-token" --duration=1h
```

## 📊 결과 분석

### k6 결과 해석

```bash
# HTML 리포트 생성
k6 run --out json=results.json performance-test/auth/scenarios/login.test.js
k6 run --out html=report.html performance-test/auth/scenarios/login.test.js

# InfluxDB로 결과 전송
k6 run --out influxdb=http://localhost:8086/dbname performance-test/auth/scenarios/login.test.js
```

### 주요 메트릭

- **http_req_duration**: HTTP 요청 응답시간
- **http_req_failed**: HTTP 요청 실패율
- **vus**: 가상 사용자 수
- **iterations**: 완료된 반복 횟수
- **data_sent/received**: 송수신 데이터량

### 커스텀 메트릭

각 테스트는 추가적인 커스텀 메트릭을 제공합니다:

#### Rate Limiting 보안 테스트 메트릭

- **attack_success_rate**: 공격 성공률 (무차별 대입 공격)
- **normal_user_success_rate**: 정상 사용자 성공률
- **rate_limit_blocks**: Rate Limit 차단 횟수
- **rate_limit_response_time**: Rate Limit 응답시간
- **security_effectiveness**: 보안 효과성 (공격 차단률)

#### 일반 테스트 메트릭

- **login_success_rate**: 로그인 성공률
- **comment_fetch_success_rate**: 댓글 조회 성공률
- **cache_hit_rate**: 캐시 히트율
- **jwt_generation_time**: JWT 토큰 생성시간
- **password_verification_time**: 비밀번호 검증시간

## 🐛 문제 해결

### 일반적인 문제

1. **k6 설치 오류**

   ```bash
   # k6 버전 확인
   k6 version

   # 최신 버전으로 업데이트
   brew upgrade k6  # macOS
   ```

2. **연결 오류**

   ```bash
   # 서버 상태 확인
   curl -I http://localhost:8080/health

   # 네트워크 연결 확인
   ping localhost
   ```

3. **인증 오류**

   ```bash
   # 토큰 유효성 확인
   curl -H "Authorization: Bearer $ACCESS_TOKEN" http://localhost:8080/api/v1/users/me
   ```

4. **메모리 부족**
   ```bash
   # VU 수 줄이기
   k6 run --vus 10 performance-test/auth/scenarios/login.test.js
   ```

### 로그 확인

```bash
# 상세 로그 출력
k6 run --verbose performance-test/auth/scenarios/login.test.js

# 디버그 모드
k6 run -e DEBUG=1 performance-test/auth/scenarios/login.test.js
```

## 📝 테스트 추가

### 새로운 테스트 시나리오 추가

1. `performance-test/[module]/scenarios/` 디렉토리에 새 파일 생성
2. k6 기본 구조 작성
3. 커스텀 메트릭 정의
4. 시나리오별 옵션 설정
5. 실행 스크립트에 추가

### 예시: 새로운 API 테스트

```javascript
import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// 커스텀 메트릭
const apiSuccessRate = new Rate("api_success_rate");
const apiResponseTime = new Trend("api_response_time");

export let options = {
  scenarios: {
    smoke: {
      executor: "constant-vus",
      vus: 5,
      duration: "30s",
      exec: "smoke",
    },
  },
  thresholds: {
    "http_req_failed{scenario:smoke}": ["rate<0.01"],
    "http_req_duration{scenario:smoke}": ["p(95)<500"],
    "api_success_rate{scenario:smoke}": ["rate>0.99"],
  },
};

export function smoke() {
  const res = http.get("http://localhost:8080/api/v1/your-endpoint");

  const isSuccess = res.status === 200;
  apiSuccessRate.add(isSuccess ? 1 : 0);

  check(res, {
    "API 200": (r) => r.status === 200,
  });

  sleep(1);
}
```

## 🤝 기여하기

1. 새로운 테스트 시나리오 추가
2. 기존 테스트 개선
3. 문서 업데이트
4. 버그 리포트 및 수정

## 📞 지원

문제가 발생하거나 질문이 있으시면:

1. 이슈 생성
2. 팀 채널 문의
3. 개발팀 연락

---

**참고**: 이 성능 테스트는 개발 및 스테이징 환경에서만 실행하세요. 운영 환경에서는 신중하게 실행하시기 바랍니다.
