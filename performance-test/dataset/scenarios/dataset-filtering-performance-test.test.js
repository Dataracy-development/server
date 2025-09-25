import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// Custom metrics
const filteringSuccessRate = new Rate("filtering_success_rate");
const filteringResponseTime = new Trend("filtering_response_time");
const filteringErrorRate = new Rate("filtering_error_rate");
const filteringLoadTime = new Trend("filtering_load_time");

// Test configuration
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const SCENARIO = __ENV.SCENARIO || "smoke";

// Test scenarios
const scenarios = {
  smoke: {
    executor: "constant-vus",
    vus: 1,
    duration: "30s",
    tags: { test_type: "smoke" },
  },
  load: {
    executor: "constant-vus",
    vus: 10,
    duration: "60s",
    tags: { test_type: "load" },
  },
  stress: {
    executor: "ramping-vus",
    startVUs: 5,
    stages: [
      { duration: "30s", target: 20 },
      { duration: "60s", target: 50 },
      { duration: "30s", target: 5 },
    ],
    tags: { test_type: "stress" },
  },
  spike: {
    executor: "ramping-vus",
    startVUs: 5,
    stages: [
      { duration: "10s", target: 5 },
      { duration: "10s", target: 100 },
      { duration: "10s", target: 5 },
    ],
    tags: { test_type: "spike" },
  },
};

export const options = {
  scenarios: {
    [SCENARIO]: scenarios[SCENARIO],
  },
  thresholds: {
    http_req_failed: ["rate<0.02"],
    http_req_duration: ["p(95)<5000"],
    filtering_success_rate: ["rate>0.98"],
    filtering_response_time: ["p(95)<3000"],
  },
};

export default function () {
  const startTime = Date.now();

  // 다양한 필터링 시나리오 테스트
  const filterScenarios = [
    // 기본 검색 (키워드만)
    {
      name: "keyword_search",
      url: `${BASE_URL}/api/v1/datasets/search?keyword=데이터&page=0&size=10&sortType=LATEST`,
    },
    // 복합 필터링 (키워드 + 주제 + 데이터소스)
    {
      name: "complex_filter",
      url: `${BASE_URL}/api/v1/datasets/search?keyword=분석&topicId=1&dataSourceId=2&page=0&size=10&sortType=POPULAR`,
    },
    // 날짜 범위 필터링
    {
      name: "date_filter",
      url: `${BASE_URL}/api/v1/datasets/search?year=2024&page=0&size=10&sortType=LATEST`,
    },
    // 모든 필터 적용
    {
      name: "all_filters",
      url: `${BASE_URL}/api/v1/datasets/search?keyword=머신러닝&topicId=3&dataSourceId=1&dataTypeId=2&year=2023&page=0&size=10&sortType=POPULAR`,
    },
  ];

  // 랜덤하게 필터링 시나리오 선택
  const selectedScenario = filterScenarios[Math.floor(Math.random() * filterScenarios.length)];

  // Test filtering API
  const response = http.get(selectedScenario.url, {
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
  });

  const endTime = Date.now();
  const responseTime = endTime - startTime;

  // Validate response
  const success = check(response, {
    [`${selectedScenario.name} status is 200`]: (r) => r.status === 200,
    [`${selectedScenario.name} response time < 5s`]: (r) => r.timings.duration < 5000,
    [`${selectedScenario.name} has data`]: (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && Array.isArray(body.data);
      } catch (e) {
        return false;
      }
    },
    [`${selectedScenario.name} has pagination`]: (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.pagination && typeof body.pagination.totalElements === "number";
      } catch (e) {
        return false;
      }
    },
  });

  // Record metrics
  filteringSuccessRate.add(success);
  filteringResponseTime.add(response.timings.duration);
  filteringErrorRate.add(!success);
  filteringLoadTime.add(responseTime);

  sleep(1);
}

export function handleSummary(data) {
  return {
    "dataset-filtering-performance-test-results.json": JSON.stringify(data, null, 2),
  };
}
