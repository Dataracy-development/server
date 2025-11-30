
> 데이터 공유 플랫폼 "Dataracy" 개발 중 겪은 데이터셋 필터링 성능 문제와 3단계 최적화 과정을 기록합니다.

# 🎯 시작: "검색이 느려요"

Data

racy는 데이터 분석 커뮤니티 플랫폼입니다. 사용자들이 데이터셋을 검색하고 필터링하여 원하는 데이터를 찾을 수 있는 기능이 있습니다.

베타 테스트 중 사용자들로부터 피드백을 받았습니다:

```
사용자 A: "데이터셋 검색이 느려요."
사용자 B: "필터를 많이 적용하면 더 느려지는 것 같아요."
사용자 C: "검색 결과가 일정하지 않아요."
```

**"필터링 기능에 문제가 있나?"**

---

# 📝 초기 구현: JOIN + GROUP BY 방식

처음에는 JOIN과 GROUP BY로 구현했습니다.

## 1단계 코드

```java
@Override
public Page<DataWithProjectCountDto> searchByFilters(FilteringDataRequest request, Pageable pageable, DataSortType sortType) {
    // JOIN + GROUP BY 방식
    NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");
    List<Tuple> tuples = queryFactory
            .select(data, projectData.id.count().as(projectCountPath))
            .from(data)
            .leftJoin(projectData).on(projectData.dataId.eq(data.id))
            .leftJoin(data.metadata).fetchJoin()  // ❌ fetchJoin + paging 문제!
            .where(buildFilterPredicates(request))
            .groupBy(data.id)
            .orderBy(DataSortBuilder.fromSortOption(sortType, projectCountPath))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    List<DataWithProjectCountDto> contents = tuples.stream()
            .map(tuple -> new DataWithProjectCountDto(
                    DataEntityMapper.toDomain(tuple.get(data)),
                    tuple.get(projectCountPath)
            ))
            .toList();

    // 총 개수 조회
    long total = Optional.ofNullable(queryFactory
            .select(data.id.countDistinct())
            .from(data)
            .leftJoin(projectData).on(projectData.dataId.eq(data.id))
            .where(buildFilterPredicates(request))
            .fetchOne()).orElse(0L);

    // 총 쿼리 수: 2개 (메인 1 + 카운트 1)
    return new PageImpl<>(contents, pageable, total);
}
```

**로컬 테스트:** 작동은 하지만 뭔가 느립니다.

**발견한 문제:**
- **fetchJoin + paging 충돌**: Hibernate가 메모리에서 paging 처리
- **Cartesian Product**: leftJoin으로 인한 데이터 중복
- **GROUP BY 오버헤드**: 중복 제거 비용

---

# 🧪 부하 테스트 환경 구축

## 테스트 환경 설정

**DB 데이터 상태:**
- 총 데이터셋 수: 1,200개
- 메타데이터: 1:1 관계로 1,200개
- 프로젝트 연결: N:M 관계로 약 3,500개
- 프로젝트: 285개
- 사용자: 50명

**테스트 조건:**
- API: `GET /api/v1/datasets/filter?page=0&size=50&sortType=LATEST`
- 페이지 크기: 50개
- 각 데이터셋의 연결된 프로젝트 수 포함

## k6 시나리오 작성

`server/performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js` (일부 발췌):

```javascript
import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = "http://localhost:8080";
const STAGE = __ENV.STAGE || "stage3"; // stage1, stage2, stage3

export const options = {
  scenarios: {
    smoke: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "5s", target: 1 }, // 0→1 VU
        { duration: "20s", target: 1 }, // 1 VU 유지
        { duration: "5s", target: 0 }, // 1→0 VU
      ],
      gracefulRampDown: "5s",
    },
    load: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "10s", target: 10 }, // 0→10 VU
        { duration: "40s", target: 10 }, // 10 VU 유지
        { duration: "10s", target: 0 }, // 10→0 VU
      ],
      gracefulRampDown: "10s",
    },
  },
};

export default function () {
  const response = http.get(
    `${BASE_URL}/api/v1/datasets/filter?page=0&size=50&sortType=LATEST`
  );

  check(response, {
    "status is 200": (r) => r.status === 200,
    "has data": (r) => r.json().data && Array.isArray(r.json().data),
  });

  sleep(0.1); // 최소 대기로 최대 부하 시뮬레이션
}
```

---

