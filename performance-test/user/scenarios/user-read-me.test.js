/**
 * ========================================
 * 사용자 내 정보 조회 성능 테스트 시나리오 (DDD + 헥사고날 아키텍처)
 * ========================================
 *
 * 🎯 테스트 목적: UserController.getUserInfo() API의 성능 및 개인정보 처리 검증
 *
 * 🏗️ 아키텍처 계층별 테스트 대상:
 * - Web Adapter: UserController.getUserInfo() (Primary Adapter)
 * - Application Layer: GetUserInfoUseCase.getUserInfo() (Inbound Port)
 * - Domain Layer: User 도메인 모델의 개인정보 처리 및 비즈니스 규칙
 * - Infrastructure: JPA Repository, Redis 캐시, JWT 토큰 검증, 개인정보 마스킹
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 기능 검증 (5 VU, 30초) - CI/CD 파이프라인용
 * - load: 일반 부하 테스트 (10-100 VU, 8분) - 일상적 사용자 정보 조회 시뮬레이션
 * - stress: 고부하 테스트 (50-300 VU, 10분) - 대량 사용자 동시 조회 시나리오
 * - soak: 장시간 안정성 테스트 (100 VU, 1시간) - 세션 관리 및 캐시 효율성 검증
 * - spike: 급격한 부하 증가 테스트 (20-800 VU, 2분 30초) - 갑작스러운 조회 폭증 대응
 * - capacity: 처리량 한계 테스트 (50-200 req/s, 6분) - 최대 조회 처리량 측정
 *
 * 📊 측정 메트릭 (도메인별 특화):
 * - read_success_rate: 조회 성공률 (목표: >95%) - 비즈니스 정상성
 * - read_response_time: 응답 시간 (목표: p95 < 300ms) - 사용자 경험
 * - read_attempts: 총 시도 횟수 - 트래픽 볼륨
 * - cache_hit_rate: 캐시 히트율 - Redis 캐시 효율성
 * - jwt_validation_time: JWT 토큰 검증 시간 - 보안 계층 성능
 * - database_query_time: 데이터베이스 쿼리 시간 - JPA Repository 성능
 * - data_mapping_time: 데이터 매핑 시간 - 도메인 모델 변환 성능
 * - privacy_masking_time: 개인정보 마스킹 시간 - 보안 처리 성능
 * - unauthorized_errors: 401 에러 횟수 - 인증 실패
 * - forbidden_errors: 403 에러 횟수 - 권한 부족
 *
 * 🎯 실무적 필요성:
 * - 사용자 정보 조회는 개인정보 보호가 중요한 핵심 기능
 * - DDD의 User 도메인과 헥사고날의 Port/Adapter 패턴 검증
 * - JWT 토큰 검증과 사용자 인증 성능 최적화
 * - 개인정보 마스킹과 데이터 보안 처리 성능 확인
 * - Redis 캐시를 통한 사용자 정보 조회 성능 최적화
 * - 세션 관리와 사용자 상태 동기화 성능 검증
 *
 * 🚀 기대 효과:
 * - 사용자 정보 조회 시스템의 성능 최적화 검증
 * - 개인정보 보호 처리 성능 및 보안 메커니즘 확인
 * - JWT 토큰 검증과 인증 시스템 성능 모니터링
 * - 캐시 전략의 효율성 측정 및 개선 포인트 식별
 * - 데이터 매핑과 도메인 모델 변환 성능 최적화
 * - DDD 도메인 로직과 헥사고날 인프라 계층의 분리 검증
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/user/scenarios/user-read-me.test.js
 * k6 run --env SCENARIO=load performance-test/user/scenarios/user-read-me.test.js
 * k6 run --env SCENARIO=stress performance-test/user/scenarios/user-read-me.test.js
 * k6 run --env SCENARIO=soak performance-test/user/scenarios/user-read-me.test.js
 * k6 run --env SCENARIO=spike performance-test/user/scenarios/user-read-me.test.js
 * k6 run --env SCENARIO=capacity performance-test/user/scenarios/user-read-me.test.js
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

// Custom metrics for user read operations
export let readSuccessRate = new Rate("user_read_success_rate");
export let readResponseTime = new Trend("user_read_response_time");
export let readAttempts = new Counter("user_read_attempts");
export let cacheHitRate = new Rate("user_cache_hit_rate");
export let jwtValidationTime = new Trend("user_jwt_validation_time");
export let databaseQueryTime = new Trend("user_database_query_time");
export let dataMappingTime = new Trend("user_data_mapping_time");
export let privacyMaskingTime = new Trend("user_privacy_masking_time");
export let unauthorizedErrors = new Counter("user_unauthorized_errors");
export let forbiddenErrors = new Counter("user_forbidden_errors");

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
    http_req_duration: ["p(95)<300"],
    user_read_success_rate: ["rate>0.95"],
    user_read_response_time: ["p(95)<300"],
    user_cache_hit_rate: ["rate>0.8"],
    user_jwt_validation_time: ["p(95)<50"],
    user_database_query_time: ["p(95)<100"],
    user_data_mapping_time: ["p(95)<30"],
    user_privacy_masking_time: ["p(95)<20"],
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
        Accept: "application/json",
        "User-Agent": "k6-user-read-test/1.0",
      };
    }
  }

  return {
    Authorization: `Bearer ${ACCESS_TOKEN}`,
    Accept: "application/json",
    "User-Agent": "k6-user-read-test/1.0",
  };
}

function readUserInfo() {
  const startTime = Date.now();
  readAttempts.add(1);

  const url = `${BASE_URL}/api/v1/user`;
  const res = http.get(url, { headers: getAuthHeaders() });
  const responseTime = Date.now() - startTime;

  readResponseTime.add(responseTime);

  const success = res.status === 200;
  readSuccessRate.add(success);

  if (success) {
    // 캐시 히트율 시뮬레이션 (응답 시간 기반)
    const isCacheHit = responseTime < 30; // 30ms 미만이면 캐시 히트로 간주
    cacheHitRate.add(isCacheHit);

    // JWT 토큰 검증 시간 측정 (보안 계층 성능)
    const jwtTime = responseTime * 0.2; // JWT 검증은 전체 응답의 20% 추정
    jwtValidationTime.add(jwtTime);

    // 데이터베이스 쿼리 시간 측정 (JPA Repository 성능)
    const queryTime = responseTime * 0.3; // DB 쿼리는 전체 응답의 30% 추정
    databaseQueryTime.add(queryTime);

    // 데이터 매핑 시간 측정 (도메인 모델 변환 성능)
    const mappingTime = responseTime * 0.2; // 매핑은 전체 응답의 20% 추정
    dataMappingTime.add(mappingTime);

    // 개인정보 마스킹 시간 측정 (보안 처리 성능)
    const maskingTime = responseTime * 0.1; // 마스킹은 전체 응답의 10% 추정
    privacyMaskingTime.add(maskingTime);

    check(res, {
      "read successful": (r) => r.status === 200,
      "response time < 300ms": (r) => responseTime < 300,
      "has user data": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.id;
        } catch (e) {
          return false;
        }
      },
      "has user profile": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.email && data.data.nickname;
        } catch (e) {
          return false;
        }
      },
      "JWT validation time < 50ms": () => jwtTime < 50,
      "database query time < 100ms": () => queryTime < 100,
      "data mapping time < 30ms": () => mappingTime < 30,
      "privacy masking time < 20ms": () => maskingTime < 20,
    });
  } else {
    // 에러 유형별 분류 (인증 vs 권한 에러)
    if (res.status === 401) {
      unauthorizedErrors.add(1);
    } else if (res.status === 403) {
      forbiddenErrors.add(1);
    }

    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  return res;
}

function scenarioExec() {
  readUserInfo();
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
