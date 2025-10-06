# 📊 프로젝트 API

## 📋 **개요**

프로젝트 관련 API는 프로젝트 CRUD, 검색, 좋아요, 이어가기 기능을 제공합니다.

**Base URL**: `/api/v1/projects`

---

## 🔑 **엔드포인트**

### **프로젝트 관리 (Command)**

#### **1. 프로젝트 업로드**

**엔드포인트**: `POST /api/v1/projects`

**설명**: 새로운 프로젝트 정보를 업로드하여 데이터베이스에 저장

**요청 헤더**:

```http
Content-Type: multipart/form-data
Authorization: Bearer {access_token}
```

**요청 본문** (multipart/form-data):

```
thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "프로젝트 제목",
  "topicId": 1,
  "analysisPurposeId": 2,
  "dataSourceId": 3,
  "authorLevelId": 1,
  "isContinue": true,
  "parentProjectId": 1,
  "content": "프로젝트 내용",
  "dataIds": [1, 2, 3]
}
```

**응답**:

```json
{
  "httpStatus": 201,
  "code": "201",
  "message": "제출이 완료되었습니다",
  "data": {
    "id": 123,
    "title": "프로젝트 제목",
    "content": "프로젝트 내용",
    "projectThumbnailUrl": "https://example.com/thumbnail.jpg",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

---

#### **2. 프로젝트 수정**

**엔드포인트**: `PUT /api/v1/projects/{projectId}`

**설명**: 기존 프로젝트를 전달받은 정보와 선택적 썸네일 파일로 수정

**경로 변수**:

- `projectId`: 수정할 프로젝트의 고유 ID (1 이상)

**요청 헤더**:

```http
Content-Type: multipart/form-data
Authorization: Bearer {access_token}
```

**요청 본문** (multipart/form-data):

```
thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "수정된 제목",
  "topicId": 2,
  "analysisPurposeId": 3,
  "dataSourceId": 4,
  "authorLevelId": 2,
  "isContinue": false,
  "parentProjectId": null,
  "content": "수정된 내용",
  "dataIds": [2, 3, 4]
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "프로젝트 수정이 완료되었습니다.",
  "data": null
}
```

---

#### **3. 프로젝트 삭제**

**엔드포인트**: `DELETE /api/v1/projects/{projectId}`

**설명**: 지정한 ID의 프로젝트를 삭제 (소프트 삭제)

**경로 변수**:

- `projectId`: 삭제할 프로젝트의 ID (1 이상)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "프로젝트 삭제가 완료되었습니다.",
  "data": null
}
```

---

#### **4. 프로젝트 복원**

**엔드포인트**: `PATCH /api/v1/projects/{projectId}/restore`

**설명**: 삭제된 프로젝트를 복원

**경로 변수**:

- `projectId`: 복원할 프로젝트의 ID (1 이상)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "프로젝트 복원에 완료되었습니다.",
  "data": null
}
```

---

### **프로젝트 조회 (Read)**

#### **1. 프로젝트 상세 조회**

**엔드포인트**: `GET /api/v1/projects/{projectId}`

**설명**: 지정한 프로젝트의 상세 정보를 반환

**경로 변수**:

- `projectId`: 조회할 프로젝트의 고유 ID (1 이상)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "프로젝트 상세 정보를 조회하였습니다.",
  "data": {
    "id": 123,
    "title": "서울시 교통량 분석",
    "content": "서울시 주요 도로의 교통량을 분석한 프로젝트입니다.",
    "projectThumbnailUrl": "https://example.com/thumbnail.jpg",
    "creatorId": 1,
    "creatorName": "분석가",
    "userProfileImageUrl": "https://example.com/profile.jpg",
    "topicLabel": "교통",
    "analysisPurposeLabel": "예측",
    "dataSourceLabel": "서울시 공공데이터",
    "authorLevelLabel": "중급자",
    "likeCount": 42,
    "viewCount": 156,
    "commentCount": 8,
    "isLiked": false,
    "hasChild": true,
    "connectedDataSets": [
      {
        "id": 1,
        "title": "서울시 교통량 데이터",
        "dataThumbnailUrl": "https://example.com/data-thumb.jpg"
      }
    ],
    "parentProject": null,
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

---

#### **2. 이어가기 프로젝트 조회**

**엔드포인트**: `GET /api/v1/projects/{projectId}/continue`

**설명**: 지정한 프로젝트를 기준으로 이어지는 프로젝트들의 페이지 형식 리스트를 조회

**경로 변수**:

- `projectId`: 조회 기준이 되는 프로젝트의 ID

**쿼리 파라미터**:

- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 3)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "해당하는 프로젝트의 이어가기 프로젝트 리스트를 조회하였습니다.",
  "data": {
    "content": [
      {
        "id": 124,
        "title": "부산시 교통량 분석",
        "content": "부산시 교통량을 분석한 프로젝트",
        "projectThumbnailUrl": "https://example.com/thumbnail2.jpg",
        "creatorId": 2,
        "creatorName": "다른분석가",
        "userProfileImageUrl": "https://example.com/profile2.jpg",
        "likeCount": 25,
        "viewCount": 89,
        "createdAt": "2024-01-14T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 3,
      "sort": {
        "sorted": false
      }
    },
    "totalElements": 5,
    "totalPages": 2,
    "first": true,
    "last": false,
    "numberOfElements": 3
  }
}
```

---

#### **3. 데이터셋과 연결된 프로젝트 조회**

**엔드포인트**: `GET /api/v1/projects/connected-to-dataset`

**설명**: 지정한 데이터셋에 연결된 프로젝트 목록을 페이지네이션하여 반환

**쿼리 파라미터**:

- `dataId`: 연결된 프로젝트를 조회할 데이터셋의 식별자 (1 이상)
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 3)

**응답**:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 125,
        "title": "교통량 예측 모델",
        "description": "머신러닝을 활용한 교통량 예측",
        "thumbnailImageUrl": "https://example.com/thumbnail3.jpg",
        "author": {
          "id": 3,
          "nickname": "ML전문가",
          "profileImageUrl": "https://example.com/profile3.jpg"
        },
        "likeCount": 67,
        "viewCount": 234,
        "createdAt": "2024-01-13T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 3,
      "sort": {
        "sorted": false
      }
    },
    "totalElements": 8,
    "totalPages": 3,
    "first": true,
    "last": false,
    "numberOfElements": 3
  },
  "message": "데이터와 연결된 프로젝트 리스트 조회에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **4. 인기 프로젝트 조회**

**엔드포인트**: `GET /api/v1/projects/popular`

**설명**: 지정한 개수만큼 좋아요, 댓글, 조회수를 기준으로 인기 프로젝트 목록을 조회

**쿼리 파라미터**:

- `size`: 조회할 인기 프로젝트의 개수 (1 이상)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "인기있는 프로젝트 리스트를 조회하였습니다.",
  "data": [
    {
      "id": 126,
      "title": "부동산 가격 예측",
      "content": "딥러닝을 활용한 부동산 가격 예측 모델",
      "creatorId": 4,
      "creatorName": "부동산분석가",
      "userProfileImageUrl": "https://example.com/profile4.jpg",
      "projectThumbnailUrl": "https://example.com/thumbnail4.jpg",
      "topicLabel": "부동산",
      "analysisPurposeLabel": "예측",
      "dataSourceLabel": "공공데이터",
      "authorLevelLabel": "고급자",
      "commentCount": 23,
      "likeCount": 156,
      "viewCount": 892
    }
  ]
}
```

---

#### **5. 내 프로젝트 조회**

**엔드포인트**: `GET /api/v1/projects/me`

**설명**: 로그인한 사용자가 업로드한 프로젝트들의 페이지를 조회

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**쿼리 파라미터**:

- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 5)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "로그인한 회원이 업로드한 프로젝트 목록 조회가 완료되었습니다.",
  "data": {
    "content": [
      {
        "id": 127,
        "title": "내 첫 프로젝트",
        "content": "첫 번째 분석 프로젝트입니다",
        "projectThumbnailUrl": "https://example.com/thumbnail5.jpg",
        "likeCount": 12,
        "viewCount": 45,
        "commentCount": 3,
        "createdAt": "2024-01-11T10:30:00Z",
        "updatedAt": "2024-01-11T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      "sort": {
        "sorted": false
      }
    },
    "totalElements": 3,
    "totalPages": 1,
    "first": true,
    "last": true,
    "numberOfElements": 3
  }
}
```

