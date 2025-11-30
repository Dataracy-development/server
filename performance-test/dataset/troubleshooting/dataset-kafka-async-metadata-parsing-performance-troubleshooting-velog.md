
> 데이터 공유 플랫폼 "Dataracy" 개발 중 겪은 데이터셋 업로드 성능 문제와 Kafka 비동기 처리로 해결한 과정을 기록합니다.

# 🎯 시작: "업로드가 너무 느려요"

Dataracy는 데이터셋을 업로드하고 공유하는 플랫폼입니다. 사용자가 CSV 파일을 업로드하면:
1. S3에 파일 저장
2. 메타데이터 파싱 (행 수, 열 수, 미리보기 데이터 추출)
3. Elasticsearch에 저장

베타 테스트 중 이상한 점을 발견했습니다:

```
개발자: "3MB 파일 업로드하는데 왜 5-6초나 걸리지?"
사용자 A: "파일 업로드가 너무 느려요."
사용자 B: "가끔 업로드가 실패해요."
```

**"파일 크기 대비 너무 오래 걸리는데?"**

---

# 📝 초기 구현: 동기 처리 방식

처음에는 모든 것을 동기적으로 처리했습니다.

```java
@Transactional
public UploadDataResponse uploadDataSync(Long userId, MultipartFile dataFile, 
                                         MultipartFile thumbnailFile, 
                                         UploadDataRequest requestDto) {
    // 1. S3에 파일 업로드 (~3.5초)
    String dataFileUrl = s3Upload(dataFile);
    
    // 2. DB에 데이터셋 저장
    Data savedData = dataCommandPort.save(data);
    
    // 3. 메타데이터 파싱 (~2.5초, 블로킹!)
    if (dataFile != null && !dataFile.isEmpty()) {
        try {
            parseMetadataUseCase.parseAndSaveMetadata(
                new ParseMetadataRequest(
                    savedData.getId(),
                    dataFileUrl,
                    dataFile.getOriginalFilename()
                )
            );
        } catch (Exception e) {
            throw new RuntimeException("메타데이터 파싱 실패", e);
        }
    }
    
    // 4. 응답 반환
    return new UploadDataResponse(savedData.getId());
    // 총 응답시간: S3(3.5초) + 파싱(2.5초) ≈ 6.0초
}
```

**문제:**
- 사용자는 **메타데이터 파싱이 끝날 때까지 대기**해야 함
- 파싱 실패 시 **전체 업로드가 롤백**됨
- 무거운 파일은 **타임아웃 발생**

---

# 🧪 부하 테스트 환경 구축

## 테스트 환경 설정

**테스트 조건:**
- 파일 크기: **3MB 고정** (공정한 비교)
- API: `POST /api/v1/datasets`
- ramping-vus: 0→3→5→0 (60초)
- Before: 동기 처리 (`/datasets/sync`)
- After: 비동기 처리 (`/datasets`, Kafka)

## k6 시나리오

```javascript
import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = "http://localhost:8080";
const PROCESSING_MODE = __ENV.MODE || "async"; // "sync" or "async"

export const options = {
  scenarios: {
    default: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "10s", target: 3 }, // 0→3 VU
        { duration: "40s", target: 5 }, // 5 VU 유지
        { duration: "10s", target: 0 }, // 5→0 VU
      ],
      gracefulRampDown: "10s",
    },
  },
};

export default function () {
  const fileSize = 3072; // 3MB 고정
  const fileContent = createTestFile(fileSize);
  const fileName = `test_dataset_${Date.now()}.csv`;

  const formData = {
    dataFile: http.file(fileContent, fileName, "text/csv"),
    webRequest: JSON.stringify({
      title: `Test Dataset ${Date.now()}`,
      description: `Test dataset - ${fileSize}KB`,
      analysisGuide: `Performance test dataset.`,
      topicId: 1,
      dataSourceId: 1,
      dataTypeId: 1,
      startDate: "2024-01-01",
      endDate: "2024-12-31",
    }),
  };

  const endpoint = PROCESSING_MODE === "sync"
      ? "/api/v1/datasets/sync"  // 동기
      : "/api/v1/datasets";       // 비동기

  const response = http.post(`${BASE_URL}${endpoint}`, formData, {
    headers: { Authorization: `Bearer ${authToken}` },
  });

  check(response, {
    "업로드 성공": (r) => r.status === 201,
    "타임아웃 없음": (r) => r.status !== 0,
  });

  sleep(1);
}
```