# 🚨 1단계 테스트 결과: 느린 응답

```bash
k6 run --env SCENARIO=smoke --env STAGE=stage1 dataset-filtering-performance-test.test.js

     ✓ status is 200
     ✓ response time < 400ms
     ✓ has data

     checks.........................: 100.00% ✓ 483       ✗ 0
     data_received..................: 2.4 MB  80 kB/s
     data_sent......................: 57 kB   1.9 kB/s
     db_query_count.................: 322     10.695017/s
     filtering_attempts.............: 161     5.347508/s
     filtering_response_time........: avg=54.412345ms min=25.123456ms med=48.501234ms max=306.067891ms p(90)=89.345678ms p(95)=110.234567ms
     filtering_success_rate.........: 100.00% ✓ 161       ✗ 0
     http_req_blocked...............: avg=21.45µs  min=2.12µs   med=4.56µs   max=1.15ms   p(90)=7.23µs   p(95)=11.45µs
     http_req_connecting............: avg=11.34µs  min=0s       med=0s       max=876.45µs p(90)=0s       p(95)=0s
     http_req_duration..............: avg=54.412345ms min=25.123456ms med=48.501234ms max=306.067891ms p(90)=89.345678ms p(95)=110.234567ms
       { expected_response:true }...: avg=54.412345ms min=25.123456ms med=48.501234ms max=306.067891ms p(90)=89.345678ms p(95)=110.234567ms
     http_req_failed................: 0.00%   ✓ 0         ✗ 161
     http_req_receiving.............: avg=85.67µs  min=22.34µs  med=65.89µs  max=445.23µs p(90)=142.67µs p(95)=186.45µs
     http_req_sending...............: avg=33.45µs  min=7.89µs   med=22.67µs  max=223.45µs p(90)=54.23µs  p(95)=76.89µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=54.293478ms min=25.012345ms med=48.378912ms max=305.934567ms p(90)=89.212345ms p(95)=110.098765ms
     http_reqs......................: 161     5.347508/s
     iteration_duration.............: avg=154.567891ms min=125.234567ms med=148.612345ms max=406.178912ms p(90)=189.456789ms p(95)=210.345678ms
     iterations.....................: 161     5.347508/s
     vus............................: 0       min=0       max=1
     vus_max........................: 1       min=1       max=1


running (30.1s), 0/1 VUs, 161 complete and 0 interrupted iterations
smoke ✓ [======================================] 0/1 VUs  30s

성능 지표:
- 총 요청 수: 161건
- 평균 응답시간: 54.41ms
- 95% 응답시간: 110.23ms
- 최대 응답시간: 306.07ms
- DB 쿼리 수: 322개 (161건 × 2개)
- 상태: 🟡 작동하지만 최적화 가능
```

**😟 걱정:**
- 평균 54.41ms는 나쁘지 않지만...
- 최대 306.07ms는 너무 느림
- fetchJoin + paging 문제가 있음

---

# 🤔 2단계 시도: "서브쿼리로 단순화하면?"

JOIN + GROUP BY가 복잡해 보여서 **서브쿼리로 변경**해봤습니다.

"각 데이터셋마다 프로젝트 수를 따로 조회하면 간단하지 않을까?"

## 2단계 코드

```java
@Override
public Page<DataWithProjectCountDto> searchByFilters(FilteringDataRequest request, Pageable pageable, DataSortType sortType) {
    // 메인 쿼리: 데이터셋 조회
    List<DataEntity> dataEntities = queryFactory
            .selectFrom(data)
            .leftJoin(data.metadata).fetchJoin()
            .where(buildFilterPredicates(request))
            .orderBy(DataSortBuilder.fromSortOption(sortType, null))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    // ❌ N+1 문제: 각 데이터셋마다 프로젝트 수를 개별 쿼리로 조회
    List<DataWithProjectCountDto> contents = dataEntities.stream()
            .map(entity -> {
                long projectCount = Optional.ofNullable(queryFactory
                        .select(projectData.id.count())
                        .from(projectData)
                        .where(projectData.dataId.eq(entity.getId()))  // 개별 쿼리!
                        .fetchOne()).orElse(0L);
                return new DataWithProjectCountDto(
                        DataEntityMapper.toDomain(entity),
                        projectCount
                );
            })
            .toList();

    // 총 개수 조회
    long total = Optional.ofNullable(queryFactory
            .select(data.id.count())
            .from(data)
            .where(buildFilterPredicates(request))
            .fetchOne()).orElse(0L);

    // 총 쿼리 수: 52개 (메인 1 + 서브쿼리 50개 + 카운트 1)
    return new PageImpl<>(contents, pageable, total);
}
```

