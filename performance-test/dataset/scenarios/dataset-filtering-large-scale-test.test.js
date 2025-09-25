import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// Custom metrics for large-scale testing
const largeScaleSuccessRate = new Rate("large_scale_success_rate");
const largeScaleResponseTime = new Trend("large_scale_response_time");
const largeScaleErrorRate = new Rate("large_scale_error_rate");
const largeScaleLoadTime = new Trend("large_scale_load_time");
const queryCount = new Counter("total_queries");

// Test configuration
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const SCENARIO = __ENV.SCENARIO || "fixedLargePageTest";

// N+1 문제 극대화 테스트 시나리오
const scenarios = {
  // 고정된 큰 페이지 크기로 N+1 문제 극대화 (3명 동시 사용자, 100개 페이지)
  fixedLargePageTest: {
    executor: "constant-vus",
    vus: 3,
    duration: "120s",
    tags: { test_type: "fixedLargePageTest" },
  },
  // 더 많은 동시 사용자로 확장성 테스트 (10명 동시 사용자, 100개 페이지)
  scalabilityTest: {
    executor: "constant-vus",
    vus: 10,
    duration: "120s",
    tags: { test_type: "scalabilityTest" },
  },
  // 스트레스 테스트 (20명 동시 사용자, 100개 페이지)
  stressTest: {
    executor: "constant-vus",
    vus: 20,
    duration: "120s",
    tags: { test_type: "stressTest" },
  },
};

export const options = {
  scenarios: {
    [SCENARIO]: scenarios[SCENARIO],
  },
  thresholds: {
    http_req_failed: ["rate<0.05"], // 더 관대한 임계값
    http_req_duration: ["p(95)<10000"], // 더 관대한 응답시간
    large_scale_success_rate: ["rate>0.95"],
    large_scale_response_time: ["p(95)<8000"],
  },
};

export default function () {
  const startTime = Date.now();

  // 3단계: 배치 처리 방식 테스트 (최적화된 버전)
  const selectedScenario = {
    name: "stage3_batch_processing",
    url: `${BASE_URL}/api/v1/datasets/filter?page=0&size=50&sortType=LATEST`,
    expectedQueries: 3, // 1개 메인 쿼리 + 1개 배치 쿼리 + 1개 카운트 쿼리
    expectedQueriesOptimized: 3, // 3단계: 1개 메인 + 1개 배치 + 1개 카운트
    stage: "3단계_배치_처리",
  };

  // 실제 DB 쿼리 수 측정을 위한 헤더 추가
  const params = {
    headers: {
      "Content-Type": "application/json",
      "X-Request-ID": `test-${Date.now()}-${Math.random()}`,
      "X-Test-Scenario": selectedScenario.name,
    },
  };

  // Test filtering API
  const response = http.get(selectedScenario.url, params);

  const endTime = Date.now();
  const responseTime = endTime - startTime;

  // Validate response
  const success = check(response, {
    [`${selectedScenario.name} status is 200`]: (r) => r.status === 200,
    [`${selectedScenario.name} response time < 10s`]: (r) =>
      r.timings.duration < 10000,
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
        return (
          body.pagination && typeof body.pagination.totalElements === "number"
        );
      } catch (e) {
        return false;
      }
    },
    [`${selectedScenario.name} has project counts`]: (r) => {
      try {
        const body = JSON.parse(r.body);
        if (body.data && Array.isArray(body.data)) {
          // 프로젝트 수가 포함되어 있는지 확인
          return body.data.some(
            (item) =>
              item.projectCount !== undefined ||
              (item.data && item.projectCount !== undefined)
          );
        }
        return false;
      } catch (e) {
        return false;
      }
    },
  });

  // Record metrics
  largeScaleSuccessRate.add(success);
  largeScaleResponseTime.add(response.timings.duration);
  largeScaleErrorRate.add(!success);
  largeScaleLoadTime.add(responseTime);

  // 예상 쿼리 수 기록 (최적화 전 기준)
  queryCount.add(selectedScenario.expectedQueries);

  // 응답 데이터 크기 기반 대기 시간
  const dataSize = response.body.length;
  const waitTime = Math.min(Math.max(dataSize / 10000, 0.5), 3); // 0.5~3초
  sleep(waitTime);
}

export function handleSummary(data) {
  const summary = {
    timestamp: new Date().toISOString(),
    scenario: SCENARIO,
    metrics: {
      success_rate: data.metrics.large_scale_success_rate?.values?.rate || 0,
      avg_response_time:
        data.metrics.large_scale_response_time?.values?.avg || 0,
      p95_response_time:
        data.metrics.large_scale_response_time?.values?.["p(95)"] || 0,
      max_response_time:
        data.metrics.large_scale_response_time?.values?.max || 0,
      total_requests: data.metrics.http_reqs?.values?.count || 0,
      total_queries: data.metrics.total_queries?.values?.count || 0,
      error_rate: data.metrics.large_scale_error_rate?.values?.rate || 0,
    },
    thresholds: {
      success_rate_threshold: 0.95,
      response_time_threshold: 8000,
      error_rate_threshold: 0.05,
    },
    analysis: {
      query_efficiency:
        data.metrics.total_queries?.values?.count > 0
          ? data.metrics.http_reqs?.values?.count /
            data.metrics.total_queries?.values?.count
          : 0,
      performance_impact:
        data.metrics.large_scale_response_time?.values?.avg > 5000
          ? "HIGH"
          : data.metrics.large_scale_response_time?.values?.avg > 2000
          ? "MEDIUM"
          : "LOW",
    },
  };

  return {
    "dataset-filtering-large-scale-test-results.json": JSON.stringify(
      summary,
      null,
      2
    ),
    stdout: `
=== 대용량 데이터셋 필터링 성능 테스트 결과 ===
시나리오: ${SCENARIO}
성공률: ${(summary.metrics.success_rate * 100).toFixed(2)}%
평균 응답시간: ${summary.metrics.avg_response_time.toFixed(2)}ms
95% 응답시간: ${summary.metrics.p95_response_time.toFixed(2)}ms
최대 응답시간: ${summary.metrics.max_response_time.toFixed(2)}ms
총 요청 수: ${summary.metrics.total_requests}
예상 DB 쿼리 수: ${summary.metrics.total_queries}
에러율: ${(summary.metrics.error_rate * 100).toFixed(2)}%
쿼리 효율성: ${summary.analysis.query_efficiency.toFixed(2)} (요청당 쿼리 수)
성능 영향도: ${summary.analysis.performance_impact}
============================================
    `,
  };
}