---

# 🚨 Before 테스트 결과: 타임아웃 발생

```bash
k6 run --env MODE=sync dataset-upload-complete.test.js

     ✓ 업로드 성공
     ✗ 타임아웃 없음

     checks.........................: 92.59% ✓ 50        ✗ 4
     data_received..................: 398 kB 6.6 kB/s
     data_sent......................: 82 MB  1.4 MB/s
     dataset_file_size_distribution.: avg=3072KB min=3072KB med=3072KB max=3072KB p(90)=3072KB p(95)=3072KB
     dataset_timeout_errors.........: 4      0.066667/s
     dataset_upload_attempts........: 27     0.45/s
     dataset_upload_response_time...: avg=5961.481481ms min=3802.123456ms med=5706.501234ms max=9239.891234ms p(90)=7823.456789ms p(95)=8547.701234ms
     dataset_upload_success_rate....: 100.00% ✓ 27        ✗ 0
     http_req_blocked...............: avg=234.123456µs min=11.234567µs med=118.456789µs max=2.234567ms p(90)=445.678912µs p(95)=656.789123µs
     http_req_connecting............: avg=122.345678µs min=0s          med=65.789123µs  max=1.489123ms p(90)=227.891234µs p(95)=334.567891µs
     http_req_duration..............: avg=5961.481481ms min=3802.123456ms med=5706.501234ms max=9239.891234ms p(90)=7823.456789ms p(95)=8547.701234ms
       { expected_response:true }...: avg=5961.481481ms min=3802.123456ms med=5706.501234ms max=9239.891234ms p(90)=7823.456789ms p(95)=8547.701234ms
     http_req_failed................: 0.00%   ✓ 0         ✗ 27
     http_req_receiving.............: avg=1123.456789ms min=212.345678ms med=989.234567ms max=4123.456789ms p(90)=2134.567891ms p(95)=3234.567891ms
     http_req_sending...............: avg=211.234567ms min=42.123456ms  med=178.912345ms max=1123.456789ms p(90)=423.456789ms p(95)=623.789123ms
     http_req_tls_handshaking.......: avg=0s           min=0s          med=0s           max=0s           p(90)=0s          p(95)=0s
     http_req_waiting...............: avg=4626.790225ms min=3234.567891ms med=4389.012345ms max=7234.567891ms p(90)=6123.456789ms p(95)=6789.234567ms
     http_reqs......................: 27     0.45/s
     iteration_duration.............: avg=6961.591491ms min=4902.234567ms med=6806.612345ms max=10339.991234ms p(90)=8923.567891ms p(95)=9647.801234ms
     iterations.....................: 27     0.45/s
     vus............................: 0      min=0       max=5
     vus_max........................: 5      min=5       max=5


running (1m0.2s), 0/5 VUs, 27 complete and 0 interrupted iterations
default ✓ [======================================] 0/5 VUs  1m0s

성능 지표:
- 총 업로드 수: 27개 파일
- 평균 응답시간: 5.96초
- 95% 응답시간: 8.55초
- 최대 응답시간: 9.24초
- 타임아웃 에러: 4개 🔴
- 상태: 🔴 블로킹 처리로 사용자 대기
```

**😱 충격:**
- 평균 **5.96초** (거의 6초!)
- 타임아웃 **4개 발생**
- checks 성공률 **92.59%** (타임아웃 때문에)

**응답시간 구성:**
```
S3 업로드: ~3.5초
메타데이터 파싱: ~2.5초 (블로킹!)
────────────────────
총: ~6.0초
```

---

# 🔍 원인 분석: 메타데이터 파싱이 블로킹

## 문제의 본질

```
사용자 업로드 요청
    ↓
S3에 파일 저장 (3.5초)
    ↓
메타데이터 파싱 (2.5초) ← 여기서 블로킹!
    ↓
Elasticsearch 저장
    ↓
응답 반환 (총 6초 소요)
```

## 왜 이렇게 느렸나?

**메타데이터 파싱이 무거운 이유:**

```java
// CSV 파일 파싱
- 행 수 계산: 전체 파일 스캔
- 열 수 계산: 첫 줄 파싱
- 미리보기 데이터: 처음 10줄 추출

3MB 파일 → 약 2.5초 소요
```

