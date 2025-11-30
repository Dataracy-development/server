# 데이터셋 성능 테스트 시나리오

> **인기 데이터셋 조회 성능 최적화를 위한 k6 성능 테스트 스크립트**

---

## 📋 목차

- [개요](#개요)
- [테스트 시나리오](#테스트-시나리오)
- [실행 방법](#실행-방법)
- [트러블슈팅 문서와의 관계](#트러블슈팅-문서와의-관계)
- [주요 메트릭](#주요-메트릭)
- [테스트 결과 해석](#테스트-결과-해석)

---

## 개요

이 디렉토리에는 **인기 데이터셋 조회 API**의 성능을 측정하고 검증하기 위한 k6 테스트 스크립트가 포함되어 있습니다.

### 테스트 파일

- **`dataset-popular-performance-test.test.js`**: 인기 데이터셋 조회 성능 테스트
  - N+1 개선 전/후 비교
  - 캐싱 적용 전/후 비교 (캐시 미스/히트)
  - Load 테스트 (10 VUs)

---

## 테스트 시나리오

### 1. Smoke 테스트 (기준선 측정)

**목적**: 단일 사용자 시나리오에서 안정성 및 성능 측정

**설정**:

```javascript
{
  executor: "ramping-vus",
  startVUs: 0,
  stages: [
    { duration: "5s", target: 1 },   // Ramp-up: 0 → 1 VU
    { duration: "20s", target: 1 },  // Peak: 1 VU 유지
    { duration: "5s", target: 0 },   // Ramp-down: 1 → 0 VU
  ],
  gracefulRampDown: "5s",
}
```

**특징**:

- 총 시간: 30초
- 평균 VU: 0.83 (weighted average)
- Sleep: 0.1초 (최소 대기, 최대 부하)
- 목적: N+1 개선 전/후, 캐시 미스/히트 비교

### 2. Load 테스트 (확장성 검증)

**목적**: 실제 운영 환경의 동시 사용자 시뮬레이션

**설정**:

```javascript
{
  executor: "ramping-vus",
  startVUs: 0,
  stages: [
    { duration: "10s", target: 10 },  // Ramp-up: 0 → 10 VU
    { duration: "40s", target: 10 },  // Peak: 10 VU 유지
    { duration: "10s", target: 0 },   // Ramp-down: 10 → 0 VU
  ],
  gracefulRampDown: "10s",
}
```

**특징**:

- 총 시간: 60초
- 평균 VU: 8.33 (weighted average)
- Sleep: 0.1초
- 목적: 캐시 히트 상황에서 확장성 검증

---

## 실행 방법

### 사전 준비

1. **서버 실행**:

   ```bash
   # Spring Boot 서버 실행
   ./gradlew bootRun
   ```

2. **Redis 실행** (캐싱 테스트 시):
   ```bash
   redis-server
   ```

### Smoke 테스트 실행

#### 1) N+1 개선 전 (최악의 상태)

```bash
# N+1 문제가 있는 초기 코드로 서버 실행 후
k6 run --env SCENARIO=smoke \
       performance-test/dataset/scenarios/dataset-popular-performance-test.test.js

예상 결과:
- 평균 응답시간: ~165ms
- 총 요청 수: ~94건
- DB 쿼리 수: 36개 (메인 1 + N+1 문제 35개)
```

#### 2) N+1 개선 후

```bash
# N+1 개선 코드로 서버 재시작 후
k6 run --env SCENARIO=smoke \
       performance-test/dataset/scenarios/dataset-popular-performance-test.test.js

예상 결과:
- 평균 응답시간: ~58ms (64.7% 개선)
- 총 요청 수: ~157건 (67% 증가)
- DB 쿼리 수: 7개 (80.6% 감소)
```

#### 3) 캐시 미스 (첫 요청 또는 캐시 만료)

```bash
# Redis 캐시 삭제
redis-cli del "popular:datasets" "popular:datasets:metadata"

# 캐싱 적용 코드로 서버 재시작 후
k6 run --env SCENARIO=smoke \
       performance-test/dataset/scenarios/dataset-popular-performance-test.test.js

예상 결과:
- 평균 응답시간: ~61ms (N+1 개선 후와 유사)
- 총 요청 수: ~154건
- DB 쿼리 수: 7개 (N+1 개선된 로직 사용)
```

#### 4) 캐시 히트 (일반적인 운영 상황)

```bash
# 캐시가 이미 생성된 상태에서 테스트
# (위 캐시 미스 테스트 직후 재실행)
k6 run --env SCENARIO=smoke \
       performance-test/dataset/scenarios/dataset-popular-performance-test.test.js

예상 결과:
- 평균 응답시간: ~11ms (82.0% 개선)
- 총 요청 수: ~224건 (45% 증가)
- DB 쿼리 수: 0개 (캐시만 사용)
```

### Load 테스트 실행

```bash
# 캐시 히트 상황에서 10 VUs로 부하 테스트
k6 run --env SCENARIO=load \
       performance-test/dataset/scenarios/dataset-popular-performance-test.test.js

예상 결과:
- 평균 응답시간: ~12.67ms (Smoke보다 약간 느림, 동시성 오버헤드)
- 총 요청 수: ~4438건
- 평균 처리량: ~73.86 RPS
- DB 쿼리 수: 0개
```

---

## 트러블슈팅 문서와의 관계

### k6 시나리오 vs 트러블슈팅 문서 일치 확인

| **항목**             | **k6 시나리오**                   | **트러블슈팅 문서**               | **일치 여부** |
| -------------------- | --------------------------------- | --------------------------------- | ------------- |
| **Executor**         | `ramping-vus`                     | `ramping-vus`                     | ✅            |
| **Smoke 설정**       | 0→1→0, 30s                        | 0→1→0, 30s                        | ✅            |
| **Load 설정**        | 0→10→0, 60s                       | 0→10→0, 60s                       | ✅            |
| **API 엔드포인트**   | `/api/v1/datasets/popular?size=5` | `/api/v1/datasets/popular?size=5` | ✅            |
| **Sleep 시간**       | 0.1초                             | 0.1초                             | ✅            |
| **N+1 개선 전 결과** | avg=165.42ms, 94건, 36개 쿼리     | avg=165.42ms, 94건, 36개 쿼리     | ✅            |
| **N+1 개선 후 결과** | avg=58.34ms, 157건, 7개 쿼리      | avg=58.34ms, 157건, 7개 쿼리      | ✅            |
| **캐시 미스 결과**   | avg=61.28ms, 154건, 7개 쿼리      | avg=61.28ms, 154건, 7개 쿼리      | ✅            |
| **캐시 히트 결과**   | avg=11.01ms, 224건, 0개 쿼리      | avg=11.01ms, 224건, 0개 쿼리      | ✅            |
| **Load 테스트 결과** | avg=12.67ms, 4438건               | avg=12.67ms, 4438건               | ✅            |

### 트러블슈팅 문서 위치

- **문서 경로**: `performance-test/dataset/troubleshooting/dataset-popular-caching-performance-optimization-troubleshooting.md`
- **내용**: N+1 개선, Redis 캐싱, 배치 처리 등 전체 최적화 과정
- **k6 결과**: 이 시나리오 파일의 실제 실행 결과를 기반으로 작성

---

## 주요 메트릭

### k6 Custom Metrics

```javascript
- popular_data_success_rate: 인기 데이터 조회 성공률 (목표: >98%)
- popular_data_response_time: 응답 시간
- popular_data_error_rate: 에러율
- popular_data_load_time: 로드 시간
```

### Check 항목 (4개)

1. `status is 200`: HTTP 200 응답 확인
2. `response time < 2s`: 응답시간 2초 이하
3. `has popular data`: 응답 데이터 존재 확인
4. `popular data count`: 데이터 개수 확인 (최대 5개)

### Checks 계산

```
N+1 개선 전: 94 requests × 4 checks = 376 total checks ✅
N+1 개선 후: 157 requests × 4 checks = 628 total checks ✅
캐시 미스: 154 requests × 4 checks = 616 total checks ✅
캐시 히트: 224 requests × 4 checks = 896 total checks ✅
Load: 4438 requests × 4 checks = 17,752 total checks ✅
```

---

## 테스트 결과 해석

### ramping-vus에서 처리량 계산

`ramping-vus`는 시간 기반 실행이므로 **응답시간이 빠를수록 더 많은 요청을 처리**합니다.

**Smoke 테스트 (30초, 평균 0.83 VU)**:

```
N+1 개선 전 (165.42ms):
- 1회 처리: 165.42ms (응답) + 100ms (sleep) = 265.42ms
- 30초 처리: 30,000ms / 265.42ms × 0.83 = 93.8건 ≈ 94건 ✅

N+1 개선 후 (58.34ms):
- 1회 처리: 58.34ms + 100ms = 158.34ms
- 30초 처리: 30,000ms / 158.34ms × 0.83 = 157.2건 ≈ 157건 ✅

캐시 미스 (61.28ms):
- 1회 처리: 61.28ms + 100ms = 161.28ms
- 30초 처리: 30,000ms / 161.28ms × 0.83 = 154.3건 ≈ 154건 ✅

캐시 히트 (11.01ms):
- 1회 처리: 11.01ms + 100ms = 111.01ms
- 30초 처리: 30,000ms / 111.01ms × 0.83 = 224.3건 ≈ 224건 ✅

→ 응답시간이 빠를수록 처리량이 자연스럽게 증가! ✅
```

**Load 테스트 (60초, 평균 8.33 VU)**:

```
캐시 히트 (12.67ms):
- 1회 처리: 12.67ms + 100ms = 112.67ms
- 60초 처리: 60,000ms / 112.67ms × 8.33 = 4436건 ≈ 4438건 ✅

→ 10 VUs에서도 안정적인 성능 유지! ✅
```

### iteration_duration 검증

`iteration_duration` = `http_req_duration` + `sleep(0.1)` + k6 오버헤드

```
N+1 개선 전: 165.42ms + 100ms ≈ 265.57ms ✅
N+1 개선 후: 58.34ms + 100ms ≈ 158.47ms ✅
캐시 미스: 61.28ms + 100ms ≈ 161.39ms ✅
캐시 히트: 11.01ms + 100ms ≈ 111.15ms ✅
Load: 12.67ms + 100ms ≈ 112.79ms ✅

→ 모든 값이 수학적으로 정확! ✅
```

### 주요 발견사항

1. **N+1 문제의 심각성**:

   - 쿼리 수: 36개 → 7개 (80.6% 감소)
   - 응답시간: 165.42ms → 58.34ms (64.7% 개선)
   - 처리량: 94건 → 157건 (67% 증가)

2. **캐싱의 효과**:

   - 캐시 미스: 61.28ms (N+1 개선 후와 유사)
   - 캐시 히트: 11.01ms (82.0% 추가 개선)
   - 처리량: 154건 → 224건 (45% 증가)

3. **확장성**:
   - Load 테스트: 4438회 요청 100% 성공
   - 평균 처리량: 73.86 RPS
   - 동시 10 VUs에도 안정적 성능 유지

---

## 주의사항

1. **테스트 순서**:

   - 반드시 N+1 개선 전 → N+1 개선 후 → 캐시 미스 → 캐시 히트 순서로 테스트
   - 각 단계마다 서버 재시작 또는 브랜치 전환 필요

2. **데이터 일관성**:

   - 전후 비교를 위해 동일한 데이터베이스 상태 유지
   - 테스트 중 데이터 변경 최소화

3. **Redis 상태**:

   - 캐시 미스 테스트 전: `redis-cli del "popular:datasets" "popular:datasets:metadata"`
   - 캐시 히트 테스트: 캐시가 이미 생성된 상태에서 테스트

4. **서버 상태**:
   - JVM Warm-up을 위해 첫 테스트 결과는 제외
   - CPU, 메모리 사용률 모니터링

---

## 관련 문서

- [인기 데이터셋 조회 성능 최적화 트러블슈팅](../troubleshooting/dataset-popular-caching-performance-optimization-troubleshooting.md)
- [데이터셋 필터링 성능 최적화 README](./README_FILTERING.md)

---

_이 테스트는 실제 운영 환경을 시뮬레이션하여 성능 개선 효과를 정량적으로 검증하기 위해 설계되었습니다. 모든 수치는 ramping-vus 기반으로 측정되어 응답시간 개선이 처리량 증가로 자연스럽게 반영됩니다._
