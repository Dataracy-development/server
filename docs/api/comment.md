# 💬 댓글 API

## 📋 **개요**

댓글 관련 API는 프로젝트에 대한 피드백 댓글의 CRUD 기능을 제공합니다.

**Base URL**: `/api/v1/projects`

---

## 🔑 **엔드포인트**

### **댓글 관리 (Command)**

#### **1. 댓글 작성**

**엔드포인트**: `POST /api/v1/projects/{projectId}/comments`

**설명**: 지정한 프로젝트에 피드백 댓글을 생성

**경로 변수**:

- `projectId`: 댓글을 추가할 프로젝트의 ID (1 이상)

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "content": "댓글 내용입니다.",
  "parentCommentId": 123
}
```

**응답**:

```json
{
  "httpStatus": 201,
  "code": "201",
  "message": "댓글 작성이 완료되었습니다",
  "data": {
    "id": 789
  }
}
```

---

#### **2. 댓글 수정**

**엔드포인트**: `PUT /api/v1/projects/{projectId}/comments/{commentId}`

**설명**: 프로젝트 내 특정 댓글의 내용을 수정

**경로 변수**:

- `projectId`: 댓글이 속한 프로젝트의 ID (1 이상)
- `commentId`: 수정할 댓글의 ID (1 이상)

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "content": "수정된 댓글 내용입니다."
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "댓글 수정이 완료되었습니다.",
  "data": null
}
```

---

#### **3. 댓글 삭제**

**엔드포인트**: `DELETE /api/v1/projects/{projectId}/comments/{commentId}`

**설명**: 프로젝트에서 특정 피드백 댓글을 삭제

**경로 변수**:

- `projectId`: 댓글이 속한 프로젝트의 ID (1 이상)
- `commentId`: 삭제할 피드백 댓글의 ID (1 이상)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "댓글 삭제가 완료되었습니다.",
  "data": null
}
```

---

### **댓글 조회 (Read)**

#### **1. 댓글 목록 조회**

**엔드포인트**: `GET /api/v1/projects/{projectId}/comments`

**설명**: 지정한 프로젝트의 댓글 목록을 페이지네이션하여 조회

**경로 변수**:

- `projectId`: 댓글을 조회할 프로젝트의 ID (1 이상)

**쿼리 파라미터**:

- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 기준 (기본값: createdAt,desc)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "프로젝트에 대한 댓글 목록 조회가 완료되었습니다.",
  "data": {
    "content": [
      {
        "id": 790,
        "content": "정말 좋은 분석이네요!",
        "creatorId": 2,
        "creatorName": "분석애호가",
        "userProfileImageUrl": "https://example.com/profile2.jpg",
        "authorLevelLabel": "중급자",
        "parentCommentId": null,
        "childCommentCount": 2,
        "isLiked": false,
        "createdAt": "2024-01-15T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      "sort": {
        "sorted": true,
        "direction": "DESC",
        "property": "createdAt"
      }
    },
    "totalElements": 15,
    "totalPages": 3,
    "first": true,
    "last": false,
    "numberOfElements": 5
  }
}
```

---

#### **2. 답글 목록 조회**

**엔드포인트**: `GET /api/v1/projects/{projectId}/comments/{commentId}`

**설명**: 특정 댓글에 대한 답글 목록을 페이지네이션하여 조회

**경로 변수**:

- `projectId`: 댓글이 속한 프로젝트의 ID (1 이상)
- `commentId`: 답글을 조회할 부모 댓글의 ID (1 이상)

**쿼리 파라미터**:

- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 5)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "댓글에 대한 답글 목록 조회가 완료되었습니다.",
  "data": {
    "content": [
      {
        "id": 793,
        "content": "답글 1",
        "creatorId": 4,
        "creatorName": "답글작성자1",
        "userProfileImageUrl": "https://example.com/profile4.jpg",
        "authorLevelLabel": "초급자",
        "parentCommentId": 792,
        "isLiked": false,
        "createdAt": "2024-01-15T12:00:00Z"
      },
      {
        "id": 794,
        "content": "답글 2",
        "creatorId": 5,
        "creatorName": "답글작성자2",
        "userProfileImageUrl": "https://example.com/profile5.jpg",
        "authorLevelLabel": "중급자",
        "parentCommentId": 792,
        "isLiked": true,
        "createdAt": "2024-01-15T12:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      "sort": {
        "sorted": true,
        "direction": "ASC",
        "property": "createdAt"
      }
    },
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true,
    "numberOfElements": 2
  }
}
```

---

## ❌ **에러 코드**

| 코드          | HTTP 상태 | 설명                                        |
| ------------- | --------- | ------------------------------------------- |
| `COMMENT-001` | 500       | 피드백 댓글 업로드에 실패했습니다           |
| `COMMENT-002` | 404       | 해당 피드백 댓글 리소스가 존재하지 않습니다 |
| `COMMENT-003` | 404       | 해당 부모 댓글 리소스가 존재하지 않습니다   |
| `COMMENT-004` | 403       | 작성자만 수정 및 삭제, 복원이 가능합니다    |
| `COMMENT-005` | 403       | 답글에 다시 답글을 작성할 순 없습니다       |
| `COMMENT-006` | 403       | 해당 프로젝트에 작성된 댓글이 아닙니다      |
| `AUTH-011`    | 401       | 유효하지 않은 액세스 토큰입니다             |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **댓글 작성**

```bash
curl -X POST "https://api.dataracy.store/api/v1/projects/123/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "content": "정말 좋은 분석이네요!",
    "parentCommentId": null
  }'
```

#### **답글 작성**

```bash
curl -X POST "https://api.dataracy.store/api/v1/projects/123/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "content": "감사합니다!",
    "parentCommentId": 790
  }'
```

#### **댓글 목록 조회**

```bash
curl -X GET "https://api.dataracy.store/api/v1/projects/123/comments?page=0&size=5" \
  -H "Content-Type: application/json"
```

#### **답글 목록 조회**

```bash
curl -X GET "https://api.dataracy.store/api/v1/projects/123/comments/790?page=0&size=5" \
  -H "Content-Type: application/json"
```

### **JavaScript 예제**

#### **댓글 작성**

```javascript
const createComment = async (projectId, content, parentCommentId = null) => {
  const response = await fetch(`/api/v1/projects/${projectId}/comments`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({
      content,
      parentCommentId,
    }),
  });

  return response.json();
};
```

#### **댓글 수정**

```javascript
const updateComment = async (projectId, commentId, content) => {
  const response = await fetch(
    `/api/v1/projects/${projectId}/comments/${commentId}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({ content }),
    }
  );

  return response.json();
};
```

#### **댓글 목록 조회**

```javascript
const getComments = async (projectId, page = 0, size = 5) => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    sort: "createdAt,desc",
  });

  const response = await fetch(
    `/api/v1/projects/${projectId}/comments?${params}`
  );
  return response.json();
};
```

#### **답글 목록 조회**

```javascript
const getReplyComments = async (projectId, commentId, page = 0, size = 5) => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
  });

  const response = await fetch(
    `/api/v1/projects/${projectId}/comments/${commentId}?${params}`
  );
  return response.json();
};
```

#### **댓글 삭제**

```javascript
const deleteComment = async (projectId, commentId) => {
  const response = await fetch(
    `/api/v1/projects/${projectId}/comments/${commentId}`,
    {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  return response.json();
};
```

---

## 📊 **댓글 정책**

### **댓글 제한사항**

- **최대 길이**: 500자
- **일일 작성 한도**: 50개
- **답글 깊이**: 최대 2단계

### **댓글 정렬 옵션**

- **최신순**: `createdAt,desc` (기본값)
- **오래된순**: `createdAt,asc`
- **좋아요순**: `likeCount,desc`

### **댓글 권한**

- **작성자**: 수정, 삭제 가능
- **프로젝트 작성자**: 모든 댓글 삭제 가능
- **관리자**: 모든 댓글 수정, 삭제 가능

---

**💡 댓글 관련 문제가 발생하면 개발팀에 문의하세요!**
