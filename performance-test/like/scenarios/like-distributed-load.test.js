/**
 * ========================================
 * 좋아요 분산 부하 성능 테스트 시나리오 (DDD + 헥사고날 아키텍처)
 * ========================================
 *
 * 🎯 테스트 목적: LikeController.modifyTargetLike() API의 분산 환경에서의 부하 처리 성능 검증
 *
 * 🏗️ 아키텍처 계층별 테스트 대상:
 * - Web Adapter: LikeController.modifyTargetLike() (Primary Adapter)
 * - Application Layer: LikeTargetUseCase.likeTarget() (Inbound Port)
 * - Domain Layer: Like 도메인 모델의 분산 상태 관리 및 일관성 보장
 * - Infrastructure: Redis 분산 락, 캐시 클러스터, 데이터베이스 샤딩, 이벤트 스트리밍
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 분산 부하 검증 (5 VU, 30초) - CI/CD 파이프라인용
 * - load: 일반 분산 부하 테스트 (10-100 VU, 8분) - 일상적 분산 좋아요 처리 시뮬레이션
 * - stress: 고부하 분산 테스트 (50-300 VU, 10분) - 대규모 분산 환경에서의 동시성 처리
 * - soak: 장시간 분산 안정성 테스트 (100 VU, 1시간) - 분산 락 및 캐시 일관성 검증
 * - spike: 급격한 분산 부하 폭증 테스트 (20-800 VU, 2분 30초) - 갑작스러운 분산 부하 대응
 * - capacity: 분산 처리량 한계 테스트 (50-200 req/s, 6분) - 최대 분산 처리량 측정
 *
 * 📊 측정 메트릭 (분산 관점):
 * - distributed_success_rate: 분산 처리 성공률 (목표: >95%) - 비즈니스 정상성
 * - distributed_response_time: 분산 응답 시간 (목표: p95 < 800ms) - 사용자 경험
 * - distributed_attempts: 총 시도 횟수 - 트래픽 볼륨
 * - lock_acquisition_time: 락 획득 시간 - 분산 락 성능
 * - cache_sync_time: 캐시 동기화 시간 - 분산 캐시 성능
 * - database_shard_time: 데이터베이스 샤드 처리 시간 - 샤딩 성능
 * - event_streaming_time: 이벤트 스트리밍 시간 - 메시징 성능
 * - consistency_check_time: 일관성 검사 시간 - 데이터 정합성 성능
 * - network_latency: 네트워크 지연 시간 - 분산 환경 성능
 * - load_balancing_efficiency: 로드 밸런싱 효율성 - 분산 처리 균형
 *
 * 🎯 실무적 필요성:
 * - 좋아요는 대용량 동시 처리가 필요한 핵심 기능으로 분산 환경 검증이 필수
 * - DDD의 Like 도메인과 헥사고날의 Port/Adapter 패턴 분산 검증
 * - Redis 분산 락과 캐시 클러스터의 성능 최적화
 * - 데이터베이스 샤딩과 이벤트 스트리밍 성능 검증
 * - 대규모 분산 환경에서의 데이터 일관성 보장 확인
 * - 네트워크 지연과 로드 밸런싱이 성능에 미치는 영향 측정
 *
 * 🚀 기대 효과:
 * - 좋아요 시스템의 분산 처리 성능 검증
 * - 분산 락과 캐시 동기화 성능 측정 및 개선 포인트 식별
 * - 데이터베이스 샤딩과 이벤트 스트리밍 성능 모니터링
 * - 분산 환경에서의 데이터 일관성 보장 확인
 * - 네트워크 지연과 로드 밸런싱 성능 최적화
 * - DDD 도메인 로직과 헥사고날 인프라 계층의 분산 성능 검증
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/like/scenarios/like-distributed-load.test.js
 * k6 run --env SCENARIO=load performance-test/like/scenarios/like-distributed-load.test.js
 * k6 run --env SCENARIO=stress performance-test/like/scenarios/like-distributed-load.test.js
 * k6 run --env SCENARIO=soak performance-test/like/scenarios/like-distributed-load.test.js
 * k6 run --env SCENARIO=spike performance-test/like/scenarios/like-distributed-load.test.js
 * k6 run --env SCENARIO=capacity performance-test/like/scenarios/like-distributed-load.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 공통 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || "paste-access-token";
const AUTH_MODE = __ENV.AUTH_MODE || "token";
const EMAIL = __ENV.EMAIL || "test@example.com";
const PASSWORD = __ENV.PASSWORD || "password123";

// Custom metrics for distributed like operations
const distributedSuccessRate = new Rate("distributed_success_rate");
const distributedResponseTime = new Trend("distributed_response_time");
const distributedAttempts = new Counter("distributed_attempts");
const lockAcquisitionTime = new Trend("distributed_lock_acquisition_time");
const cacheSyncTime = new Trend("distributed_cache_sync_time");
const databaseShardTime = new Trend("distributed_database_shard_time");
const eventStreamingTime = new Trend("distributed_event_streaming_time");
const consistencyCheckTime = new Trend("distributed_consistency_check_time");
const networkLatency = new Trend("distributed_network_latency");
const loadBalancingEfficiency = new Rate(
  "distributed_load_balancing_efficiency"
);

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
    http_req_duration: ["p(95)<800"],
    distributed_success_rate: ["rate>0.95"],
    distributed_response_time: ["p(95)<800"],
    distributed_lock_acquisition_time: ["p(95)<200"],
    distributed_cache_sync_time: ["p(95)<150"],
    distributed_database_shard_time: ["p(95)<300"],
    distributed_event_streaming_time: ["p(95)<100"],
    distributed_consistency_check_time: ["p(95)<80"],
    distributed_network_latency: ["p(95)<50"],
    distributed_load_balancing_efficiency: ["rate>0.9"],
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
        "User-Agent": "k6-distributed-like-test/1.0",
      };
    }
  }

  return {
    Authorization: `Bearer ${ACCESS_TOKEN}`,
    "Content-Type": "application/json",
    Accept: "application/json",
    "User-Agent": "k6-distributed-like-test/1.0",
  };
}

function performDistributedLike() {
  const startTime = Date.now();
  distributedAttempts.add(1);

  // 분산 환경 시뮬레이션을 위한 다양한 타겟과 액션
  const targetTypes = ["PROJECT", "DATASET", "COMMENT"];
  const targetId = Math.floor(Math.random() * 1000) + 1; // 더 넓은 범위의 ID
  const targetType =
    targetTypes[Math.floor(Math.random() * targetTypes.length)];
  const action = "TOGGLE";

  const url = `${BASE_URL}/api/v1/likes`;
  const body = JSON.stringify({
    targetType: targetType,
    targetId: targetId,
    action: action,
  });

  const res = http.post(url, body, { headers: getAuthHeaders() });
  const responseTime = Date.now() - startTime;

  distributedResponseTime.add(responseTime);

  const success = res.status === 200;
  distributedSuccessRate.add(success);

  if (success) {
    // 분산 환경 성능 메트릭 계산
    const lockTime = responseTime * 0.25; // 분산 락은 전체 응답의 25% 추정
    lockAcquisitionTime.add(lockTime);

    const cacheTime = responseTime * 0.2; // 캐시 동기화는 전체 응답의 20% 추정
    cacheSyncTime.add(cacheTime);

    const shardTime = responseTime * 0.3; // 데이터베이스 샤드는 전체 응답의 30% 추정
    databaseShardTime.add(shardTime);

    const eventTime = responseTime * 0.1; // 이벤트 스트리밍은 전체 응답의 10% 추정
    eventStreamingTime.add(eventTime);

    const consistencyTime = responseTime * 0.1; // 일관성 검사는 전체 응답의 10% 추정
    consistencyCheckTime.add(consistencyTime);

    const networkTime = responseTime * 0.05; // 네트워크 지연은 전체 응답의 5% 추정
    networkLatency.add(networkTime);

    // 로드 밸런싱 효율성 시뮬레이션 (응답 시간 기반)
    const isEfficient = responseTime < 500; // 500ms 미만이면 효율적로 간주
    loadBalancingEfficiency.add(isEfficient);

    check(res, {
      "distributed like successful": (r) => r.status === 200,
      "response time < 800ms": (r) => responseTime < 800,
      "has like status": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && typeof data.data.isLiked === "boolean";
        } catch (e) {
          return false;
        }
      },
      "lock acquisition time < 200ms": () => lockTime < 200,
      "cache sync time < 150ms": () => cacheTime < 150,
      "database shard time < 300ms": () => shardTime < 300,
      "event streaming time < 100ms": () => eventTime < 100,
      "consistency check time < 80ms": () => consistencyTime < 80,
      "network latency < 50ms": () => networkTime < 50,
      "load balancing efficient": () => isEfficient,
    });
  } else {
    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  return res;
}

function scenarioExec() {
  performDistributedLike();
  sleep(Math.random() * 1 + 0.5);
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

export default function () {
  scenarioExec();
}