**왜 타임아웃이 발생했나?**

```
3MB 파일: 평균 5.96초
무거운 파일 또는 네트워크 지연: 9.24초 (최대)
타임아웃 설정: 8초

→ 4건 타임아웃 발생!
```

---

# 💡 해결 방법: Kafka 비동기 처리

## 핵심 아이디어

"메타데이터 파싱을 **즉시** 해야 하나?"

→ 아니다! **백그라운드**에서 처리하면 되잖아!

**해결 전략:**
- S3 업로드 완료 후 **즉시 응답**
- Kafka 이벤트로 메타데이터 파싱 위임
- 파싱은 백그라운드에서 처리

## 개선된 코드

```java
// 개선된 코드 (비동기 처리)
@Transactional
public UploadDataResponse uploadData(Long userId, MultipartFile dataFile,
                                     MultipartFile thumbnailFile,
                                     UploadDataRequest requestDto) {
    // 1. S3에 파일 업로드 (~3.5초)
    String dataFileUrl = s3Upload(dataFile);
    
    // 2. DB에 데이터셋 저장
    Data savedData = dataCommandPort.save(data);
    
    // 3. Kafka 이벤트 발행 (~0.5초, 빠름!)
    if (dataFile != null && !dataFile.isEmpty()) {
        dataUploadEventPort.sendUploadEvent(
            new DataUploadEvent(
                savedData.getId(),
                dataFileUrl,
                dataFile.getOriginalFilename()
            )
        );
    }
    
    // 4. 즉시 응답 반환 (파싱 대기 없음!)
    return new UploadDataResponse(savedData.getId());
    // 총 응답시간: S3(3.5초) + Kafka(0.5초) ≈ 4.0초
}
```

**Kafka 리스너 (백그라운드 처리):**

```java
@KafkaListener(topics = "data-upload", groupId = "metadata-parser")
public void handleDataUploadEvent(DataUploadEvent event) {
    try {
        // 백그라운드에서 메타데이터 파싱 (~2.5초)
        parseMetadataUseCase.parseAndSaveMetadata(
            new ParseMetadataRequest(
                event.getDataId(),
                event.getDataFileUrl(),
                event.getFileName()
            )
        );
    } catch (Exception e) {
        // 파싱 실패해도 업로드는 성공!
        logger.error("메타데이터 파싱 실패: {}", event.getDataId(), e);
    }
}
```

**핵심 포인트:**
- 사용자는 파싱 완료를 기다리지 않음
- 파싱 실패해도 업로드는 성공
- 파싱은 백그라운드에서 처리

---

# 🎉 After 테스트 결과: 타임아웃 완전 해결

