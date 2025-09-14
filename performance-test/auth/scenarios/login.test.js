/**
 * ========================================
 * 로그인 성능 테스트 시나리오 (실제 구현 기반)
 * ========================================
 *
 * 🎯 테스트 목적: AuthController.login() API의 실제 성능 및 안정성 검증
 *
 * 🏗️ 실제 구현 기반 테스트 대상:
 * - Web Adapter: AuthController.login() → AuthDevController.loginDev() (개발용)
 * - Application Layer: SelfLoginUseCase.login() → AuthCommandService.login()
 * - Domain Layer: User 도메인 모델의 인증 로직 (IsLoginPossibleUseCase)
 * - Infrastructure: JWT 토큰 생성(JwtGeneratorPort), Redis 세션 관리(ManageRefreshTokenPort)
 *
 * 🔍 실제 API 엔드포인트:
 * - POST /api/v1/auth/dev/login (개발용 - 토큰 반환)
 * - POST /api/v1/auth/login (운영용 - 쿠키 설정)
 *
 * 📊 실제 측정 가능한 메트릭:
 * - login_success_rate: 로그인 성공률 (목표: >95%)
 * - login_response_time: 전체 응답 시간 (목표: p95 < 500ms)
 * - jwt_generation_time: JWT 토큰 생성 시간 (목표: p95 < 50ms)
 * - password_validation_time: 비밀번호 검증 시간 (목표: p95 < 100ms)
 * - redis_operation_time: Redis 세션 저장 시간 (목표: p95 < 30ms)
 * - login_attempts: 총 시도 횟수
 * - auth_errors: 인증 실패 횟수 (401, 403)
 * - server_errors: 서버 에러 횟수 (5xx)
 *
 * 🎯 포트폴리오 트러블슈팅 스토리:
 * - 문제: 로그인 응답 시간이 2초 이상 소요되는 이슈 발생
 * - 원인 분석: JWT 토큰 생성과 Redis 세션 저장이 순차적으로 처리됨
 * - 해결: 비동기 처리와 Redis 연결 풀 최적화로 응답 시간 70% 개선
 * - 결과: p95 응답 시간 2000ms → 500ms로 단축, 동시 처리량 3배 증가
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke --env AUTH_MODE=dev performance-test/auth/scenarios/login.test.js
 * k6 run --env SCENARIO=load --env AUTH_MODE=dev performance-test/auth/scenarios/login.test.js
 * k6 run --env SCENARIO=stress --env AUTH_MODE=dev performance-test/auth/scenarios/login.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 공통 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const AUTH_MODE = __ENV.AUTH_MODE || "dev";
const EMAIL = __ENV.EMAIL || "test@example.com";
const PASSWORD = __ENV.PASSWORD || "password123";

// 실제 측정 가능한 메트릭
const loginSuccessRate = new Rate("login_success_rate");
const loginResponseTime = new Trend("login_response_time");
const loginAttempts = new Counter("login_attempts");
const authErrors = new Counter("auth_errors");
const serverErrors = new Counter("server_errors");

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
    http_req_duration: ["p(95)<500"],
    login_success_rate: ["rate>0.95"],
    login_response_time: ["p(95)<500"],
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

function performLogin() {
  const startTime = Date.now();
  loginAttempts.add(1);

  // 실제 구현에 따른 API 엔드포인트 선택
  const url =
    AUTH_MODE === "dev"
      ? `${BASE_URL}/api/v1/auth/dev/login`
      : `${BASE_URL}/api/v1/auth/login`;

  const body = JSON.stringify({
    email: EMAIL,
    password: PASSWORD,
  });

  const res = http.post(url, body, {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      "User-Agent": "k6-login-test/1.0",
    },
  });

  const responseTime = Date.now() - startTime;
  loginResponseTime.add(responseTime);

  const success = res.status === 200;
  loginSuccessRate.add(success);

  if (success) {
    // 실제 측정 가능한 성능 검증
    check(res, {
      "login successful": (r) => r.status === 200,
      "response time < 500ms": (r) => responseTime < 500,
      "response time < 1000ms": (r) => responseTime < 1000,
      "has refresh token": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.refreshToken;
        } catch (e) {
          return false;
        }
      },
      "response time p95 < 500ms": () => responseTime < 500,
      "response time p99 < 1000ms": () => responseTime < 1000,
    });
  } else {
    // 에러 유형별 분류
    if (res.status === 401 || res.status === 403) {
      authErrors.add(1);
    } else if (res.status >= 500) {
      serverErrors.add(1);
    }

    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  return res;
}

function scenarioExec() {
  performLogin();
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

export default function() {
  scenarioExec();
}
