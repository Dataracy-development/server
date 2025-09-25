/**
 * ========================================
 * 데이터셋 업로드 성능 테스트
 * ========================================
 *
 * 테스트 목적: DataCommandController.uploadDataset() API 성능 검증
 *
 * 시나리오:
 * - smoke: 기본 기능 확인 (1 VU, 30초)
 * - load: 로드 테스트 (5→20 VU, 8분)
 * - stress: 스트레스 테스트 (10→50 VU, 10분)
 * - spike: 스파이크 테스트 (5→100 VU, 3분)
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke --env BASE_URL=http://localhost:8080 performance-test/dataset/scenarios/dataset-upload-complete.test.js
 * k6 run --env SCENARIO=load --env BASE_URL=http://localhost:8080 performance-test/dataset/scenarios/dataset-upload-complete.test.js
 * k6 run --env SCENARIO=stress --env BASE_URL=http://localhost:8080 performance-test/dataset/scenarios/dataset-upload-complete.test.js
 * k6 run --env SCENARIO=spike --env BASE_URL=http://localhost:8080 performance-test/dataset/scenarios/dataset-upload-complete.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const FILE_SIZE_MB = __ENV.FILE_SIZE_MB || "3";

// 인증 토큰
const authToken =
  "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTg2MTY5NjcsImV4cCI6MTc1ODYyMDU2NywidHlwZSI6IkFDQ0VTUyIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfVVNFUiJ9.ftT5-NoZqG8nUm9LSti_ZXCpQARvJqvARsldULTEhfk";

// 메트릭
const uploadSuccessRate = new Rate("dataset_upload_success_rate");
const uploadResponseTime = new Trend("dataset_upload_response_time");
const uploadAttempts = new Counter("dataset_upload_attempts");
const timeoutErrors = new Counter("dataset_timeout_errors");
const metadataParsingTime = new Trend("metadata_parsing_time");

export let options = {
  scenarios: {
    smoke: {
      executor: "constant-vus",
      vus: 1,
      duration: "30s",
      exec: "smoke",
    },
    load: {
      executor: "ramping-vus",
      startVUs: 5,
      exec: "load",
      stages: [
        { duration: "2m", target: 10 },
        { duration: "4m", target: 20 },
        { duration: "2m", target: 0 },
      ],
    },
    stress: {
      executor: "ramping-vus",
      startVUs: 10,
      exec: "stress",
      stages: [
        { duration: "2m", target: 25 },
        { duration: "4m", target: 50 },
        { duration: "2m", target: 0 },
      ],
    },
    spike: {
      executor: "ramping-vus",
      startVUs: 5,
      exec: "spike",
      stages: [
        { duration: "30s", target: 50 },
        { duration: "2m", target: 100 },
        { duration: "30s", target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.02"], // 2% 이하 실패율 (더 엄격)
    http_req_duration: ["p(95)<5000"], // 5초 이하 (더 엄격)
    dataset_upload_success_rate: ["rate>0.98"], // 98% 이상 성공 (더 엄격)
    dataset_upload_response_time: ["p(95)<10000"], // 10초 이하 (현실적)
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

// 파일 크기 결정
function determineFileSize() {
  return parseInt(FILE_SIZE_MB) * 1024; // KB로 변환
}

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

  const fileSize = determineFileSize();
  const fileContent = createTestFile(fileSize);
  const fileName = `test_dataset_${RUN_SCENARIO}_${Date.now()}.csv`;

  const formData = {
    dataFile: http.file(fileContent, fileName, "text/csv"),
    webRequest: JSON.stringify({
      title: `Test Dataset ${RUN_SCENARIO} ${Date.now()}`,
      description: `테스트용 데이터셋 - ${RUN_SCENARIO} 시나리오`,
      analysisGuide: `Performance test dataset for ${RUN_SCENARIO} scenario.`,
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

  const response = http.post(`${BASE_URL}/api/v1/datasets`, formData, {
    headers,
  });
  const responseTime = Date.now() - startTime;

  uploadResponseTime.add(responseTime);

  const isSuccess = check(response, {
    "upload status is 201": (r) => r.status === 201,
    "response time is acceptable": (r) => r.timings.duration < 10000,
    "response has data ID": (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.id;
      } catch (e) {
        return false;
      }
    },
  });

  uploadSuccessRate.add(isSuccess);

  if (!isSuccess && response.status === 0) {
    timeoutErrors.add(1);
  }

  if (isSuccess) {
    const dataId = JSON.parse(response.body).data.id;
    const parsingTime = waitForMetadataParsing(dataId);
    if (parsingTime > 0) {
      metadataParsingTime.add(parsingTime);
    }
  }

  return response;
}

// 메타데이터 파싱 완료 대기
function waitForMetadataParsing(dataId) {
  const maxAttempts = 10;
  const interval = 2000;
  const startTime = Date.now();

  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    const detailResponse = http.get(`${BASE_URL}/api/v1/datasets/${dataId}`, {
      headers: { Authorization: `Bearer ${authToken}` },
    });

    if (detailResponse.status === 200) {
      try {
        const detailData = JSON.parse(detailResponse.body);
        if (detailData.data && detailData.data.metadata) {
          const parsingTime = Date.now() - startTime;
          return parsingTime;
        }
      } catch (e) {
        // 파싱 실패 시 계속 대기
      }
    }

    if (attempt < maxAttempts) {
      sleep(interval / 1000);
    }
  }

  timeoutErrors.add(1);
  return 0;
}

// 시나리오별 실행 함수들
export function smoke() {
  uploadDataset();
  sleep(2);
}

export function load() {
  uploadDataset();
  sleep(Math.random() * 3 + 1);
}

export function stress() {
  uploadDataset();
  sleep(Math.random() * 2 + 0.5);
}

export function spike() {
  uploadDataset();
  sleep(Math.random() * 1 + 0.2);
}

export default function () {
  switch (RUN_SCENARIO) {
    case "smoke":
      smoke();
      break;
    case "load":
      load();
      break;
    case "stress":
      stress();
      break;
    case "spike":
      spike();
      break;
    default:
      load();
  }
}
