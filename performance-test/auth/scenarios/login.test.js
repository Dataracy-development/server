/**
 * ========================================
 * 로그인 성능 테스트 시나리오 (실제 구현 기반)
 * ========================================
 *
 * 테스트 목적: AuthController.login() API의 실제 성능 및 안정성 검증 (Before - 레이트 리미팅 없음)
 *
 * 실제 구현 기반 테스트 대상:
 * - Web Adapter: AuthController.login() → AuthDevController.loginDev() (개발용)
 * - Application Layer: SelfLoginUseCase.login() → AuthCommandService.login()
 * - Domain Layer: User 도메인 모델의 인증 로직 (IsLoginPossibleUseCase)
 * - Infrastructure: JWT 토큰 생성(JwtGeneratorPort), Redis 세션 관리(ManageRefreshTokenPort)
 *
 * 실제 API 엔드포인트:
 * - POST /api/v1/auth/dev/login (개발용 - 토큰 반환)
 * - POST /api/v1/auth/login (운영용 - 쿠키 설정)
 *
 * 실제 측정 가능한 메트릭:
 * - login_success_rate: 로그인 성공률 (목표: >95%)
 * - login_response_time: 전체 응답 시간 (목표: p95 < 500ms)
 * - login_attempts: 총 시도 횟수
 * - auth_errors: 인증 실패 횟수 (400, 401, 403, 404)
 * - server_errors: 서버 에러 횟수 (5xx)
 * - concurrent_users: 동시 사용자 수
 * - throughput: 초당 처리 요청 수 (목표: >200 req/s)
 * - error_rate: 에러 발생률 (목표: <5%)
 *
 * 포트폴리오 트러블슈팅 스토리 (Before):
 *
 * > **"레이트 리미팅 없이 무차별 대입 공격에 취약한 로그인 시스템"**
 * >
 * > **문제 상황**:
 * > - 레이트 리미팅이 없어 무차별 대입 공격에 취약
 * > - 무제한 로그인 시도로 인한 서버 부하
 * > - DDoS 공격에 대한 방어 메커니즘 부족
 * > - 보안 사고 발생 가능성 높음
 * >
 * > **현재 보안 상태**:
 * > - BCrypt 비밀번호 해싱: ✅ 구현됨
 * > - JWT 토큰 시스템: ✅ 구현됨
 * > - Redis 세션 관리: ✅ 구현됨
 * > - 분산 락 시스템: ✅ 구현됨
 * > - 보안 로깅: ✅ 구현됨
 * > - ❌ 레이트 리미팅: 미구현 (주요 취약점)
 * > - ❌ 계정 잠금: 미구현
 * > - ❌ IP 차단: 미구현
 * > - ❌ 공격 탐지: 미구현
 *
 * 실행 명령어 (IntelliJ Terminal에서 실행):
 *
 * # 1. 기본 스모크 테스트 (5명, 30초)
 * k6 run --env SCENARIO=smoke --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js
 *
 * # 2. 로드 테스트 (10→100명, 8분)
 * k6 run --env SCENARIO=load --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js
 *
 * # 3. 스트레스 테스트 (20→300명, 8분)
 * k6 run --env SCENARIO=stress --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js
 *
 * # 4. 스파이크 테스트 (20→800명, 2.5분)
 * k6 run --env SCENARIO=spike --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js
 *
 * # 5. 용량 테스트 (50→200 req/s, 6분)
 * k6 run --env SCENARIO=capacity --env AUTH_MODE=dev --env BASE_URL=http://localhost:8080 performance-test/auth/scenarios/login.test.js
 *
 * # 6. 운영 환경 테스트 (쿠키 기반)
 * k6 run --env SCENARIO=load --env AUTH_MODE=prod --env BASE_URL=https://api.dataracy.com performance-test/auth/scenarios/login.test.js
 *
 * # 7. 커스텀 사용자 정보로 테스트
 * k6 run --env SCENARIO=load --env AUTH_MODE=dev --env EMAIL=test@example.com --env PASSWORD=testpass123 performance-test/auth/scenarios/login.test.js
 *
 * 결과 해석 가이드:
 * - login_success_rate > 0.95: 로그인 성공률 95% 이상
 * - login_response_time p95 < 500ms: 95% 요청이 500ms 이내 응답
 * - http_req_failed < 0.05: HTTP 요청 실패율 5% 미만
 * - throughput > 200: 초당 200개 이상 요청 처리
 * - error_rate < 0.05: 에러 발생률 5% 미만
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 공통 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const AUTH_MODE = __ENV.AUTH_MODE || "dev";
const EMAIL = __ENV.EMAIL || "wnsgudAws@gmail.com";
const PASSWORD = __ENV.PASSWORD || "juuuunny123@";

// 실제 측정 가능한 메트릭
const loginSuccessRate = new Rate("login_success_rate");
const loginResponseTime = new Trend("login_response_time");
const loginAttempts = new Counter("login_attempts");
const authErrors = new Counter("auth_errors");
const serverErrors = new Counter("server_errors");
const badRequestErrors = new Counter("bad_request_errors"); // 400
const unauthorizedErrors = new Counter("unauthorized_errors"); // 401
const forbiddenErrors = new Counter("forbidden_errors"); // 403
const notFoundErrors = new Counter("not_found_errors"); // 404
const concurrentUsers = new Counter("concurrent_users");
const errorRate = new Rate("error_rate");

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
    // 성능 목표 (1차 - Rate Limiting 없음, 100% 정상 사용자)
    http_req_failed: ["rate<0.01"], // 전체 실패율 1% 이하
    http_req_duration: ["p(95)<500"], // p95 응답시간 500ms 이하
    login_success_rate: ["rate>0.99"], // 99% 이상 성공 (100% 정상 사용자)
    login_response_time: ["p(95)<500"], // p95 응답시간 500ms 이하
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

function performLogin() {
  const startTime = Date.now();
  loginAttempts.add(1);
  concurrentUsers.add(1);

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

  // 메트릭 기록
  loginResponseTime.add(responseTime);

  const success = res.status === 200;
  loginSuccessRate.add(success);
  errorRate.add(!success);

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
    // 에러 유형별 분류 (실제 구현 기반)
    if (res.status === 400) {
      badRequestErrors.add(1);
      authErrors.add(1);
    } else if (res.status === 401) {
      unauthorizedErrors.add(1);
      authErrors.add(1);
    } else if (res.status === 403) {
      forbiddenErrors.add(1);
      authErrors.add(1);
    } else if (res.status === 404) {
      notFoundErrors.add(1);
      authErrors.add(1);
    } else if (res.status >= 500) {
      serverErrors.add(1);
    }

    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  concurrentUsers.add(-1);
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

// 포트폴리오 결과 해석을 위한 함수
// k6 기본 터미널 출력 사용 (handleSummary 제거)

export default function () {
  scenarioExec();
}