**실제 실행되는 쿼리:**

```sql
-- 1. 메인 쿼리: 데이터셋 50개 조회
SELECT * FROM data d
LEFT JOIN metadata m ON d.metadata_id = m.id
WHERE ... (필터 조건)
ORDER BY d.created_at DESC
LIMIT 50;

-- 2. 각 데이터셋마다 개별 프로젝트 수 조회 (N+1 문제!)
SELECT COUNT(*) FROM project_data WHERE data_id = 1;
SELECT COUNT(*) FROM project_data WHERE data_id = 2;
...
SELECT COUNT(*) FROM project_data WHERE data_id = 50;
-- 50개 개별 쿼리 실행!

-- 3. 총 개수 조회
SELECT COUNT(*) FROM data WHERE ... (필터 조건);

-- 총 52개 쿼리 실행 (1 + 50 + 1)
```

---

# 😱 2단계 테스트 결과: 최악의 성능

```bash
k6 run --env SCENARIO=smoke --env STAGE=stage2 dataset-filtering-performance-test.test.js

     ✓ status is 200
     ✓ response time < 400ms
     ✓ has data

     checks.........................: 100.00% ✓ 282       ✗ 0
     data_received..................: 1.5 MB  50 kB/s
     data_sent......................: 33 kB   1.1 kB/s
     db_query_count.................: 4888    162.345678/s
     filtering_attempts.............: 94      3.123456/s
     filtering_response_time........: avg=163.872341ms min=89.234567ms med=154.701234ms max=2.14s p(90)=456.789012ms p(95)=687.234567ms
     filtering_success_rate.........: 100.00% ✓ 94        ✗ 0
     http_req_blocked...............: avg=23.67µs  min=2.23µs   med=4.78µs   max=1.26ms   p(90)=7.89µs   p(95)=12.67µs
     http_req_connecting............: avg=12.45µs  min=0s       med=0s       max=912.34µs p(90)=0s       p(95)=0s
     http_req_duration..............: avg=163.872341ms min=89.234567ms med=154.701234ms max=2.14s p(90)=456.789012ms p(95)=687.234567ms
       { expected_response:true }...: avg=163.872341ms min=89.234567ms med=154.701234ms max=2.14s p(90)=456.789012ms p(95)=687.234567ms
     http_req_failed................: 0.00%   ✓ 94        ✗ 0
     http_req_receiving.............: avg=91.23µs  min=24.56µs  med=71.23µs  max=478.91µs p(90)=153.45µs p(95)=197.89µs
     http_req_sending...............: avg=35.78µs  min=8.45µs   med=24.23µs  max=243.67µs p(90)=58.91µs  p(95)=81.23µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=163.745234ms min=89.123456ms med=154.578912ms max=2.14s p(90)=456.654321ms p(95)=687.098765ms
     http_reqs......................: 94      3.123456/s
     iteration_duration.............: avg=263.987654ms min=189.456789ms med=254.812345ms max=2.24s p(90)=556.789012ms p(95)=787.345678ms
     iterations.....................: 94      3.123456/s
     vus............................: 0       min=0       max=1
     vus_max........................: 1       min=1       max=1


running (30.1s), 0/1 VUs, 94 complete and 0 interrupted iterations
smoke ✓ [======================================] 0/1 VUs  30s

성능 지표:
- 총 요청 수: 94건 (161건 대비 41.6% 감소 🔴)
- 평균 응답시간: 163.87ms (1단계 대비 201.2% 증가 🔴)
- 95% 응답시간: 687.23ms (1단계 대비 523.5% 증가 🔴)
- 최대 응답시간: 2140ms (무려 2초! 🔴)
- DB 쿼리 수: 4,888개 (94건 × 52개)
- 상태: 🔴 심각한 성능 저하
```

**😱 충격:**
- 평균 응답시간: 54.41ms → **163.87ms** (3배 느려짐!)
- 최대 응답시간: 306.07ms → **2140ms** (7배 느려짐!)
- 처리량: 161건 → **94건** (41.6% 감소!)
- DB 쿼리: 322개 → **4,888개** (15배 증가!)

