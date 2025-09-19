# 공통 테스트 구조 가이드 (실제 구현 기반)

## 📋 표준 테스트 파일 구조

모든 성능 테스트 파일은 실제 Java 구현 코드를 기반으로 다음 구조를 따라야 합니다:

### 1. 파일 헤더 (주석)

```javascript
/**
 * ========================================
 * [기능명] 성능 테스트 시나리오 (실제 구현 기반)
 * ========================================
 *
 * 🎯 테스트 목적: [실제 API명]의 실제 성능 및 [기능] 최적화 검증
 *
 * 🏗️ 실제 구현 기반 테스트 대상:
 * - Web Adapter: [실제 Controller명]
 * - Application Layer: [실제 Service명]
 * - Domain Layer: [도메인 모델명] 도메인 로직
 * - Infrastructure: [실제 사용 기술들]
 *
 * 🔍 실제 API 엔드포인트:
 * - [HTTP Method] [실제 엔드포인트]
 * - [Request/Response 구조]
 *
 * 📊 실제 측정 가능한 메트릭:
 * - [메트릭명]: [설명] (목표: [기준값])
 * - [메트릭명]: [설명] (목표: [기준값])
 * - ...
 *
 * 🎯 포트폴리오 트러블슈팅 스토리:
 * - 문제: [실제 발생한 문제]
 * - 원인 분석: [문제 원인]
 * - 해결: [적용한 해결책]
 * - 결과: [개선 결과]
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/[도메인]/scenarios/[파일명].test.js
 * k6 run --env SCENARIO=load performance-test/[도메인]/scenarios/[파일명].test.js
 * k6 run --env SCENARIO=stress performance-test/[도메인]/scenarios/[파일명].test.js
 * k6 run --env SCENARIO=soak performance-test/[도메인]/scenarios/[파일명].test.js
 * k6 run --env SCENARIO=spike performance-test/[도메인]/scenarios/[파일명].test.js
 */
```

### 2. Import 및 설정

```javascript
import http from "k6/http";
import { check, sleep, Rate, Trend, Counter } from "k6";

// ==================== 공통 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const [도메인별_설정] = __ENV.[도메인별_설정] || "[기본값]";

// 실제 구현 기반 메트릭
export let [기능]_success_rate = new Rate("[기능]_success_rate");
export let [기능]_response_time = new Trend("[기능]_response_time");
export let [기능]_attempts = new Counter("[기능]_attempts");
export let [기능]_errors = new Counter("[기능]_errors");
export let server_errors = new Counter("server_errors");
```

### 3. 시나리오 설정

```javascript
export let options = {
  scenarios: {
    smoke: {
      executor: "constant-vus",
      vus: 5,
      duration: "30s",
      exec: "smoke",
    },
    load: {
      executor: "ramping-vus",
      startVUs: 10,
      exec: "load",
      stages: [
        { duration: "2m", target: 50 },
        { duration: "4m", target: 100 },
        { duration: "2m", target: 0 },
      ],
    },
    stress: {
      executor: "ramping-vus",
      startVUs: 20,
      exec: "stress",
      stages: [
        { duration: "2m", target: 100 },
        { duration: "3m", target: 200 },
        { duration: "3m", target: 300 },
        { duration: "2m", target: 0 },
      ],
    },
    soak: {
      executor: "constant-vus",
      vus: 100,
      duration: "1h",
      exec: "soak",
    },
    spike: {
      executor: "ramping-vus",
      startVUs: 20,
      exec: "spike",
      stages: [
        { duration: "15s", target: 400 },
        { duration: "2m", target: 800 },
        { duration: "15s", target: 0 },
      ],
    },
    capacity: {
      executor: "ramping-arrival-rate",
      startRate: 50,
      timeUnit: "1s",
      preAllocatedVUs: 100,
      maxVUs: 1000,
      exec: "capacity",
      stages: [
        { target: 100, duration: "2m" },
        { target: 200, duration: "2m" },
        { target: 0, duration: "2m" },
      ],
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<[목표값]"],
    [기능]_success_rate: ["rate>0.95"],
    [기능]_response_time: ["p(95)<[목표값]"],
    // 도메인별 특화 메트릭
  },
};
```

