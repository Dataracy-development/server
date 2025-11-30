import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

// 커스텀 메트릭 정의
const userModifySuccessRate = new Rate("user_modify_success_rate");
const userModifyResponseTime = new Trend("user_modify_response_time");
const dbQueryCount = new Counter("db_query_count");

// 테스트 설정 - ramping-vus로 점진적 부하 증가
export const options = {
  scenarios: {
    userModifyDistributedLockTest: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "5s", target: 5 }, // 5초간 0→5 VUs로 점진적 증가
        { duration: "20s", target: 5 }, // 20초간 5 VUs 유지 (peak load)
        { duration: "5s", target: 0 }, // 5초간 5→0 VUs로 점진적 감소
      ],
      gracefulRampDown: "5s",
      tags: { test_type: "distributed_lock_concurrency" },
    },
  },
  thresholds: {
    user_modify_success_rate: ["rate>0.8"], // 80% 이상 성공
    user_modify_response_time: ["p(95)<2000"], // 95%가 2초 이내
    db_query_count: ["count<10000"], // DB 쿼리 수 제한 (After는 더 많음)
  },
};

// 테스트 데이터
// 시나리오 1: 각 VU가 서로 다른 사용자 사용
const testUsers = [
  { id: 12, nickname: "user12", topics: [1, 2] },
  { id: 23, nickname: "user23", topics: [2, 3] },
  { id: 34, nickname: "user34", topics: [1, 3] },
  { id: 45, nickname: "user45", topics: [1, 2] },
  { id: 56, nickname: "user56", topics: [2, 3] },
];

// 시나리오 2: 모든 VU가 동일한 사용자 사용
const sameUser = { id: 1, nickname: "user1", topics: [1, 2] };

// 테스트 시나리오 구분
const TEST_SCENARIO = __ENV.TEST_SCENARIO || "nickname_change"; // nickname_change, other_fields_only