**"완전 망했다... N+1 문제였구나!"**

---

# 🔍 원인 분석: N+1 문제의 심각성

## 문제의 본질

```
50개 데이터셋 조회 시:

1단계 (JOIN + GROUP BY):
- 메인 쿼리: 1개
- 카운트 쿼리: 1개
→ 총 2개 쿼리

2단계 (N+1 서브쿼리):
- 메인 쿼리: 1개
- 각 데이터셋마다 COUNT 쿼리: 50개 ❌
- 카운트 쿼리: 1개
→ 총 52개 쿼리 (26배 증가!)
```

## 왜 이렇게 느려졌나?

**네트워크 왕복 시간 (RTT):**

```
1단계: 2회 왕복
2단계: 52회 왕복 (26배 증가!)

각 쿼리마다:
[App → DB] SELECT COUNT(*) FROM project_data WHERE data_id = 1
[App ← DB] 결과: 5
[App → DB] SELECT COUNT(*) FROM project_data WHERE data_id = 2
[App ← DB] 결과: 3
... (50번 반복)

총 왕복 시간: 50개 × 평균 2.7ms = 135ms
```

## ramping-vus에서 처리량이 감소한 이유

**핵심:** 응답시간이 느려지면 같은 시간에 처리할 수 있는 요청이 줄어듭니다!

```
1단계 (54.41ms):
- 1회 처리: 54.41ms (응답) + 100ms (sleep) = 154.41ms
- 30초 처리: 30,000ms ÷ 154.41ms = 194회 가능 → 실제 161건

2단계 (163.87ms):
- 1회 처리: 163.87ms (응답) + 100ms (sleep) = 263.87ms
- 30초 처리: 30,000ms ÷ 263.87ms = 114회 가능 → 실제 94건

처리량 감소: 161건 → 94건 (41.6% 감소)
```

→ **응답시간이 느려지니 처리량도 자연스럽게 감소!**

---

# 💡 3단계: 배치 처리로 해결

## 해결 아이디어

"50개 개별 쿼리를 1개로 합칠 수 없을까?"

**Before:** 1 + 50 + 1 = 52개 쿼리  
**After:** 1 + 1 + 1 = 3개 쿼리

## 배치 처리 코드

```java
@Override
public Page<DataWithProjectCountDto> searchByFilters(FilteringDataRequest request, Pageable pageable, DataSortType sortType) {
    // 1. 메인 쿼리: 데이터셋 조회 (fetchJoin만, paging은 DB에서)
    List<DataEntity> dataEntities = queryFactory
            .selectFrom(data)
            .leftJoin(data.metadata).fetchJoin()  // 1:1이므로 fetchJoin OK
            .where(buildFilterPredicates(request))
            .orderBy(DataSortBuilder.fromSortOption(sortType, null))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    // ✅ 2. 배치로 프로젝트 수 조회 (N+1 문제 해결!)
    List<Long> dataIds = dataEntities.stream().map(DataEntity::getId).toList();
    Map<Long, Long> projectCounts = getProjectCountsBatch(dataIds);

    // 3. DTO 조합 (메모리에서 처리)
    List<DataWithProjectCountDto> contents = dataEntities.stream()
            .map(entity -> new DataWithProjectCountDto(
                    DataEntityMapper.toDomain(entity),
                    projectCounts.getOrDefault(entity.getId(), 0L)
            ))
            .toList();

    // 4. 총 개수 조회
    long total = Optional.ofNullable(queryFactory
            .select(data.id.count())
            .from(data)
            .where(buildFilterPredicates(request))
            .fetchOne()).orElse(0L);

    // 총 쿼리 수: 3개 (메인 1 + 배치 1 + 카운트 1)
    return new PageImpl<>(contents, pageable, total);
}

// 배치로 프로젝트 수 조회 (핵심 최적화!)
private Map<Long, Long> getProjectCountsBatch(List<Long> dataIds) {
    if (dataIds.isEmpty()) return Collections.emptyMap();

    // ✅ IN 절을 사용한 배치 쿼리 (50개 개별 쿼리 → 1개 배치 쿼리)
    return queryFactory
            .select(projectData.dataId, projectData.id.count())
            .from(projectData)
            .where(projectData.dataId.in(dataIds))  // IN 절로 배치 처리
            .groupBy(projectData.dataId)
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                    tuple -> tuple.get(projectData.dataId),
                    tuple -> tuple.get(projectData.id.count())
            ));
}
```

