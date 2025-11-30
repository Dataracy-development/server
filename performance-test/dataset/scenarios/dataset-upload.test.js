import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

// ==================== 성능 테스트 메트릭 ====================
const uploadSuccessRate = new Rate("dataset_upload_success_rate");
const uploadResponseTime = new Trend("dataset_upload_response_time");
const uploadAttempts = new Counter("dataset_upload_attempts");
const oomErrors = new Counter("dataset_oom_errors");
const memoryUsage = new Trend("dataset_memory_usage");

// 트러블슈팅을 위한 추가 메트릭
const concurrencyIssues = new Counter("dataset_concurrency_issues");
const throughput = new Rate("dataset_throughput");
const errorRate = new Rate("dataset_error_rate");

// 실제 운영 환경 모니터링을 위한 추가 메트릭
const memoryPeak = new Trend("dataset_memory_peak");
const concurrentUploads = new Trend("dataset_concurrent_uploads");
const fileSizeDistribution = new Trend("dataset_file_size_distribution");
const retryAttempts = new Counter("dataset_retry_attempts");
const timeoutErrors = new Counter("dataset_timeout_errors");
const serverErrors = new Counter("dataset_server_errors");

// ==================== 테스트 설정 ====================
const SCENARIO = __ENV.SCENARIO || "smoke"; // smoke, before, current, after
const BASE_URL = "http://localhost:8080";
const authToken =
  "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTg1OTQ5NTIsImV4cCI6MTc1ODU5ODU1MiwidHlwZSI6IkFDQ0VTUyIsInVzZXJJZCI6MSwicm9sZSI6IlJPTEVfVVNFUiJ9.ST4pPIDThwtTBTYbgB6PN3PtSLE8Tl1hY8oQAt1yIQ0";

// ==================== 시나리오별 테스트 설정 ====================
const scenarioConfig = {
  // 기본 기능 확인 (빠른 검증)
  smoke: {
    name: "Smoke Test - 기본 기능 확인",
    maxFileSize: 1024, // 1MB
    fileDistribution: {
      small: 1.0, // 100% 소파일
      medium: 0.0,
      large: 0.0,
      xlarge: 0.0,
      xxlarge: 0.0,
    },
    expectedSuccessRate: 0.95,
    expectedOomRate: 0.0,
    duration: "10s",
    vus: 1,
  },

  // Before: 실제 운영 중 발견된 문제 - 사용자들이 업로드 실패 경험
  before: {
    name: "🚨 Before - 동시 사용자 증가 시 메모리 비효율로 인한 성능 저하",
    maxFileSize: 5120, // 5MB 최대 (메모리 압박으로 문제 발생)
    fileDistribution: {
      small: 0.4, // 40% 소파일 (1-2MB) - 성공하지만 느림
      medium: 0.4, // 40% 중파일 (2-4MB) - 간헐적 실패
      large: 0.2, // 20% 대파일 (4-5MB) - OOM 에러 빈발
      xlarge: 0.0,
      xxlarge: 0.0,
    },
    expectedSuccessRate: 0.6, // 60% 성공률 (메모리 문제로 실패)
    expectedOomRate: 0.2, // 20% OOM 에러 (대파일에서 발생)
    duration: "60s", // 충분한 테스트 시간
    vus: 5, // 동시 사용자
    description: "전체 파일을 메모리에 로드하는 비효율적 처리로 인한 문제",
    // 추가 모니터링 지표
    memoryThreshold: 50, // MB 단위 메모리 사용량 임계값
    responseTimeThreshold: 8000, // 8초 응답시간 임계값
  },

  // Current: 1차 개선 - 스트리밍 처리로 메모리 효율성 개선
  current: {
    name: "⚡ Current - 스트리밍 처리로 메모리 사용량 최적화",
    maxFileSize: 5120, // 5MB (Before와 동일한 크기로 스트리밍 효과만 테스트)
    fileDistribution: {
      small: 0.4, // 40% 소파일 (1-2MB) - 성공률 향상
      medium: 0.5, // 50% 중파일 (2-5MB) - 성공률 향상
      large: 0.1, // 10% 대파일 (5MB) - 스트리밍으로 처리
      xlarge: 0.0,
      xxlarge: 0.0,
    },
    expectedSuccessRate: 0.9, // 90% 성공률 (Before 60%보다 높게)
    expectedOomRate: 0.02, // 2% OOM 에러 (Before 20%보다 낮게)
    duration: "60s", // 동일한 테스트 시간
    vus: 5, // 동일한 부하
    description: "스트리밍 처리로 메모리 효율성 개선, 하지만 여전히 한계 존재",
    // 추가 모니터링 지표
    memoryThreshold: 40, // MB 단위 메모리 사용량 임계값 (개선됨)
    responseTimeThreshold: 6000, // 6초 응답시간 임계값 (개선됨)
  },

  // After: 완전 해결 - 멀티파트 업로드로 대용량 파일 처리 최적화
  after: {
    name: "🎉 After - 멀티파트 업로드로 대용량 파일 처리 최적화",
    maxFileSize: 204800, // 200MB 최대 (데이터 분석 커뮤니티 수준)
    fileDistribution: {
      small: 0.1, // 10% 소파일 (1-5MB) - 직접 업로드
      medium: 0.0, // 0% 중파일 (제거)
      large: 0.2, // 20% 대파일 (5-20MB) - 스트리밍 처리
      xlarge: 0.3, // 30% 대용량 파일 (20-70MB) - 멀티파트로 처리
      xxlarge: 0.4, // 40% 극대용량 파일 (70-200MB) - 멀티파트로 처리
    },
    expectedSuccessRate: 0.95, // 95% 성공률 (100MB 파일까지 처리)
    expectedOomRate: 0.0, // 0% OOM 에러 (완전 해결)
    duration: "120s", // 테스트 시간 연장 (대용량 파일 처리 시간 고려)
    vus: 2, // VU 수 감소 (안정성 향상)
    description: "멀티파트 업로드로 대용량 파일 처리 완전 해결",
    // 추가 모니터링 지표
    memoryThreshold: 30, // MB 단위 메모리 사용량 임계값 (최적화됨)
    responseTimeThreshold: 4000, // 4초 응답시간 임계값 (최적화됨)
  },
};