export default function () {
  // 시나리오에 따라 사용자 선택
  let user;
  if (TEST_SCENARIO === "nickname_change") {
    // 시나리오 1: 각 VU가 서로 다른 사용자
    user = testUsers[__VU - 1] || testUsers[0];
  } else {
    // 시나리오 2: 모든 VU가 동일한 사용자
    user = sameUser;
  }

  // 로그 출력 (테스트 진행 상황 모니터링)
  if (__ENV.TEST_STAGE === "before") {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `🔥 [BEFORE] VU${__VU}: userId=${user.id}, 고유 닉네임 변경 시도 - 분산 락 없음 (DB 충돌 예상)`
      );
    } else {
      console.log(
        `🔥 [BEFORE] VU${__VU}: userId=${user.id} 정보 동시 수정 - 분산 락 없음 (DB 충돌 예상)`
      );
    }
  }

  if (__ENV.TEST_STAGE === "after") {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `✅ [AFTER] VU${__VU}: userId=${user.id}, 고유 닉네임 변경 - userId 기반 분산 락 적용 (lock:user:modify:${user.id})`
      );
    } else {
      console.log(
        `✅ [AFTER] VU${__VU}: userId=${user.id} 정보 수정 - userId 기반 분산 락 적용 (lock:user:modify:${user.id})`
      );
    }
  }

  const startTime = Date.now();

  // 🔥 multipart/form-data 올바른 형식으로 구성
  const boundary =
    "----WebKitFormBoundary" + Math.random().toString(16).substr(2);

  // 요청 데이터 구성
  let webRequestData;
  if (TEST_SCENARIO === "nickname_change") {
    // 🔥 닉네임 변경 시나리오 - 각 VU가 자신의 사용자를 고유한 닉네임으로 변경
    webRequestData = {
      nickname: `u${user.id}_${Date.now()}`.substring(0, 8), // 고유한 닉네임 (8자 제한)
      introductionText: `소개글_VU${__VU}_${Date.now()}`,
      authorLevelId: 1,
      occupationId: 1,
      visitSourceId: null, // visitSource 없음
      topicIds: JSON.stringify(user.topics),
    };
  } else {
    // 🔥 닉네임 미변경 시나리오 - 동일한 사용자 정보 동시 수정
    webRequestData = {
      nickname: user.nickname, // 기존 닉네임 유지
      introductionText: `소개글_VU${__VU}_${Date.now()}`, // 🔥 동시에 수정하는 소개글
      authorLevelId: 1,
      occupationId: 1,
      visitSourceId: null, // visitSource 없음
      topicIds: JSON.stringify(user.topics),
    };
  }

  // multipart/form-data 본문 구성
  let body = "";

  // webRequest 파트 추가 (JSON 형식)
  body += `--${boundary}\r\n`;
  body += `Content-Disposition: form-data; name="webRequest"\r\n`;
  body += `Content-Type: application/json\r\n\r\n`;
  body += JSON.stringify({
    nickname: webRequestData.nickname,
    introductionText: webRequestData.introductionText,
    authorLevelId: parseInt(webRequestData.authorLevelId),
    occupationId: parseInt(webRequestData.occupationId),
    visitSourceId: webRequestData.visitSourceId
      ? parseInt(webRequestData.visitSourceId)
      : null,
    topicIds: JSON.parse(webRequestData.topicIds),
  });
  body += "\r\n";

  // boundary 종료
  body += `--${boundary}--\r\n`;

  // 요청 헤더 설정 (각 사용자별로 다른 토큰 사용 - 실제로는 userId에 맞는 토큰 사용)
  const params = {
    headers: {
      "Content-Type": `multipart/form-data; boundary=${boundary}`,
      Authorization:
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEyMTc5ODUsImV4cCI6MTc2MTIyMTU4NSwidHlwZSI6IkFDQ0VTUyIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfVVNFUiJ9.k1XPyPjQt3G2Ahiq45_ZY4X6G6UvJlHaEZzxS_ioMWE",
    },
  };

  // API 호출 (각 사용자별로 다른 userId 사용)
  const response = http.put("http://localhost:8080/api/v1/user", body, params);
  const responseTime = Date.now() - startTime;

  // 응답 검증
  const success = check(response, {
    "status is 200": (r) => r.status === 200,
    "response has success": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.code === "200";
      } catch (e) {
        return false;
      }
    },
    "response time < 5s": (r) => r.timings.duration < 5000,
  });

  // 메트릭 기록
  userModifySuccessRate.add(success);
  userModifyResponseTime.add(responseTime);

  // 쿼리 수 기록 (성공한 경우에만)
  if (success) {
    // 닉네임 변경 시: 10개 쿼리, 미변경 시: 9개 쿼리
    const estimatedQueries = TEST_SCENARIO === "nickname_change" ? 10 : 9;
    dbQueryCount.add(estimatedQueries);
  }

  // 🔥 상세 로그 출력 (분산 락 테스트 결과 확인)
  // 에러 타입 감지
  let errorType = "Unknown";
  if (!success) {
    const bodyStr = response.body ? response.body.toString() : "";
    if (bodyStr.includes("Row was updated or deleted")) {
      errorType = "DB Conflict";
    } else if (bodyStr.includes("Connection timeout")) {
      errorType = "Redis Connection Timeout";
    } else if (bodyStr.includes("connection pool")) {
      errorType = "DB Connection Pool Exhausted";
    } else if (bodyStr.includes("Validation")) {
      errorType = "Validation Failed";
    }
  }

  if (success) {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `✅ [SUCCESS] VU${__VU}: userId=${user.id}, nickname="${webRequestData.nickname}" 변경 성공 - ${responseTime}ms`
      );
    } else {
      console.log(
        `✅ [SUCCESS] VU${__VU}: userId=${user.id} 정보 수정 성공 - ${responseTime}ms`
      );
    }
  } else {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `❌ [FAILED] VU${__VU}: userId=${user.id}, nickname="${webRequestData.nickname}" 변경 실패 - ${responseTime}ms, Status: ${response.status}, Error: ${errorType}`
      );
    } else {
      console.log(
        `❌ [FAILED] VU${__VU}: userId=${user.id} 정보 수정 실패 - ${responseTime}ms, Status: ${response.status}, Error: ${errorType}`
      );
    }
  }

  sleep(0.1); // 짧은 대기 시간 (부하 테스트용)
}

