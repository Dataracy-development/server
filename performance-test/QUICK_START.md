# 🛡️ Rate Limiting 보안 강화 프로젝트 - 성능 테스트 빠른 시작 가이드

## 📋 개요

이 가이드는 **Rate Limiting 구현을 통한 보안 강화 프로젝트**의 4단계 점진적 개선 과정을 검증하는 성능 테스트를 위한 통일된 구조의 테스트 스위트를 제공합니다.

### 🎯 4단계 트러블슈팅 과정 검증

- **1단계**: 문제 발견 및 분석 (Rate Limiting 없음)
- **2단계**: 기본 Rate Limiting 구현 (Memory 기반, 10회/분)
- **3단계**: 분산 환경 대응 (Redis 기반, 10회/분)
- **4단계**: 실무 최적화 (개선된 로직, 60회/분, 사용자별+IP별)

### 🔍 핵심 성과 지표

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

### 🗂️ 포트폴리오용 핵심 테스트 파일 구조 (총 20개 테스트)

```
performance-test/
├── auth/           # 인증 도메인 (2개 테스트)
│   ├── login.test.js              # 로그인 성능 테스트
│   └── login-abuse.test.js        # 로그인 공격 방어 테스트
├── comment/        # 댓글 도메인 (2개 테스트)
│   ├── find-comments.test.js      # 댓글 조회 성능 테스트
│   └── modify-basic.test.js       # 댓글 수정 동시성 테스트
├── dataset/        # 데이터셋 도메인 (4개 테스트)
│   ├── dataset-upload.test.js     # 대용량 파일 업로드 테스트
│   ├── dataset-detail.test.js     # 복합 쿼리 최적화 테스트
│   ├── dataset-popular.test.js    # 인기도 계산 시스템 테스트
│   └── dataset-filter.test.js     # Elasticsearch 검색 테스트
├── like/           # 좋아요 도메인 (2개 테스트)
│   ├── like-toggle-hotspot.test.js # 핫스팟 분산 락 테스트
│   └── like-distributed-load.test.js # 분산 부하 처리 테스트
├── project/        # 프로젝트 도메인 (6개 테스트)
│   ├── project-upload.test.js     # 대용량 프로젝트 업로드 테스트
│   ├── project-search.test.js     # AI 기반 검색 시스템 테스트
│   ├── project-detail-read.test.js # 복잡한 도메인 로직 테스트
│   ├── project-popular-read.test.js # 인기 콘텐츠 캐싱 테스트
│   ├── project-latest-read.test.js # 실시간 데이터 신선도 테스트
│   └── project-filtered-read.test.js # 동적 필터링 시스템 테스트
└── user/           # 사용자 도메인 (3개 테스트)
    ├── user-signup.test.js        # 회원가입 프로세스 최적화 테스트
    ├── user-read-me.test.js       # 개인정보 보호 마스킹 테스트
    └── user-modify.test.js        # 이벤트 기반 아키텍처 테스트
```

## 📋 포트폴리오용 핵심 테스트 실행 명령어

### 1. 전체 테스트 실행

```bash
# 모든 도메인 테스트 실행 (20개 핵심 테스트)
./performance-test/run-all-tests.sh

# 특정 도메인만 실행
./performance-test/run-all-tests.sh --auth-only
./performance-test/run-all-tests.sh --comment-only
./performance-test/run-all-tests.sh --dataset-only
./performance-test/run-all-tests.sh --like-only
./performance-test/run-all-tests.sh --project-only
./performance-test/run-all-tests.sh --user-only
```

### 2. 도메인별 테스트 실행

#### 🔐 인증 도메인 (Auth) - 4개 테스트

**포트폴리오 가치**: Rate Limiting 보안 강화, 4단계 점진적 개선 과정 검증

**핵심 기능**:

- `login.test.js`: 기본 로그인 성능 검증, JWT 토큰 생성, 비밀번호 검증
- `login-abuse.test.js`: 무차별 대입 공격 시뮬레이션, 보안 취약점 발견
- `login-with-rate-limit.test.js`: Rate Limiting 적용 로그인 테스트, 정상 사용자 경험 측정
- `login-abuse-with-rate-limit.test.js`: Rate Limiting 적용 공격 테스트, 보안 효과 검증

**측정 메트릭**:

- 공격 성공률, 정상 사용자 성공률, Rate Limit 차단 수
- 응답시간, 보안 효과성, 의심 행동 패턴 감지

```bash
# 1단계: 기본 로그인 테스트 (Rate Limiting 없음)
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login.test.js
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse.test.js

# 2-4단계: Rate Limiting 적용 테스트
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login-with-rate-limit.test.js
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
```

#### 💬 댓글 도메인 (Comment) - 2개 테스트

**포트폴리오 가치**: 무한 스크롤 최적화, 실시간 댓글 시스템

**핵심 기능**:

- `find-comments.test.js`: 커서 기반 페이징, 댓글 캐싱, 무한 스크롤
- `modify-basic.test.js`: 낙관적 락, WebSocket 실시간 업데이트, 이벤트 기반 아키텍처

**측정 메트릭**:

- 페이징 성능, 캐시 효율성, 동시성 처리 성능
- 이벤트 발행 시간, 데이터 일관성

```bash
# 전체 댓글 테스트
./performance-test/run-comment-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/comment/scenarios/find-comments.test.js
k6 run --env SCENARIO=load performance-test/comment/scenarios/modify-basic.test.js
```

#### 📊 데이터셋 도메인 (Dataset) - 4개 테스트

**포트폴리오 가치**: 대용량 파일 처리, 복합 쿼리 최적화, 실시간 검색 시스템

**핵심 기능**:

- `dataset-upload.test.js`: 멀티파트 업로드, S3 Transfer Acceleration, WebSocket 진행률
- `dataset-detail.test.js`: N+1 쿼리 해결, Fetch Join, Redis 캐시 최적화
- `dataset-popular.test.js`: Redis Sorted Set, 실시간 인기도 관리, 배치 작업
- `dataset-filter.test.js`: Elasticsearch 최적화, QueryDSL, 검색 관련성 알고리즘

**측정 메트릭**:

- 파일 처리시간, S3 업로드시간, 복합 쿼리 성능
- 인기도 계산시간, Elasticsearch 쿼리시간, 검색 정확도

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

**핵심 기능**:

- `like-toggle-hotspot.test.js`: Redis 분산 락, 핫스팟 충돌 처리, 이벤트 소싱
- `like-distributed-load.test.js`: Redis Cluster, 이벤트 기반 아키텍처, Circuit Breaker

**측정 메트릭**:

- 분산 락 획득시간, 핫스팟 충돌 횟수, 데이터 일관성
- 분산 처리 성능, 동기화 시간, 시스템 가용성

```bash
# 전체 좋아요 테스트
./performance-test/run-like-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/like/scenarios/like-toggle-hotspot.test.js
k6 run --env SCENARIO=load performance-test/like/scenarios/like-distributed-load.test.js
```

#### 🚀 프로젝트 도메인 (Project) - 6개 테스트

**포트폴리오 가치**: 대용량 업로드, AI 검색, 복잡한 도메인 로직, 캐싱 전략

**핵심 기능**:

- `project-upload.test.js`: 스트리밍 업로드, 파일 압축, 메모리 최적화
- `project-search.test.js`: Elasticsearch 7.x, 한국어 분석기, 개인화 추천
- `project-detail-read.test.js`: CQRS 패턴, Read Model 최적화, 권한 검증 캐시
- `project-popular-read.test.js`: Redis 다층 캐싱, CDN 활용, 예측적 워밍업
- `project-latest-read.test.js`: TTL 기반 무효화, 실시간 캐시 업데이트, 데이터 신선도
- `project-filtered-read.test.js`: QueryDSL 동적 쿼리, 필터 조건별 인덱스, 사용자 패턴 분석

**측정 메트릭**:

- 업로드 처리시간, 검색 응답시간, 복잡한 쿼리 성능
- 캐시 히트율, 데이터 신선도, 필터링 정확성

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

**핵심 기능**:

- `user-signup.test.js`: 원스텝 회원가입, 비동기 이메일 검증, 소셜 로그인
- `user-read-me.test.js`: 개인정보 마스킹, 권한 기반 접근제어, 감사 로그
- `user-modify.test.js`: 이벤트 기반 아키텍처, 비동기 이벤트 발행, Circuit Breaker

**측정 메트릭**:

- 회원가입 성공률, 검증시간, 데이터 마스킹시간
- 이벤트 발행시간, 시스템 간 동기화, 장애 격리

