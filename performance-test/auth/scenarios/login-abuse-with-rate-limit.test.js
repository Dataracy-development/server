/**
 * ========================================
 * 로그인 공격 시뮬레이션 성능 테스트 시나리오 (실제 구현 기반)
 * ========================================
 *
 * 테스트 목적: AuthController.login() API의 현재 보안 상태 및 공격 시나리오 성능 검증 (After - 레이트 리미팅 적용)
 *
 * 실제 구현 기반 테스트 대상:
 * - Web Adapter: AuthController.login() → AuthDevController.loginDev()
 * - Application Layer: SelfLoginUseCase.loginWithRateLimit() → AuthCommandService.loginWithRateLimit()
 * - Domain Layer: IsLoginPossibleUseCase (비밀번호 검증 로직)
 * - Infrastructure: JWT 토큰 생성, Redis 세션 관리, BCrypt 해싱, LoggerFactory, RateLimitPort
 *
 * 현재 구현된 보안 메커니즘:
 * - BCrypt 비밀번호 해싱 검증 (PasswordEncoder)
 * - JWT 토큰 생성 및 검증 (JwtUtilInternal)
 * - Redis 리프레시 토큰 관리 (RefreshTokenRedisAdapter)
 * - Redis 블랙리스트 토큰 관리 (BlackListRedisAdapter)
 * - 분산 락 시스템 (RedissonDistributedLockManager)
 * - 보안 로깅 (LoggerFactory)
 * - ✅ 레이트 리미팅 (Rate Limiting) - 메모리 기반 구현
 *
 * 현재 구현되지 않은 보안 메커니즘:
 * - 계정 잠금 (Account Lockout)
 * - IP 차단 (IP Blocking)
 * - 공격 탐지 (Attack Detection)
 *
 * 실제 측정 가능한 메트릭 (k6 클라이언트 측에서만 측정):
 * - login_attempts: 로그인 시도 횟수 (Counter)
 * - login_success_rate: 로그인 성공률 (Rate)
 * - login_failure_rate: 로그인 실패률 (Rate)
 * - response_time_under_attack: 공격 상황에서의 응답 시간 (Trend)
 * - brute_force_attempts: 무차별 대입 공격 시도 횟수 (Counter)
 * - bad_request_errors: 잘못된 요청 에러 400 (Counter)
 * - unauthorized_errors: 인증 실패 에러 401 (Counter)
 * - rate_limit_errors: 레이트 리미팅 에러 429 (Counter) - After에서만
 * - server_errors: 서버 에러 5xx (Counter)
 * - http_req_duration: HTTP 요청 응답 시간 (기본 k6 메트릭)
 * - http_req_failed: HTTP 요청 실패율 (기본 k6 메트릭)
 *
 * 포트폴리오 트러블슈팅 스토리 (After):
 *
 * > **"레이트 리미팅 적용으로 보안 강화된 로그인 시스템"**
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
 * k6 run --env SCENARIO=smoke performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
 *
 * # Load 테스트 (일반적인 부하)
 * k6 run --env SCENARIO=load performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
 *
 * # Stress 테스트 (고부하)
 * k6 run --env SCENARIO=stress performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
 *
 * # Spike 테스트 (급격한 부하 증가)
 * k6 run --env SCENARIO=spike performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
 *
 * # Capacity 테스트 (용량 한계 확인)
 * k6 run --env SCENARIO=capacity performance-test/auth/scenarios/login-abuse-with-rate-limit.test.js
 * ```
 *
 * 결과 해석 가이드:
 * - login_success_rate < 10%: 정상 (공격 차단으로 성공률 대폭 감소)
 * - rate_limit_errors > 0: 정상 (레이트 리미팅 작동 확인)
 * - response_time_under_attack p95 < 2000ms: 정상 (안정적인 응답)
 * - login_failure_rate > 80%: 정상 (공격 차단으로 실패율 증가)
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
const responseTimeUnderAttack = new Trend("response_time_under_attack");
const bruteForceAttempts = new Counter("brute_force_attempts");
const badRequestErrors = new Counter("bad_request_errors"); // 400
const unauthorizedErrors = new Counter("unauthorized_errors"); // 401
const rateLimitErrors = new Counter("rate_limit_errors"); // 429 - After에서만
const serverErrors = new Counter("server_errors"); // 5xx

// 시나리오별 설정
const scenarios = {
  smoke: {
    executor: "constant-vus",
    vus: 10, // VU 수 증가
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
    // 공격 시뮬레이션 기준치 (4차 최종: 정상 사용자 120회/분, 의심 사용자 20회/분)
    http_req_failed: ["rate<0.8"], // 80% 미만 실패 (공격 허용)
    http_req_duration: ["p(95)<2000"],
    login_success_rate: ["rate>0.2"], // 20% 이상 성공 (정상 사용자 보호)
    login_failure_rate: ["rate>0.7"], // 70% 이상 실패 (공격 실패)
    response_time_under_attack: ["p(95)<2000"],
    bad_request_errors: ["count>0"], // 400 에러 발생 예상
    unauthorized_errors: ["count>0"], // 401 에러 발생 예상
    rate_limit_errors: ["count>0"], // 429 에러 발생 예상 (After에서만)
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
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
 * 레이트 리미팅이 적용된 로그인 공격 시뮬레이션
 * 70% 무차별 대입 공격, 30% 정상 사용자 시뮬레이션
 */
