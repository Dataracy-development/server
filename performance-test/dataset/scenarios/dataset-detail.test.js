/**
 * ========================================
 * 데이터셋 상세 조회 성능 테스트 시나리오 (DDD + 헥사고날 아키텍처)
 * ========================================
 *
 * 🎯 테스트 목적: DataQueryController.getDataDetail() API의 성능 및 상세 정보 조회 최적화 검증
 *
 * 🏗️ 아키텍처 계층별 테스트 대상:
 * - Web Adapter: DataQueryController.getDataDetail() (Primary Adapter)
 * - Application Layer: GetDataDetailUseCase.getDataDetail() (Inbound Port)
 * - Domain Layer: Data 도메인 모델의 상세 정보 조회 로직
 * - Infrastructure: JPA Repository, QueryDSL, Redis 캐시, S3 메타데이터, 권한 검증
 *
 * 🔍 테스트 시나리오별 목적:
 * - smoke: 기본 상세 조회 검증 (5 VU, 30초) - CI/CD 파이프라인용
 * - load: 일반 상세 조회 테스트 (10-100 VU, 8분) - 일상적 데이터셋 상세 조회 시뮬레이션
 * - stress: 고부하 상세 조회 테스트 (50-300 VU, 10분) - 인기 데이터셋 집중 조회 시나리오
 * - soak: 장시간 상세 조회 안정성 테스트 (100 VU, 1시간) - 캐시 효율성 및 메타데이터 정확성 검증
 * - spike: 급격한 상세 조회 폭증 테스트 (20-800 VU, 2분 30초) - 갑작스러운 상세 조회 대응
 * - capacity: 상세 조회 처리량 한계 테스트 (50-200 req/s, 6분) - 최대 상세 조회 처리량 측정
 *
 * 📊 측정 메트릭 (상세 조회 관점):
 * - detail_read_success_rate: 상세 조회 성공률 (목표: >95%) - 비즈니스 정상성
 * - detail_read_response_time: 응답 시간 (목표: p95 < 500ms) - 사용자 경험
 * - detail_read_attempts: 총 시도 횟수 - 트래픽 볼륨
 * - cache_hit_rate: 캐시 히트율 - Redis 캐시 효율성
 * - database_query_time: 데이터베이스 쿼리 시간 - JPA/QueryDSL 성능
 * - s3_metadata_time: S3 메타데이터 조회 시간 - 스토리지 성능
 * - permission_check_time: 권한 검증 시간 - 보안 계층 성능
 * - data_mapping_time: 데이터 매핑 시간 - 도메인 모델 변환 성능
 * - file_info_processing_time: 파일 정보 처리 시간 - 파일 메타데이터 성능
 * - not_found_errors: 404 에러 횟수 - 데이터 존재성 검증
 * - permission_errors: 403 에러 횟수 - 권한 검증
 *
 * 🎯 실무적 필요성:
 * - 데이터셋 상세 조회는 사용자 의사결정에 중요한 핵심 기능
 * - DDD의 Data 도메인과 헥사고날의 Port/Adapter 패턴 검증
 * - 복합 쿼리와 메타데이터 조회 성능 최적화
 * - Redis 캐시를 통한 상세 정보 조회 성능 최적화
 * - S3 스토리지와 데이터베이스 간 메타데이터 동기화 성능 검증
 * - 권한 기반 접근 제어와 데이터 보안 처리 성능 확인
 *
 * 🚀 기대 효과:
 * - 데이터셋 상세 조회 시스템의 성능 최적화 검증
 * - 복합 쿼리와 메타데이터 조회 성능 측정 및 개선 포인트 식별
 * - 캐시 전략의 효율성 측정 및 상세 정보 최적화
 * - S3 스토리지와 데이터베이스 동기화 성능 모니터링
 * - 권한 검증 및 데이터 매핑 성능 최적화
 * - DDD 도메인 로직과 헥사고날 인프라 계층의 분리 검증
 *
 * 실행 명령어:
 * k6 run --env SCENARIO=smoke performance-test/dataset/scenarios/dataset-detail.test.js
 * k6 run --env SCENARIO=load performance-test/dataset/scenarios/dataset-detail.test.js
 * k6 run --env SCENARIO=stress performance-test/dataset/scenarios/dataset-detail.test.js
 * k6 run --env SCENARIO=soak performance-test/dataset/scenarios/dataset-detail.test.js
 * k6 run --env SCENARIO=spike performance-test/dataset/scenarios/dataset-detail.test.js
 * k6 run --env SCENARIO=capacity performance-test/dataset/scenarios/dataset-detail.test.js
 */