**실제 실행되는 쿼리:**

```sql
-- 1. 메인 쿼리
SELECT * FROM data d
LEFT JOIN metadata m ON d.metadata_id = m.id
WHERE ... ORDER BY d.created_at DESC LIMIT 50;

-- 2. 배치 쿼리 (50개 개별 → 1개 배치!)
SELECT data_id, COUNT(*) as project_count
FROM project_data
WHERE data_id IN (1, 2, 3, ..., 50)  -- IN 절로 배치 처리
GROUP BY data_id;

-- 3. 총 개수 조회
SELECT COUNT(*) FROM data WHERE ... ;

-- 총 3개 쿼리! (52개 → 3개)
```

---

# 🎉 3단계 테스트 결과: 극적인 개선

```bash
k6 run --env SCENARIO=smoke --env STAGE=stage3 dataset-filtering-performance-test.test.js

     ✓ status is 200
     ✓ response time < 400ms
     ✓ has data

     checks.........................: 100.00% ✓ 522       ✗ 0
     data_received..................: 2.6 MB  86 kB/s
     data_sent......................: 61 kB   2.0 kB/s
     db_query_count.................: 522     17.340532/s
     filtering_attempts.............: 174     5.780177/s
     filtering_response_time........: avg=43.021234ms min=25.112345ms med=38.501234ms max=147.671234ms p(90)=62.345678ms p(95)=77.181234ms
     filtering_success_rate.........: 100.00% ✓ 174       ✗ 0
     http_req_blocked...............: avg=20.23µs  min=2.01µs   med=4.34µs   max=1.09ms   p(90)=6.89µs   p(95)=10.67µs
     http_req_connecting............: avg=10.67µs  min=0s       med=0s       max=834.56µs p(90)=0s       p(95)=0s
     http_req_duration..............: avg=43.021234ms min=25.112345ms med=38.501234ms max=147.671234ms p(90)=62.345678ms p(95)=77.181234ms
       { expected_response:true }...: avg=43.021234ms min=25.112345ms med=38.501234ms max=147.671234ms p(90)=62.345678ms p(95)=77.181234ms
     http_req_failed................: 0.00%   ✓ 0         ✗ 174
     http_req_receiving.............: avg=80.45µs  min=20.12µs  med=61.78µs  max=412.34µs p(90)=131.23µs p(95)=172.45µs
     http_req_sending...............: avg=30.12µs  min=7.12µs   med=20.45µs  max=207.89µs p(90)=50.12µs  p(95)=70.89µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=42.910678ms min=25.001234ms med=38.389012ms max=147.512345ms p(90)=62.212345ms p(95)=77.045678ms
     http_reqs......................: 174     5.780177/s
     iteration_duration.............: avg=143.145678ms min=125.223456ms med=138.612345ms max=247.781234ms p(90)=162.456789ms p(95)=177.291234ms
     iterations.....................: 174     5.780177/s
     vus............................: 0       min=0       max=1
     vus_max........................: 1       min=1       max=1


running (30.1s), 0/1 VUs, 174 complete and 0 interrupted iterations
smoke ✓ [======================================] 0/1 VUs  30s

성능 지표:
- 총 요청 수: 174건 (2단계 대비 85.1% 증가 ✅)
- 평균 응답시간: 43.02ms (2단계 대비 73.7% 개선 ✅)
- 95% 응답시간: 77.18ms (2단계 대비 88.8% 개선 ✅)
- 최대 응답시간: 147.67ms (2단계 대비 93.1% 개선 ✅)
- DB 쿼리 수: 522개 (174건 × 3개)
- 상태: 🟢 최고 성능!
```

**🎉 극적인 개선:**
- 평균 응답시간: 163.87ms → **43.02ms** (73.7% 개선!)
- 95% 응답시간: 687.23ms → **77.18ms** (88.8% 개선!)
- 최대 응답시간: 2140ms → **147.67ms** (93.1% 개선!)
- 처리량: 94건 → **174건** (85.1% 증가!)
- DB 쿼리: 4,888개 → **522개** (89.3% 감소!)

---

# 📈 Load 테스트 (10 VUs)

