/**
 * ========================================
 * Kafka 비동기 메타데이터 파싱 성능 테스트
 * ========================================
 *
 * 테스트 목적: 동기 vs 비동기 메타데이터 파싱 성능 비교
 *
 * 시나리오:
 * - MODE=sync: 동기 처리 (Before)
 * - MODE=async: 비동기 처리 (After)
 *
 * 실행 명령어:
 * # Before (동기 처리)
 * k6 run --env MODE=sync performance-test/dataset/scenarios/dataset-upload-complete.test.js
 *
 * # After (비동기 처리)
 * k6 run --env MODE=async performance-test/dataset/scenarios/dataset-upload-complete.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const PROCESSING_MODE = __ENV.MODE || "async"; // "sync" or "async"
const FILE_SIZE_KB = 3072; // 3MB 고정 (공정한 비교)

// 인증 토큰
const authToken =
  "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTg2MTY5NjcsImV4cCI6MTc1ODYyMDU2NywidHlwZSI6IkFDQ0VTUyIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfVVNFUiJ9.ftT5-NoZqG8nUm9LSti_ZXCpQARvJqvARsldULTEhfk";

// 메트릭
const uploadSuccessRate = new Rate("dataset_upload_success_rate");
const uploadResponseTime = new Trend("dataset_upload_response_time");
const uploadAttempts = new Counter("dataset_upload_attempts");
const timeoutErrors = new Counter("dataset_timeout_errors");
const fileSizeDistribution = new Trend("dataset_file_size_distribution");

export let options = {
  scenarios: {
    default: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "10s", target: 3 }, // Ramp-up: 0 → 3 VU
        { duration: "40s", target: 5 }, // Peak: 5 VU 유지
        { duration: "10s", target: 0 }, // Ramp-down: 5 → 0 VU
      ],
      gracefulRampDown: "10s",
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.02"],
    dataset_upload_success_rate: ["rate>0.90"],
    dataset_upload_response_time: ["p(95)<10000"],
    dataset_timeout_errors: ["count<10"],
  },
};

// 테스트 파일 생성
function createTestFile(sizeKB) {
  const rows = Math.floor((sizeKB * 1024) / 50);
  let csvContent = "id,name,email,age,city\n";

  for (let i = 1; i <= rows; i++) {
    csvContent += `${i},User${i},user${i}@example.com,${20 + (i % 50)},City${
      i % 10
    }\n`;
  }

  return csvContent;
}

// 데이터셋 업로드 함수
function uploadDataset() {
  const startTime = Date.now();
  uploadAttempts.add(1);

  const fileContent = createTestFile(FILE_SIZE_KB);
  const fileName = `test_dataset_${PROCESSING_MODE}_${Date.now()}.csv`;

  const formData = {
    dataFile: http.file(fileContent, fileName, "text/csv"),
    webRequest: JSON.stringify({
      title: `Test Dataset ${PROCESSING_MODE} ${Date.now()}`,
      description: `테스트용 데이터셋 - ${PROCESSING_MODE} 모드`,
      analysisGuide: `Performance test dataset for ${PROCESSING_MODE} mode.`,
      topicId: 1,
      dataSourceId: 1,
      dataTypeId: 1,
      startDate: "2024-01-01",
      endDate: "2024-12-31",
    }),
  };

  const headers = {
    Authorization: `Bearer ${authToken}`,
  };

  // 동기/비동기 모드에 따라 다른 엔드포인트 사용
  const endpoint =
    PROCESSING_MODE === "sync"
      ? "/api/v1/datasets/sync" // 동기 처리 엔드포인트
      : "/api/v1/datasets"; // 비동기 처리 엔드포인트

  const response = http.post(`${BASE_URL}${endpoint}`, formData, {
    headers,
  });
  const responseTime = Date.now() - startTime;

  uploadResponseTime.add(responseTime);
  fileSizeDistribution.add(FILE_SIZE_KB);

  const isSuccess = check(response, {
    "업로드 성공": (r) => r.status === 201,
    "타임아웃 없음": (r) => r.status !== 0,
  });

  uploadSuccessRate.add(isSuccess);

  if (!isSuccess && response.status === 0) {
    timeoutErrors.add(1);
  }

  return response;
}

export default function () {
  uploadDataset();
  sleep(1); // 다음 업로드까지 1초 대기
}
