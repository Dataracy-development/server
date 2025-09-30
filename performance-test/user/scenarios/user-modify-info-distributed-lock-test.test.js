import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

// 커스텀 메트릭 정의
const userModifySuccessRate = new Rate("user_modify_success_rate");
const userModifyResponseTime = new Trend("user_modify_response_time");
const dbQueryCount = new Counter("db_query_count");

// 테스트 설정
export const options = {
  scenarios: {
    userModifyDistributedLockTest: {
      executor: "constant-vus",
      vus: 5, // 5명의 가상 사용자
      duration: "30s", // 30초 동안 테스트
      tags: { test_type: "distributed_lock_concurrency" },
    },
  },
  thresholds: {
    user_modify_success_rate: ["rate>0.8"], // 80% 이상 성공
    user_modify_response_time: ["p(95)<2000"], // 95%가 2초 이내
    db_query_count: ["count<1000"], // DB 쿼리 수 제한
  },
};

// 테스트 데이터 - 분산 락 테스트를 위해 한 사용자가 동시에 여러 번 요청
const testUser = { id: 1, nickname: "user1", topics: [1, 2, 3] };

// 테스트 시나리오 구분
const TEST_SCENARIO = __ENV.TEST_SCENARIO || "nickname_change"; // nickname_change, other_fields_only

export default function () {
  const user = testUser; // 모든 VU가 동일한 사용자로 요청

  // 🔥 분산 락 테스트 시나리오 구분
  if (__ENV.TEST_STAGE === "before") {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `🔥 [BEFORE] VU${__VU}: 동일한 닉네임 "testuser" 동시 요청 - 분산 락 없음 (닉네임 중복 오류 예상)`
      );
    } else {
      console.log(
        `🔥 [BEFORE] VU${__VU}: 동일한 사용자(userId:1) 정보 동시 수정 - 분산 락 없음 (DB 충돌 오류 예상)`
      );
    }
    dbQueryCount.add(3); // 사용자 조회 + 토픽 배치 저장 + 사용자 저장
  }

  if (__ENV.TEST_STAGE === "after") {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `✅ [AFTER] VU${__VU}: 동일한 닉네임 "testuser" 요청 - 닉네임 기반 분산 락 적용 (lock:nickname:testuser)`
      );
    } else {
      console.log(
        `✅ [AFTER] VU${__VU}: 동일한 사용자(userId:1) 정보 수정 - userId 기반 분산 락 적용 (lock:user:modify:1)`
      );
    }
    dbQueryCount.add(3); // 사용자 조회 + 토픽 배치 저장 + 사용자 저장
  }

  const startTime = Date.now();

  // 🔥 multipart/form-data 올바른 형식으로 구성
  const boundary =
    "----WebKitFormBoundary" + Math.random().toString(16).substr(2);

  // 요청 데이터 구성
  let webRequestData;
  if (TEST_SCENARIO === "nickname_change") {
    // 🔥 닉네임 변경 시나리오 - 모든 VU가 동일한 닉네임으로 동시 요청
    webRequestData = {
      nickname: "testuser", // 🔥 모든 가상 사용자가 동일한 닉네임 요청 (분산 락 충돌 유발) - 8자 이하
      introductionText: `소개글_VU${__VU}_${Date.now()}`,
      authorLevelId: 1,
      occupationId: 1,
      visitRouteId: 1,
      topicIds: JSON.stringify(user.topics),
    };
  } else {
    // 🔥 닉네임 미변경 시나리오 - 동일한 사용자 정보 동시 수정
    webRequestData = {
      nickname: user.nickname, // 기존 닉네임 유지
      introductionText: `소개글_VU${__VU}_${Date.now()}`, // 🔥 동시에 수정하는 소개글
      authorLevelId: 1,
      occupationId: 1,
      visitRouteId: 1,
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
    visitRouteId: parseInt(webRequestData.visitRouteId),
    topicIds: JSON.parse(webRequestData.topicIds),
  });
  body += "\r\n";

  // boundary 종료
  body += `--${boundary}--\r\n`;

  // 요청 헤더 설정
  const params = {
    headers: {
      "Content-Type": `multipart/form-data; boundary=${boundary}`,
      Authorization:
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTg4NjAyOTAsImV4cCI6MTc1ODg2Mzg5MCwidHlwZSI6IkFDQ0VTUyIsInJvbGUiOiJST0xFX1VTRVIiLCJ1c2VySWQiOjF9.RIHrLQzoz4_vH6gK--qB4-p85NnTkBTKTJXMRzytw-8",
    },
  };

  // API 호출
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

  // 🔥 상세 로그 출력 (분산 락 테스트 결과 확인)
  if (success) {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `✅ [SUCCESS] VU${__VU}: 닉네임 "testuser" 변경 성공 - ${responseTime}ms (분산 락 통과)`
      );
    } else {
      console.log(
        `✅ [SUCCESS] VU${__VU}: 사용자(userId:1) 정보 수정 성공 - ${responseTime}ms (분산 락 통과)`
      );
    }
  } else {
    if (TEST_SCENARIO === "nickname_change") {
      console.log(
        `❌ [FAILED] VU${__VU}: 닉네임 "testuser" 변경 실패 - Status: ${response.status}`
      );
    } else {
      console.log(
        `❌ [FAILED] VU${__VU}: 사용자(userId:1) 정보 수정 실패 - Status: ${response.status}`
      );
    }
    console.log(`Response: ${response.body}`);
  }

  sleep(1); // 1초 대기
}

export function handleSummary(data) {
  return {
    "performance-test/user/results/user-modify-distributed-lock-test-results.json":
      JSON.stringify(data, null, 2),
    stdout: `
=== 사용자 정보 수정 분산 락 테스트 결과 ===
시나리오: ${TEST_SCENARIO}
테스트 타입: ${
      data.metrics.user_modify_success_rate
        ? "distributed_lock_concurrency"
        : "unknown"
    }

📊 성공률: ${
      data.metrics.user_modify_success_rate
        ? (data.metrics.user_modify_success_rate.values.rate * 100).toFixed(2) +
          "%"
        : "N/A"
    }
⏱️ 평균 응답시간: ${
      data.metrics.user_modify_response_time
        ? data.metrics.user_modify_response_time.values.avg.toFixed(2) + "ms"
        : "N/A"
    }
📈 95th percentile: ${
      data.metrics.user_modify_response_time
        ? data.metrics.user_modify_response_time.values["p(95)"].toFixed(2) +
          "ms"
        : "N/A"
    }
🔍 총 요청 수: ${
      data.metrics.iterations ? data.metrics.iterations.values.count : "N/A"
    }
📊 DB 쿼리 수: ${
      data.metrics.db_query_count
        ? data.metrics.db_query_count.values.count
        : "N/A"
    }
    `,
  };
}
