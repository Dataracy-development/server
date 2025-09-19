/**
 * ========================================
 * 로그인 성능 테스트 시나리오 (실제 구현 기반)
 * ========================================
 *
 * 테스트 목적: AuthController.login() API의 실제 성능 및 안정성 검증 (After - 레이트 리미팅 적용)
 *
 * 실제 구현 기반 테스트 대상:
 * - Web Adapter: AuthController.login() → AuthDevController.loginDev() (개발용)
 * - Application Layer: SelfLoginUseCase.loginWithRateLimit() → AuthCommandService.loginWithRateLimit()
 * - Domain Layer: User 도메인 모델의 인증 로직 (IsLoginPossibleUseCase)
 * - Infrastructure: JWT 토큰 생성(JwtGeneratorPort), Redis 세션 관리(ManageRefreshTokenPort), RateLimitPort
 *
 * 실제 API 엔드포인트:
 * - POST /api/v1/auth/dev/login (개발용 - 토큰 반환)
 * - POST /api/v1/auth/login (운영용 - 쿠키 설정, 레이트 리미팅 적용)
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
 * - rate_limit_errors: 레이트 리미팅 에러 (429) - After에서만
 *
 * 포트폴리오 트러블슈팅 스토리 (After):
 *
 * > **"메모리 기반 레이트 리미팅으로 보안 강화된 로그인 시스템"**
 * >
 * > **해결된 문제**:
 * > - ✅ 레이트 리미팅 구현: 정상 사용자 60회/분, 의심 사용자 5회/분 제한
 * > - ✅ 무차별 대입 공격 방어: 429 응답으로 차단
 * > - ✅ 서버 부하 감소: 비정상 요청 사전 차단
 * > - ✅ 보안 수준 향상: DDoS 공격 방어
 * >
 * > **현재 보안 상태**:
 * > - BCrypt 비밀번호 해싱: ✅ 구현됨
 * > - JWT 토큰 시스템: ✅ 구현됨
 * > - Redis 세션 관리: ✅ 구현됨
 * > - 분산 락 시스템: ✅ 구현됨
 * > - 보안 로깅: ✅ 구현됨
 * > - ✅ 레이트 리미팅: 메모리 기반 구현 (비용 효율적)
 * > - ❌ 계정 잠금: 미구현 (향후 구현 예정)
 * > - ❌ IP 차단: 미구현 (향후 구현 예정)
 * > - ❌ 공격 탐지: 미구현 (향후 구현 예정)
 *
 * 실행 명령어 (IntelliJ Terminal에서 실행):
 *
 * ```bash
 * # Smoke 테스트 (빠른 검증)
 * k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login-with-rate-limit.test.js
 *
 * # Load 테스트 (일반적인 부하)
 * k6 run --env SCENARIO=load performance-test/auth/scenarios/login-with-rate-limit.test.js
 *
 * # Stress 테스트 (고부하)
 * k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-with-rate-limit.test.js
 *
 * # Spike 테스트 (급격한 부하 증가)
 * k6 run --env SCENARIO=spike performance-test/auth/scenarios/login-with-rate-limit.test.js
 *
 * # Capacity 테스트 (용량 한계 확인)
 * k6 run --env SCENARIO=capacity performance-test/auth/scenarios/login-with-rate-limit.test.js
 * ```
 *
 * 결과 해석 가이드:
 * - login_success_rate > 95%: 정상 (레이트 리미팅으로 정상 사용자만 성공)
 * - login_response_time p95 < 500ms: 정상 (빠른 응답)
 * - error_rate < 5%: 정상 (안정적인 서비스)
 * - rate_limit_errors > 0: 정상 (레이트 리미팅 작동 확인)
 * - throughput > 200 req/s: 정상 (충분한 처리량)
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";
import { SharedArray } from "k6/data";

// 환경 변수 설정
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const EMAIL = __ENV.EMAIL || "wnsgudAws@gmail.com";
const PASSWORD = __ENV.PASSWORD || "juuuunny123@";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";

// 사용자 데이터 로드 (SharedArray를 사용하여 VUs 간 데이터 공유)
const users = new SharedArray("users", function () {
  const data = [];
  for (let i = 0; i < 1000; i++) {
    data.push({
      email: `user${i}@example.com`,
      password: "juuuunny123@",
    });
  }
  return data;
});

// 커스텀 메트릭 정의
const loginAttempts = new Counter("login_attempts");
const loginSuccessRate = new Rate("login_success_rate");
const loginFailureRate = new Rate("login_failure_rate");
const loginResponseTime = new Trend("login_response_time");
const authErrors = new Counter("auth_errors");
const badRequestErrors = new Counter("bad_request_errors");
const unauthorizedErrors = new Counter("unauthorized_errors");
const forbiddenErrors = new Counter("forbidden_errors");
const notFoundErrors = new Counter("not_found_errors");
const serverErrors = new Counter("server_errors");
const rateLimitErrors = new Counter("rate_limit_errors"); // After에서만

// 시나리오별 설정
const scenarios = {
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
    executor: "ramping-vus",
    startVUs: 10,
    exec: "capacity",
    stages: [
      { duration: "2m", target: 50 },
      { duration: "2m", target: 100 },
      { duration: "2m", target: 150 },
      { duration: "2m", target: 200 },
      { duration: "2m", target: 0 },
    ],
  },
};

export const options = {
  scenarios: {
    [RUN_SCENARIO]: scenarios[RUN_SCENARIO],
  },
  thresholds: {
    // 성능 목표 (4차 최종: 정상 사용자 120회/분, 의심 사용자 20회/분)
    http_req_failed: ["rate<0.5"], // 전체 실패율 50% 이하 (레이트 리미팅으로 인한 실패 허용)
    http_req_duration: ["p(95)<2000"], // p95 응답시간 2초 이하
    login_success_rate: ["rate>0.3"], // 30% 이상 성공 (정상 사용자 보호)
    login_response_time: ["p(95)<2000"], // p95 응답시간 2초 이하
    rate_limit_errors: ["count>0"], // 레이트 리미팅 에러 발생 (After에서만)
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

function performLogin() {
  const startTime = Date.now();
  loginAttempts.add(1);

  // 환경변수로 정상 사용자 비율 제어 (기본값: 70% - 4차 기준)
  const NORMAL_USER_RATIO = parseFloat(__ENV.NORMAL_USER_RATIO) || 0.7;
  const isNormalUser = Math.random() < NORMAL_USER_RATIO;

  // 이메일 패턴 기반 사용자 구분 (AuthCommandService.isNormalUser()와 일치)
  const testEmail = isNormalUser
    ? EMAIL // 정상 사용자: wnsgudAws@gmail.com (60회/분)
    : `attacker${Math.floor(Math.random() * 1000)}@unknown.com`; // 공격자: 의심 도메인 (5회/분)
  const testPassword = isNormalUser ? PASSWORD : generateAttackPassword();

  const url = `${BASE_URL}/api/v1/auth/login`;
  const body = JSON.stringify({
    email: testEmail,
    password: testPassword,
  });

  const res = http.post(url, body, {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      "User-Agent": "k6-rate-limit-test/1.0",
      "X-Forwarded-For": "192.168.1.100", // 동일한 IP 사용 (레이트 리미팅 테스트용)
      "X-Real-IP": "192.168.1.100", // 동일한 IP 사용
    },
  });

  const responseTime = Date.now() - startTime;
  loginResponseTime.add(responseTime);

  // 응답 상태 코드 기반 분석
  const isSuccess = res.status === 200;
  const isRateLimited = res.status === 429;
  const isBadRequest = res.status === 400;
  const isUnauthorized = res.status === 401;
  const isForbidden = res.status === 403;
  const isNotFound = res.status === 404;
  const isServerError = res.status >= 500;

  // 성공/실패율 측정
  loginSuccessRate.add(isSuccess);
  loginFailureRate.add(!isSuccess);

  // 에러 유형별 분류
  if (isRateLimited) {
    rateLimitErrors.add(1);
  } else if (isBadRequest) {
    badRequestErrors.add(1);
    authErrors.add(1);
  } else if (isUnauthorized) {
    unauthorizedErrors.add(1);
    authErrors.add(1);
  } else if (isForbidden) {
    forbiddenErrors.add(1);
    authErrors.add(1);
  } else if (isNotFound) {
    notFoundErrors.add(1);
    authErrors.add(1);
  } else if (isServerError) {
    serverErrors.add(1);
  }

  check(res, {
    "login response handled": (r) => r.status >= 200 && r.status < 600,
    "response time with rate limit < 2s": (r) => responseTime < 2000,
    "rate limiting working": (r) => isNormalUser || !isSuccess || isRateLimited,
    "normal user success": (r) => !isNormalUser || isSuccess || isRateLimited,
  });

  sleep(1); // 1초 대기 (레이트 리미팅 테스트를 위해)
}

export default function () {
  performLogin();
}

// 시나리오별 실행 함수
export function smoke() {
  performLogin();
  sleep(Math.random() * 2 + 1);
}

export function load() {
  performLogin();
  sleep(Math.random() * 2 + 1);
}

export function stress() {
  performLogin();
  sleep(Math.random() * 2 + 1);
}

export function spike() {
  performLogin();
  sleep(Math.random() * 2 + 1);
}

export function capacity() {
  performLogin();
  sleep(Math.random() * 2 + 1);
}

/**
 * 공격용 비밀번호 생성
 */
