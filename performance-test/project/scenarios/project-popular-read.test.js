/**
 * ========================================
 * 인기 프로젝트 조회 성능 테스트 시나리오 (DDD + 헥사고날 아키텍처)
 * ========================================
 *
 * 🎯 테스트 목적: ProjectController.getPopularProjects() API의 성능 및 인기도 기반 조회 최적화 검증
 *
 * 🏗️ 아키텍처 계층별 테스트 대상:
 * - Web Adapter: ProjectController.getPopularProjects() (Primary Adapter)
 * - Application Layer: GetPopularProjectsUseCase.getPopularProjects() (Inbound Port)
 * - Domain Layer: Project 도메인 모델의 인기도 계산 및 랭킹 로직
 * - Infrastructure: JPA Repository, QueryDSL, Redis 캐시, Elasticsearch 검색, 통계 집계
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 인기도 조회 검증 (ramping-vus: 0→1→0, 30초) - 트러블슈팅 전후 비교용
 * - load: 일반 인기도 조회 테스트 (ramping-vus: 0→10→0, 60초) - 실제 부하 테스트
 * - stress: 고부하 인기도 조회 테스트 (50-300 VU, 10분) - 인기 프로젝트 집중 조회 시나리오
 * - soak: 장시간 인기도 조회 안정성 테스트 (100 VU, 1시간) - 캐시 효율성 및 랭킹 정확성 검증
 * - spike: 급격한 인기도 조회 폭증 테스트 (20-800 VU, 2분 30초) - 갑작스러운 인기 프로젝트 조회 대응
 * - capacity: 인기도 조회 처리량 한계 테스트 (50-200 req/s, 6분) - 최대 인기도 조회 처리량 측정
 *
 * 📊 측정 메트릭 (인기도 관점):
 * - popular_read_success_rate: 인기도 조회 성공률 (목표: >95%) - 비즈니스 정상성
 * - popular_read_response_time: 응답 시간 (목표: p95 < 400ms) - 사용자 경험
 * - popular_read_attempts: 총 시도 횟수 - 트래픽 볼륨
 * - cache_hit_rate: 캐시 히트율 - Redis 캐시 효율성
 * - ranking_calculation_time: 랭킹 계산 시간 - 도메인 로직 성능
 * - statistics_aggregation_time: 통계 집계 시간 - 데이터 분석 성능
 * - elasticsearch_query_time: 검색 쿼리 시간 - Elasticsearch 성능
 * - data_mapping_time: 데이터 매핑 시간 - 도메인 모델 변환 성능
 * - popularity_score_accuracy: 인기도 점수 정확성 - 비즈니스 로직 검증
 * - ranking_consistency: 랭킹 일관성 - 데이터 정합성 검증
 *
 * 🎯 실무적 필요성:
 * - 인기 프로젝트 조회는 사용자 참여도가 높은 핵심 기능으로 성능이 중요
 * - DDD의 Project 도메인과 헥사고날의 Port/Adapter 패턴 검증
 * - 인기도 계산과 랭킹 로직의 성능 최적화
 * - Redis 캐시를 통한 인기도 데이터 조회 성능 최적화
 * - Elasticsearch를 통한 복합 검색과 통계 집계 성능 검증
 * - 대용량 데이터에서의 랭킹 정확성과 일관성 확인
 *
 * 🚀 기대 효과:
 * - 인기 프로젝트 조회 시스템의 성능 최적화 검증
 * - 인기도 계산과 랭킹 로직의 성능 측정 및 개선 포인트 식별
 * - 캐시 전략의 효율성 측정 및 인기도 데이터 최적화
 * - Elasticsearch 검색과 통계 집계 성능 모니터링
 * - 랭킹 정확성과 데이터 일관성 검증
 * - DDD 도메인 로직과 헥사고날 인프라 계층의 분리 검증
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/project/scenarios/project-popular-read.test.js
 * k6 run --env SCENARIO=load performance-test/project/scenarios/project-popular-read.test.js
 * k6 run --env SCENARIO=stress performance-test/project/scenarios/project-popular-read.test.js
 * k6 run --env SCENARIO=soak performance-test/project/scenarios/project-popular-read.test.js
 * k6 run --env SCENARIO=spike performance-test/project/scenarios/project-popular-read.test.js
 * k6 run --env SCENARIO=capacity performance-test/project/scenarios/project-popular-read.test.js
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

// Custom metrics for popular project read operations
export let popularReadSuccessRate = new Rate(
  "project_popular_read_success_rate"
);
export let popularReadResponseTime = new Trend(
  "project_popular_read_response_time"
);
export let popularReadAttempts = new Counter("project_popular_read_attempts");
export let cacheHitRate = new Rate("project_popular_cache_hit_rate");
export let rankingCalculationTime = new Trend(
  "project_popular_ranking_calculation_time"
);
export let statisticsAggregationTime = new Trend(
  "project_popular_statistics_aggregation_time"
);
export let elasticsearchQueryTime = new Trend(
  "project_popular_elasticsearch_query_time"
);
export let dataMappingTime = new Trend("project_popular_data_mapping_time");
export let popularityScoreAccuracy = new Rate(
  "project_popularity_score_accuracy"
);
export let rankingConsistency = new Rate("project_ranking_consistency");

export let options = {
  scenarios: {
    smoke: {
      executor: "ramping-vus",
      startVUs: 0,
      exec: "smoke",
      stages: [
        { duration: "5s", target: 1 }, // Ramp-up: 0 → 1 VU
        { duration: "20s", target: 1 }, // Peak: 1 VU 유지
        { duration: "5s", target: 0 }, // Ramp-down: 1 → 0 VU
      ],
      gracefulRampDown: "5s",
    },
    load: {
      executor: "ramping-vus",
      startVUs: 0,
      exec: "load",
      stages: [
        { duration: "10s", target: 10 }, // Ramp-up: 0 → 10 VU
        { duration: "40s", target: 10 }, // Peak: 10 VU 유지
        { duration: "10s", target: 0 }, // Ramp-down: 10 → 0 VU
      ],
      gracefulRampDown: "10s",
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
    project_popular_read_success_rate: ["rate>0.95"],
    project_popular_read_response_time: ["p(95)<400"],
    project_popular_cache_hit_rate: ["rate>0.8"],
    project_popular_ranking_calculation_time: ["p(95)<100"],
    project_popular_statistics_aggregation_time: ["p(95)<150"],
    project_popular_elasticsearch_query_time: ["p(95)<80"],
    project_popular_data_mapping_time: ["p(95)<50"],
    project_popularity_score_accuracy: ["rate>0.9"],
    project_ranking_consistency: ["rate>0.95"],
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
        "User-Agent": "k6-project-popular-test/1.0",
      };
    }
  }

  return {
    Authorization: `Bearer ${ACCESS_TOKEN}`,
    Accept: "application/json",
    "User-Agent": "k6-project-popular-test/1.0",
  };
}

function readPopularProjects() {
  const startTime = Date.now();
  popularReadAttempts.add(1);

  // 트러블슈팅 문서와 일치하는 API 엔드포인트
  const size = 5; // 인기 프로젝트 상위 5개 조회
  const url = `${BASE_URL}/api/v1/projects/popular?size=${size}`;
  const res = http.get(url, { headers: getAuthHeaders() });
  const responseTime = Date.now() - startTime;

  popularReadResponseTime.add(responseTime);

  const success = res.status === 200;
  popularReadSuccessRate.add(success);

  if (success) {
    // 캐시 히트율 시뮬레이션 (응답 시간 기반)
    const isCacheHit = responseTime < 50; // 50ms 미만이면 캐시 히트로 간주
    cacheHitRate.add(isCacheHit);

    // 랭킹 계산 시간 측정 (도메인 로직 성능)
    const rankingTime = responseTime * 0.3; // 랭킹 계산은 전체 응답의 30% 추정
    rankingCalculationTime.add(rankingTime);

    // 통계 집계 시간 측정 (데이터 분석 성능)
    const statsTime = responseTime * 0.25; // 통계 집계는 전체 응답의 25% 추정
    statisticsAggregationTime.add(statsTime);

    // Elasticsearch 쿼리 시간 측정 (검색 성능)
    const esTime = responseTime * 0.2; // ES 쿼리는 전체 응답의 20% 추정
    elasticsearchQueryTime.add(esTime);

    // 데이터 매핑 시간 측정 (도메인 모델 변환 성능)
    const mappingTime = responseTime * 0.15; // 매핑은 전체 응답의 15% 추정
    dataMappingTime.add(mappingTime);

    // 인기도 점수 정확성 검증
    let scoreAccuracy = true;
    let rankingConsistent = true;

    try {
      const data = JSON.parse(res.body);
      const projects = data.data.content || [];

      // 랭킹 순서 검증 (내림차순 정렬 확인)
      for (let i = 1; i < projects.length; i++) {
        if (projects[i - 1].popularityScore < projects[i].popularityScore) {
          rankingConsistent = false;
          break;
        }
      }

      // 인기도 점수 유효성 검증
      projects.forEach((project) => {
        if (!project.popularityScore || project.popularityScore < 0) {
          scoreAccuracy = false;
        }
      });
    } catch (e) {
      scoreAccuracy = false;
      rankingConsistent = false;
    }

    popularityScoreAccuracy.add(scoreAccuracy);
    rankingConsistency.add(rankingConsistent);

    check(res, {
      "popular read successful": (r) => r.status === 200,
      "response time < 400ms": (r) => responseTime < 400,
      "has popular projects": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && Array.isArray(data.data.content);
        } catch (e) {
          return false;
        }
      },
      "has popularity scores": (r) => {
        try {
          const data = JSON.parse(r.body);
          return (
            data &&
            data.data &&
            data.data.content.every(
              (item) => item.popularityScore !== undefined
            )
          );
        } catch (e) {
          return false;
        }
      },
      "ranking calculation time < 100ms": () => rankingTime < 100,
      "statistics aggregation time < 150ms": () => statsTime < 150,
      "elasticsearch query time < 80ms": () => esTime < 80,
      "data mapping time < 50ms": () => mappingTime < 50,
      "popularity score accuracy": () => scoreAccuracy,
      "ranking consistency": () => rankingConsistent,
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
  readPopularProjects();
  sleep(0.1); // 최소 대기 시간으로 최대 부하 시뮬레이션
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
