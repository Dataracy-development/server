# 📁 파일 API

## 📋 **개요**

파일 관련 API는 파일 업로드, 다운로드, PreSigned URL 생성을 제공합니다.

**Base URL**: `/api/v1`

---

## 🔑 **엔드포인트**

### **파일 다운로드**

#### **1. PreSigned URL 생성**

**엔드포인트**: `GET /api/v1/files/download-url`

**설명**: 파일 다운로드를 위한 PreSigned URL 생성

**쿼리 파라미터**:

- `fileKey`: S3에 저장된 파일 키 (필수)
- `expiresIn`: URL 만료 시간 (초, 기본값: 300)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "PreSigned URL 생성에 성공했습니다.",
  "data": {
    "downloadUrl": "https://s3.amazonaws.com/bucket/file.csv?X-Amz-Algorithm=...",
    "expiresAt": "2024-01-15T10:35:00Z"
  }
}
```

---

#### **2. 파일 다운로드 (직접)**

**엔드포인트**: `GET /api/v1/files/{fileId}/download`

**설명**: 파일 ID를 통한 직접 다운로드

**경로 변수**:

- `fileId`: 다운로드할 파일의 ID

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**: 파일 스트림 (Content-Type: application/octet-stream)

---

### **파일 업로드**

#### **1. PreSigned URL 생성 (업로드용)**

**엔드포인트**: `POST /api/v1/files/upload-url`

**설명**: 파일 업로드를 위한 PreSigned URL 생성

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "fileName": "document.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "expiresIn": 300
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "업로드 PreSigned URL 생성에 성공했습니다.",
  "data": {
    "uploadUrl": "https://s3.amazonaws.com/bucket/file.pdf?X-Amz-Algorithm=...",
    "fileKey": "uploads/2024/01/15/uuid-document.pdf",
    "expiresAt": "2024-01-15T10:35:00Z"
  }
}
```

---

## ❌ **에러 코드**

| 코드                   | HTTP 상태 | 설명                    |
| ---------------------- | --------- | ----------------------- |
| `FILE_NOT_FOUND`       | 404       | 파일을 찾을 수 없음     |
| `FILE_ACCESS_DENIED`   | 403       | 파일 접근 권한 없음     |
| `INVALID_FILE_TYPE`    | 400       | 지원하지 않는 파일 형식 |
| `FILE_SIZE_EXCEEDED`   | 413       | 파일 크기 초과          |
| `UPLOAD_URL_EXPIRED`   | 410       | 업로드 URL 만료         |
| `DOWNLOAD_URL_EXPIRED` | 410       | 다운로드 URL 만료       |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **PreSigned URL 생성**

```bash
curl -X GET "https://api.dataracy.co.kr/api/v1/files/download-url?fileKey=uploads/document.pdf&expiresIn=600" \
  -H "Authorization: Bearer {access_token}"
```

#### **업로드 URL 생성**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/files/upload-url" \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "document.pdf",
    "fileSize": 1024000,
    "contentType": "application/pdf"
  }'
```

### **JavaScript 예제**

#### **파일 업로드 플로우**

```javascript
const uploadFile = async (file) => {
  // 1. PreSigned URL 생성
  const urlResponse = await fetch("/api/v1/files/upload-url", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({
      fileName: file.name,
      fileSize: file.size,
      contentType: file.type,
    }),
  });

  const { data } = await urlResponse.json();

  // 2. S3에 직접 업로드
  const uploadResponse = await fetch(data.uploadUrl, {
    method: "PUT",
    body: file,
    headers: {
      "Content-Type": file.type,
    },
  });

  if (uploadResponse.ok) {
    return { success: true, fileKey: data.fileKey };
  } else {
    throw new Error("Upload failed");
  }
};
```

#### **파일 다운로드**

```javascript
const downloadFile = async (fileKey) => {
  // 1. PreSigned URL 생성
  const response = await fetch(
    `/api/v1/files/download-url?fileKey=${fileKey}&expiresIn=600`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  const { data } = await response.json();

  // 2. 다운로드 실행
  window.open(data.downloadUrl, "_blank");
};
```

---

## 📊 **파일 정책**

### **지원하는 파일 형식**

#### **이미지**

- **JPG/JPEG**: 최대 10MB
- **PNG**: 최대 10MB
- **GIF**: 최대 5MB
- **WebP**: 최대 10MB

#### **문서**

- **PDF**: 최대 50MB
- **DOC/DOCX**: 최대 20MB
- **XLS/XLSX**: 최대 20MB
- **PPT/PPTX**: 최대 20MB

#### **데이터**

- **CSV**: 최대 100MB
- **JSON**: 최대 50MB
- **XML**: 최대 50MB
- **TXT**: 최대 10MB

### **보안 정책**

- **바이러스 스캔**: 업로드된 파일 자동 스캔
- **파일 타입 검증**: MIME 타입 및 확장자 검증
- **접근 제어**: 사용자별 파일 접근 권한 관리
- **URL 만료**: PreSigned URL 자동 만료

### **저장 정책**

- **S3 버킷**: AWS S3에 파일 저장
- **폴더 구조**: `uploads/{year}/{month}/{day}/{uuid}-{filename}`
- **백업**: 중요 파일 자동 백업
- **생명주기**: 90일 후 자동 아카이빙

---

**💡 파일 관련 문제가 발생하면 개발팀에 문의하세요!**