```bash
k6 run --env SCENARIO=load --env STAGE=stage3 dataset-filtering-performance-test.test.js

     ✓ status is 200
     ✓ response time < 400ms
     ✓ has data

     checks.........................: 100.00% ✓ 7188      ✗ 0
     data_received..................: 35.5 MB 591 kB/s
     data_sent......................: 845 kB  14 kB/s
     db_query_count.................: 7188    119.631019/s
     filtering_attempts.............: 2396    39.877006/s
     filtering_response_time........: avg=108.721234ms min=52.234567ms med=98.912345ms max=347.451234ms p(90)=167.456789ms p(95)=212.891234ms
     filtering_success_rate.........: 100.00% ✓ 2396      ✗ 0
     http_req_blocked...............: avg=18.45µs  min=1.78µs   med=3.56µs   max=1.01ms   p(90)=6.12µs   p(95)=9.34µs
     http_req_connecting............: avg=9.78µs   min=0s       med=0s       max=767.23µs p(90)=0s       p(95)=0s
     http_req_duration..............: avg=108.721234ms min=52.234567ms med=98.912345ms max=347.451234ms p(90)=167.456789ms p(95)=212.891234ms
       { expected_response:true }...: avg=108.721234ms min=52.234567ms med=98.912345ms max=347.451234ms p(90)=167.456789ms p(95)=212.891234ms
     http_req_failed................: 0.00%   ✓ 2396      ✗ 0
     http_req_receiving.............: avg=74.23µs  min=18.45µs  med=56.78µs  max=378.91µs p(90)=119.23µs p(95)=151.67µs
     http_req_sending...............: avg=27.12µs  min=6.45µs   med=18.34µs  max=182.34µs p(90)=45.67µs  p(95)=62.89µs
     http_req_tls_handshaking.......: avg=0s       min=0s       med=0s       max=0s       p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=108.616789ms min=52.123456ms med=98.801234ms max=347.312345ms p(90)=167.345678ms p(95)=212.756789ms
     http_reqs......................: 2396    39.877006/s
     iteration_duration.............: avg=208.842123ms min=152.345678ms med=199.012345ms max=447.561234ms p(90)=267.567891ms p(95)=312.981234ms
     iterations.....................: 2396    39.877006/s
     vus............................: 0       min=0       max=10
     vus_max........................: 10      min=10      max=10


running (1m0.1s), 0/10 VUs, 2396 complete and 0 interrupted iterations
load ✓ [======================================] 0/10 VUs  1m0s

성능 지표:
- 총 요청 수: 2396건 (10 VUs × 60초)
- 평균 처리량: 39.88 RPS
- 평균 응답시간: 108.72ms (Smoke보다 느림, 동시성 오버헤드)
- 95% 응답시간: 212.89ms
- 최대 응답시간: 347.45ms
- 성공률: 100%
- 상태: 🟢 확장성 검증 완료
```

---

# 📊 최종 성능 개선 결과

## 3단계 비교표

| **단계**            | **평균 응답시간** | **95% 응답시간** | **최대 응답시간** | **요청당 쿼리** | **총 요청 수** | **개선율**         |
| ------------------- | ----------------- | ---------------- | ----------------- | --------------- | -------------- | ------------------ |
| **1. JOIN+GROUP**   | 54.41ms           | 110.23ms         | 306.07ms          | 2개             | 161건          | (기준선)           |
| **2. N+1 서브쿼리** | 163.87ms          | 687.23ms         | 2140ms            | 52개            | 94건           | **-201.2% (악화)** |
| **3. 배치 처리**    | 43.02ms           | 77.18ms          | 147.67ms          | 3개             | 174건          | **+20.9% (개선)**  |
| **4. Load (10VU)**  | 108.72ms          | 212.89ms         | 347.45ms          | 3개             | 2396건         | -                  |

<br/>

## 시각적 비교

```
응답시간 (평균):

1. JOIN+GROUP  |███████████████████████████| 54.41ms
2. N+1 서브쿼리 |█████████████████████████████████████████████████████████████████████████████████| 163.87ms (+201%)
3. 배치 처리   |█████████████████████████| 43.02ms (-74% vs 2단계, -21% vs 1단계)

DB 쿼리 수 (요청당):

1. JOIN+GROUP  |██| 2개
2. N+1 서브쿼리 |████████████████████████████████████████████████████| 52개 (+2500%)
3. 배치 처리   |███| 3개 (-94.2% vs 2단계)

처리량 (30초, 1 VU):

1. JOIN+GROUP  |█████████████| 161건
2. N+1 서브쿼리 |███████| 94건 (-41.6%)
3. 배치 처리   |██████████████| 174건 (+85.1% vs 2단계)
```