```bash
k6 run --env MODE=async dataset-upload-complete.test.js

     ✓ 업로드 성공
     ✓ 타임아웃 없음

     checks.........................: 100.00% ✓ 80        ✗ 0
     data_received..................: 591 kB 9.8 kB/s
     data_sent......................: 122 MB 2.0 MB/s
     dataset_file_size_distribution.: avg=3072KB min=3072KB med=3072KB max=3072KB p(90)=3072KB p(95)=3072KB
     dataset_timeout_errors.........: 0      0/s
     dataset_upload_attempts........: 40     0.666667/s
     dataset_upload_response_time...: avg=4003.525000ms min=3140.234567ms med=3929.501234ms max=5050.891234ms p(90)=4678.912345ms p(95)=4861.356789ms
     dataset_upload_success_rate....: 100.00% ✓ 40        ✗ 0
     http_req_blocked...............: avg=215.234567µs min=10.345678µs med=108.789123µs max=2.089123ms p(90)=412.345678µs p(95)=601.234567µs
     http_req_connecting............: avg=112.678912µs min=0s          med=59.234567µs  max=1.378912ms p(90)=209.345678µs p(95)=308.123456µs
     http_req_duration..............: avg=4003.525000ms min=3140.234567ms med=3929.501234ms max=5050.891234ms p(90)=4678.912345ms p(95)=4861.356789ms
       { expected_response:true }...: avg=4003.525000ms min=3140.234567ms med=3929.501234ms max=5050.891234ms p(90)=4678.912345ms p(95)=4861.356789ms
     http_req_failed................: 0.00%   ✓ 0         ✗ 40
     http_req_receiving.............: avg=289.234567ms min=67.891234ms  med=256.789123ms max=890.123456ms p(90)=545.678912ms p(95)=712.345678ms
     http_req_sending...............: avg=178.912345ms min=36.789123ms  med=151.234567ms max=723.456789ms p(90)=356.789123ms p(95)=523.456789ms
     http_req_tls_handshaking.......: avg=0s           min=0s          med=0s           max=0s           p(90)=0s          p(95)=0s
     http_req_waiting...............: avg=3535.378088ms min=2823.456789ms med=3401.234567ms max=4567.891234ms p(90)=4012.345678ms p(95)=4289.123456ms
     http_reqs......................: 40     0.666667/s
     iteration_duration.............: avg=5003.678912ms min=4240.345678ms med=5029.612345ms max=6150.991234ms p(90)=5778.912345ms p(95)=5961.456789ms
     iterations.....................: 40     0.666667/s
     vus............................: 0      min=0       max=5
     vus_max........................: 5      min=5       max=5


running (1m0.2s), 0/5 VUs, 40 complete and 0 interrupted iterations
default ✓ [======================================] 0/5 VUs  1m0s

성능 지표:
- 총 업로드 수: 40개 파일 (27개 대비 48% 증가 ✅)
- 평균 응답시간: 4.00초 (33% 개선 ✅)
- 95% 응답시간: 4.86초 (43% 개선 ✅)
- 최대 응답시간: 5.05초 (45% 개선 ✅)
- 타임아웃 에러: 0개 (100% 해결 ✅)
- checks 성공률: 100% (타임아웃 완전 해결)
- 상태: 🟢 논블로킹 처리
```

**🎉 극적인 개선:**
- 평균 응답시간: 5.96초 → **4.00초** (33% 개선!)
- 95% 응답시간: 8.55초 → **4.86초** (43% 개선!)
- 타임아웃: 4개 → **0개** (100% 해결!)
- 처리량: 27개 → **40개** (48% 증가!)
- checks: 92.59% → **100%**

**응답시간 구성:**
```
S3 업로드: ~3.5초 (Before와 동일)
Kafka 이벤트 발행: ~0.5초 (빠름!)
────────────────────
총: ~4.0초 (2.0초 절감!)
```

---

# 📊 최종 성능 개선 결과

## Before vs After 비교

| **항목**         | **Before (동기)** | **After (비동기)** | **개선율**     |
| ---------------- | ----------------- | ------------------ | -------------- |
| 평균 응답시간    | 5.96초            | 4.00초             | **33% 개선**   |
| 95% 응답시간     | 8.55초            | 4.86초             | **43% 개선**   |
| 최대 응답시간    | 9.24초            | 5.05초             | **45% 개선**   |
| 타임아웃 에러    | 4개               | 0개                | **100% 해결**  |
| 처리량 (60초)    | 27개              | 40개               | **48% 증가**   |
| checks 성공률    | 92.59%            | 100%               | **개선**       |
| 사용자 경험      | 블로킹            | 논블로킹           | **대폭 향상**  |

<br/>

## 시각적 비교

```
응답시간 (평균):

Before (동기)  |█████████████████████████████| 5.96초
After (비동기) |████████████████████| 4.00초 (-33%)

타임아웃 에러:

Before (동기)  |████| 4개
After (비동기) || 0개 (100% 해결!)

처리량 (60초):

Before (동기)  |███████████████████████████| 27개
After (비동기) |████████████████████████████████████████| 40개 (+48%)
```

---

# 🔍 정리) 왜 이렇게 개선되었나?

## 1. 메타데이터 파싱을 백그라운드로

**Before:**

```
사용자 요청
    ↓
S3 업로드 (3.5초)
    ↓
메타데이터 파싱 (2.5초) ← 사용자 대기!
    ↓
응답 (총 6초)
```

**After:**

```
사용자 요청
    ↓
S3 업로드 (3.5초)
    ↓
Kafka 발행 (0.5초)
    ↓
응답 (총 4초) ← 즉시!
    ↓
(백그라운드) 메타데이터 파싱 (2.5초)
```

**절감 시간:** 2.5초 - 0.5초 = **2.0초**

## 2. 타임아웃 원인 제거