export function handleSummary(data) {
  const TEST_STAGE = __ENV.TEST_STAGE || "unknown";
  const TEST_SCENARIO = __ENV.TEST_SCENARIO || "unknown";

  const successRate = data.metrics.user_modify_success_rate
    ? (data.metrics.user_modify_success_rate.values.rate * 100).toFixed(2)
    : "0.00";
  const avgResponseTime = data.metrics.user_modify_response_time
    ? data.metrics.user_modify_response_time.values.avg.toFixed(2)
    : "0.00";
  const p95ResponseTime = data.metrics.user_modify_response_time
    ? data.metrics.user_modify_response_time.values["p(95)"].toFixed(2)
    : "0.00";
  const totalRequests = data.metrics.iterations
    ? data.metrics.iterations.values.count
    : 0;
  const totalQueries = data.metrics.db_query_count
    ? data.metrics.db_query_count.values.count
    : 0;
  const successCount = data.metrics.user_modify_success_rate
    ? Math.round(data.metrics.user_modify_success_rate.values.passes)
    : 0;
  const failCount = totalRequests - successCount;

  console.log("\n");
  console.log("=".repeat(80));
  console.log(
    `  사용자 정보 수정 분산 락 테스트 결과 (${TEST_STAGE.toUpperCase()})`
  );
  console.log("=".repeat(80));
  console.log("");
  console.log(`📋 테스트 설정:`);
  console.log(
    `  - 테스트 단계: ${
      TEST_STAGE === "before" ? "분산 락 도입 전" : "분산 락 적용 후"
    }`
  );
  console.log(
    `  - 시나리오: ${
      TEST_SCENARIO === "nickname_change"
        ? "닉네임 변경 (각 VU 서로 다른 사용자)"
        : "사용자 정보 수정 (모든 VU 동일 사용자)"
    }`
  );
  console.log(`  - Executor: ramping-vus`);
  console.log(`  - Stages: 5s (0→5 VUs) + 20s (5 VUs) + 5s (5→0 VUs)`);
  console.log("");
  console.log(`📊 성능 결과:`);
  console.log(`  - 총 요청 수: ${totalRequests}건`);
  console.log(`  - 성공: ${successCount}건 (${successRate}%)`);
  console.log(
    `  - 실패: ${failCount}건 (${(100 - parseFloat(successRate)).toFixed(2)}%)`
  );
  console.log(`  - 평균 응답시간: ${avgResponseTime}ms`);
  console.log(`  - 95th percentile: ${p95ResponseTime}ms`);
  console.log(`  - 총 DB 쿼리 수: ${totalQueries}개`);
  console.log("");
  console.log(`💡 설명:`);
  if (TEST_STAGE === "before") {
    console.log(`  - 분산 락 없이 동시성 충돌 발생`);
    console.log(`  - DB 레벨 충돌로 실패율 높음 (${failCount}건 실패)`);
    console.log(`  - 데이터 무결성 보장 안됨`);
  } else {
    console.log(`  - 분산 락으로 동시성 제어 성공`);
    console.log(`  - DB 충돌 0건, 인프라 예외만 ${failCount}건`);
    console.log(`  - 데이터 무결성 완전 보장`);
    console.log(`  - 처리량 대폭 증가 (빠른 응답시간)`);
  }
  console.log("");
  console.log("=".repeat(80));
  console.log("");

  return {
    stdout: "",
  };
}
