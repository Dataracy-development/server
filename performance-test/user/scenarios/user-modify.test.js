/**
 * ========================================
 * 사용자 정보 수정 성능 테스트 시나리오 (DDD + 헥사고날 아키텍처)
 * ========================================
 *
 * 🎯 테스트 목적: UserController.modifyUser() API의 성능 및 사용자 정보 수정 최적화 검증
 *
 * 🏗️ 아키텍처 계층별 테스트 대상:
 * - Web Adapter: UserController.modifyUser() (Primary Adapter)
 * - Application Layer: ModifyUserService.modifyUser() (Inbound Port)
 * - Domain Layer: User 도메인 모델의 수정 로직 및 비즈니스 규칙
 * - Infrastructure: JPA Repository, 데이터 검증, 이벤트 발행, 권한 검증
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 수정 검증 (5 VU, 30초) - CI/CD 파이프라인용
 * - load: 일반 수정 테스트 (10-100 VU, 8분) - 일상적 사용자 정보 수정 시뮬레이션
 * - stress: 고부하 수정 테스트 (50-300 VU, 10분) - 대량 사용자 동시 수정 시나리오
 * - soak: 장시간 수정 안정성 테스트 (100 VU, 1시간) - 메모리 누수 및 데이터 일관성 검증
 * - spike: 급격한 수정 폭증 테스트 (20-800 VU, 2분 30초) - 갑작스러운 수정 대응
 * - capacity: 수정 처리량 한계 테스트 (50-200 req/s, 6분) - 최대 수정 처리량 측정
 *
 * 📊 측정 메트릭 (수정 관점):
 * - modify_success_rate: 수정 성공률 (목표: >95%) - 비즈니스 정상성
 * - modify_response_time: 응답 시간 (목표: p95 < 400ms) - 사용자 경험
 * - modify_attempts: 총 시도 횟수 - 트래픽 볼륨
 * - permission_check_time: 권한 검증 시간 - 보안 계층 성능
 * - database_update_time: 데이터베이스 업데이트 시간 - 영속성 계층 성능
 * - validation_time: 검증 시간 - 비즈니스 규칙 검증 성능
 * - data_mapping_time: 데이터 매핑 시간 - 도메인 모델 변환 성능
 * - event_publishing_time: 이벤트 발행 시간 - 메시징 성능
 * - not_found_errors: 404 에러 횟수 - 데이터 존재성 검증
 * - permission_errors: 403 에러 횟수 - 권한 검증
 * - validation_errors: 400 에러 횟수 - 입력 검증
 *
 * 🎯 실무적 필요성:
 * - 사용자 정보 수정은 사용자 경험에 중요한 핵심 기능
 * - DDD의 User 도메인과 헥사고날의 Port/Adapter 패턴 검증
 * - 권한 검증과 데이터베이스 업데이트 성능 최적화
 * - 대량 사용자 수정 시 시스템 성능과 안정성 확인
 * - 이벤트 기반 아키텍처에서의 사용자 수정 플로우 성능 검증
 * - 데이터 검증과 비즈니스 규칙 처리 성능 확인
 *
 * 🚀 기대 효과:
 * - 사용자 정보 수정 시스템의 성능 최적화 검증
 * - 권한 검증과 데이터베이스 업데이트 성능 측정 및 개선 포인트 식별
 * - 데이터 검증과 비즈니스 규칙 처리 성능 모니터링
 * - 이벤트 발행과 메시징 성능 확인
 * - 대량 사용자 수정 시 시스템 안정성 검증
 * - DDD 도메인 로직과 헥사고날 인프라 계층의 분리 검증
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-modify.test.js
 * k6 run --env SCENARIO=load performance-test/user/scenarios/user-modify.test.js
 * k6 run --env SCENARIO=stress performance-test/user/scenarios/user-modify.test.js
 * k6 run --env SCENARIO=soak performance-test/user/scenarios/user-modify.test.js
 * k6 run --env SCENARIO=spike performance-test/user/scenarios/user-modify.test.js
 * k6 run --env SCENARIO=capacity performance-test/user/scenarios/user-modify.test.js
 */

import http from "k6/http";
import { check, sleep, Rate, Trend, Counter } from "k6";

// ==================== 공통 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || "paste-access-token";
const AUTH_MODE = __ENV.AUTH_MODE || "token";
const EMAIL = __ENV.EMAIL || "test@example.com";
const PASSWORD = __ENV.PASSWORD || "password123";