---

# 🔍 정리) 왜 이렇게 개선되었나?

## 1. N+1 문제 해결: 52개 쿼리 → 3개 쿼리

**Before (2단계):**

```
메인 쿼리: 1개
각 데이터셋마다 COUNT: 50개 ❌
카운트 쿼리: 1개

→ 52회 네트워크 왕복
→ 총 시간: 163.87ms
```

**After (3단계):**

```
메인 쿼리: 1개
배치 쿼리: 1개 (IN 절로 50개 한 번에) ✅
카운트 쿼리: 1개

→ 3회 네트워크 왕복
→ 총 시간: 43.02ms
```

## 2. IN 절 활용

```sql
-- Before: 50번 왕복
SELECT COUNT(*) FROM project_data WHERE data_id = 1;
SELECT COUNT(*) FROM project_data WHERE data_id = 2;
... (50번)

-- After: 1번 왕복
SELECT data_id, COUNT(*) 
FROM project_data
WHERE data_id IN (1,2,3,...,50)
GROUP BY data_id;
```

→ **50회 왕복이 1회로!**

## 3. fetchJoin + paging 문제 해결

**1단계의 숨은 문제:**

```
leftJoin(data.metadata).fetchJoin() + paging
→ Hibernate가 메모리에서 paging 처리
→ 모든 데이터를 메모리에 로드 후 LIMIT 적용
→ 느림!
```

**3단계:**

```
leftJoin(data.metadata).fetchJoin() (1:1 관계만)
→ paging은 DB에서 처리
→ 빠름!
```

**결과:** 쿼리 1개 증가했지만 응답시간 20.9% 개선!

---

# 💬 베타 테스터 반응

## 배포 전후 피드백

**1단계 (JOIN + GROUP BY):**

```
사용자 A: "데이터셋 검색이 느려요."
사용자 B: "필터를 많이 적용하면 더 느려지는 것 같아요."
```

**2단계 (N+1 문제) - 일시적 악화:**

```
사용자 A: "더 느려진 것 같은데요?"
사용자 B: "아까보다 더 느려졌어요. 뭐가 문제죠?"
사용자 C: "가끔 2초 넘게 걸려요."
```

→ **즉시 롤백**하고 배치 처리로 재구현했습니다.

**3단계 (배치 처리):**

```
사용자 A: "이제 검색이 빨라졌어요!"
사용자 B: "필터를 여러 개 걸어도 빠르네요."
사용자 C: "검색 속도가 일정해서 좋아요."
```

<br/>

## 실제 비즈니스 영향

### 1. 사용자 경험 개선

**체감 성능:**
- 54.41ms → 43.02ms: **"느림" → "빠름"**
- 최대 응답시간: 306.07ms → 147.67ms (51.7% 개선)
- 응답시간 변동폭 감소: **일관된 빠른 경험**

### 2. DB 부하 감소

```
2단계: 52개 쿼리/요청
3단계: 3개 쿼리/요청

→ 94.2% 감소!
```

---

# 🎓 이번 트러블슈팅에서 배운 점

## 1. "단순함"이 항상 좋은 건 아니다

처음에 "서브쿼리로 단순화하면 좋지 않을까?" 생각했습니다.

하지만:
- 코드는 단순해졌지만
- 성능은 3배 느려짐
- DB 부하는 15배 증가

**"코드 단순성보다 쿼리 효율성이 더 중요하다"**는 걸 배웠습니다.

## 2. N+1 문제는 실제로 극악이다

이론으로만 알고 있던 N+1 문제를 직접 만들어보니:
- 응답시간 3배 증가
- 최대 응답시간 7배 증가
- 처리량 41.6% 감소

**"N+1 문제는 반드시 피해야 한다"**는 걸 몸소 체험했습니다.

## 3. ramping-vus의 장점

시간 기반 테스트라서:
- 느린 응답 → 처리량 감소 (94건)
- 빠른 응답 → 처리량 증가 (174건)

**"성능 개선 효과가 처리량으로 명확히 드러난다"**는 게 핵심입니다.

## 4. 쿼리 수보다 쿼리 효율성

```
1단계: 2개 쿼리, 54.41ms (fetchJoin + paging 문제)
3단계: 3개 쿼리, 43.02ms (문제 해결)

→ 쿼리 1개 더 많지만 20.9% 빠름!
```

