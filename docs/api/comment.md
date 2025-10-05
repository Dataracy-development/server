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
  "success": true,
  "data": {
    "id": 789,
    "content": "댓글 내용입니다.",
    "author": {
      "id": 1,
      "nickname": "댓글작성자",
      "profileImageUrl": "https://example.com/profile.jpg"
    },
    "parentCommentId": 123,
    "likeCount": 0,
    "replyCount": 0,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "message": "피드백 댓글 작성에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
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
  "success": true,
  "data": null,
  "message": "댓글 수정에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
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
  "success": true,
  "data": null,
  "message": "해당하는 피드백 댓글 삭제에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
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
  "success": true,
  "data": {
    "content": [
      {
        "id": 790,
        "content": "정말 좋은 분석이네요!",
        "author": {
          "id": 2,
          "nickname": "분석애호가",
          "profileImageUrl": "https://example.com/profile2.jpg"
        },
        "parentCommentId": null,
        "likeCount": 5,
        "replyCount": 2,
        "replies": [
          {
            "id": 791,
            "content": "감사합니다!",
            "author": {
              "id": 1,
              "nickname": "프로젝트작성자",
              "profileImageUrl": "https://example.com/profile.jpg"
            },
            "parentCommentId": 790,
            "likeCount": 1,
            "replyCount": 0,
            "createdAt": "2024-01-15T11:00:00Z"
          }
        ],
        "createdAt": "2024-01-15T10:30:00Z"
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
  },
  "message": "댓글 목록 조회에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 댓글 상세 조회**

**엔드포인트**: `GET /api/v1/projects/{projectId}/comments/{commentId}`

**설명**: 특정 댓글의 상세 정보와 답글들을 조회

**경로 변수**:

- `projectId`: 댓글이 속한 프로젝트의 ID (1 이상)
- `commentId`: 조회할 댓글의 ID (1 이상)

**응답**:

```json
{
  "success": true,
  "data": {
    "id": 792,
    "content": "상세 조회할 댓글 내용",
    "author": {
      "id": 3,
      "nickname": "댓글상세조회자",
      "profileImageUrl": "https://example.com/profile3.jpg"
    },
    "parentCommentId": null,
    "likeCount": 8,
    "replyCount": 3,
    "replies": [
      {
        "id": 793,
        "content": "답글 1",
        "author": {
          "id": 4,
          "nickname": "답글작성자1",
          "profileImageUrl": "https://example.com/profile4.jpg"
        },
        "parentCommentId": 792,
        "likeCount": 2,
        "replyCount": 0,
        "createdAt": "2024-01-15T12:00:00Z"
      },
      {
        "id": 794,
        "content": "답글 2",
        "author": {
          "id": 5,
          "nickname": "답글작성자2",
          "profileImageUrl": "https://example.com/profile5.jpg"
        },
        "parentCommentId": 792,
        "likeCount": 1,
        "replyCount": 0,
        "createdAt": "2024-01-15T12:30:00Z"
      }
    ],
    "createdAt": "2024-01-15T11:30:00Z",
    "updatedAt": "2024-01-15T11:30:00Z"
  },
  "message": "댓글 상세 조회에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## ❌ **에러 코드**

| 코드                       | HTTP 상태 | 설명                     |
| -------------------------- | --------- | ------------------------ |
| `COMMENT_NOT_FOUND`        | 404       | 댓글을 찾을 수 없음      |
| `COMMENT_ACCESS_DENIED`    | 403       | 댓글 접근 권한 없음      |
| `INVALID_COMMENT_ID`       | 400       | 잘못된 댓글 ID           |
| `INVALID_PROJECT_ID`       | 400       | 잘못된 프로젝트 ID       |
| `PARENT_COMMENT_NOT_FOUND` | 404       | 부모 댓글을 찾을 수 없음 |
| `COMMENT_TOO_LONG`         | 400       | 댓글 내용이 너무 김      |
| `INVALID_TOKEN`            | 401       | 유효하지 않은 토큰       |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **댓글 작성**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/projects/123/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "content": "정말 좋은 분석이네요!",
    "parentCommentId": null
  }'
```

#### **답글 작성**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/projects/123/comments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "content": "감사합니다!",
    "parentCommentId": 790
  }'
```

#### **댓글 목록 조회**

```bash
curl -X GET "https://api.dataracy.co.kr/api/v1/projects/123/comments?page=0&size=10" \
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
const getComments = async (projectId, page = 0, size = 20) => {
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
