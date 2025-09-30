import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend } from "k6/metrics";

// 커스텀 메트릭 정의
const rateLimitSuccessRate = new Rate("rate_limit_success_rate");
const rateLimitResponseTime = new Trend("rate_limit_response_time");
const memoryUsageTrend = new Trend("memory_usage_trend");

export let options = {
  scenarios: {
    // 간단한 테스트 시나리오
    simple_test: {
      executor: "constant-vus",
      vus: 5,
      duration: "30s",
      exec: "testSimple",
    },
  },
  thresholds: {
    rate_limit_success_rate: ["rate>0.8"],
    rate_limit_response_time: ["p(95)<5000"],
  },
};

const BASE_URL = "http://localhost:8080";

// 간단한 테스트 함수
export function testSimple() {
  const startTime = Date.now();

  // 인증이 필요 없는 헬스체크 엔드포인트 호출
  const response = http.get(`${BASE_URL}/actuator/health`, {
    headers: {
      "X-Forwarded-For": `192.168.1.${__VU}`,
    },
  });

  const endTime = Date.now();
  const responseTime = endTime - startTime;

  // 메트릭 수집
  rateLimitSuccessRate.add(response.status === 200);
  rateLimitResponseTime.add(responseTime);

  // 메모리 사용량 시뮬레이션
  memoryUsageTrend.add(Math.random() * 50 + 150);

  check(response, {
    "response time < 5000ms": (r) => responseTime < 5000,
    "status is 200": (r) => r.status === 200,
  });

  sleep(1); // 1초 간격으로 요청
}

export function handleSummary(data) {
  return {
    "performance-test/auth/results/rate-limit-performance-comparison-summary.json":
      JSON.stringify(data, null, 2),
    stdout: `
=== 간단한 성능 테스트 결과 ===
- 성공률: ${data.metrics.rate_limit_success_rate.values.rate.toFixed(3)}
- 평균 응답시간: ${data.metrics.rate_limit_response_time.values.avg.toFixed(
      2
    )}ms
- 95% 응답시간: ${data.metrics.rate_limit_response_time.values["p(95)"].toFixed(
      2
    )}ms
- 총 요청 수: ${data.metrics.http_reqs.values.count}
        `,
  };
}
