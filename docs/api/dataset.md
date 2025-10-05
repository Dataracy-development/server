# 📊 데이터셋 API

## 📋 **개요**

데이터셋 관련 API는 데이터셋 업로드, 다운로드, 메타데이터 관리, 검색 기능을 제공합니다.

**Base URL**: `/api/v1/datasets`

---

## 🔑 **엔드포인트**

### **데이터셋 관리 (Command)**

#### **1. 데이터셋 업로드**

**엔드포인트**: `POST /api/v1/datasets`

**설명**: 새 데이터셋 파일(및 선택적 썸네일)과 메타데이터를 업로드하여 데이터셋 레코드를 생성

**요청 헤더**:

```http
Content-Type: multipart/form-data
Authorization: Bearer {access_token}
```

**요청 본문** (multipart/form-data):

```
dataFile: [파일] (필수)
thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "데이터셋 제목",
  "description": "데이터셋 설명",
  "dataSourceId": 1,
  "dataTypeId": 2
}
```

**응답**:

```json
{
  "success": true,
  "data": {
    "id": 456,
    "title": "데이터셋 제목",
    "description": "데이터셋 설명",
    "dataType": {
      "id": 2,
      "name": "CSV"
    },
    "dataSource": {
      "id": 1,
      "name": "공공데이터"
    },
    "fileUrl": "https://example.com/dataset.csv",
    "thumbnailImageUrl": "https://example.com/thumbnail.jpg",
    "fileSize": 1024000,
    "uploadedBy": {
      "id": 1,
      "nickname": "사용자명",
      "profileImageUrl": "https://example.com/profile.jpg"
    },
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "message": "데이터셋 업로드에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 데이터셋 수정**

**엔드포인트**: `PUT /api/v1/datasets/{dataId}`

**설명**: 지정한 데이터셋 ID에 해당하는 데이터셋을 새로운 파일, 썸네일, 메타데이터로 수정

**경로 변수**:

- `dataId`: 수정할 데이터셋의 고유 ID (1 이상)

**요청 헤더**:

```http
Content-Type: multipart/form-data
Authorization: Bearer {access_token}
```

**요청 본문** (multipart/form-data):

```
dataFile: [파일] (필수)
thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "수정된 제목",
  "description": "수정된 설명",
  "dataSourceId": 2,
  "dataTypeId": 3
}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "데이터셋 수정에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **3. 데이터셋 삭제**

**엔드포인트**: `DELETE /api/v1/datasets/{dataId}`

**설명**: 지정한 ID의 데이터셋을 삭제

**경로 변수**:

- `dataId`: 삭제할 데이터셋의 ID (1 이상)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "해당하는 데이터셋 삭제에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **데이터셋 조회 (Read)**

#### **1. 인기 데이터셋 조회**

**엔드포인트**: `GET /api/v1/datasets/popular`

**설명**: 다운로드 수와 연결된 프로젝트 수를 기준으로 인기 있는 데이터셋 목록을 조회

**쿼리 파라미터**:

- `size`: 반환할 데이터셋의 최대 개수 (1 이상)

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 457,
      "title": "서울시 교통량 데이터",
      "description": "서울시 주요 도로의 교통량 데이터",
      "dataType": {
        "id": 2,
        "name": "CSV"
      },
      "dataSource": {
        "id": 1,
        "name": "서울시 공공데이터"
      },
      "thumbnailImageUrl": "https://example.com/thumbnail2.jpg",
      "downloadCount": 1250,
      "projectCount": 45,
      "uploadedBy": {
        "id": 2,
        "nickname": "데이터업로더",
        "profileImageUrl": "https://example.com/profile2.jpg"
      },
      "createdAt": "2024-01-14T10:30:00Z"
    }
  ],
  "message": "인기 있는 데이터셋 조회에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 데이터셋 상세 조회**

**엔드포인트**: `GET /api/v1/datasets/{dataId}`

**설명**: 지정된 데이터셋 ID에 해당하는 데이터셋의 상세 정보를 조회

**경로 변수**:

- `dataId`: 조회할 데이터셋의 고유 식별자

**응답**:

```json
{
  "success": true,
  "data": {
    "id": 458,
    "title": "부동산 가격 데이터",
    "description": "전국 부동산 가격 데이터셋",
    "dataType": {
      "id": 2,
      "name": "CSV"
    },
    "dataSource": {
      "id": 3,
      "name": "국토교통부"
    },
    "fileUrl": "https://example.com/realestate.csv",
    "thumbnailImageUrl": "https://example.com/thumbnail3.jpg",
    "fileSize": 2048000,
    "downloadCount": 890,
    "projectCount": 23,
    "uploadedBy": {
      "id": 3,
      "nickname": "부동산분석가",
      "profileImageUrl": "https://example.com/profile3.jpg"
    },
    "createdAt": "2024-01-13T10:30:00Z",
    "updatedAt": "2024-01-13T10:30:00Z"
  },
  "message": "데이터셋 세부정보 조회에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **3. 최근 데이터셋 조회**

**엔드포인트**: `GET /api/v1/datasets/recent`

**설명**: 최근에 추가된 데이터셋의 최소 정보 목록을 반환

**쿼리 파라미터**:

- `size`: 반환할 데이터셋의 개수 (1 이상)

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 459,
      "title": "최근 업로드된 데이터",
      "description": "방금 업로드된 데이터셋",
      "dataType": {
        "id": 3,
        "name": "JSON"
      },
      "thumbnailImageUrl": "https://example.com/thumbnail4.jpg",
      "uploadedBy": {
        "id": 4,
        "nickname": "신규사용자",
        "profileImageUrl": "https://example.com/profile4.jpg"
      },
      "createdAt": "2024-01-15T09:30:00Z"
    }
  ],
  "message": "최근 데이터셋 조회에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **데이터셋 검색 (Search)**

#### **1. 데이터셋 검색**

**엔드포인트**: `GET /api/v1/datasets/search`

**설명**: 키워드, 필터를 이용한 데이터셋 검색

**쿼리 파라미터**:

- `q`: 검색 키워드
- `dataSourceId`: 데이터 출처 ID (선택사항)
- `dataTypeId`: 데이터 타입 ID (선택사항)
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 기준 (기본값: createdAt,desc)

**응답**:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 460,
        "title": "검색된 데이터셋",
        "description": "검색 키워드가 포함된 데이터셋",
        "dataType": {
          "id": 2,
          "name": "CSV"
        },
        "dataSource": {
          "id": 2,
          "name": "기상청"
        },
        "thumbnailImageUrl": "https://example.com/thumbnail5.jpg",
        "downloadCount": 567,
        "projectCount": 12,
        "uploadedBy": {
          "id": 5,
          "nickname": "기상분석가",
          "profileImageUrl": "https://example.com/profile5.jpg"
        },
        "createdAt": "2024-01-12T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "direction": "DESC",
        "property": "createdAt"
      }
    },
    "totalElements": 25,
    "totalPages": 2,
    "first": true,
    "last": false,
    "numberOfElements": 20
  },
  "message": "데이터셋 검색에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 실시간 검색**

**엔드포인트**: `GET /api/v1/datasets/search/real-time`

**설명**: 실시간 데이터셋 검색 (자동완성용)

**쿼리 파라미터**:

- `q`: 검색 키워드
- `limit`: 반환할 결과 수 (기본값: 10)

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 461,
      "title": "실시간 검색 결과",
      "description": "실시간으로 검색된 데이터셋",
      "dataType": {
        "id": 2,
        "name": "CSV"
      },
      "thumbnailImageUrl": "https://example.com/thumbnail6.jpg"
    }
  ],
  "message": "실시간 데이터셋 검색에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## ❌ **에러 코드**

| 코드                    | HTTP 상태 | 설명                    |
| ----------------------- | --------- | ----------------------- |
| `DATASET_NOT_FOUND`     | 404       | 데이터셋을 찾을 수 없음 |
| `DATASET_ACCESS_DENIED` | 403       | 데이터셋 접근 권한 없음 |
| `INVALID_DATASET_ID`    | 400       | 잘못된 데이터셋 ID      |
| `FILE_UPLOAD_FAILURE`   | 500       | 파일 업로드 실패        |
| `FILE_SIZE_EXCEEDED`    | 413       | 파일 크기 초과          |
| `INVALID_FILE_FORMAT`   | 400       | 잘못된 파일 형식        |
| `INVALID_TOKEN`         | 401       | 유효하지 않은 토큰      |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **데이터셋 업로드**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/datasets" \
  -H "Authorization: Bearer {access_token}" \
  -F "dataFile=@dataset.csv" \
  -F "thumbnailFile=@thumbnail.jpg" \
  -F "webRequest={\"title\":\"데이터셋 제목\",\"description\":\"데이터셋 설명\",\"dataSourceId\":1,\"dataTypeId\":2};type=application/json"
```

#### **데이터셋 검색**

```bash
curl -X GET "https://api.dataracy.co.kr/api/v1/datasets/search?q=교통량&page=0&size=10" \
  -H "Content-Type: application/json"
```

### **JavaScript 예제**

#### **데이터셋 업로드**

```javascript
const uploadDataset = async (datasetData, dataFile, thumbnailFile) => {
  const formData = new FormData();
  formData.append("dataFile", dataFile);
  formData.append("webRequest", JSON.stringify(datasetData));
  if (thumbnailFile) {
    formData.append("thumbnailFile", thumbnailFile);
  }

  const response = await fetch("/api/v1/datasets", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: formData,
  });

  return response.json();
};
```

#### **데이터셋 검색**

```javascript
const searchDatasets = async (keyword, filters = {}) => {
  const params = new URLSearchParams({
    q: keyword,
    page: 0,
    size: 20,
    ...filters,
  });

  const response = await fetch(`/api/v1/datasets/search?${params}`);
  return response.json();
};
```

#### **실시간 검색**

```javascript
const realTimeSearch = async (keyword) => {
  const params = new URLSearchParams({
    q: keyword,
    limit: 10,
  });

  const response = await fetch(`/api/v1/datasets/search/real-time?${params}`);
  return response.json();
};
```

---

## 📊 **데이터셋 통계**

### **지원하는 파일 형식**

- **CSV**: 쉼표로 구분된 값
- **JSON**: JavaScript Object Notation
- **XLSX**: Excel 파일
- **TXT**: 텍스트 파일

### **파일 크기 제한**

- **최대 파일 크기**: 100MB
- **최대 썸네일 크기**: 10MB

### **업로드 제한**

- **일일 업로드 한도**: 10개
- **동시 업로드**: 3개

---

**💡 데이터셋 관련 문제가 발생하면 개발팀에 문의하세요!**