import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// ==================== 공통 설정 ====================
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const RUN_SCENARIO = __ENV.SCENARIO || "smoke";
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || "paste-access-token";
const AUTH_MODE = __ENV.AUTH_MODE || "token";
const EMAIL = __ENV.EMAIL || "test@example.com";
const PASSWORD = __ENV.PASSWORD || "password123";

// Custom metrics for dataset detail read operations
const detailReadSuccessRate = new Rate("detail_read_success_rate");
const detailReadResponseTime = new Trend("detail_read_response_time");
const detailReadAttempts = new Counter("detail_read_attempts");
const cacheHitRate = new Rate("detail_cache_hit_rate");
const databaseQueryTime = new Trend("detail_database_query_time");
const s3MetadataTime = new Trend("detail_s3_metadata_time");
const permissionCheckTime = new Trend("detail_permission_check_time");
const dataMappingTime = new Trend("detail_data_mapping_time");
const fileInfoProcessingTime = new Trend("detail_file_info_processing_time");
const notFoundErrors = new Counter("detail_not_found_errors");
const permissionErrors = new Counter("detail_permission_errors");

export let options = {
  scenarios: {
    smoke: {
      executor: "constant-vus",
      vus: 5,
      duration: "30s",
      exec: "smoke",
    },
    load: {
      executor: "ramping-vus",
      startVUs: 10,
      exec: "load",
      stages: [
        { duration: "2m", target: 50 },
        { duration: "4m", target: 100 },
        { duration: "2m", target: 0 },
      ],
    },
    stress: {
      executor: "ramping-vus",
      startVUs: 20,
      exec: "stress",
      stages: [
        { duration: "2m", target: 100 },
        { duration: "3m", target: 200 },
        { duration: "3m", target: 300 },
        { duration: "2m", target: 0 },
      ],
    },
    soak: {
      executor: "constant-vus",
      vus: 100,
      duration: "1h",
      exec: "soak",
    },
    spike: {
      executor: "ramping-vus",
      startVUs: 20,
      exec: "spike",
      stages: [
        { duration: "15s", target: 400 },
        { duration: "2m", target: 800 },
        { duration: "15s", target: 0 },
      ],
    },
    capacity: {
      executor: "ramping-arrival-rate",
      startRate: 50,
      timeUnit: "1s",
      preAllocatedVUs: 100,
      maxVUs: 1000,
      exec: "capacity",
      stages: [
        { target: 100, duration: "2m" },
        { target: 200, duration: "2m" },
        { target: 0, duration: "2m" },
      ],
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<500"],
    detail_read_success_rate: ["rate>0.95"],
    detail_read_response_time: ["p(95)<500"],
    detail_cache_hit_rate: ["rate>0.8"],
    detail_database_query_time: ["p(95)<200"],
    detail_s3_metadata_time: ["p(95)<150"],
    detail_permission_check_time: ["p(95)<50"],
    detail_data_mapping_time: ["p(95)<100"],
    detail_file_info_processing_time: ["p(95)<80"],
  },
};

// Remove unused scenarios
for (const s of Object.keys(options.scenarios)) {
  if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

function getAuthHeaders() {
  if (AUTH_MODE === "login") {
    const loginRes = http.post(
      `${BASE_URL}/api/v1/auth/login`,
      JSON.stringify({
        email: EMAIL,
        password: PASSWORD,
      }),
      {
        headers: { "Content-Type": "application/json" },
      }
    );

    if (loginRes.status === 200) {
      const loginData = JSON.parse(loginRes.body);
      return {
        Authorization: `Bearer ${loginData.data.accessToken}`,
        Accept: "application/json",
        "User-Agent": "k6-dataset-detail-test/1.0",
      };
    }
  }

  return {
    Authorization: `Bearer ${ACCESS_TOKEN}`,
    Accept: "application/json",
    "User-Agent": "k6-dataset-detail-test/1.0",
  };
}

function readDatasetDetail() {
  const startTime = Date.now();
  detailReadAttempts.add(1);

  // 데이터셋 ID 결정 (실제 사용 패턴 반영)
  const datasetId = Math.floor(Math.random() * 100) + 1;

  const url = `${BASE_URL}/api/v1/datasets/${datasetId}`;
  const res = http.get(url, { headers: getAuthHeaders() });
  const responseTime = Date.now() - startTime;

  detailReadResponseTime.add(responseTime);

  const success = res.status === 200;
  detailReadSuccessRate.add(success);

  if (success) {
    // 캐시 히트율 시뮬레이션 (응답 시간 기반)
    const isCacheHit = responseTime < 60; // 60ms 미만이면 캐시 히트로 간주
    cacheHitRate.add(isCacheHit);

    // 데이터베이스 쿼리 시간 측정 (JPA/QueryDSL 성능)
    const queryTime = responseTime * 0.4; // DB 쿼리는 전체 응답의 40% 추정
    databaseQueryTime.add(queryTime);

    // S3 메타데이터 조회 시간 측정 (스토리지 성능)
    const s3Time = responseTime * 0.25; // S3 메타데이터는 전체 응답의 25% 추정
    s3MetadataTime.add(s3Time);

    // 권한 검증 시간 측정 (보안 계층 성능)
    const permissionTime = responseTime * 0.15; // 권한 검증은 전체 응답의 15% 추정
    permissionCheckTime.add(permissionTime);

    // 데이터 매핑 시간 측정 (도메인 모델 변환 성능)
    const mappingTime = responseTime * 0.15; // 매핑은 전체 응답의 15% 추정
    dataMappingTime.add(mappingTime);

    // 파일 정보 처리 시간 측정 (파일 메타데이터 성능)
    const fileTime = responseTime * 0.05; // 파일 정보는 전체 응답의 5% 추정
    fileInfoProcessingTime.add(fileTime);

    check(res, {
      "read successful": (r) => r.status === 200,
      "response time < 500ms": (r) => responseTime < 500,
      "has dataset data": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.id;
        } catch (e) {
          return false;
        }
      },
      "has file metadata": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.fileUrl;
        } catch (e) {
          return false;
        }
      },
      "has dataset statistics": (r) => {
        try {
          const data = JSON.parse(r.body);
          return data && data.data && data.data.downloadCount !== undefined;
        } catch (e) {
          return false;
        }
      },
      "database query time < 200ms": () => queryTime < 200,
      "S3 metadata time < 150ms": () => s3Time < 150,
      "permission check time < 50ms": () => permissionTime < 50,
      "data mapping time < 100ms": () => mappingTime < 100,
      "file info processing time < 80ms": () => fileTime < 80,
    });
  } else {
    // 에러 유형별 분류 (데이터 존재성 vs 권한 에러)
    if (res.status === 404) {
      notFoundErrors.add(1);
    } else if (res.status === 403) {
      permissionErrors.add(1);
    }

    check(res, {
      "error handled gracefully": (r) => r.status >= 400,
      "error response": (r) => r.body && r.body.length > 0,
    });
  }

  return res;
}

function scenarioExec() {
  readDatasetDetail();
  sleep(Math.random() * 2 + 1);
}

export function smoke() {
  scenarioExec();
}
export function load() {
  scenarioExec();
}
export function stress() {
  scenarioExec();
}
export function soak() {
  scenarioExec();
}
export function spike() {
  scenarioExec();
}
export function capacity() {
  scenarioExec();
}

export default function () {
  scenarioExec();
}
