/**
 * ========================================
 * Comment N+1 쿼리 최적화 성능 테스트
 * ========================================
 *
 * 테스트 목적: N+1 쿼리 vs 배치 처리 성능 비교
 *
 * 시나리오:
 * - MODE=before: N+1 쿼리 (Before)
 * - MODE=after: 배치 처리 (After)
 *
 * 실행 명령어:
 * # Before (N+1 쿼리)
 * k6 run --env MODE=before performance-test/comment/scenarios/find-comments.test.js
 *
 * # After (배치 처리)
 * k6 run --env MODE=after performance-test/comment/scenarios/find-comments.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const OPTIMIZATION_MODE = __ENV.MODE || "after"; // "before" or "after"
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || "paste-access-token";

// 메트릭
export let readSuccessRate = new Rate("comment_read_success_rate");
export let readResponseTime = new Trend("comment_read_response_time");
export let readAttempts = new Counter("comment_read_attempts");

export let options = {
  scenarios: {
    default: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "5s", target: 3 }, // Ramp-up: 0 → 3 VU
        { duration: "20s", target: 5 }, // Peak: 5 VU 유지
        { duration: "5s", target: 0 }, // Ramp-down: 5 → 0 VU
      ],
      gracefulRampDown: "5s",
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.05"],
    comment_read_success_rate: ["rate>0.95"],
    comment_read_response_time: ["p(95)<100"],
  },
};

// 댓글 조회 함수
function readComments() {
  const startTime = Date.now();
  readAttempts.add(1);

  // 댓글 조회 파라미터 (실제 사용 패턴 반영)
  const projectId = Math.floor(Math.random() * 100) + 1; // 1~100
  const page = Math.floor(Math.random() * 3); // 0~2 (초기 페이지 집중)
  const size = 20; // 페이지 크기 고정 (20개)

  const url = `${BASE_URL}/api/v1/projects/${projectId}/comments?page=${page}&size=${size}`;
  const response = http.get(url, {
    headers: { Authorization: `Bearer ${ACCESS_TOKEN}` },
  });
  const responseTime = Date.now() - startTime;

  readResponseTime.add(responseTime);

  const success = response.status === 200;
  readSuccessRate.add(success);

  check(response, {
    "댓글 조회 성공": (r) => r.status === 200,
    "응답시간 합리적": (r) => r.timings.duration < 100,
  });

  return response;
}

export default function () {
  readComments();
  sleep(0.1); // 다음 요청까지 0.1초 대기
}
