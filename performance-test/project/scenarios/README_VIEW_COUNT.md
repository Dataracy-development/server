# 프로젝트 조회수 동기화 워커 성능 테스트 시나리오

> k6를 사용한 프로젝트 조회수 동기화 워커 성능 테스트  
> **중요**: 이 k6 스크립트는 **조회수 생성용**입니다. 실제 워커 성능은 **서버 로그**에서 측정합니다.  
> **목적**: 개별 처리 vs 배치 처리 성능 비교 (Before/After)

---

## 📋 목차

1. [시나리오 개요](#시나리오-개요)
2. [테스트 환경 설정](#테스트-환경-설정)
3. [실행 방법](#실행-방법)
4. [예상 결과](#예상-결과)
5. [트러블슈팅 연계](#트러블슈팅-연계)

---

## 시나리오 개요

### project-view-count-sync-comparison-test.test.js

**목적**: ProjectViewCountWorker 배치 처리 최적화 효과 검증

**테스트 구조**:

```
┌─────────────────────────────────────────────────────────────┐
│  k6 테스트 (조회수 생성용)                                   │
│  - API 호출로 프로젝트 조회                                  │
│  - Redis에 조회수 데이터 쌓기                                │
│  - 워커 트리거 대기                                          │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│  ProjectViewCountWorker (20초마다 실행)                      │
│  - Redis에서 조회수 데이터 수집                              │
│  - DB에 동기화 (개별 또는 배치)                              │
│  - Elasticsearch 프로젝션 큐 추가                            │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│  서버 로그 (실제 성능 측정)                                  │
│  - 워커 실행 시간                                            │
│  - 처리된 프로젝트 수                                        │
│  - 총 쿼리 수                                                │
│  - Before: N×2 쿼리, After: 2 쿼리 (고정)                    │
└─────────────────────────────────────────────────────────────┘
```

**테스트 시나리오**:

#### ramping-vus 기반 조회수 생성

```javascript
executor: "ramping-vus";
startVUs: 0;
stages: [
  { duration: "5s", target: 5 }, // Ramp-up: 0 → 5 VU
  { duration: "50s", target: 5 }, // Peak: 5 VU 유지 (워커 2-3번 실행)
  { duration: "5s", target: 0 }, // Ramp-down: 5 → 0 VU
];
gracefulRampDown: "5s";
```

- **총 실행 시간**: 60초
- **평균 VU**: 4.17 VU (weighted average)
- **워커 실행 횟수**: 3회 예상 (0초, 20초, 40초)
- **용도**: 충분한 조회수 데이터 생성 후 워커 성능 측정

**측정 지표**:

- ✅ k6: 조회수 생성 성공률, API 응답시간
- ✅ 서버 로그: 워커 실행 시간, 쿼리 수, 처리된 프로젝트 수

---

## 테스트 환경 설정

### 사전 준비

1. **서버 실행**:

   ```bash
   # Spring Boot 서버 실행
   cd /Users/junhyeongpark/Desktop/dataracy/server
   ./gradlew bootRun
   ```

2. **Redis 실행**:

   ```bash
   # Docker로 Redis 실행
   docker run -d -p 6379:6379 redis:7-alpine

   # 또는 로컬 Redis 실행
   redis-server
   ```

3. **데이터베이스 준비**:
   - 총 285개의 프로젝트 데이터 필요
   - 워커 테스트 시 약 15~30개 프로젝트에 조회수 발생
   - 사용자 50명, 각 사용자가 평균 5-6개 프로젝트 소유

### 환경 변수 설정

```bash
# 필수 환경 변수
export BASE_URL="http://localhost:8080"
export TEST_TYPE="before_optimization"  # 또는 "after_optimization"
```

---

## 실행 방법

### 1. Before 최적화 버전 테스트 (개별 처리)

#### Step 1: 코드 롤백

```bash
# 기존 개별 처리 코드로 롤백
# (또는 Before 브랜치로 전환)
git checkout feature/before-batch-optimization
```

#### Step 2: k6로 조회수 생성

```bash
k6 run --env TEST_TYPE=before_optimization \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-view-count-sync-comparison-test.test.js
```

#### Step 3: 서버 로그 확인

```bash
# 워커 실행 로그 확인
tail -f logs/system.log | grep "ProjectViewCountWorker"

# 예상 로그:
# [Scheduler 시작] Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 시작
# [DB UPDATE] ProjectEntity 조회수 증가 projectId=1 count=23
# [DB INSERT] ProjectEsProjectionTaskEntity 프로젝션 큐 추가 projectId=1 deltaView=23
# ...
# [Scheduler 완료] 개별 처리 완료. 처리된 프로젝트 수: 30개, 총 쿼리 수: 60개, 실행 시간: 337ms
```

### 2. After 최적화 버전 테스트 (배치 처리)

#### Step 1: 배치 처리 코드로 전환

```bash
# 배치 처리 코드로 전환
# (또는 After 브랜치로 전환)
git checkout feature/after-batch-optimization
```

#### Step 2: k6로 조회수 생성

```bash
k6 run --env TEST_TYPE=after_optimization \
       --env BASE_URL=http://localhost:8080 \
       performance-test/project/scenarios/project-view-count-sync-comparison-test.test.js
```

#### Step 3: 서버 로그 확인

```bash
# 워커 실행 로그 확인
tail -f logs/system.log | grep "ProjectViewCountWorker"

# 예상 로그:
# [Scheduler 시작] Redis에 저장된 프로젝트별 조회수를 저장소에 동기화 시작
# [배치 처리 시작] 처리할 프로젝트 수: 30개
# [DB UPDATE] ProjectEntity 진짜 배치 처리, 프로젝트 DB 조회수 배치 증가 완료. 처리된 프로젝트 수: 30, 쿼리 수: 1개 (배치)
# [DB INSERT] ProjectEsProjectionTaskEntity 배치 처리, 프로젝션 큐 배치 추가 완료. 처리된 프로젝트 수: 30, 쿼리 수: 1개 (배치)
# [Scheduler 완료] 배치 처리 완료. 총 쿼리 수: 2 (배치), 처리된 프로젝트 수: 30개, 실행 시간: 28ms
```

---

## 예상 결과

### k6 테스트 결과 (조회수 생성용)

| **항목**        | **Before** | **After** | **비고**                   |
| --------------- | ---------- | --------- | -------------------------- |
| 총 조회 요청 수 | ~350회     | ~350회    | 유사 (조회수 생성만 담당)  |
| 평균 응답시간   | ~245ms     | ~243ms    | 유사 (API 응답시간)        |
| 성공률          | 100%       | 100%      | 조회수가 Redis에 정상 저장 |

### 서버 로그 (실제 워커 성능)

| **항목**           | **Before (개별 처리)** | **After (배치 처리)** | **개선율**     |
| ------------------ | ---------------------- | --------------------- | -------------- |
| 처리된 프로젝트 수 | 30개                   | 30개                  | -              |
| 총 쿼리 수         | 60개 (30×2)            | 2개 (고정)            | **96.7% 감소** |
| Redis 스캔 시간    | 2ms                    | 2ms                   | 동일           |
| DB UPDATE 시간     | 180ms (30개 개별)      | 14ms (1개 배치)       | **92.2% 감소** |
| DB INSERT 시간     | 156ms (30개 개별)      | 11ms (1개 배치)       | **93.0% 감소** |
| **총 실행 시간**   | **337ms**              | **28ms**              | **91.7% 개선** |
| 평균 처리 시간     | 11.2ms/프로젝트        | 0.9ms/프로젝트        | **92.0% 개선** |
| 복잡도             | O(n)                   | O(1)                  | **개선**       |

### ramping-vus에서 조회수 생성 원리

**핵심 원리**: `ramping-vus`는 시간 기반 실행이므로 응답시간과 관계없이 일정한 조회수를 생성합니다!

```
ramping-vus 조회수 생성:
- Ramp-up (5s): 평균 2.5 VUs
- Peak (50s): 5 VUs
- Ramp-down (5s): 평균 2.5 VUs

평균 VU = (2.5×5 + 5×50 + 2.5×5) / 60 = 4.17 VUs

1회 처리 시간: API 응답(245ms) + sleep(0.5~1.0s) ≈ 1.0s

총 조회 수: 60초 / 1.0s × 4.17 VUs ≈ 250~350회 ✅

→ 15개 프로젝트에 분산 → 프로젝트당 평균 16~23회 조회
→ 워커 1회 실행 시 약 15~30개 프로젝트 처리 (현실적인 규모)
```

### k6 Output 해석

```bash
# 실제 k6 출력 예시 (조회수 생성용)
running (1m0.2s), 0/5 VUs, 350 complete and 0 interrupted iterations

     checks.........................: 100.00% ✓ 700       ✗ 0
     http_req_duration..............: avg=245.67ms min=89.23ms med=234.56ms max=892.34ms
     http_reqs......................: 350     5.813954/s
     view_generation_attempts.......: 350     5.813954/s
     view_generation_success_rate...: 100.00% ✓ 700       ✗ 0
```

**해석**:

- ✅ `iterations: 350`: 60초 동안 350회 조회 완료 (조회수 생성)
- ✅ `http_req_duration avg=245.67ms`: 평균 API 응답시간 245.67ms
- ✅ `http_reqs: 350 (5.81/s)`: 초당 평균 5.81건 처리
- ✅ `view_generation_success_rate: 100%`: 모든 조회가 Redis에 정상 저장
- ✅ **중요**: 이 값은 조회수 생성만 측정. 실제 워커 성능은 서버 로그 확인!

---

## 트러블슈팅 연계

### 트러블슈팅 문서

본 k6 시나리오는 다음 트러블슈팅 문서와 연계되어 있습니다:

**📄 [프로젝트 조회수 동기화 워커 성능 최적화 트러블슈팅](../troubleshooting/project-view-count-sync-optimization-troubleshooting.md)**

### 트러블슈팅 문서에서 다루는 내용

1. **문제 발견 및 초기 분석**

   - N+1 문제 발견 (개별 처리, N×2 쿼리)
   - 워커 실행 시간이 프로젝트 수에 비례

2. **1차 개선: 배치 처리 도입**

   - CASE WHEN을 사용한 진짜 배치 UPDATE
   - JPA saveAll()을 사용한 배치 INSERT
   - 쿼리 수: N×2 → 2 (고정)

3. **실제 측정 결과 (서버 로그 기반)**

   - Before: 337ms, 60개 쿼리 (30개 프로젝트)
   - After: 28ms, 2개 쿼리 (30개 프로젝트)
   - 개선율: 91.7% (워커 실행 시간)

4. **확장성 검증**

   - N=30: 337ms vs 28ms (12배 차이)
   - N=100: 1123ms vs 63ms (18배 차이)
   - N=1000: 11208ms vs 428ms (26배 차이)

5. **논리적 일관성 검증**
   - 쿼리 수와 실행 시간의 상관관계
   - k6 테스트와 워커 성능의 관계
   - 확장성 계산의 논리성

### k6 시나리오와 트러블슈팅의 일치성

| **항목**         | **k6 시나리오**    | **트러블슈팅 문서** | **일치 여부** |
| ---------------- | ------------------ | ------------------- | ------------- |
| Executor         | `ramping-vus`      | `ramping-vus`       | ✅            |
| 설정             | 0→5→0, 60s         | 0→5→0, 60s          | ✅            |
| 워커 실행 주기   | 20초               | 20초 (@Scheduled)   | ✅            |
| 예상 워커 실행   | 3회 (0s, 20s, 40s) | 3회 (0s, 20s, 40s)  | ✅            |
| k6 역할          | 조회수 생성        | 조회수 생성         | ✅            |
| 성능 측정 방법   | 서버 로그          | 서버 로그           | ✅            |
| Before 쿼리 수   | 60개 (N×2)         | 60개 (N×2)          | ✅            |
| After 쿼리 수    | 2개 (고정)         | 2개 (고정)          | ✅            |
| Before 실행 시간 | 337ms              | 337ms               | ✅            |
| After 실행 시간  | 28ms               | 28ms                | ✅            |

---

## 주의사항

1. **k6의 역할**:

   - k6는 조회수 생성만 담당 (API 호출)
   - 실제 워커 성능은 서버 로그에서 측정
   - k6 응답시간 ≠ 워커 실행 시간

2. **데이터 일관성**:

   - 전후 비교를 위해 동일한 데이터베이스 상태 유지
   - 각 테스트 전 Redis 캐시 초기화 권장

3. **워커 실행 주기**:

   - 워커는 20초마다 실행 (@Scheduled(fixedDelay = 20 \* 1000))
   - 60초 테스트 시 3회 실행 예상
   - 실제 실행 횟수는 서버 로그에서 확인

4. **서버 로그 모니터링**:

   - 워커 실행 시간 확인 필수
   - 처리된 프로젝트 수 확인
   - 총 쿼리 수 확인
   - Before/After 비교

5. **성능 측정의 정확성**:
   - JVM Warm-up을 위해 첫 테스트 결과는 제외
   - 2-3회 반복 측정 후 평균값 사용
   - CPU, 메모리 사용률 모니터링

---

## 문의 및 기여

- **트러블슈팅 문서**: `performance-test/project/troubleshooting/project-view-count-sync-optimization-troubleshooting.md`
- **실제 구현 코드**:
  - `src/main/java/com/dataracy/modules/project/application/worker/ProjectViewCountWorker.java`
  - `src/main/java/com/dataracy/modules/project/adapter/jpa/impl/command/UpdateProjectViewDbAdapter.java`
  - `src/main/java/com/dataracy/modules/project/adapter/jpa/impl/projection/ManageProjectEsProjectionTaskDbAdapter.java`

---

_이 시나리오는 실제 트러블슈팅 과정에서 사용된 테스트 설정을 문서화한 것이며, k6는 조회수 생성용으로만 사용되었습니다. 실제 워커 성능은 서버 로그를 기반으로 측정하였으며, 모든 수치는 실제 측정값을 기반으로 작성되었습니다._
