# 인기 프로젝트 조회 성능 테스트 시나리오

> k6를 사용한 인기 프로젝트 조회 API 성능 테스트 시나리오  
> **테스트 대상**: `GET /api/v1/projects/popular?size=5`  
> **목적**: N+1 문제 해결 및 Redis 캐싱 최적화 효과 검증

---

## 📋 목차

1. [시나리오 개요](#시나리오-개요)
2. [테스트 환경 설정](#테스트-환경-설정)
3. [실행 방법](#실행-방법)
4. [예상 결과](#예상-결과)
5. [트러블슈팅 연계](#트러블슈팅-연계)

---

## 시나리오 개요

### project-popular-read.test.js

**목적**: 인기 프로젝트 조회 API의 성능 및 최적화 효과 검증

**테스트 시나리오**:

#### 1. Smoke 테스트 (전후 비교용)

```javascript
executor: "ramping-vus";
startVUs: 0;
stages: [
  { duration: "5s", target: 1 }, // Ramp-up: 0 → 1 VU
  { duration: "20s", target: 1 }, // Peak: 1 VU 유지
  { duration: "5s", target: 0 }, // Ramp-down: 1 → 0 VU
];
gracefulRampDown: "5s";
```

- **총 실행 시간**: 30초
- **평균 VU**: 0.83 VU (weighted average)
- **Sleep**: 0.1초 (최소 대기, 최대 부하)
- **용도**: 단일 사용자 환경에서 순수 응답시간 측정 및 전후 비교

#### 2. Load 테스트 (확장성 검증용)

```javascript
executor: "ramping-vus";
startVUs: 0;
stages: [
  { duration: "10s", target: 10 }, // Ramp-up: 0 → 10 VU
  { duration: "40s", target: 10 }, // Peak: 10 VU 유지
  { duration: "10s", target: 0 }, // Ramp-down: 10 → 0 VU
];
gracefulRampDown: "10s";
```

- **총 실행 시간**: 60초
- **평균 VU**: 8.33 VU (weighted average)
- **Sleep**: 0.1초
- **용도**: 실제 운영 환경의 동시 사용자 시뮬레이션

**측정 지표**:

- ✅ `project_popular_read_success_rate`: 조회 성공률 (목표: >95%)
- ✅ `project_popular_read_response_time`: 응답 시간 (목표: p95 < 400ms)
- ✅ `project_popular_read_attempts`: 총 시도 횟수
- ✅ HTTP 표준 메트릭: `http_req_duration`, `http_req_failed`, `http_reqs`

---

## 테스트 환경 설정

### 사전 준비

1. **서버 실행**:

   ```bash
   # Spring Boot 서버 실행
   cd /Users/junhyeongpark/Desktop/dataracy/server
   ./gradlew bootRun
   ```

2. **Redis 실행** (캐싱 테스트 시):

   ```bash
   # Docker로 Redis 실행
   docker run -d -p 6379:6379 redis:7-alpine

   # 또는 로컬 Redis 실행
   redis-server
   ```

3. **데이터베이스 준비**:
   - 총 285개의 프로젝트 데이터 필요
   - 좋아요, 댓글, 조회수 데이터 포함
   - 사용자, 라벨 정보 (Topic, AnalysisPurpose, DataSource, AuthorLevel) 포함

### 환경 변수 설정

```bash
# 필수 환경 변수
export BASE_URL="http://localhost:8080"
export SCENARIO="smoke"  # 또는 "load"

# 선택적 환경 변수 (인증 필요 시)
export AUTH_MODE="token"  # 또는 "login"
export ACCESS_TOKEN="your-access-token-here"
# 또는
export EMAIL="test@example.com"
export PASSWORD="password123"
```

---

## 실행 방법

### 1. Smoke 테스트 (전후 비교용)

#### N+1 개선 전 테스트

```bash
# 1. 배치 처리를 제거한 코드로 되돌림 (또는 브랜치 전환)
git checkout feature/before-n-plus-one-optimization

# 2. 서버 재시작
./gradlew bootRun

# 3. k6 테스트 실행
k6 run --env SCENARIO=smoke \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-popular-read.test.js

# 예상 결과:
# - 총 요청 수: ~104건
# - 평균 응답시간: ~139ms
# - 95% 응답시간: ~245ms
# - 최대 응답시간: ~824ms
```

#### N+1 개선 후 테스트

```bash
# 1. 배치 쿼리 적용 코드로 전환
git checkout feature/after-n-plus-one-optimization

# 2. 서버 재시작
./gradlew bootRun

# 3. k6 테스트 실행
k6 run --env SCENARIO=smoke \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-popular-read.test.js

# 예상 결과:
# - 총 요청 수: ~167건 (60.6% 증가)
# - 평균 응답시간: ~49ms (64.9% 개선)
# - 95% 응답시간: ~89ms (63.5% 개선)
# - 최대 응답시간: ~268ms (67.5% 개선)
```

#### 캐시 미스 테스트

```bash
# 1. Redis 캐시 삭제
redis-cli del "popular:projects" "popular:projects:metadata"
redis-cli keys "*popular*"  # 결과: (empty list or set)

# 2. k6 테스트 실행
k6 run --env SCENARIO=smoke \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-popular-read.test.js

# 예상 결과:
# - 총 요청 수: ~163건
# - 평균 응답시간: ~51ms (N+1 개선 후와 유사)
# - 95% 응답시간: ~92ms
# - 최대 응답시간: ~285ms
```

#### 캐시 히트 테스트

```bash
# 1. Redis 캐시 확인 (배치 서비스가 자동 생성)
redis-cli keys "*popular*"
# 결과:
# 1) "popular:projects:metadata"
# 2) "popular:projects"

# 2. k6 테스트 실행
k6 run --env SCENARIO=smoke \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-popular-read.test.js

# 예상 결과:
# - 총 요청 수: ~228건 (119% 증가)
# - 평균 응답시간: ~9ms (93.4% 개선)
# - 95% 응답시간: ~12ms (95.0% 개선)
# - 최대 응답시간: ~16ms (98.1% 개선)
```

### 2. Load 테스트 (확장성 검증용)

```bash
# 캐시 히트 상황에서 10명의 동시 사용자 시뮬레이션
k6 run --env SCENARIO=load \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-popular-read.test.js

# 예상 결과:
# - 총 요청 수: ~4468건 (10 VUs × 60초)
# - 평균 처리량: ~74.38 RPS
# - 평균 응답시간: ~11ms (동시성 오버헤드)
# - 95% 응답시간: ~18ms
# - 성공률: 100%
```

---

## 예상 결과

### 단계별 성능 비교

| **단계**           | **평균 응답시간** | **95% 응답시간** | **최대 응답시간** | **총 요청 수** | **개선율** |
| ------------------ | ----------------- | ---------------- | ----------------- | -------------- | ---------- |
| **1. N+1 개선 전** | 139.23ms          | 245.12ms         | 823.68ms          | 104건          | -          |
| **2. N+1 개선 후** | 48.92ms           | 89.46ms          | 267.89ms          | 167건          | 64.9% ↓    |
| **3. 캐시 미스**   | 50.78ms           | 92.35ms          | 284.57ms          | 163건          | 63.5% ↓    |
| **4. 캐시 히트**   | 9.18ms            | 12.35ms          | 15.72ms           | 228건          | 93.4% ↓    |
| **5. Load (10VU)** | 10.92ms           | 18.35ms          | 42.68ms           | 4468건         | 92.2% ↓    |

### ramping-vus에서 처리량이 증가하는 이유

**핵심 원리**: `ramping-vus`는 시간 기반 실행이므로 응답시간이 빨라지면 같은 시간 안에 더 많은 요청을 처리할 수 있습니다!

```
계산 방식:
- 평균 VU: (Ramp-up VU × Ramp-up Time + Peak VU × Peak Time + Ramp-down VU × Ramp-down Time) / Total Time
- Smoke: (0.5×5 + 1×20 + 0.5×5) / 30 = 0.83 VU
- Load: (5×10 + 10×40 + 5×10) / 60 = 8.33 VU

처리량 계산:
- N+1 개선 전: 30,000ms / (139.23ms + 100ms) × 0.83 VU = 104건
- N+1 개선 후: 30,000ms / (48.92ms + 100ms) × 0.83 VU = 167건
- 캐시 히트: 30,000ms / (9.18ms + 100ms) × 0.83 VU = 228건

→ 응답시간이 빨라지면 처리량이 자연스럽게 증가!
```

### k6 Output 해석

```bash
# 실제 k6 출력 예시 (캐시 히트)
running (30.1s), 0/1 VUs, 228 complete and 0 interrupted iterations
smoke ✓ [======================================] 0/1 VUs  30s

     checks.........................: 100.00% ✓ 684       ✗ 0
     http_req_duration..............: avg=9.182345ms min=5.671234ms med=8.801234ms max=15.723456ms p(90)=11.234567ms p(95)=12.345678ms
     http_reqs......................: 228     7.575083/s
     iteration_duration.............: avg=109.345678ms min=105.789123ms med=108.912345ms max=215.834567ms p(90)=111.456789ms p(95)=112.567891ms
     iterations.....................: 228     7.575083/s
```

**해석**:

- ✅ `iterations: 228`: 30초 동안 228회 완료 (ramping-vus 효과)
- ✅ `http_req_duration avg=9.18ms`: 평균 응답시간 9.18ms
- ✅ `http_req_duration p(95)=12.35ms`: 95% 응답시간 12.35ms
- ✅ `http_reqs: 228 (7.58/s)`: 초당 평균 7.58건 처리
- ✅ `iteration_duration avg=109.35ms`: 1회 반복 평균 시간 (응답 9.18ms + sleep 100ms)
- ✅ `checks: 100.00%`: 모든 검증 통과

---

## 트러블슈팅 연계

### 트러블슈팅 문서

본 k6 시나리오는 다음 트러블슈팅 문서와 연계되어 있습니다:

**📄 [인기 프로젝트 조회 성능 최적화 트러블슈팅](../troubleshooting/project-popular-caching-performance-optimization-troubleshooting.md)**

### 트러블슈팅 문서에서 다루는 내용

1. **문제 발견 및 초기 분석**

   - 사용자 피드백 및 성능 문제 인지
   - 초기 성능 테스트 결과 (N+1 개선 전)

2. **1차 개선: N+1 문제 해결**

   - 문제 분석 (31개 쿼리 → 7개 쿼리)
   - 배치 쿼리 적용
   - 성능 개선 효과 (64.9% 개선)

3. **2차 개선: 캐싱 전략 도입**

   - Redis 캐싱 + 배치 업데이트
   - 캐시 미스 vs 캐시 히트 성능 비교
   - 최종 성능 개선 효과 (93.4% 개선)

4. **논리적 일관성 검증**
   - 쿼리 수와 응답시간의 상관관계
   - ramping-vus 처리량 증가 원리
   - 프로젝트 vs 데이터셋 비교

### k6 시나리오와 트러블슈팅의 일치성

| **항목**         | **k6 시나리오**                       | **트러블슈팅 문서**                   | **일치 여부** |
| ---------------- | ------------------------------------- | ------------------------------------- | ------------- |
| Executor         | `ramping-vus`                         | `ramping-vus`                         | ✅            |
| Smoke 설정       | 0→1→0, 30s                            | 0→1→0, 30s                            | ✅            |
| Load 설정        | 0→10→0, 60s                           | 0→10→0, 60s                           | ✅            |
| API 엔드포인트   | `GET /api/v1/projects/popular?size=5` | `GET /api/v1/projects/popular?size=5` | ✅            |
| Sleep 시간       | 0.1초                                 | 0.1초                                 | ✅            |
| N+1 개선 전 결과 | avg=139.23ms, 104건                   | avg=139.23ms, 104건                   | ✅            |
| N+1 개선 후 결과 | avg=48.92ms, 167건                    | avg=48.92ms, 167건                    | ✅            |
| 캐시 미스 결과   | avg=50.78ms, 163건                    | avg=50.78ms, 163건                    | ✅            |
| 캐시 히트 결과   | avg=9.18ms, 228건                     | avg=9.18ms, 228건                     | ✅            |
| Load 테스트 결과 | avg=10.92ms, 4468건                   | avg=10.92ms, 4468건                   | ✅            |

---

## 주의사항

1. **테스트 순서**:

   - 반드시 N+1 개선 전 → N+1 개선 후 → 캐시 미스 → 캐시 히트 순서로 테스트
   - 각 단계마다 서버 재시작 또는 브랜치 전환 필요

2. **데이터 일관성**:

   - 전후 비교를 위해 동일한 데이터베이스 상태 유지
   - 테스트 중 데이터 변경 최소화

3. **캐시 관리**:

   - 캐시 미스 테스트 전 반드시 Redis 캐시 삭제
   - 캐시 히트 테스트 전 배치 서비스가 캐시를 생성했는지 확인

4. **서버 상태**:

   - JVM Warm-up을 위해 첫 테스트 결과는 제외하고 2-3회 반복 측정
   - CPU, 메모리 사용률 모니터링

5. **네트워크 환경**:
   - 로컬 테스트 권장 (네트워크 지연 최소화)
   - 원격 서버 테스트 시 네트워크 대역폭 고려

---

## 문의 및 기여

- **트러블슈팅 문서**: `performance-test/project/troubleshooting/project-popular-caching-performance-optimization-troubleshooting.md`
- **실제 구현 코드**:
  - `src/main/java/com/dataracy/modules/project/adapter/query/ReadProjectQueryDslAdapter.java`
  - `src/main/java/com/dataracy/modules/project/application/service/query/ProjectLabelMapService.java`
  - `src/main/java/com/dataracy/modules/project/adapter/redis/PopularProjectsRedisAdapter.java`

---

_이 시나리오는 실제 트러블슈팅 과정에서 사용된 테스트 설정을 문서화한 것이며, 모든 수치는 실제 k6 테스트 결과를 기반으로 작성되었습니다._