### 4. 메인 함수

```javascript
function perform[기능]() {
  const startTime = Date.now();
  [기능]_attempts.add(1);

  const url = `${BASE_URL}[실제_엔드포인트]`;
  const [요청_데이터] = [요청_구조];

  const res = http.[HTTP_METHOD](url, [요청_데이터], {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      "User-Agent": "k6-[기능]-test/1.0",
      // 인증이 필요한 경우
      "Authorization": `Bearer ${ACCESS_TOKEN}`,
    },
  });

  const responseTime = Date.now() - startTime;
  [기능]_response_time.add(responseTime);

  const success = res.status === 200;
  [기능]_success_rate.add(success);

  if (success) {
    // 성공 시 세부 메트릭 측정
    check(res, {
      "[기능] successful": (r) => r.status === 200,
      "response time < [목표값]ms": (r) => responseTime < [목표값],
      // 도메인별 검증 로직
    });
  } else {
    // 에러 분류
    if (res.status >= 500) {
      server_errors.add(1);
    } else {
      [기능]_errors.add(1);
    }

    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  return res;
}
```

### 5. 시나리오 실행 함수

```javascript
function scenarioExec() {
  perform[기능]();
  sleep(Math.random() * 2 + 1);
}

export function smoke() {
  scenarioExec();
}
export function load() {
  scenarioExec();
}
export function stress() {
  scenarioExec();
}
export function soak() {
  scenarioExec();
}
export function spike() {
  scenarioExec();
}
export function capacity() {
  scenarioExec();
}
```

## 📊 표준 메트릭 명명 규칙

### 공통 메트릭

- `[기능]_success_rate`: 성공률 (Rate)
- `[기능]_response_time`: 응답 시간 (Trend)
- `[기능]_attempts`: 총 시도 횟수 (Counter)
- `[기능]_errors`: 에러 횟수 (Counter)
- `server_errors`: 서버 에러 횟수 (Counter)

### 도메인별 특화 메트릭

- **Auth**: `login_success_rate`, `login_response_time`, `login_attempts`, `auth_errors`, `bad_request_errors`, `unauthorized_errors`, `forbidden_errors`, `not_found_errors`, `concurrent_users`, `throughput`, `error_rate`
- **Project**: `file_processing_time`, `s3_upload_time`, `metadata_processing_time`
- **Dataset**: `file_processing_time`, `s3_upload_time`, `thumbnail_processing_time`
- **Like**: `distributed_lock_acquisition_time`, `hotspot_conflicts`
- **Comment**: `query_execution_time`, `pagination_processing_time`
- **User**: `email_validation_time`, `password_hashing_time`, `duplicate_check_time`

## 🎯 포트폴리오 트러블슈팅 스토리 템플릿

각 테스트 파일에는 다음 형식의 트러블슈팅 스토리가 포함되어야 합니다:

```
🎯 포트폴리오 트러블슈팅 스토리:
- 문제: [실제 발생한 구체적인 문제 상황]
- 원인 분석: [문제의 근본 원인 분석]
- 해결: [적용한 구체적인 해결책]
- 결과: [정량적 개선 결과]

⚠️ 주의사항:
- 클라이언트 측 k6에서 측정 가능한 메트릭만 사용
- 서버 내부 처리 시간은 측정 불가능하므로 제외
- 실제 코드 구현을 기반으로 한 현실적인 스토리 작성
```

## 📝 체크리스트

- [ ] 실제 API 엔드포인트 사용
- [ ] 실제 구현 기반 메트릭 측정
- [ ] 포트폴리오 트러블슈팅 스토리 포함
- [ ] 표준 메트릭 명명 규칙 준수
- [ ] 적절한 임계값 설정
- [ ] 에러 처리 로직 포함
- [ ] 성능 검증 로직 포함