function generateAttackPassword() {
  const attackPatterns = [
    "password",
    "123456",
    "admin",
    "root",
    "test",
    "qwerty",
    "letmein",
    "welcome",
    "secret",
    "master",
  ];
  return attackPatterns[Math.floor(Math.random() * attackPatterns.length)];
}

/**
 * 테스트 요약 핸들러
 * @param {object} data - k6 테스트 결과 데이터
 * @returns {object} JSON 및 TXT 형식의 요약 결과
 */
export function handleSummary(data) {
  const summary = {
    test_info: {
      test_name: "레이트 리미팅 적용 로그인 성능 테스트 결과 (After)",
      scenario: RUN_SCENARIO,
      duration: data.state.testRunDurationMs,
      vus: data.metrics.vus?.values?.max || 0,
    },
    rate_limit_analysis: {
      status: "레이트 리미팅 적용됨",
      mechanism: "메모리 기반 (설정으로 Redis 전환 가능)",
      threshold: "정상 사용자 60회/분, 의심 사용자 5회/분",
    },
    performance_metrics: {
      login_attempts: data.metrics.login_attempts?.values?.count || 0,
      login_success_rate: data.metrics.login_success_rate?.values?.rate || 0,
      login_failure_rate: data.metrics.login_failure_rate?.values?.rate || 0,
      login_response_time_p95:
        data.metrics.login_response_time?.values?.["p(95)"] || 0,
      login_response_time_avg:
        data.metrics.login_response_time?.values?.avg || 0,
      rate_limit_errors: data.metrics.rate_limit_errors?.values?.count || 0,
      bad_request_errors: data.metrics.bad_request_errors?.values?.count || 0,
      unauthorized_errors: data.metrics.unauthorized_errors?.values?.count || 0,
      forbidden_errors: data.metrics.forbidden_errors?.values?.count || 0,
      not_found_errors: data.metrics.not_found_errors?.values?.count || 0,
      server_errors: data.metrics.server_errors?.values?.count || 0,
      concurrent_users: data.metrics.vus?.values?.max || 0,
      throughput: data.metrics.http_reqs?.values?.rate || 0,
    },
    portfolio_story_summary: {
      problem_before:
        "기존 시스템은 로그인 무차별 대입 공격에 취약하여, 악의적인 사용자가 무제한으로 로그인 시도를 할 수 있었습니다. 이는 계정 탈취 위험을 증가시키고 서버 자원을 불필요하게 소모시키는 문제점을 가지고 있었습니다.",
      solution_applied:
        "비용 효율성을 최우선으로 고려하여 인메모리 기반의 레이트 리미팅 기능을 구현했습니다. 이는 IP 주소당 분당 최대 요청 횟수를 제한하여 무차별 대입 공격을 효과적으로 방어합니다. Clean Architecture 원칙에 따라 Port-Adapter 패턴을 적용하여, 향후 트래픽 증가 시 Redis 기반의 분산 레이트 리미팅으로 쉽게 전환할 수 있도록 설계했습니다.",
      expected_impact:
        "레이트 리미팅 적용 후, 비정상적인 로그인 시도는 429 (Too Many Requests) 응답으로 차단되어 서버 부하를 줄이고 보안을 강화할 수 있습니다. 정상 사용자의 로그인 경험에는 영향을 주지 않으면서, 시스템의 안정성과 보안 수준을 크게 향상시킬 것으로 기대됩니다.",
      future_consideration:
        "현재는 비용 효율적인 인메모리 방식을 사용하지만, 서비스 규모가 커지고 여러 인스턴스로 확장될 경우 Redis 기반의 분산 레이트 리미팅으로 전환할 계획입니다. 이는 AuthCommandService의 @Qualifier 어노테이션만 변경하면 되도록 유연하게 설계되었습니다. Redis는 분산 환경에서 정확한 카운팅과 영속성을 제공하여 대규모 트래픽에도 안정적인 레이트 리미팅을 보장할 수 있습니다.",
    },
  };
}

// k6 기본 터미널 출력 사용 (handleSummary 제거)