---

#### **6. 좋아요한 프로젝트 조회**

**엔드포인트**: `GET /api/v1/projects/like`

**설명**: 로그인한 회원이 좋아요한 프로젝트의 페이징된 목록을 조회

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**쿼리 파라미터**:

- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 5)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "로그인한 회원이 좋아요한 프로젝트 목록 조회가 완료되었습니다.",
  "data": {
    "content": [
      {
        "id": 128,
        "title": "좋아요한 프로젝트",
        "content": "마음에 들어서 좋아요한 프로젝트",
        "projectThumbnailUrl": "https://example.com/thumbnail6.jpg",
        "creatorId": 5,
        "creatorName": "좋은분석가",
        "userProfileImageUrl": "https://example.com/profile5.jpg",
        "likeCount": 89,
        "viewCount": 234,
        "commentCount": 12,
        "createdAt": "2024-01-10T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      "sort": {
        "sorted": false
      }
    },
    "totalElements": 7,
    "totalPages": 2,
    "first": true,
    "last": false,
    "numberOfElements": 5
  }
}
```

---

### **프로젝트 검색 (Search)**

#### **1. 프로젝트 검색**

**엔드포인트**: `GET /api/v1/projects/search`

**설명**: 키워드, 필터를 이용한 프로젝트 검색

**쿼리 파라미터**:

- `q`: 검색 키워드
- `analysisPurposeId`: 분석 목적 ID (선택사항)
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
  "message": "필터링된 프로젝트 리스트를 조회하였습니다.",
  "data": {
    "content": [
      {
        "id": 129,
        "title": "검색된 프로젝트",
        "content": "검색 키워드가 포함된 프로젝트",
        "projectThumbnailUrl": "https://example.com/thumbnail7.jpg",
        "creatorId": 6,
        "creatorName": "검색된분석가",
        "userProfileImageUrl": "https://example.com/profile6.jpg",
        "likeCount": 34,
        "viewCount": 123,
        "commentCount": 5,
        "createdAt": "2024-01-09T10:30:00Z"
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
    "totalElements": 15,
    "totalPages": 1,
    "first": true,
    "last": true,
    "numberOfElements": 15
  }
}
```

---

## ❌ **에러 코드**

| 코드          | HTTP 상태 | 설명                                          | Enum 이름                      |
| ------------- | --------- | --------------------------------------------- | ------------------------------ |
| `PROJECT-001` | 500       | 프로젝트 업로드에 실패했습니다                | `FAIL_SAVE_PROJECT`            |
| `PROJECT-002` | 404       | 해당 프로젝트 리소스가 존재하지 않습니다      | `NOT_FOUND_PROJECT`            |
| `PROJECT-003` | 404       | 해당 부모 프로젝트 리소스가 존재하지 않습니다 | `NOT_FOUND_PARENT_PROJECT`     |
| `PROJECT-008` | 403       | 작성자만 수정 및 삭제, 복원이 가능합니다      | `NOT_MATCH_CREATOR`            |
| `PROJECT-010` | 400       | 유효하지 않은 썸네일 파일 URL입니다           | `INVALID_THUMBNAIL_FILE_URL`   |
| `FILE-001`    | 400       | 이미지 파일은 최대 10MB까지 업로드 가능합니다 | `OVER_MAXIMUM_IMAGE_FILE_SIZE` |
| `AUTH-011`    | 401       | 유효하지 않은 액세스 토큰입니다               | `INVALID_ACCESS_TOKEN`         |

---

## ✅ **성공 응답 코드**

