import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// Custom metrics
const popularDataSuccessRate = new Rate("popular_data_success_rate");
const popularDataResponseTime = new Trend("popular_data_response_time");
const popularDataErrorRate = new Rate("popular_data_error_rate");
const popularDataLoadTime = new Trend("popular_data_load_time");

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
    http_req_duration: ["p(95)<2000"],
    popular_data_success_rate: ["rate>0.98"],
    popular_data_response_time: ["p(95)<1500"],
  },
};

export default function () {
  const startTime = Date.now();

  // Test popular datasets API
  const response = http.get(`${BASE_URL}/api/v1/datasets/popular?size=5`, {
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
  });

  const endTime = Date.now();
  const responseTime = endTime - startTime;

  // Validate response
  const success = check(response, {
    "status is 200": (r) => r.status === 200,
    "response time < 2s": (r) => r.timings.duration < 2000,
    "has popular data": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && Array.isArray(body.data);
      } catch (e) {
        return false;
      }
    },
    "popular data count": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.length <= 5;
      } catch (e) {
        return false;
      }
    },
  });

  // Record metrics
  popularDataSuccessRate.add(success);
  popularDataResponseTime.add(response.timings.duration);
  popularDataErrorRate.add(!success);
  popularDataLoadTime.add(responseTime);

  sleep(1);
}

export function handleSummary(data) {
  return {
    "popular-datasets-performance-test-results.json": JSON.stringify(
      data,
      null,
      2
    ),
  };
}
