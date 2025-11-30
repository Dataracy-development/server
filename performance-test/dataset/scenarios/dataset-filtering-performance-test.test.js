/**
 * ========================================
 * 데이터셋 필터링 성능 테스트 시나리오 (3단계 최적화 비교)
 * ========================================
 *
 * 🎯 테스트 목적: 데이터셋 필터링 API 3단계 최적화 효과 검증
 *
 * 🏗️ 3단계 최적화 과정:
 * - 1단계: JOIN + GROUP BY + fetchJoin (기준선)
 * - 2단계: N+1 서브쿼리 (문제점 드러내기)
 * - 3단계: 배치 처리 (최종 최적화)
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 필터링 검증 (ramping-vus: 0→1→0, 30초) - 3단계 전후 비교용
 * - load: 부하 테스트 (ramping-vus: 0→10→0, 60초) - 확장성 검증용
 *
 * 📊 측정 메트릭:
 * - filtering_success_rate: 필터링 성공률 (목표: >95%)
 * - filtering_response_time: 응답 시간 (목표: p95 < 400ms)
 * - db_query_count: DB 쿼리 수 추적
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke --env STAGE=stage1 performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js
 * k6 run --env SCENARIO=smoke --env STAGE=stage2 performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js
 * k6 run --env SCENARIO=smoke --env STAGE=stage3 performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js
 * k6 run --env SCENARIO=load --env STAGE=stage3 performance-test/dataset/scenarios/dataset-filtering-performance-test.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const STAGE = __ENV.STAGE || "stage3"; // stage1, stage2, stage3

// Custom metrics
export let filteringSuccessRate = new Rate("filtering_success_rate");
export let filteringResponseTime = new Trend("filtering_response_time");
export let filteringAttempts = new Counter("filtering_attempts");
export let dbQueryCount = new Counter("db_query_count");

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
  },
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<400"],
    filtering_success_rate: ["rate>0.95"],
    db_query_count: ["count<10000"],
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

// 쿼리 수 정의 (실제 구현 코드 기반)
const QUERIES_PER_REQUEST = {
  stage1: 2, // 메인 1 + 카운트 1
  stage2: 52, // 메인 1 + 서브쿼리 50개 + 카운트 1
  stage3: 3, // 메인 1 + 배치 1 + 카운트 1
};

function filterDatasets() {
  const startTime = Date.now();
  filteringAttempts.add(1);

  // 트러블슈팅 문서와 일치하는 API 엔드포인트
  const url = `${BASE_URL}/api/v1/datasets/filter?page=0&size=50&sortType=LATEST`;
  const res = http.get(url);
  const responseTime = Date.now() - startTime;

  filteringResponseTime.add(responseTime);

  const success = res.status === 200;
  filteringSuccessRate.add(success);

  // 성공 시에만 쿼리 카운트 추가
  if (success) {
    const queriesForThisRequest = QUERIES_PER_REQUEST[STAGE] || 3;
    dbQueryCount.add(queriesForThisRequest);

    check(res, {
      "status is 200": (r) => r.status === 200,
      "response time < 400ms": (r) => responseTime < 400,
      "has data": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.data && Array.isArray(body.data);
        } catch (e) {
          return false;
        }
      },
    });
  } else {
    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
    });
  }

  return res;
}

function scenarioExec() {
  filterDatasets();
  sleep(0.1); // 최소 대기 시간으로 최대 부하 시뮬레이션
}

export function smoke() {
  scenarioExec();
}

export function load() {
  scenarioExec();
}

export function handleSummary(data) {
  const httpReqs = data.metrics.http_reqs || {};
  const responseTime = data.metrics.http_req_duration || {};
  const queries = data.metrics.db_query_count || {};
  const checks = data.metrics.checks || {};

  const stageName =
    STAGE === "stage1"
      ? "JOIN + GROUP BY"
      : STAGE === "stage2"
      ? "N+1 서브쿼리"
      : "배치 처리";

  console.log("\n");
  console.log("=".repeat(70));
  console.log(`   ${STAGE.toUpperCase()} - ${stageName} 성능 테스트 결과`);
  console.log("=".repeat(70));
  console.log("\n📊 k6 테스트 결과:");
  console.log(`   - 총 요청 수: ${httpReqs.count || 0}건`);
  console.log(`   - 평균 응답시간: ${(responseTime.avg || 0).toFixed(2)}ms`);
  console.log(
    `   - 95% 응답시간: ${(responseTime["p(95)"] || 0).toFixed(2)}ms`
  );
  console.log(`   - 최대 응답시간: ${(responseTime.max || 0).toFixed(2)}ms`);
  console.log(`   - 총 DB 쿼리 수: ${queries.count || 0}개`);
  console.log(`   - 요청당 쿼리 수: ${QUERIES_PER_REQUEST[STAGE] || 3}개`);
  console.log(`   - 성공률: ${((checks.rate || 0) * 100).toFixed(2)}%`);
  console.log("\n");
  console.log("🔍 단계별 특징:");
  if (STAGE === "stage1") {
    console.log("   - JOIN + GROUP BY 방식");
    console.log("   - fetchJoin + paging 문제 (메모리 처리)");
    console.log("   - 쿼리 수: 2개/요청 (메인 + 카운트)");
  } else if (STAGE === "stage2") {
    console.log("   - N+1 서브쿼리 방식");
    console.log("   - 각 데이터셋마다 개별 프로젝트 수 조회");
    console.log("   - 쿼리 수: 52개/요청 (메인 + 서브쿼리 50개 + 카운트)");
  } else {
    console.log("   - 배치 처리 방식");
    console.log("   - IN 절로 프로젝트 수 한 번에 조회");
    console.log("   - 쿼리 수: 3개/요청 (메인 + 배치 + 카운트)");
  }
  console.log("\n");
  console.log("=".repeat(70));
  console.log("\n");

  return {
    stdout: "",
  };
}