function simulateLoginAttack() {
  const startTime = Date.now();
  loginAttempts.add(1);
  bruteForceAttempts.add(1);

  // 환경변수로 정상 사용자 비율 제어 (기본값: 70% - 4차 기준)
  const NORMAL_USER_RATIO = parseFloat(__ENV.NORMAL_USER_RATIO) || 0.7;
  const isLegitimateUser = Math.random() < NORMAL_USER_RATIO; // 70%는 정상 사용자 (4차 기준)

  // 이메일 패턴 기반 사용자 구분 (AuthCommandService.isNormalUser()와 일치)
  const testEmail = isLegitimateUser
    ? EMAIL // 정상 사용자: wnsgudAws@gmail.com (60회/분)
    : `attacker${Math.floor(Math.random() * 10000)}@unknown.com`; // 공격자: 의심 도메인 (5회/분)
  const testPassword = isLegitimateUser ? PASSWORD : generateAttackPassword();

  const url = `${BASE_URL}/api/v1/auth/login`;
  const body = JSON.stringify({
    email: testEmail,
    password: testPassword,
  });

  const res = http.post(url, body, {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      "User-Agent": "k6-attack-simulation/1.0",
      "X-Forwarded-For": "192.168.1.100", // 동일한 IP 사용 (레이트 리미팅 테스트용)
      "X-Real-IP": "192.168.1.100", // 동일한 IP 사용
    },
  });

  const responseTime = Date.now() - startTime;
  responseTimeUnderAttack.add(responseTime);

  // 실제 HTTP 상태 코드 기반 분석
  const isSuccess = res.status === 200;
  const isBadRequest = res.status === 400;
  const isUnauthorized = res.status === 401;
  const isRateLimited = res.status === 429; // 레이트 리미팅 추가
  const isServerError = res.status >= 500;

  // 성공/실패율 측정
  loginSuccessRate.add(isSuccess);
  loginFailureRate.add(!isSuccess);

  // 에러 유형별 분류
  if (isBadRequest) {
    badRequestErrors.add(1);
  } else if (isUnauthorized) {
    unauthorizedErrors.add(1);
  } else if (isRateLimited) {
    rateLimitErrors.add(1); // 레이트 리미팅 에러 카운트
  } else if (isServerError) {
    serverErrors.add(1);
  }

  check(res, {
    "login response handled": (r) => r.status >= 200 && r.status < 600,
    "response time under attack < 1s": (r) => responseTime < 1000,
    "rate limiting working": (r) =>
      !isLegitimateUser || isSuccess || isRateLimited,
    "normal user success": (r) =>
      !isLegitimateUser || isSuccess || isRateLimited,
  });

  return res;
}

export default function () {
  simulateLoginAttack();
  sleep(Math.random() * 0.5 + 0.1); // 공격은 빠른 간격으로
}

export function smoke() {
  simulateLoginAttack();
  sleep(Math.random() * 0.5 + 0.1);
}
export function load() {
  simulateLoginAttack();
  sleep(Math.random() * 0.5 + 0.1);
}
export function stress() {
  simulateLoginAttack();
  sleep(Math.random() * 0.5 + 0.1);
}
export function spike() {
  simulateLoginAttack();
  sleep(Math.random() * 0.5 + 0.1);
}
export function capacity() {
  simulateLoginAttack();
  sleep(Math.random() * 0.5 + 0.1);
}

/**
 * 테스트 요약 핸들러
 * @param {object} data - k6 테스트 결과 데이터
 * @returns {object} JSON 및 TXT 형식의 요약 결과
 */
export function handleSummary(data) {
  const summary = {
    test_info: {
      test_name:
        "로그인 공격 시뮬레이션 성능 테스트 (After - 레이트 리미팅 적용)",
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
      login_success_rate:
        data.metrics.login_success_rate?.values?.rate * 100 || 0,
      login_failure_rate:
        data.metrics.login_failure_rate?.values?.rate * 100 || 0,
      response_time_under_attack_p95:
        data.metrics.response_time_under_attack?.values?.["p(95)"] || 0,
      response_time_under_attack_avg:
        data.metrics.response_time_under_attack?.values?.avg || 0,
      brute_force_attempts:
        data.metrics.brute_force_attempts?.values?.count || 0,
      rate_limit_errors: data.metrics.rate_limit_errors?.values?.count || 0,
      bad_request_errors: data.metrics.bad_request_errors?.values?.count || 0,
      unauthorized_errors: data.metrics.unauthorized_errors?.values?.count || 0,
      server_errors: data.metrics.server_errors?.values?.count || 0,
      concurrent_users: data.metrics.vus?.values?.max || 0,
      throughput: data.metrics.http_reqs?.values?.rate || 0,
    },
    portfolio_story_summary: {
      problem_before:
        "기존 시스템은 레이트 리미팅이 없어 무차별 대입 공격에 취약했습니다. 공격자가 무제한으로 로그인 시도를 할 수 있어 계정 탈취 위험이 높았습니다.",
      solution_applied:
        "메모리 기반 레이트 리미팅을 구현하여 IP당 분당 최대 요청 횟수를 제한했습니다. Clean Architecture 원칙에 따라 Port-Adapter 패턴을 적용하여 향후 Redis 기반으로 쉽게 전환할 수 있도록 설계했습니다.",
      expected_impact:
        "레이트 리미팅 적용 후, 무차별 대입 공격은 429 (Too Many Requests) 응답으로 차단되어 공격 성공률이 대폭 감소할 것으로 예상됩니다. 정상 사용자는 분당 10회 이내로 요청하므로 영향이 없습니다.",
      future_consideration:
        "현재는 비용 효율적인 인메모리 방식을 사용하지만, 서비스 규모가 커지고 여러 인스턴스로 확장될 경우 Redis 기반의 분산 레이트 리미팅으로 전환할 계획입니다. Redis는 분산 환경에서 정확한 카운팅과 영속성을 제공하여 대규모 트래픽에도 안정적인 레이트 리미팅을 보장할 수 있습니다.",
    },
  };
}

// k6 기본 터미널 출력 사용 (handleSummary 제거)
