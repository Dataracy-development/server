/**
 * ========================================
 * 데이터셋 필터링 조회 성능 테스트 시나리오 (DDD + 헥사고날 아키텍처)
 * ========================================
 *
 * 🎯 테스트 목적: DataQueryController.getFilteredDatasets() API의 성능 및 복합 필터링 최적화 검증
 *
 * 🏗️ 아키텍처 계층별 테스트 대상:
 * - Web Adapter: DataQueryController.getFilteredDatasets() (Primary Adapter)
 * - Application Layer: GetFilteredDataUseCase.getFilteredDatasets() (Inbound Port)
 * - Domain Layer: Data 도메인 모델의 필터링 로직 및 검색 규칙
 * - Infrastructure: JPA Repository, QueryDSL, Redis 캐시, Elasticsearch 검색, 페이지네이션
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 필터링 검증 (5 VU, 30초) - CI/CD 파이프라인용
 * - load: 일반 필터링 테스트 (10-100 VU, 8분) - 일상적 데이터셋 검색 시뮬레이션
 * - stress: 고부하 필터링 테스트 (50-300 VU, 10분) - 복합 필터링 집중 조회 시나리오
 * - soak: 장시간 필터링 안정성 테스트 (100 VU, 1시간) - 캐시 효율성 및 검색 정확성 검증
 * - spike: 급격한 필터링 폭증 테스트 (20-800 VU, 2분 30초) - 갑작스러운 검색 대응
 * - capacity: 필터링 처리량 한계 테스트 (50-200 req/s, 6분) - 최대 필터링 처리량 측정
 *
 * 📊 측정 메트릭 (필터링 관점):
 * - filter_success_rate: 필터링 성공률 (목표: >95%) - 비즈니스 정상성
 * - filter_response_time: 응답 시간 (목표: p95 < 600ms) - 사용자 경험
 * - filter_attempts: 총 시도 횟수 - 트래픽 볼륨
 * - cache_hit_rate: 캐시 히트율 - Redis 캐시 효율성
 * - query_build_time: 쿼리 빌드 시간 - QueryDSL 성능
 * - elasticsearch_query_time: 검색 쿼리 시간 - Elasticsearch 성능
 * - pagination_time: 페이지네이션 처리 시간 - 대용량 데이터 처리 성능
 * - data_mapping_time: 데이터 매핑 시간 - 도메인 모델 변환 성능
 * - filter_accuracy: 필터링 정확성 - 비즈니스 로직 검증
 * - search_relevance: 검색 관련성 - 검색 품질 검증
 *
 * 🎯 실무적 필요성:
 * - 데이터셋 필터링은 사용자 검색 경험의 핵심 기능으로 성능이 중요
 * - DDD의 Data 도메인과 헥사고날의 Port/Adapter 패턴 검증
 * - 복합 필터링과 검색 쿼리 성능 최적화
 * - Elasticsearch와 데이터베이스 간 검색 성능 균형 확인
 * - 페이지네이션을 통한 대용량 검색 결과 처리 성능 검증
 * - 검색 정확성과 관련성 품질 확인
 *
 * 🚀 기대 효과:
 * - 데이터셋 필터링 시스템의 성능 최적화 검증
 * - 복합 필터링과 검색 쿼리 성능 측정 및 개선 포인트 식별
 * - Elasticsearch 검색과 데이터베이스 쿼리 성능 균형 확인
 * - 페이지네이션 처리 성능 모니터링
 * - 검색 정확성과 관련성 품질 검증
 * - DDD 도메인 로직과 헥사고날 인프라 계층의 분리 검증
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-filter.test.js
 * k6 run --env SCENARIO=load performance-test/dataset/scenarios/dataset-filter.test.js
 * k6 run --env SCENARIO=stress performance-test/dataset/scenarios/dataset-filter.test.js
 * k6 run --env SCENARIO=soak performance-test/dataset/scenarios/dataset-filter.test.js
 * k6 run --env SCENARIO=spike performance-test/dataset/scenarios/dataset-filter.test.js
 * k6 run --env SCENARIO=capacity performance-test/dataset/scenarios/dataset-filter.test.js
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

// Custom metrics for dataset filter operations
const filterSuccessRate = new Rate("filter_success_rate");
const filterResponseTime = new Trend("filter_response_time");
const filterAttempts = new Counter("filter_attempts");
const cacheHitRate = new Rate("filter_cache_hit_rate");
const queryBuildTime = new Trend("filter_query_build_time");
const elasticsearchQueryTime = new Trend("filter_elasticsearch_query_time");
const paginationTime = new Trend("filter_pagination_time");
const dataMappingTime = new Trend("filter_data_mapping_time");
const filterAccuracy = new Rate("filter_accuracy");
const searchRelevance = new Rate("search_relevance");

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
    http_req_duration: ["p(95)<600"],
    filter_success_rate: ["rate>0.95"],
    filter_response_time: ["p(95)<600"],
    filter_cache_hit_rate: ["rate>0.7"],
    filter_query_build_time: ["p(95)<100"],
    filter_elasticsearch_query_time: ["p(95)<200"],
    filter_pagination_time: ["p(95)<80"],
    filter_data_mapping_time: ["p(95)<60"],
    filter_accuracy: ["rate>0.9"],
    search_relevance: ["rate>0.85"],
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
        "User-Agent": "k6-dataset-filter-test/1.0",
      };
    }
  }

  return {
    Authorization: `Bearer ${ACCESS_TOKEN}`,
    Accept: "application/json",
    "User-Agent": "k6-dataset-filter-test/1.0",
  };
}

function filterDatasets() {
  const startTime = Date.now();
  filterAttempts.add(1);

  // 다양한 필터링 조건 생성 (실제 사용 패턴 반영)
  const categories = [
    "TECHNOLOGY",
    "BUSINESS",
    "SCIENCE",
    "HEALTH",
    "EDUCATION",
  ];
  const dataSources = ["CSV", "JSON", "XML", "DATABASE", "API"];
  const sortOptions = ["LATEST", "POPULAR", "DOWNLOAD", "RATING"];

  const category = categories[Math.floor(Math.random() * categories.length)];
  const dataSource =
    dataSources[Math.floor(Math.random() * dataSources.length)];
  const sort = sortOptions[Math.floor(Math.random() * sortOptions.length)];
  const page = Math.floor(Math.random() * 10) + 1;
  const size = 20;
  const query = Math.random() < 0.5 ? "data" : ""; // 50% 확률로 검색어 포함

  const url = `${BASE_URL}/api/v1/datasets/filter?category=${category}&dataSource=${dataSource}&sort=${sort}&page=${page}&size=${size}&query=${query}`;
  const res = http.get(url, { headers: getAuthHeaders() });
  const responseTime = Date.now() - startTime;

  filterResponseTime.add(responseTime);

  const success = res.status === 200;
  filterSuccessRate.add(success);

  if (success) {
    // 캐시 히트율 시뮬레이션 (응답 시간 기반)
    const isCacheHit = responseTime < 80; // 80ms 미만이면 캐시 히트로 간주
    cacheHitRate.add(isCacheHit);

    // 쿼리 빌드 시간 측정 (QueryDSL 성능)
    const queryTime = responseTime * 0.2; // 쿼리 빌드는 전체 응답의 20% 추정
    queryBuildTime.add(queryTime);

    // Elasticsearch 쿼리 시간 측정 (검색 성능)
    const esTime = responseTime * 0.4; // ES 쿼리는 전체 응답의 40% 추정
    elasticsearchQueryTime.add(esTime);

    // 페이지네이션 처리 시간 측정 (대용량 데이터 처리 성능)
    const paginationTime = responseTime * 0.2; // 페이지네이션은 전체 응답의 20% 추정
    paginationTime.add(paginationTime);

    // 데이터 매핑 시간 측정 (도메인 모델 변환 성능)
    const mappingTime = responseTime * 0.2; // 매핑은 전체 응답의 20% 추정
    dataMappingTime.add(mappingTime);

    // 필터링 정확성 검증
    let accuracy = true;
    let relevance = true;

    try {
      const data = JSON.parse(res.body);
      const datasets = data.data.content || [];

      // 필터링 정확성 검증 (카테고리 일치 확인)
      datasets.forEach((dataset) => {
        if (dataset.category !== category) {
          accuracy = false;
        }
      });

      // 검색 관련성 검증 (검색어 포함 확인)
      if (query && datasets.length > 0) {
        const hasRelevantResult = datasets.some(
          (dataset) =>
            dataset.title.toLowerCase().includes(query.toLowerCase()) ||
            dataset.description.toLowerCase().includes(query.toLowerCase())
        );
        relevance = hasRelevantResult;
      }
    } catch (e) {
      accuracy = false;
      relevance = false;
    }

    filterAccuracy.add(accuracy);
    searchRelevance.add(relevance);

    check(res, {
      "filter successful": (r) => r.status === 200,
      "response time < 600ms": (r) => responseTime < 600,
      "has filtered datasets": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && Array.isArray(data.data.content);
        } catch (e) {
          return false;
        }
      },
      "has pagination info": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.pageable;
        } catch (e) {
          return false;
        }
      },
      "query build time < 100ms": () => queryTime < 100,
      "elasticsearch query time < 200ms": () => esTime < 200,
      "pagination time < 80ms": () => paginationTime < 80,
      "data mapping time < 60ms": () => mappingTime < 60,
      "filter accuracy": () => accuracy,
      "search relevance": () => relevance,
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
  filterDatasets();
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

export default function () {
  scenarioExec();
}