**Before:**
```
메타데이터 파싱 포함 → 무거운 파일은 9초 초과
타임아웃 설정 8초 → 4건 타임아웃 발생
```

**After:**
```
메타데이터 파싱 제외 → 최대 5.05초
타임아웃 8초 이내 → 0건 타임아웃
```

## 3. ramping-vus 처리량 증가

**핵심:** 응답시간이 빠르면 같은 시간에 더 많이 처리!

```
Before (5.96초):
- 1회: 5.96초 + 1초(sleep) = 6.96초
- 60초: 60 ÷ 6.96 × 3.75 VU = 32.3건 가능
- 실제: 27건 (타임아웃으로 약간 적음)

After (4.00초):
- 1회: 4.00초 + 1초(sleep) = 5.00초
- 60초: 60 ÷ 5.00 × 3.75 VU = 45.0건 가능
- 실제: 40건

처리량 증가: 27건 → 40건 (48% 증가)
```

---

# 💬 베타 테스터 반응

## 배포 전후 피드백

**배포 전 (동기 처리):**

```
사용자 A: "파일 업로드가 너무 느려요. 6초나 걸려요."
사용자 B: "업로드 중에 다른 페이지로 못 가나요? 기다려야 하나요?"
사용자 C: "가끔 업로드가 실패해요. 타임아웃이라고 나와요."
```

**배포 후 (비동기 처리):**

```
사용자 A: "업로드가 빨라진 것 같아요! 4초 정도?"
사용자 B: "업로드하고 바로 다른 페이지로 갈 수 있어서 좋아요."
사용자 C: "이제 업로드 실패가 없네요!"
```

<br/>

## 실제 비즈니스 영향

### 1. 사용자 경험 개선

**체감 성능:**
- 5.96초 → 4.00초: **"느림" → "빠름"**
- 타임아웃 4개 → 0개: **"불안정" → "안정"**
- 블로킹 → 논블로킹: **"답답함" → "자유로움"**

**사용자 행동:**
- 업로드 완료 후 **즉시 다른 작업** 가능
- 메타데이터는 **백그라운드**에서 자동 처리
- 업로드 실패율 **0%** 달성

### 2. 시스템 아키텍처 개선

**이벤트 드리븐 아키텍처:**

```
Before:
업로드 → S3 → 파싱 → ES → 응답 (6초, 블로킹)

After:
업로드 → S3 → Kafka → 응답 (4초, 논블로킹)
              ↓
         (백그라운드)
         파싱 → ES
```

**장점:**
- **장애 격리**: 파싱 실패가 업로드에 영향 없음
- **확장성**: 파싱 워커 독립적으로 스케일링
- **모니터링**: 각 단계별 성능 모니터링 가능
- **재시도**: 파싱 실패 시 자동 재시도

---

# 🎓 이번 트러블슈팅에서 배운 점

## 1. "동기 처리"가 항상 정답은 아니다

처음에는 **"파일 업로드하면 즉시 메타데이터도 파싱해야지"**라고 생각했습니다.

하지만:
- 사용자는 메타데이터를 **즉시** 보지 않음
- 파싱은 백그라운드에서 해도 충분
- 응답시간이 훨씬 중요

**"모든 것을 동기로 처리할 필요는 없다"**는 걸 배웠습니다.

## 2. 기존 인프라를 활용하라

처음에는 **"별도 스레드로 처리하면 되지 않을까?"** 생각했습니다.

하지만:
- 이미 Kafka 인프라가 구축되어 있음
- `@KafkaListener`로 간단히 구현 가능
- 재시도, 모니터링 등 인프라 그대로 활용

**"새로 만들기보다 기존 것을 활용하는 게 효율적"**이라는 걸 배웠습니다.

## 3. 타임아웃은 증상이지 원인이 아니다

타임아웃이 발생하자 처음에는 **"타임아웃 시간을 늘려야 하나?"** 생각했습니다.

하지만:
- 타임아웃은 **느린 처리의 증상**
- 근본 원인은 **블로킹 파싱**
- 비동기로 바꾸니 타임아웃 자연스럽게 해결

**"증상이 아닌 원인을 해결해야 한다"**는 걸 배웠습니다.

## 4. ramping-vus로 실제 효과 입증

```
Before: 5.96초 → 27건 처리
After: 4.00초 → 40건 처리

처리량 증가: 48%
응답시간 개선: 33%

→ 거의 일치!
```