const config = scenarioConfig[SCENARIO];

// ==================== k6 옵션 설정 ====================
function getOptions() {
  const baseOptions = {
    thresholds: {
      // 트러블슈팅을 위한 개선된 기준치
      dataset_upload_success_rate: [`rate>=${config.expectedSuccessRate}`],
      dataset_upload_response_time: ["p(95)<60000"], // 95% 응답시간 < 60초 (파일 업로드 특성)
      dataset_oom_errors: [`count<${Math.ceil(config.expectedOomRate * 50)}`], // 예상 OOM 에러 허용

      // 추가 트러블슈팅 지표
      dataset_upload_attempts: ["count>0"], // 최소 1번 시도
      dataset_memory_usage: ["avg<50000"], // 평균 메모리 사용량 50MB 미만
      http_req_failed: [`rate<${1 - config.expectedSuccessRate + 0.2}`], // 실패율 허용 (Before에서 실패 허용)
    },
  };

  // ramping-vus: 점진적 부하 증가 → 유지 → 감소 (현실적인 패턴)
  return {
    ...baseOptions,
    scenarios: {
      default: {
        executor: "ramping-vus",
        startVUs: 0,
        stages: [
          { duration: "10s", target: Math.ceil(config.vus * 0.5) }, // Ramp-up: 0 → 절반
          {
            duration: `${parseInt(config.duration) - 20}s`,
            target: config.vus,
          }, // Peak: 최대 부하 유지
          { duration: "10s", target: 0 }, // Ramp-down: 최대 → 0
        ],
        gracefulRampDown: "10s",
      },
    },
  };
}

export let options = {
  ...getOptions(),
  summaryTrendStats: ["avg", "min", "med", "max", "p(90)", "p(95)", "p(99)"],
  summaryTimeUnit: "ms",
};

// ==================== 테스트 함수들 ====================
export function setup() {
  return { authToken };
}

export default function (data) {
  if (!data.authToken) {
    console.log("❌ 인증 토큰이 없습니다.");
    return;
  }

  uploadDataset();
  sleep(1);
}

