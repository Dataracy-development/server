/**
 * ========================================
 * 프로젝트 조회수 동기화 워커 성능 테스트 (조회수 생성용)
 * ========================================
 *
 * 🎯 테스트 목적: ProjectViewCountWorker 배치 처리 최적화 효과 검증
 *
 * 🔍 테스트 방식:
 * - 이 k6 스크립트는 "조회수 생성용"입니다
 * - 실제 워커 성능은 "서버 로그"에서 측정합니다
 * - k6 → Redis에 조회수 쌓기 → 워커 실행 → 로그 확인
 *
 * 📊 측정 대상:
 * - Before: 개별 처리 (N×2 쿼리, O(n) 복잡도)
 * - After: 배치 처리 (2 쿼리 고정, O(1) 복잡도)
 *
 * 🚀 워커 실행 주기:
 * - @Scheduled(fixedDelay = 20 * 1000) - 20초마다 실행
 *
 * 실행 명령어:
 * k6 run --env TEST_TYPE=before_optimization performance-test/project/scenarios/project-view-count-sync-comparison-test.test.js
 * k6 run --env TEST_TYPE=after_optimization performance-test/project/scenarios/project-view-count-sync-comparison-test.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Counter } from "k6/metrics";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const TEST_TYPE = __ENV.TEST_TYPE || "before_optimization";

// Custom metrics
export let viewGenerationSuccessRate = new Rate("view_generation_success_rate");
export let viewGenerationAttempts = new Counter("view_generation_attempts");

export const options = {
  scenarios: {
    view_generation: {
      executor: "ramping-vus",
      startVUs: 0,
      exec: "generateViews",
      stages: [
        { duration: "5s", target: 5 }, // Ramp-up: 0 → 5 VU
        { duration: "50s", target: 5 }, // Peak: 5 VU 유지 (워커 2-3번 실행)
        { duration: "5s", target: 0 }, // Ramp-down: 5 → 0 VU
      ],
      gracefulRampDown: "5s",
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<2000"],
    http_req_failed: ["rate<0.1"],
    view_generation_success_rate: ["rate>0.95"],
  },
};

// 테스트 대상 프로젝트 ID (DB에 실제 존재하는 프로젝트)
const projectIds = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];

export function generateViews() {
  viewGenerationAttempts.add(1);

  // 랜덤 프로젝트 선택
  const projectId = projectIds[Math.floor(Math.random() * projectIds.length)];

  // 고유한 viewer ID 생성
  const viewerId = `test_viewer_${__VU}_${Date.now()}_${Math.random()}`;

  // 프로젝트 조회 (Redis에 조회수 증가)
  const response = http.get(`${BASE_URL}/api/v1/projects/${projectId}`, {
    headers: {
      "X-Viewer-ID": viewerId,
      "Content-Type": "application/json",
    },
  });

  const success = check(response, {
    "프로젝트 조회 성공": (r) => r.status === 200,
    "조회수 데이터 포함": (r) => {
      try {
        const data = JSON.parse(r.body);
        return data.data && data.data.viewCount !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  viewGenerationSuccessRate.add(success);

  // 워커 실행 주기를 고려한 대기 시간
  // 60초 동안 5 VUs가 실행 → 충분한 조회수 생성
  sleep(0.5 + Math.random() * 0.5); // 0.5~1.0초 대기 (자연스러운 패턴)
}

export function handleSummary(data) {
  const isBefore = TEST_TYPE === "before_optimization";

  const httpReqs = data.metrics.http_reqs || {};
  const iterations = data.metrics.iterations || {};
  const checks = data.metrics.checks || {};
  const responseTime = data.metrics.http_req_duration || {};

  const metrics = {
    avg_response_time: responseTime.avg || 0,
    p95_response_time: responseTime["p(95)"] || 0,
    max_response_time: responseTime.max || 0,
    total_iterations: iterations.count || 0,
    total_requests: httpReqs.count || 0,
    successful_requests: httpReqs.count - (httpReqs.failed || 0),
    failed_requests: httpReqs.failed || 0,
    success_rate: checks.rate ? checks.rate * 100 : 0,
  };

  // 워커 실행 횟수 계산 (20초마다 실행, 60초 동안)
  const workerExecutions = 3; // 0초, 20초, 40초에 실행

  console.log("\n");
  console.log("=".repeat(70));
  console.log(
    `   ${isBefore ? "Before" : "After"} 최적화 - 조회수 생성 테스트 결과`
  );
  console.log("=".repeat(70));
  console.log("\n📊 k6 테스트 결과 (조회수 생성용):");
  console.log(`   - 평균 응답시간: ${metrics.avg_response_time.toFixed(2)}ms`);
  console.log(`   - 95% 응답시간: ${metrics.p95_response_time.toFixed(2)}ms`);
  console.log(`   - 총 조회 요청 수: ${metrics.total_requests}회`);
  console.log(`   - 성공한 요청 수: ${metrics.successful_requests}회`);
  console.log(`   - 성공률: ${metrics.success_rate.toFixed(2)}%`);
  console.log("\n");
  console.log("⏰ 워커 실행 정보:");
  console.log(
    `   - 워커 실행 주기: 20초마다 (@Scheduled(fixedDelay = 20 * 1000))`
  );
  console.log(
    `   - 예상 워커 실행 횟수: ${workerExecutions}회 (0초, 20초, 40초)`
  );
  console.log(`   - 총 테스트 시간: 60초`);
  console.log("\n");
  console.log("🔍 워커 성능 측정 방법:");
  console.log("   1. 이 k6 스크립트는 조회수 생성만 담당합니다");
  console.log(
    "   2. 실제 워커 성능은 '서버 로그'에서 측정합니다 (트러블슈팅 문서 참고)"
  );
  console.log("   3. 로그에서 확인할 지표:");
  console.log("      - 처리된 프로젝트 수");
  console.log("      - 실행 시간 (ms)");
  console.log("      - 총 쿼리 수 (Before: N×2, After: 2 고정)");
  console.log("\n");
  console.log("📝 예상 워커 로그 예시:");
  if (isBefore) {
    console.log("   [Before] 개별 처리:");
    console.log("   - 처리된 프로젝트 수: 15개");
    console.log("   - 총 쿼리 수: 30개 (15×2)");
    console.log("   - 실행 시간: ~X ms (N에 비례)");
  } else {
    console.log("   [After] 배치 처리:");
    console.log("   - 처리된 프로젝트 수: 15개");
    console.log("   - 총 쿼리 수: 2개 (고정)");
    console.log("   - 실행 시간: ~Y ms (N과 무관)");
  }
  console.log("\n");
  console.log("=".repeat(70));
  console.log("\n");

  return {
    stdout: "",
  };
}