```bash
# 전체 사용자 테스트
./performance-test/run-user-tests.sh

# 개별 테스트
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-signup.test.js
k6 run --env SCENARIO=load performance-test/user/scenarios/user-read-me.test.js
k6 run --env SCENARIO=stress performance-test/user/scenarios/user-modify.test.js
```

## 🎯 포트폴리오용 시나리오별 실행 명령어

### Smoke Test (기본 기능 검증) - 30초, 5 VU

**포트폴리오 가치**: CI/CD 파이프라인용 빠른 검증, 기본 기능 동작 확인

```bash
# 핵심 20개 테스트 smoke 검증
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login.test.js
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login-abuse.test.js
k6 run --env SCENARIO=smoke performance-test/comment/scenarios/find-comments.test.js
k6 run --env SCENARIO=smoke performance-test/comment/scenarios/modify-basic.test.js
k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-upload.test.js
k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-detail.test.js
k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-popular.test.js
k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-filter.test.js
k6 run --env SCENARIO=smoke performance-test/like/scenarios/like-toggle-hotspot.test.js
k6 run --env SCENARIO=smoke performance-test/like/scenarios/like-distributed-load.test.js
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-upload.test.js
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-search.test.js
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-detail-read.test.js
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-popular-read.test.js
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-latest-read.test.js
k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-filtered-read.test.js
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-signup.test.js
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-read-me.test.js
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-modify.test.js
```

### Load Test (일반 부하 테스트) - 8분, 10-100 VU

**포트폴리오 가치**: 일상적 트래픽 시뮬레이션, 실제 사용자 패턴 검증

```bash
# 핵심 20개 테스트 load 검증
k6 run --env SCENARIO=load performance-test/auth/scenarios/login.test.js
k6 run --env SCENARIO=load performance-test/comment/scenarios/find-comments.test.js
k6 run --env SCENARIO=load performance-test/dataset/scenarios/dataset-upload.test.js
k6 run --env SCENARIO=load performance-test/like/scenarios/like-toggle-hotspot.test.js
k6 run --env SCENARIO=load performance-test/project/scenarios/project-upload.test.js
k6 run --env SCENARIO=load performance-test/user/scenarios/user-signup.test.js
```

### Stress Test (고부하 테스트) - 10분, 50-300 VU

**포트폴리오 가치**: 시스템 한계점 탐색, 핫스팟 처리, 동시성 검증

```bash
# 핵심 20개 테스트 stress 검증
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse.test.js
k6 run --env SCENARIO=stress performance-test/comment/scenarios/modify-basic.test.js
k6 run --env SCENARIO=stress performance-test/dataset/scenarios/dataset-popular.test.js
k6 run --env SCENARIO=stress performance-test/like/scenarios/like-toggle-hotspot.test.js
k6 run --env SCENARIO=stress performance-test/project/scenarios/project-detail-read.test.js
k6 run --env SCENARIO=stress performance-test/user/scenarios/user-modify.test.js
```

### Soak Test (장시간 안정성 테스트) - 1시간, 100 VU

**포트폴리오 가치**: 메모리 누수 검증, 캐시 효율성, 장시간 안정성

```bash
# 핵심 20개 테스트 soak 검증
k6 run --env SCENARIO=soak performance-test/dataset/scenarios/dataset-filter.test.js
k6 run --env SCENARIO=soak performance-test/like/scenarios/like-distributed-load.test.js
k6 run --env SCENARIO=soak performance-test/project/scenarios/project-popular-read.test.js
k6 run --env SCENARIO=soak performance-test/user/scenarios/user-read-me.test.js
```

### Spike Test (급격한 부하 증가 테스트) - 2분 30초, 20-800 VU

**포트폴리오 가치**: 갑작스러운 트래픽 폭증 대응, 시스템 복구력 검증

```bash
# 핵심 20개 테스트 spike 검증
k6 run --env SCENARIO=spike performance-test/project/scenarios/project-latest-read.test.js
k6 run --env SCENARIO=spike performance-test/project/scenarios/project-filtered-read.test.js
```

### Capacity Test (처리량 한계 테스트) - 6분, 50-200 req/s

**포트폴리오 가치**: 최대 처리량 측정, 병목 지점 식별, 성능 한계 확인