| 코드  | HTTP 상태 | 설명                                                          | Enum 이름                                |
| ----- | --------- | ------------------------------------------------------------- | ---------------------------------------- |
| `201` | 201       | 제출이 완료되었습니다                                         | `CREATED_PROJECT`                        |
| `200` | 200       | 실시간 프로젝트 리스트를 조회하였습니다                       | `FIND_REAL_TIME_PROJECTS`                |
| `200` | 200       | 유사 프로젝트 리스트를 조회하였습니다                         | `FIND_SIMILAR_PROJECTS`                  |
| `200` | 200       | 인기있는 프로젝트 리스트를 조회하였습니다                     | `FIND_POPULAR_PROJECTS`                  |
| `200` | 200       | 필터링된 프로젝트 리스트를 조회하였습니다                     | `FIND_FILTERED_PROJECTS`                 |
| `200` | 200       | 프로젝트 상세 정보를 조회하였습니다                           | `GET_PROJECT_DETAIL`                     |
| `200` | 200       | 해당하는 프로젝트의 이어가기 프로젝트 리스트를 조회하였습니다 | `GET_CONTINUE_PROJECTS`                  |
| `200` | 200       | 해당하는 데이터셋을 이용한 프로젝트 리스트를 조회하였습니다   | `GET_CONNECTED_PROJECTS_ASSOCIATED_DATA` |
| `200` | 200       | 프로젝트 수정이 완료되었습니다                                | `MODIFY_PROJECT`                         |
| `200` | 200       | 프로젝트 삭제가 완료되었습니다                                | `DELETE_PROJECT`                         |
| `200` | 200       | 프로젝트 복원에 완료되었습니다                                | `RESTORE_PROJECT`                        |
| `200` | 200       | 로그인한 회원이 업로드한 프로젝트 목록 조회가 완료되었습니다  | `GET_USER_PROJECTS`                      |
| `200` | 200       | 로그인한 회원이 좋아요한 프로젝트 목록 조회가 완료되었습니다  | `GET_LIKE_PROJECTS`                      |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **프로젝트 업로드**

```bash
curl -X POST "https://api.dataracy.store/api/v1/projects" \
  -H "Authorization: Bearer {access_token}" \
  -F "webRequest={\"title\":\"프로젝트 제목\",\"topicId\":1,\"analysisPurposeId\":2,\"dataSourceId\":3,\"authorLevelId\":1,\"isContinue\":true,\"parentProjectId\":1,\"content\":\"프로젝트 내용\",\"dataIds\":[1,2,3]};type=application/json" \
  -F "thumbnailFile=@thumbnail.jpg"
```

#### **프로젝트 검색**

```bash
curl -X GET "https://api.dataracy.store/api/v1/projects/search?q=교통량&page=0&size=10" \
  -H "Content-Type: application/json"
```

### **JavaScript 예제**

#### **프로젝트 업로드**

```javascript
const uploadProject = async (projectData, thumbnailFile) => {
  const formData = new FormData();
  formData.append(
    "webRequest",
    JSON.stringify({
      title: projectData.title,
      topicId: projectData.topicId,
      analysisPurposeId: projectData.analysisPurposeId,
      dataSourceId: projectData.dataSourceId,
      authorLevelId: projectData.authorLevelId,
      isContinue: projectData.isContinue,
      parentProjectId: projectData.parentProjectId,
      content: projectData.content,
      dataIds: projectData.dataIds,
    })
  );
  if (thumbnailFile) {
    formData.append("thumbnailFile", thumbnailFile);
  }

  const response = await fetch("/api/v1/projects", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: formData,
  });

  return response.json();
};
```

#### **프로젝트 검색**

```javascript
const searchProjects = async (keyword, filters = {}) => {
  const params = new URLSearchParams({
    q: keyword,
    page: 0,
    size: 20,
    ...filters,
  });

  const response = await fetch(`/api/v1/projects/search?${params}`);
  return response.json();
};
```

---

**💡 프로젝트 관련 문제가 발생하면 개발팀에 문의하세요!**
