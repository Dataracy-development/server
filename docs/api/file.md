# 📁 파일 API

## 📋 **개요**

파일 관련 API는 파일 업로드, 다운로드, PreSigned URL 생성을 제공합니다.

**Base URL**: `/api/v1`

---

## 🔑 **엔드포인트**

### **파일 다운로드**

#### **1. PreSigned URL 생성**

**엔드포인트**: `GET /api/v1/files/pre-signed-url`

**설명**: 파일 다운로드를 위한 PreSigned URL 생성

**쿼리 파라미터**:

- `s3Url`: S3에 저장된 파일의 URL (필수)
- `expirationSeconds`: URL 만료 시간 (초, 기본값: 300)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "COMMON-200",
  "message": "성공입니다.",
  "data": {
    "preSignedUrl": "https://s3.amazonaws.com/bucket/file.csv?X-Amz-Algorithm=..."
  }
}
```

**참고**:

- `expirationSeconds` 범위: 1~3600초 (기본값: 300초)
- PreSigned URL은 지정된 시간 후 자동 만료됩니다

---

## ❌ **에러 코드**

| 코드       | HTTP 상태 | 설명                                           | Enum 이름                      |
| ---------- | --------- | ---------------------------------------------- | ------------------------------ |
| `FILE-001` | 400       | 이미지 파일은 최대 10MB까지 업로드 가능합니다  | `OVER_MAXIMUM_IMAGE_FILE_SIZE` |
| `FILE-002` | 400       | 이미지 파일은 jpg, jpeg, png 형식만 허용됩니다 | `BAD_REQUEST_IMAGE_FILE_TYPE`  |
| `FILE-003` | 400       | 파일은 최대 200MB까지 업로드 가능합니다        | `OVER_MAXIMUM_FILE_SIZE`       |
| `FILE-004` | 400       | 파일은 csv, xlsx, json 형식만 허용됩니다       | `BAD_REQUEST_FILE_TYPE`        |
| `FILE-005` | 500       | 파일 업로드에 실패했습니다                     | `FILE_UPLOAD_FAILURE`          |
| `FILE-006` | 500       | 썸네일 생성에 실패했습니다                     | `THUMBNAIL_GENERATION_FAILURE` |
| `AUTH-011` | 401       | 유효하지 않은 액세스 토큰입니다                | `INVALID_ACCESS_TOKEN`         |

---

## ✅ **성공 응답 코드**

| 코드         | HTTP 상태 | 설명                | Enum 이름    |
| ------------ | --------- | ------------------- | ------------ |
| `COMMON-200` | 200       | 성공입니다          | `OK`         |
| `COMMON-201` | 201       | 생성에 성공했습니다 | `CREATED`    |
| `COMMON-204` | 204       | 성공입니다          | `NO_CONTENT` |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **PreSigned URL 생성**

```bash
curl -X GET "https://api.dataracy.store/api/v1/files/pre-signed-url?s3Url=https://s3.amazonaws.com/bucket/file.pdf&expirationSeconds=600" \
  -H "Authorization: Bearer {access_token}"
```

### **JavaScript 예제**

#### **파일 다운로드**

```javascript
const downloadFile = async (s3Url) => {
  // 1. PreSigned URL 생성
  const response = await fetch(
    `/api/v1/files/pre-signed-url?s3Url=${encodeURIComponent(
      s3Url
    )}&expirationSeconds=600`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  const { data } = await response.json();

  // 2. 다운로드 실행
  window.open(data.preSignedUrl, "_blank");
};
```

---

## 📊 **파일 정책**

### **지원하는 파일 형식**

#### **이미지 파일**

- **JPG/JPEG**: 최대 5MB (실제 구현)
- **PNG**: 최대 5MB (실제 구현)
- **확장자 검증**: `(?i).+\\.(jpg|jpeg|png)$` 정규식

#### **일반 파일**

- **CSV**: 최대 200MB (실제 구현)
- **XLSX**: 최대 200MB (실제 구현)
- **JSON**: 최대 200MB (실제 구현)
- **확장자 검증**: csv, xlsx, json 형식만 허용

### **파일 검증 정책**

- **이미지 파일**: `FileUtil.validateImageFile()` 사용
- **일반 파일**: `FileUtil.validateGeneralFile()` 사용
- **파일 크기 검증**: 업로드 전 자동 검증
- **확장자 검증**: 정규식 패턴 매칭

### **저장 정책**

- **S3 버킷**: AWS S3에 파일 저장
- **업로드 방식**: 파일 크기에 따른 최적화
  - **직접 업로드**: 작은 파일 (가장 효율적)
  - **스트리밍 업로드**: 중간 크기 파일
  - **멀티파트 업로드**: 대용량 파일 (메모리 효율적)
- **썸네일 생성**: 이미지 파일 자동 썸네일 생성
- **Kafka 이벤트**: 파일 삭제 시 비동기 처리

---

**💡 파일 관련 문제가 발생하면 개발팀에 문의하세요!**