```bash
# 핵심 20개 테스트 capacity 검증
k6 run --env SCENARIO=capacity performance-test/auth/scenarios/login.test.js
k6 run --env SCENARIO=capacity performance-test/dataset/scenarios/dataset-upload.test.js
k6 run --env SCENARIO=capacity performance-test/project/scenarios/project-search.test.js
```

## 🔧 환경별 실행 명령어

### 개발 환경 (localhost)

```bash
# 기본 개발 환경 테스트
k6 run --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js

# 인증 모드별 테스트
k6 run --env BASE_URL=http://localhost:8080 --env AUTH_MODE=token --env TOKEN=your-token performance-test/user/scenarios/user-read-me.test.js
k6 run --env BASE_URL=http://localhost:8080 --env AUTH_MODE=login --env EMAIL=test@example.com --env PASSWORD=password performance-test/user/scenarios/user-read-me.test.js
```

### 스테이징 환경

```bash
# 스테이징 환경 테스트
k6 run --env BASE_URL=https://staging-api.dataracy.com performance-test/auth/scenarios/login.test.js
k6 run --env BASE_URL=https://staging-api.dataracy.com performance-test/user/scenarios/user-signup.test.js
```

### 프로덕션 환경

```bash
# 프로덕션 환경 테스트 (주의: 제한적 실행)
k6 run --env BASE_URL=https://api.dataracy.com performance-test/auth/scenarios/login.test.js
k6 run --env BASE_URL=https://api.dataracy.com performance-test/user/scenarios/user-read-me.test.js
```

## 🛡️ 보안 테스트 실행 명령어

### 인증 보안 테스트

```bash
# 로그인 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse.test.js

# 통합 보안 테스트
k6 run --env SCENARIO=load performance-test/auth/scenarios/auth-integration.test.js
```

### 사용자 보안 테스트

```bash
# 사용자 보안 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/user/scenarios/user-security.test.js
```

### 댓글 보안 테스트

```bash
# 댓글 보안 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/comment/scenarios/comment-security.test.js
```

### 데이터셋 보안 테스트

```bash
# 데이터셋 보안 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/dataset/scenarios/dataset-security.test.js
```

### 프로젝트 보안 테스트

```bash
# 프로젝트 보안 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/project/scenarios/project-security.test.js
```

### 좋아요 보안 테스트

```bash
# 좋아요 보안 공격 시뮬레이션
k6 run --env SCENARIO=stress performance-test/like/scenarios/like-security.test.js
```

## 📊 통합 테스트 실행 명령어

### 도메인별 통합 테스트

```bash
# 인증 통합 테스트
k6 run --env SCENARIO=load performance-test/auth/scenarios/auth-integration.test.js

# 댓글 통합 테스트
k6 run --env SCENARIO=load performance-test/comment/scenarios/comment-integration.test.js

# 데이터셋 통합 테스트
k6 run --env SCENARIO=load performance-test/dataset/scenarios/dataset-integration.test.js

# 좋아요 통합 테스트
k6 run --env SCENARIO=load performance-test/like/scenarios/like-integration.test.js

# 프로젝트 통합 테스트
k6 run --env SCENARIO=load performance-test/project/scenarios/project-integration.test.js

# 사용자 통합 테스트
k6 run --env SCENARIO=load performance-test/user/scenarios/user-integration.test.js
```

## 🚀 고급 실행 옵션

### 커스텀 VU 및 지속시간

```bash
# 커스텀 VU 수로 실행
k6 run --vus 50 --duration 5m performance-test/auth/scenarios/login.test.js

# 커스텀 RPS로 실행
k6 run --rps 100 --duration 10m performance-test/user/scenarios/user-signup.test.js
```

### 결과 출력 옵션

```bash
# JSON 결과 출력
k6 run --out json=results.json performance-test/auth/scenarios/login.test.js

# InfluxDB 결과 출력
k6 run --out influxdb=http://localhost:8086/dataracy performance-test/auth/scenarios/login.test.js

# CSV 결과 출력
k6 run --out csv=results.csv performance-test/auth/scenarios/login.test.js
```

### 병렬 실행

```bash
# 여러 테스트 동시 실행
k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login.test.js &
k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-signup.test.js &
wait
```

## 📈 모니터링 및 분석

### 실시간 모니터링

