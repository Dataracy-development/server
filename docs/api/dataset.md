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
  "topicId": 1,
  "dataSourceId": 2,
  "dataTypeId": 3,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "description": "데이터셋 설명",
  "analysisGuide": "분석 가이드"
}
```

**응답**:

```json
{
  "httpStatus": 201,
  "code": "201",
  "message": "제출이 완료되었습니다",
  "data": {
    "id": 456
  }
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
dataFile: [파일] (선택사항)
thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "수정된 제목",
  "topicId": 2,
  "dataSourceId": 3,
  "dataTypeId": 4,
  "startDate": "2024-02-01",
  "endDate": "2024-12-31",
  "description": "수정된 설명",
  "analysisGuide": "수정된 분석 가이드"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "수정이 완료되었습니다",
  "data": null
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
  "httpStatus": 200,
  "code": "200",
  "message": "데이터셋 삭제가 완료되었습니다.",
  "data": null
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
  "httpStatus": 200,
  "code": "200",
  "message": "인기 데이터셋 조회가 완료되었습니다.",
  "data": [
    {
      "id": 457,
      "title": "서울시 교통량 데이터",
      "creatorId": 2,
      "creatorName": "데이터업로더",
      "userProfileImageUrl": "https://example.com/profile2.jpg",
      "topicLabel": "교통",
      "dataSourceLabel": "서울시 공공데이터",
      "dataTypeLabel": "CSV",
      "startDate": "2024-01-01",
      "endDate": "2024-12-31",
      "description": "서울시 주요 도로의 교통량 데이터",
      "dataThumbnailUrl": "https://example.com/thumbnail2.jpg",
      "downloadCount": 1250,
      "sizeBytes": 1024000,
      "rowCount": 10000,
      "columnCount": 5,
      "createdAt": "2024-01-14T10:30:00Z",
      "countConnectedProjects": 45
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "데이터 상세 정보 조회가 완료되었습니다.",
  "data": {
    "id": 458,
    "title": "부동산 가격 데이터",
    "creatorId": 3,
    "creatorName": "부동산분석가",
    "userProfileImageUrl": "https://example.com/profile3.jpg",
    "topicLabel": "부동산",
    "dataSourceLabel": "국토교통부",
    "dataTypeLabel": "CSV",
    "authorLevelLabel": "중급자",
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "description": "전국 부동산 가격 데이터셋",
    "analysisGuide": "부동산 가격 분석 가이드",
    "dataThumbnailUrl": "https://example.com/thumbnail3.jpg",
    "downloadCount": 890,
    "sizeBytes": 2048000,
    "rowCount": 50000,
    "columnCount": 8,
    "previewJson": "{\"sample\": \"data\"}",
    "createdAt": "2024-01-13T10:30:00Z"
  }
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
  "httpStatus": 200,
  "code": "200",
  "message": "최신 데이터셋 목록 조회가 완료되었습니다.",
  "data": [
    {
      "id": 459,
      "title": "최근 업로드된 데이터",
      "creatorId": 4,
      "creatorName": "신규사용자",
      "userProfileImageUrl": "https://example.com/profile4.jpg",
      "dataThumbnailUrl": "https://example.com/thumbnail4.jpg",
      "createdAt": "2024-01-15T09:30:00Z"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "데이터셋 필터링이 완료되었습니다.",
  "data": {
    "content": [
      {
        "id": 460,
        "title": "검색된 데이터셋",
        "creatorId": 5,
        "creatorName": "기상분석가",
        "userProfileImageUrl": "https://example.com/profile5.jpg",
        "topicLabel": "기상",
        "dataSourceLabel": "기상청",
        "dataTypeLabel": "CSV",
        "dataThumbnailUrl": "https://example.com/thumbnail5.jpg",
        "downloadCount": 567,
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
  }
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
  "httpStatus": 200,
  "code": "200",
  "message": "데이터셋 자동완성을 위한 데이터셋 목록을 조회한다.",
  "data": [
    {
      "id": 461,
      "title": "실시간 검색 결과",
      "creatorId": 6,
      "creatorName": "실시간분석가",
      "userProfileImageUrl": "https://example.com/profile6.jpg",
      "dataThumbnailUrl": "https://example.com/thumbnail6.jpg"
    }
  ]
}
```

---

## ❌ **에러 코드**

| 코드       | HTTP 상태 | 설명                                                |
| ---------- | --------- | --------------------------------------------------- |
| `DATA-002` | 404       | 해당 데이터셋 리소스가 존재하지 않습니다            |
| `DATA-008` | 403       | 작성자만 수정 및 삭제, 복원이 가능합니다            |
| `DATA-001` | 500       | 데이터셋 업로드에 실패했습니다                      |
| `DATA-003` | 400       | 데이터셋 수집 시작일은 종료일보다 이전이어야 합니다 |
| `DATA-010` | 400       | 유효하지 않은 파일 url입니다                        |
| `FILE-001` | 400       | 이미지 파일은 최대 10MB까지 업로드 가능합니다       |
| `AUTH-011` | 401       | 유효하지 않은 액세스 토큰입니다                     |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **데이터셋 업로드**

```bash
curl -X POST "https://api.dataracy.store/api/v1/datasets" \
  -H "Authorization: Bearer {access_token}" \
  -F "dataFile=@dataset.csv" \
  -F "thumbnailFile=@thumbnail.jpg" \
  -F "webRequest={\"title\":\"데이터셋 제목\",\"topicId\":1,\"dataSourceId\":2,\"dataTypeId\":3,\"startDate\":\"2024-01-01\",\"endDate\":\"2024-12-31\",\"description\":\"데이터셋 설명\",\"analysisGuide\":\"분석 가이드\"};type=application/json"
```

#### **데이터셋 검색**

```bash
curl -X GET "https://api.dataracy.store/api/v1/datasets/search?q=교통량&page=0&size=10" \
  -H "Content-Type: application/json"
```

### **JavaScript 예제**

#### **데이터셋 업로드**

```javascript
const uploadDataset = async (datasetData, dataFile, thumbnailFile) => {
  const formData = new FormData();
  formData.append("dataFile", dataFile);
  formData.append(
    "webRequest",
    JSON.stringify({
      title: datasetData.title,
      topicId: datasetData.topicId,
      dataSourceId: datasetData.dataSourceId,
      dataTypeId: datasetData.dataTypeId,
      startDate: datasetData.startDate,
      endDate: datasetData.endDate,
      description: datasetData.description,
      analysisGuide: datasetData.analysisGuide,
    })
  );
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
