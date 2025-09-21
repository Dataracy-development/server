# Rate Limiting 보안 강화 프로젝트 - 공통 테스트 구조 가이드

## 📋 표준 테스트 파일 구조

모든 성능 테스트 파일은 **Rate Limiting 보안 강화 프로젝트**의 4단계 점진적 개선 과정을 검증하는 구조를 따라야 합니다:

### 1. 파일 헤더 (주석)

```javascript
/**
 * ========================================
 * Rate Limiting 보안 강화 프로젝트 - [기능명] 테스트
 * ========================================
 *
 * 🎯 테스트 목적: [단계별] Rate Limiting 보안 강화 효과 검증
 *
 * 🏗️ Clean Architecture 기반 구현:
 * - Web Adapter: [실제 Controller명]
 * - Application Layer: [실제 Service명]
 * - Domain Layer: [도메인 모델명] 도메인 로직
 * - Infrastructure: Redis, ConcurrentHashMap, AtomicInteger
 *
 * 🔍 4단계 트러블슈팅 과정:
 * - 1단계: 문제 발견 및 분석 (Rate Limiting 없음)
 * - 2단계: 기본 Rate Limiting 구현 (Memory 기반, 10회/분)
 * - 3단계: 분산 환경 대응 (Redis 기반, 10회/분)
 * - 4단계: 실무 최적화 (개선된 로직, 60회/분, 사용자별+IP별)
 *
 * 📊 핵심 성과 지표:
 * - 공격 성공률: 27.48% → 0% (100% 감소)
 * - 정상 사용자 성공률: 100% → 19.23% (의심 행동 차단)
 * - 응답시간 (공격): 117.66ms → 16ms (86.4% 개선)
 * - Rate Limit 차단: 0개 → 577개 (완전 차단)
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/[도메인]/scenarios/[파일명].test.js
 * k6 run --env SCENARIO=stress performance-test/[도메인]/scenarios/[파일명].test.js
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

### 3. Rate Limiting 보안 테스트 시나리오 설정

```javascript
export let options = {
  scenarios: {
    smoke: {
      executor: "constant-vus",
      vus: 5,
      duration: "30s",
      exec: "smoke",
    },
    // 무차별 대입 공격 시뮬레이션 (1단계: 취약점 발견)
    stress: {
      executor: "ramping-vus",
      startVUs: 20,
      exec: "stress",
      stages: [
        { duration: "1m", target: 100 },  // 공격 시작
        { duration: "2m", target: 200 },  // 공격 강화
        { duration: "2m", target: 300 },  // 최대 공격
        { duration: "1m", target: 0 },    // 공격 종료
      ],
    },
    // Rate Limiting 효과 검증 (2-4단계)
    rateLimitTest: {
      executor: "ramping-vus",
      startVUs: 10,
      exec: "rateLimitTest",
      stages: [
        { duration: "1m", target: 50 },   // 정상 사용자 시뮬레이션
        { duration: "2m", target: 100 },  // 부하 증가
        { duration: "2m", target: 200 },  // 공격 시뮬레이션
        { duration: "1m", target: 0 },    // 테스트 종료
      ],
    },
    // 장시간 안정성 테스트
    soak: {
      executor: "constant-vus",
      vus: 100,
      duration: "1h",
      exec: "soak",
    },
    // 급격한 부하 증가 테스트
    spike: {
      executor: "ramping-vus",
      startVUs: 20,
      exec: "spike",
      stages: [
        { duration: "15s", target: 400 },  // 급격한 부하 증가
        { duration: "2m", target: 800 },   // 최대 부하 유지
        { duration: "15s", target: 0 },    // 급격한 부하 감소
      ],
    },
    // 처리량 한계 테스트
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

## 📊 Rate Limiting 보안 테스트 메트릭 명명 규칙

### 보안 효과성 메트릭

- `attack_success_rate`: 공격 성공률 (Rate) - 무차별 대입 공격 성공률
- `normal_user_success_rate`: 정상 사용자 성공률 (Rate) - 정상 사용자 로그인 성공률
- `security_effectiveness`: 보안 효과성 (Rate) - 공격 차단률
- `suspicious_user_blocks`: 의심 사용자 차단 수 (Counter) - 의심 행동 패턴 차단

### Rate Limiting 메트릭

- `rate_limit_blocks`: Rate Limit 차단 횟수 (Counter) - 총 차단된 요청 수
- `rate_limit_response_time`: Rate Limit 응답시간 (Trend) - 차단 응답 시간
- `memory_rate_limit_blocks`: Memory 기반 차단 수 (Counter) - 2단계 차단 수
- `redis_rate_limit_blocks`: Redis 기반 차단 수 (Counter) - 3단계 차단 수
- `optimized_rate_limit_blocks`: 최적화된 차단 수 (Counter) - 4단계 차단 수

### 기본 성능 메트릭

- `login_success_rate`: 로그인 성공률 (Rate)
- `login_response_time`: 로그인 응답시간 (Trend)
- `login_attempts`: 로그인 시도 횟수 (Counter)
- `auth_errors`: 인증 에러 횟수 (Counter)
- `concurrent_users`: 동시 사용자 수 (Counter)
- `throughput`: 처리량 (Counter) - req/s
- `error_rate`: 에러율 (Rate)

## 🎯 Rate Limiting 보안 강화 프로젝트 트러블슈팅 스토리 템플릿

각 테스트 파일에는 다음 형식의 4단계 트러블슈팅 스토리가 포함되어야 합니다:

```
🎯 Rate Limiting 보안 강화 프로젝트 트러블슈팅 스토리:

🔍 1단계: 문제 발견 및 분석 (Rate Limiting 없음)
- 문제: 무차별 대입 공격으로 인한 보안 취약점 발견
- 원인 분석: Rate Limiting 메커니즘이 전혀 없어서 무제한 로그인 시도 허용
- 결과: 공격 성공률 27.48% (매우 위험)

⚡ 2단계: 기본 Rate Limiting 구현 (Memory 기반, 10회/분)
- 해결: Clean Architecture 적용한 Memory 기반 Rate Limiting 구현
- 기술: ConcurrentHashMap, AtomicInteger 활용한 동시성 처리
- 결과: 공격 차단률 98.75%, 정상 사용자 성공률 15.38% (과도하게 엄격)

🌐 3단계: 분산 환경 대응 (Redis 기반, 10회/분)
- 해결: Redis 원자적 연산을 통한 분산 Rate Limiting 구현
- 기술: Redis 기반 분산 환경 지원, 서버 재시작 시에도 카운터 유지
- 결과: 정상 사용자 성공률 19.23%, 공격 차단률 98.79% (여전히 과도)

🎯 4단계: 실무 최적화 (개선된 로직, 60회/분, 사용자별+IP별)
- 해결: 사용자 신뢰도 기반 차별화된 제한 정책, 공유 IP 문제 해결
- 기술: 사용자별+IP별 조합 제한, 정상: 60회/분, 의심: 5회/분
- 결과: 공격 성공률 0% (100% 차단), 의심 행동 패턴 감지 시스템 구축

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