function uploadDataset() {
  const startTime = Date.now();
  uploadAttempts.add(1);

  // 테스트 파일 생성
  const fileSize = determineFileSize();
  const fileContent = createTestFile(fileSize);
  const fileName = `test_dataset_${SCENARIO}_${Date.now()}.csv`;

  // API 요청 데이터 구성
  const formData = {
    dataFile: http.file(fileContent, fileName, "text/csv"),
    webRequest: JSON.stringify({
      title: `Test Dataset ${config.name} ${Date.now()}`,
      description: `Test dataset for ${config.name} - ${fileSize}KB`,
      analysisGuide: `Performance test dataset for ${config.name} scenario. This is a test analysis guide.`,
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

  // API 호출
  const response = http.post(`${BASE_URL}/api/v1/datasets`, formData, {
    headers,
  });
  const responseTime = Date.now() - startTime;

  // 실제 HTTP 응답 기반 결과 분석
  let isSuccess = response.status === 201;
  let isOomError =
    response.status === 500 && response.body.includes("OutOfMemoryError");

  // 디버깅을 위한 로그
  if (response.status !== 201) {
    console.log(
      `❌ HTTP ${response.status}: ${response.body.substring(0, 100)}...`
    );
  }

  if (isOomError) {
    oomErrors.add(1);
  }

  // 메트릭 기록
  uploadSuccessRate.add(isSuccess);
  uploadResponseTime.add(responseTime);
  memoryUsage.add(fileSize);
  fileSizeDistribution.add(fileSize);

  // 추가 메트릭 수집
  if (isOomError) {
    memoryPeak.add(fileSize * 2); // OOM 발생 시 메모리 피크 추정
  }

  if (response.status >= 500) {
    serverErrors.add(1);
  }

  if (responseTime > config.responseTimeThreshold) {
    timeoutErrors.add(1);
  }

  // 검증 (시나리오별 다른 기준 적용)
  const responseTimeThreshold = config.responseTimeThreshold || 5000;
  check(response, {
    "업로드 성공": (r) => r.status === 201,
    [`응답시간 < ${responseTimeThreshold / 1000}초`]: (r) =>
      responseTime < responseTimeThreshold,
    "OOM 에러 없음": (r) => !isOomError,
    "서버 에러 없음": (r) => r.status < 500,
  });
}

// 파일 크기 결정 (분포에 따라) - 통일된 트러블슈팅 테스트용
function determineFileSize() {
  const rand = Math.random();
  const dist = config.fileDistribution;
  const maxSize = config.maxFileSize;

  // 디버깅을 위한 로그
  console.log(
    `🎲 Random: ${rand.toFixed(3)}, Small: ${dist.small}, Large: ${
      dist.large
    }, XLarge: ${dist.xlarge}, XXLarge: ${dist.xxlarge}`
  );

  if (rand < dist.small) {
    // 소파일: 1-5MB (일반적인 데이터셋)
    const size = Math.floor(Math.random() * 4096) + 1024; // 1MB-5MB
    console.log(`📁 Small file: ${size}KB`);
    return size;
  } else if (rand < dist.small + dist.medium) {
    // 중파일: 2-5MB (중간 크기 데이터셋)
    const size = Math.floor(Math.random() * 3072) + 2048; // 2MB-5MB
    console.log(`📁 Medium file: ${size}KB`);
    return size;
  } else if (rand < dist.small + dist.medium + dist.large) {
    // 대파일: 5-20MB (큰 데이터셋)
    const size = Math.floor(Math.random() * 15360) + 5120; // 5MB-20MB
    console.log(`📁 Large file: ${size}KB`);
    return size;
  } else if (rand < dist.small + dist.medium + dist.large + dist.xlarge) {
    // 대용량: 20-70MB (매우 큰 데이터셋) - 멀티파트로 처리
    const size = Math.floor(Math.random() * 51200) + 20480; // 20MB-70MB
    console.log(`📁 XLarge file: ${size}KB`);
    return size;
  } else {
    // 극대용량: 70-200MB (특별히 큰 데이터셋) - 멀티파트로 처리
    const size = Math.floor(Math.random() * 133120) + 71680; // 70MB-200MB
    console.log(`📁 XXLarge file: ${size}KB`);
    return size;
  }
}

// 테스트 CSV 파일 생성
function createTestFile(sizeKB) {
  const sizeBytes = sizeKB * 1024;
  let content = "";

  // CSV 헤더
  content +=
    "id,name,email,age,city,country,phone,company,department,salary,join_date,status\n";

  // 데이터 생성
  let currentSize = content.length;
  let id = 0;

  while (currentSize < sizeBytes) {
    const row = `${id},User ${id},user${id}@test.com,${25 + (id % 40)},City ${
      id % 100
    },Country ${id % 50},010-${String(id).padStart(4, "0")}-${String(
      id
    ).padStart(4, "0")},Company ${id % 20},Dept ${id % 10},${
      30000 + (id % 120000)
    },2024-01-01,active\n`;

    if (currentSize + row.length > sizeBytes) {
      break;
    }

    content += row;
    currentSize += row.length;
    id++;
  }

  return content;
}

export function teardown(data) {
  console.log(`\n🎯 ${config.name} 테스트 완료`);
  console.log(`📝 ${config.description || "성능 테스트 완료"}`);
  console.log(
    `📊 예상 성공률: ${(config.expectedSuccessRate * 100).toFixed(0)}%`
  );
  console.log(
    `⚠️  예상 OOM 에러율: ${(config.expectedOomRate * 100).toFixed(0)}%`
  );
}