```bash
# 실시간 메트릭 출력
k6 run --env SCENARIO=load performance-test/auth/scenarios/login.test.js | tee results.log

# 상세 로그 출력
k6 run --env SCENARIO=stress --log-level debug performance-test/user/scenarios/user-signup.test.js
```

### 결과 분석

```bash
# 결과 파일 생성 후 분석
k6 run --out json=results.json performance-test/auth/scenarios/login.test.js
jq '.metrics' results.json

# HTML 보고서 생성
k6 run --out json=results.json performance-test/auth/scenarios/login.test.js
k6 run --out html=report.html performance-test/auth/scenarios/login.test.js
```

## 🎯 포트폴리오용 DDD + 헥사고날 아키텍처 기반 테스트 설계

### 🏗️ 아키텍처 계층별 테스트 전략 (20개 핵심 테스트)

- **Web Adapter (Primary)**: Controller 계층의 API 성능 및 응답 시간 측정
- **Application Layer**: UseCase 계층의 비즈니스 로직 처리 성능 검증
- **Domain Layer**: 도메인 모델의 비즈니스 규칙 및 상태 관리 성능 확인
- **Infrastructure**: JPA, Redis, Elasticsearch, S3 등 인프라 계층 성능 모니터링

### 📊 포트폴리오용 도메인별 특화 메트릭 (DDD 관점)

- **Auth 도메인 (2개)**: 로그인 성능 검증, 에러 분류 체계, 동시성 처리, 보안 공격 방어
- **User 도메인 (3개)**: 개인정보 마스킹, JWT 검증, 이벤트 기반 아키텍처, 회원가입 최적화
- **Dataset 도메인 (4개)**: 대용량 파일 처리, S3 업로드, 복합 쿼리 최적화, 실시간 검색
- **Project 도메인 (6개)**: AI 검색, 복잡한 도메인 로직, 캐싱 전략, 동적 필터링
- **Like 도메인 (2개)**: 분산 락, 핫스팟 처리, 동시성 제어, 분산 처리 최적화
- **Comment 도메인 (2개)**: 무한 스크롤, 실시간 댓글, 동시성 처리, 이벤트 기반 아키텍처

### 🔧 포트폴리오용 통일된 시나리오 (실무적 관점)

- **smoke**: CI/CD 파이프라인용 기본 기능 검증 (30초, 5 VU) - **20개 테스트 모두**
- **load**: 일상적 트래픽 시뮬레이션 (8분, 10-100 VU) - **6개 핵심 테스트**
- **stress**: 시스템 한계점 탐색 및 핫스팟 처리 (10분, 50-300 VU) - **6개 핵심 테스트**
- **soak**: 장시간 안정성 및 메모리 누수 검증 (1시간, 100 VU) - **4개 핵심 테스트**
- **spike**: 갑작스러운 트래픽 폭증 대응 (2분 30초, 20-800 VU) - **2개 핵심 테스트**
- **capacity**: 최대 처리량 한계 측정 (6분, 50-200 req/s) - **3개 핵심 테스트**

### 🎯 포트폴리오용 실무적 필요성 및 효과

- **DDD 도메인 검증**: 각 도메인의 비즈니스 규칙과 성능 최적화 포인트 식별
- **헥사고날 아키텍처**: Port/Adapter 패턴의 성능 영향 및 계층별 책임 분리 검증
- **인프라 최적화**: Redis, Elasticsearch, S3 등 외부 의존성 성능 모니터링
- **보안 성능**: JWT 검증, 개인정보 마스킹, 권한 검증 등 보안 계층 성능 확인
- **동시성 처리**: 분산 락, 캐시 동기화, 이벤트 기반 아키텍처 성능 검증
- **사용자 경험**: 응답 시간, 에러 처리, 데이터 일관성 등 UX 관련 성능 측정

### 📈 포트폴리오용 비즈니스 가치

- **개발팀**: DDD 도메인 로직과 헥사고날 인프라 계층의 성능 최적화 경험
- **운영팀**: 표준화된 모니터링으로 장애 예방 및 시스템 안정성 확보 경험
- **비즈니스**: 사용자 경험 개선 및 시스템 확장성 확보 경험
- **아키텍처**: DDD와 헥사고날 아키텍처의 성능적 효과 검증 경험

---

**💡 팁**: 테스트 실행 전에 서버가 정상적으로 동작하는지 확인하고, 테스트 환경에서 충분한 리소스가 있는지 확인하세요.