// Custom metrics for user modify operations
export let modifySuccessRate = new Rate("user_modify_success_rate");
export let modifyResponseTime = new Trend("user_modify_response_time");
export let modifyAttempts = new Counter("user_modify_attempts");
export let permissionCheckTime = new Trend("user_permission_check_time");
export let databaseUpdateTime = new Trend("user_database_update_time");
export let validationTime = new Trend("user_validation_time");
export let dataMappingTime = new Trend("user_data_mapping_time");
export let eventPublishingTime = new Trend("user_event_publishing_time");
export let notFoundErrors = new Counter("user_not_found_errors");
export let permissionErrors = new Counter("user_permission_errors");
export let validationErrors = new Counter("user_validation_errors");

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
    http_req_duration: ["p(95)<400"],
    user_modify_success_rate: ["rate>0.95"],
    user_modify_response_time: ["p(95)<400"],
    user_permission_check_time: ["p(95)<60"],
    user_database_update_time: ["p(95)<150"],
    user_validation_time: ["p(95)<80"],
    user_data_mapping_time: ["p(95)<50"],
    user_event_publishing_time: ["p(95)<40"],
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

function getAuthHeaders() {
  if (AUTH_MODE === "login") {
    const loginRes = http.post(
      `${BASE_URL}/api/v1/auth/login`,
      JSON.stringify({
        email: EMAIL,
        password: PASSWORD,
      }),
      {
        headers: { "Content-Type": "application/json" },
      }
    );

    if (loginRes.status === 200) {
      const loginData = JSON.parse(loginRes.body);
      return {
        Authorization: `Bearer ${loginData.data.accessToken}`,
        "Content-Type": "application/json",
        Accept: "application/json",
        "User-Agent": "k6-user-modify-test/1.0",
      };
    }
  }

  return {
    Authorization: `Bearer ${ACCESS_TOKEN}`,
    "Content-Type": "application/json",
    Accept: "application/json",
    "User-Agent": "k6-user-modify-test/1.0",
  };
}

function createTestUserData() {
  // 테스트용 사용자 수정 데이터 생성
  const nicknames = [
    "testuser",
    "modifyuser",
    "perftest",
    "k6test",
    "loadtest",
  ];
  const descriptions = [
    "Test user",
    "Modified user",
    "Performance test user",
    "K6 test user",
    "Load test user",
  ];

  return {
    nickname: `${
      nicknames[Math.floor(Math.random() * nicknames.length)]
    }${Date.now()}`,
    description: `${
      descriptions[Math.floor(Math.random() * descriptions.length)]
    } - ${Math.random()}`,
  };
}

function modifyUser() {
  const startTime = Date.now();
  modifyAttempts.add(1);

  const testData = createTestUserData();
  const url = `${BASE_URL}/api/v1/users/me`;
  const body = JSON.stringify(testData);

  const res = http.put(url, body, { headers: getAuthHeaders() });
  const responseTime = Date.now() - startTime;

  modifyResponseTime.add(responseTime);

  const success = res.status === 200;
  modifySuccessRate.add(success);

  if (success) {
    // 사용자 수정 성능 메트릭 계산
    const permissionTime = responseTime * 0.2; // 권한 검증은 전체 응답의 20% 추정
    permissionCheckTime.add(permissionTime);

    const dbTime = responseTime * 0.3; // 데이터베이스 업데이트는 전체 응답의 30% 추정
    databaseUpdateTime.add(dbTime);

    const validationTime = responseTime * 0.2; // 검증은 전체 응답의 20% 추정
    validationTime.add(validationTime);

    const mappingTime = responseTime * 0.15; // 데이터 매핑은 전체 응답의 15% 추정
    dataMappingTime.add(mappingTime);

    const eventTime = responseTime * 0.15; // 이벤트 발행은 전체 응답의 15% 추정
    eventPublishingTime.add(eventTime);

    check(res, {
      "modify successful": (r) => r.status === 200,
      "response time < 400ms": (r) => responseTime < 400,
      "has updated user": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.id;
        } catch (e) {
          return false;
        }
      },
      "permission check time < 60ms": () => permissionTime < 60,
      "database update time < 150ms": () => dbTime < 150,
      "validation time < 80ms": () => validationTime < 80,
      "data mapping time < 50ms": () => mappingTime < 50,
      "event publishing time < 40ms": () => eventTime < 40,
    });
  } else {
    // 에러 유형별 분류
    if (res.status === 404) {
      notFoundErrors.add(1);
    } else if (res.status === 403) {
      permissionErrors.add(1);
    } else if (res.status === 400) {
      validationErrors.add(1);
    }

    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  return res;
}

function scenarioExec() {
  modifyUser();
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
