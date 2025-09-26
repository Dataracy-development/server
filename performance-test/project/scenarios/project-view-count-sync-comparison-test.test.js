import http from "k6/http";
import { check, sleep } from "k6";

// 테스트 설정
export const options = {
  scenarios: {
    view_count_sync_comparison: {
      executor: "constant-vus",
      vus: 10, // 더 많은 VU로 증가 (워커 실행 빈도 고려)
      duration: "65s", // 워커가 3번 실행되도록 (0초, 20초, 40초) + 여유시간
      tags: { test_type: "comparison" },
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<2000"],
    http_req_failed: ["rate<0.1"],
  },
};

// 테스트 데이터
const projectIds = [1, 2, 3, 4, 5];

export default function () {
  const testType = __ENV.TEST_TYPE || "before_optimization";

  // 워커 실행 주기(20초)를 고려한 조회 패턴
  // 각 VU는 워커가 실행되기 전에 충분한 데이터를 Redis에 쌓아야 함
  const iterationsPerWorker = 5; // 워커당 5번씩 조회
  const totalIterations = iterationsPerWorker * 3; // 워커 3번 실행 예상

  for (let i = 0; i < totalIterations; i++) {
    const viewerId = `test_viewer_${__VU}_${Date.now()}_${Math.random()}_${i}`;
    const projectId = projectIds[__VU % projectIds.length];

    const viewResponse = http.get(
      `http://localhost:8080/api/v1/projects/${projectId}`,
      {
        headers: {
          "X-Viewer-ID": viewerId,
          "Content-Type": "application/json",
        },
      }
    );

    // 체크 수행
    const isSuccess = viewResponse.status === 200;
    const hasViewCount = (() => {
      try {
        const data = JSON.parse(viewResponse.body);
        return data.data && data.data.viewCount !== undefined;
      } catch (e) {
        return false;
      }
    })();

    const viewSuccess = check(viewResponse, {
      "프로젝트 조회 성공": (r) => r.status === 200,
      "조회수 데이터 포함": (r) => hasViewCount,
    });

    // 워커 실행 주기를 고려한 대기 시간
    // 20초 / 15번 반복 = 약 1.3초씩 대기
    sleep(1.3);
  }
}

export function handleSummary(data) {
  const testType = __ENV.TEST_TYPE || "before_optimization";
  const isBefore = testType === "before_optimization";

  // k6 기본 메트릭 사용 (더 안정적)
  const httpReqs = data.metrics.http_reqs || {};
  const iterations = data.metrics.iterations || {};
  const checks = data.metrics.checks || {};
  const responseTime = data.metrics.http_req_duration || {};

  const metrics = {
    avg_response_time: responseTime.avg || 0,
    p95_response_time: responseTime["p(95)"] || 0,
    max_response_time: responseTime.max || 0,
    min_response_time: responseTime.min || 0,
    total_iterations: iterations.count || 0,
    total_requests: httpReqs.count || 0,
    successful_requests: httpReqs.count - (httpReqs.failed || 0),
    failed_requests: httpReqs.failed || 0,
    success_rate: checks.rate ? checks.rate * 100 : 0,
    http_success_rate:
      httpReqs.count > 0
        ? ((httpReqs.count - (httpReqs.failed || 0)) / httpReqs.count) * 100
        : 0,
  };

  // 쿼리 수 계산 (워커 실행 주기 고려)
  // 워커가 20초마다 실행되므로 65초 동안 3번 실행 예상
  const workerExecutions = 3; // 0초, 20초, 40초에 실행
  const estimatedQueries = isBefore
    ? metrics.total_requests * 2 // 개별 처리: 요청 수 * 2 (DB UPDATE + 프로젝션 큐 INSERT)
    : workerExecutions * 2; // 배치 처리: 워커 실행 횟수 * 2 (DB 배치 UPDATE + 프로젝션 큐 배치 INSERT)

  console.log(
    `=== ${isBefore ? "Before" : "After"} 최적화 성능 테스트 결과 ===`
  );
  console.log(`평균 응답시간: ${metrics.avg_response_time.toFixed(2)}ms`);
  console.log(`95% 응답시간: ${metrics.p95_response_time.toFixed(2)}ms`);
  console.log(`최대 응답시간: ${metrics.max_response_time.toFixed(2)}ms`);
  console.log(`최소 응답시간: ${metrics.min_response_time.toFixed(2)}ms`);
  console.log(`총 반복 횟수: ${metrics.total_iterations}회`);
  console.log(`총 HTTP 요청 수: ${metrics.total_requests}개`);
  console.log(`성공한 요청 수: ${metrics.successful_requests}개`);
  console.log(`실패한 요청 수: ${metrics.failed_requests}개`);
  console.log(`HTTP 요청 성공률: ${metrics.http_success_rate.toFixed(2)}%`);
  console.log(`체크 성공률: ${metrics.success_rate.toFixed(2)}%`);
  console.log(
    `추정 쿼리 수: ${estimatedQueries}개 (${
      isBefore ? "개별 처리" : "배치 처리"
    })`
  );
  console.log(`워커 실행 횟수: ${workerExecutions}회 (20초마다 실행)`);
  console.log(`총 테스트 시간: 65초 (워커 3번 실행 고려)`);

  return {
    [`performance-test/project/results/project-view-count-sync-${testType}-results.json`]:
      JSON.stringify(
        {
          test_type: testType,
          timestamp: new Date().toISOString(),
          metrics: metrics,
          estimated_queries: estimatedQueries,
          summary: isBefore
            ? "개별 처리 버전 - 각 프로젝트마다 개별 DB UPDATE 및 프로젝션 큐 INSERT"
            : "배치 처리 버전 - 고정 2개 쿼리 (DB 배치 UPDATE + 프로젝션 큐 배치 INSERT)",
        },
        null,
        2
      ),
  };
}