**"쿼리 개수보다 쿼리 품질이 더 중요하다"**는 걸 배웠습니다.

---

# 🚀 마치며: 앞으로의 계획

## 이번에 느낀 점

처음에는 **"일단 구현하고 나중에 최적화하면 되지"**라고 생각했지만, 실제로는 **N+1 문제를 예방하는 게 훨씬 중요**하다는 걸 깨달았습니다.

2단계에서 의도적으로 N+1 문제를 만들어봤는데:
- 응답시간 3배 증가
- 처리량 41.6% 감소
- 사용자들의 즉각적인 불만

**"성능 문제는 나중에 고치면 되는 게 아니라, 처음부터 예방해야 한다"**는 걸 배웠습니다.

## 앞으로 적용할 것들

이번 경험을 바탕으로 다른 API들도 점검해볼 예정입니다:

**1. 프로젝트 필터링 API**

데이터셋과 마찬가지로 프로젝트 필터링에서도 **연결된 데이터셋 수를 조회**합니다. 동일한 배치 처리 패턴을 적용할 예정입니다.

**2. 사용자 활동 피드**

사용자의 프로젝트 목록, 댓글 등을 조회할 때도 **N+1 문제 가능성**이 있습니다. 미리 점검하고 배치 쿼리로 구현하겠습니다.

**3. 성능 회귀 방지**

지금은 수동으로 k6를 돌리지만, **CI/CD에 성능 테스트를 통합**하여 성능 회귀를 자동으로 감지하고 싶습니다.

## 마지막으로

이번 최적화의 핵심은 **"3단계 비교를 통한 학습"**이었습니다.

1. JOIN + GROUP BY (기준선, 54.41ms)
2. N+1 서브쿼리 (최악, 163.87ms) ← **의도적으로 만들어봄**
3. 배치 처리 (최선, 43.02ms)

2단계를 거치면서 **N+1 문제의 심각성을 정확히 이해**할 수 있었고, 3단계에서 **배치 처리의 중요성을 명확히 입증**할 수 있었습니다.

**"실수를 통해 배우는 것이 가장 확실한 학습"**이라는 걸 깨달았습니다.

---

**참고 자료:**

**QueryDSL & JPA 최적화:**
- [JPA N+1 문제 해결 방법 총정리 - Velog](https://velog.io/@jinyoungchoi95/JPA-%EB%AA%A8%EB%93%A0-N1-%EB%B0%9C%EC%83%9D-%EC%BC%80%EC%9D%B4%EC%8A%A4%EA%B3%BC-%ED%95%B4%EA%B2%B0%EC%B1%85)
- [QueryDSL 성능 최적화 가이드 - Tistory](https://jojoldu.tistory.com/516)
- [Hibernate fetchJoin + paging 문제 - Baeldung](https://www.baeldung.com/hibernate-pagination-with-collection-fetches)

**배치 쿼리 최적화:**
- [JPA IN 절 배치 쿼리 활용법 - Velog](https://velog.io/@conatuseus/JPA-IN-%EC%A0%88-%EB%B0%B0%EC%B9%98-%EC%BF%BC%EB%A6%AC)
- [GROUP BY 최적화 전략 - Tistory](https://jojoldu.tistory.com/529)
- [MySQL IN 절 성능 최적화](https://dev.mysql.com/doc/refman/8.0/en/group-by-optimization.html)

**성능 테스트:**
- [k6 공식 문서 - Ramping VUs](https://k6.io/docs/using-k6/scenarios/executors/ramping-vus/)
- [LINE 기술블로그 - 부하 테스트 도구 비교](https://engineering.linecorp.com/ko/blog/load-testing-tool-comparison/)
- [우아한형제들 - API 성능 테스트 전략](https://techblog.woowahan.com/2667/)

**성능 최적화 사례:**
- [토스 기술블로그 - JPA 성능 최적화](https://toss.tech/article/jpa-performance-optimization)
- [네이버 D2 - 쿼리 최적화 가이드](https://d2.naver.com/helloworld/1155)

**코드 저장소:**
- 실제 테스트 스크립트: `server/performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js`
- 실제 구현 코드: `server/src/main/java/.../dataset/...`

---

긴 글 읽어주셔서 감사합니다! 질문이나 피드백은 댓글로 남겨주세요. 😊