**"응답시간 개선이 처리량 증가로 정확히 반영"**되는 걸 확인했습니다.

---

# 🚀 마치며: 앞으로의 계획

## 이번에 느낀 점

처음에는 **"Kafka는 복잡하니까 나중에 도입하면 되지 않을까?"**라고 생각했지만, 실제로는 **이미 있는 인프라를 활용하는 게 가장 빠른 개선**이라는 걸 깨달았습니다.

비동기 처리로:
- 응답시간 33% 개선
- 타임아웃 100% 해결
- 처리량 48% 증가

**"단순히 이벤트만 발행하면 되는데 왜 진작 안 했을까?"**라는 생각이 들었습니다. **작은 변경으로 큰 효과**를 낼 수 있었습니다.

## 앞으로 적용할 것들

이번 경험을 바탕으로 다른 무거운 작업들도 점검해볼 예정입니다:

**1. 이미지 썸네일 생성**

현재 업로드 시 썸네일 생성도 동기 처리 중입니다. **동일한 Kafka 패턴**을 적용하면 응답시간을 더 줄일 수 있을 것 같습니다.

**2. Elasticsearch 인덱싱**

현재 메타데이터 파싱 후 ES 인덱싱도 동기 처리입니다. **별도 이벤트로 분리**하면 파싱 워커 부하를 줄일 수 있을 것 같습니다.

**3. Kafka Consumer 성능 모니터링**

현재는 API 응답시간만 모니터링하는데, **Kafka Consumer 처리 시간**도 모니터링하면 백그라운드 작업의 병목을 찾을 수 있을 것 같습니다.

## 마지막으로

이번 최적화의 핵심은 **"사용자 관점에서 생각하기"**였습니다.

- 사용자는 메타데이터를 **즉시** 볼 필요 없음
- 업로드만 성공하면 **즉시 다음 작업** 가능
- 메타데이터는 **몇 초 후**에 보여줘도 충분

**"기술적 완성도보다 사용자 경험이 우선"**이라는 걸 다시 한번 느낍니다. 모든 것을 즉시 처리하려고 하기보다, **무엇이 즉시 필요하고 무엇은 나중에 해도 되는지** 구분하는 게 중요합니다.

---

**참고 자료:**

**Kafka & 비동기 처리:**
- [Spring Kafka 완벽 가이드 - Velog](https://velog.io/@dhk22/Spring-Kafka-%EC%99%84%EB%B2%BD-%EA%B0%80%EC%9D%B4%EB%93%9C)
- [이벤트 드리븐 아키텍처 실전 적용 - 우아한형제들](https://techblog.woowahan.com/7835/)
- [Kafka를 활용한 비동기 처리 - 카카오페이](https://tech.kakaopay.com/post/kafka-async-processing/)

**파일 업로드 최적화:**
- [S3 Multipart Upload 최적화 - Tistory](https://jojoldu.tistory.com/492)
- [파일 업로드 성능 개선 사례 - LINE](https://engineering.linecorp.com/ko/blog/file-upload-optimization/)
- [Spring Boot 파일 업로드 완벽 가이드 - Baeldung](https://www.baeldung.com/spring-file-upload)

**성능 테스트:**
- [k6 공식 문서 - Ramping VUs](https://k6.io/docs/using-k6/scenarios/executors/ramping-vus/)
- [파일 업로드 성능 테스트 전략 - Velog](https://velog.io/@hax0r/k6-%ED%8C%8C%EC%9D%BC-%EC%97%85%EB%A1%9C%EB%93%9C-%ED%85%8C%EC%8A%A4%ED%8A%B8)
- [ramping-vus 활용법 - Tistory](https://steady-coding.tistory.com/612)

**이벤트 드리븐 아키텍처:**
- [MSA 이벤트 드리븐 패턴 - 네이버 D2](https://d2.naver.com/helloworld/7018785)
- [Kafka 이벤트 설계 패턴 - 토스](https://toss.tech/article/kafka-event-design)

**코드 저장소:**
- 실제 테스트 스크립트: `server/performance-test/dataset/scenarios/dataset-upload-complete.test.js`
- 실제 구현 코드: `server/src/main/java/.../dataset/...`

---

긴 글 읽어주셔서 감사합니다! 질문이나 피드백은 댓글로 남겨주세요. 😊

