# 데이터셋 필터링 성능 테스트 시나리오

> k6를 사용한 데이터셋 필터링 API 성능 테스트  
> **테스트 대상**: `GET /api/v1/datasets/filter?page=0&size=50&sortType=LATEST`  
> **목적**: 3단계 최적화 효과 검증 (JOIN+GROUP → N+1 → 배치 처리)

---

## 📋 목차

1. [시나리오 개요](#시나리오-개요)
2. [테스트 환경 설정](#테스트-환경-설정)
3. [실행 방법](#실행-방법)
4. [예상 결과](#예상-결과)
5. [트러블슈팅 연계](#트러블슈팅-연계)

---

## 시나리오 개요

### dataset-filtering-performance-test.test.js

**목적**: 데이터셋 필터링 API 3단계 최적화 효과 검증

**3단계 최적화 과정**:

#### 1단계: JOIN + GROUP BY (기준선)

```java
// fetchJoin + paging 문제
.leftJoin(data.metadata).fetchJoin()  // 1:1 관계
.leftJoin(projectData).on(...)        // 1:N 관계 JOIN
.groupBy(data.id)                     // GROUP BY
.offset(pageable.getOffset())
.limit(pageable.getPageSize())        // ❌ 메모리에서 paging 처리
```

- 쿼리 수: 2개 (메인 + 카운트)
- 문제: fetchJoin + paging 충돌 (메모리 처리)

#### 2단계: N+1 서브쿼리 (문제점 드러내기)

```java
// N+1 문제
dataEntities.stream().map(entity -> {
    long projectCount = queryFactory
        .select(projectData.id.count())
        .from(projectData)
        .where(projectData.dataId.eq(entity.getId()))  // ❌ 개별 쿼리!
        .fetchOne();
    ...
})
```

- 쿼리 수: 52개 (메인 + 서브쿼리 50개 + 카운트)
- 문제: N+1 문제로 쿼리 수 폭증

#### 3단계: 배치 처리 (최종 최적화)

```java
// 배치 쿼리
List<Long> dataIds = dataEntities.stream().map(DataEntity::getId).toList();
Map<Long, Long> projectCounts = getProjectCountsBatch(dataIds);

private Map<Long, Long> getProjectCountsBatch(List<Long> dataIds) {
    return queryFactory
        .select(projectData.dataId, projectData.id.count())
        .from(projectData)
        .where(projectData.dataId.in(dataIds))  // ✅ IN 절로 배치 처리
        .groupBy(projectData.dataId)
        .fetch()
        .stream()
        .collect(Collectors.toMap(...));
}
```

- 쿼리 수: 3개 (메인 + 배치 + 카운트)
- 해결: 50개 개별 쿼리 → 1개 배치 쿼리

**테스트 시나리오**:

#### 1. Smoke 테스트 (3단계 비교용)

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
- **용도**: 3단계 최적화 과정 비교

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

- ✅ `filtering_success_rate`: 필터링 성공률 (목표: >95%)
- ✅ `filtering_response_time`: 응답 시간 (목표: p95 < 400ms)
- ✅ `db_query_count`: DB 쿼리 수 추적
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

2. **데이터베이스 준비**:
   - 총 1,200개의 데이터셋 데이터 필요
   - 프로젝트 285개, 프로젝트-데이터셋 연결 약 3,500개
   - 메타데이터 1,200개 (1:1 관계)

### 환경 변수 설정

```bash
# 필수 환경 변수
export BASE_URL="http://localhost:8080"
export SCENARIO="smoke"  # 또는 "load"
export STAGE="stage1"    # stage1, stage2, stage3
```

---

## 실행 방법

### 1. Smoke 테스트 (3단계 비교)

#### Stage 1: JOIN + GROUP BY (기준선)

```bash
k6 run --env SCENARIO=smoke --env STAGE=stage1 \
       --env BASE_URL=http://localhost:8080 \
       performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js

# 예상 결과:
# - 총 요청 수: ~161건
# - 평균 응답시간: ~54ms
# - 요청당 쿼리: 2개 (메인 + 카운트)
# - 총 쿼리 수: ~322개
```

#### Stage 2: N+1 서브쿼리 (문제점 드러내기)

```bash
k6 run --env SCENARIO=smoke --env STAGE=stage2 \
       --env BASE_URL=http://localhost:8080 \
       performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js

# 예상 결과:
# - 총 요청 수: ~125건 (느린 응답으로 감소)
# - 평균 응답시간: ~99ms (82.8% 증가)
# - 요청당 쿼리: 52개 (메인 + 서브쿼리 50개 + 카운트)
# - 총 쿼리 수: ~6,500개 (N+1 문제)
```

#### Stage 3: 배치 처리 (최종 최적화)

```bash
k6 run --env SCENARIO=smoke --env STAGE=stage3 \
       --env BASE_URL=http://localhost:8080 \
       performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js

# 예상 결과:
# - 총 요청 수: ~174건 (빠른 응답으로 증가)
# - 평균 응답시간: ~43ms (56.7% 개선)
# - 요청당 쿼리: 3개 (메인 + 배치 + 카운트)
# - 총 쿼리 수: ~522개 (92.0% 감소)
```

### 2. Load 테스트 (확장성 검증)

```bash
# Stage 3 배치 처리로 Load 테스트
k6 run --env SCENARIO=load --env STAGE=stage3 \
       --env BASE_URL=http://localhost:8080 \
       performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js

# 예상 결과:
# - 총 요청 수: ~3928건 (10 VUs × 60초)
# - 평균 처리량: ~65.39 RPS
# - 평균 응답시간: ~85ms (동시성 오버헤드)
# - 95% 응답시간: ~168ms
# - 성공률: 100%
```

---

## 예상 결과

### 3단계 성능 비교

| **단계**            | **평균 응답시간** | **95% 응답시간** | **요청당 쿼리** | **총 요청 수** | **총 쿼리 수** | **개선율**        |
| ------------------- | ----------------- | ---------------- | --------------- | -------------- | -------------- | ----------------- |
| **1. JOIN+GROUP**   | 54.41ms           | 110.23ms         | 2개             | 161건          | 322개          | -                 |
| **2. N+1 서브쿼리** | 99.44ms           | 448.17ms         | 52개            | 125건          | 6,500개        | **-82.8% (악화)** |
| **3. 배치 처리**    | 43.02ms           | 77.18ms          | 3개             | 174건          | 522개          | **+20.9% (개선)** |
| **4. Load (10VU)**  | 84.56ms           | 167.89ms         | 3개             | 3928건         | 11,784개       | -                 |

### ramping-vus에서 처리량 변화 원리

**핵심 원리**: 응답시간이 개선되면 처리량이 자연스럽게 증가, 응답시간이 느려지면 처리량이 감소!

```
평균 VU: (Ramp-up + Peak + Ramp-down) / Total = 0.83 VU

1단계 (54.41ms):
30,000ms / (54.41ms + 100ms) × 0.83 = 161건 ✅

2단계 (99.44ms):
30,000ms / (99.44ms + 100ms) × 0.83 = 125건 ✅

3단계 (43.02ms):
30,000ms / (43.02ms + 100ms) × 0.83 = 174건 ✅

→ 응답시간과 처리량의 정확한 상관관계!
```

### k6 Output 해석

```bash
# 실제 k6 출력 예시 (3단계 배치 처리)
running (30.1s), 0/1 VUs, 174 complete and 0 interrupted iterations
smoke ✓ [======================================] 0/1 VUs  30s

     checks.........................: 100.00% ✓ 522       ✗ 0
     db_query_count.................: 522     17.340532/s
     filtering_response_time........: avg=43.02ms min=25.11ms med=38.50ms max=147.67ms p(95)=77.18ms
     http_reqs......................: 174     5.780177/s
     iteration_duration.............: avg=143.15ms min=125.22ms med=138.61ms max=247.78ms
```

**해석**:

- ✅ `iterations: 174`: 30초 동안 174회 완료 (ramping-vus 효과)
- ✅ `db_query_count: 522`: 총 DB 쿼리 수 (174건 × 3개)
- ✅ `http_req_duration avg=43.02ms`: 평균 응답시간 43.02ms
- ✅ `http_reqs: 174 (5.78/s)`: 초당 평균 5.78건 처리
- ✅ `iteration_duration avg=143.15ms`: 1회 반복 평균 시간 (응답 43.02ms + sleep 100ms)
- ✅ `checks: 100.00%`: 모든 검증 통과 (174건 × 3 checks = 522)

---

## 트러블슈팅 연계

### 트러블슈팅 문서

본 k6 시나리오는 다음 트러블슈팅 문서와 연계되어 있습니다:

**📄 [데이터셋 필터링 성능 최적화 트러블슈팅](../troubleshooting/dataset-filtering-performance-optimization-troubleshooting.md)**

### 트러블슈팅 문서에서 다루는 내용

1. **문제 발견 및 초기 분석**

   - fetchJoin + paging 문제 발견
   - 성능 개선 필요성 인지

2. **1단계: JOIN + GROUP BY (기준선)**

   - 기존 방식 성능 측정 (avg=54.41ms)
   - fetchJoin + paging 메모리 처리 문제

3. **2단계: N+1 서브쿼리 (문제점 드러내기)**

   - N+1 문제 발생 (avg=99.44ms, 82.8% 증가)
   - 쿼리 수 폭증 (52개/요청)

4. **3단계: 배치 처리 (최종 최적화)**

   - N+1 해결 (avg=43.02ms, 56.7% 개선 vs 2단계)
   - 쿼리 수 고정 (3개/요청)

5. **Load 테스트 (확장성 검증)**

   - 3928회 요청 100% 성공
   - 65.39 RPS 안정적 처리

6. **논리적 일관성 검증**
   - 쿼리 수와 응답시간의 상관관계
   - ramping-vus 처리량 변화 원리
   - 1단계 vs 3단계 쿼리 수 비교

### k6 시나리오와 트러블슈팅의 일치성

| **항목**         | **k6 시나리오**                   | **트러블슈팅 문서**               | **일치 여부** |
| ---------------- | --------------------------------- | --------------------------------- | ------------- |
| Executor         | `ramping-vus`                     | `ramping-vus`                     | ✅            |
| Smoke 설정       | 0→1→0, 30s                        | 0→1→0, 30s                        | ✅            |
| Load 설정        | 0→10→0, 60s                       | 0→10→0, 60s                       | ✅            |
| API 엔드포인트   | `GET /api/v1/datasets/filter?...` | `GET /api/v1/datasets/filter?...` | ✅            |
| Sleep 시간       | 0.1초                             | 0.1초                             | ✅            |
| 1단계 결과       | avg=54.41ms, 161건, 2개 쿼리      | avg=54.41ms, 161건, 2개 쿼리      | ✅            |
| 2단계 결과       | avg=99.44ms, 125건, 52개 쿼리     | avg=99.44ms, 125건, 52개 쿼리     | ✅            |
| 3단계 결과       | avg=43.02ms, 174건, 3개 쿼리      | avg=43.02ms, 174건, 3개 쿼리      | ✅            |
| Load 테스트 결과 | avg=84.56ms, 3928건               | avg=84.56ms, 3928건               | ✅            |

---

## 주의사항

1. **테스트 순서**:

   - 반드시 Stage 1 → Stage 2 → Stage 3 순서로 테스트
   - 각 단계마다 서버 재시작 또는 브랜치 전환 필요

2. **데이터 일관성**:

   - 전후 비교를 위해 동일한 데이터베이스 상태 유지
   - 테스트 중 데이터 변경 최소화

3. **쿼리 수 측정**:

   - `db_query_count` 메트릭으로 총 쿼리 수 추적
   - 요청당 쿼리 수: Stage 1 (2개), Stage 2 (52개), Stage 3 (3개)

4. **서버 상태**:

   - JVM Warm-up을 위해 첫 테스트 결과는 제외
   - CPU, 메모리 사용률 모니터링

5. **네트워크 환경**:
   - 로컬 테스트 권장 (네트워크 지연 최소화)

---

## 문의 및 기여

- **트러블슈팅 문서**: `performance-test/dataset/troubleshooting/dataset-filtering-performance-optimization-troubleshooting.md`
- **실제 구현 코드**:
  - `src/main/java/com/dataracy/modules/dataset/adapter/query/SearchDataQueryDslAdapter.java`
  - `src/main/java/com/dataracy/modules/dataset/adapter/query/ReadDataQueryDslAdapter.java`

---

_이 시나리오는 실제 트러블슈팅 과정에서 사용된 테스트 설정을 문서화한 것이며, 모든 수치는 실제 k6 테스트 결과를 기반으로 작성되었습니다._
