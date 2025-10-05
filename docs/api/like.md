# ❤️ 좋아요 API

## 📋 **개요**

좋아요 관련 API는 프로젝트와 댓글에 대한 좋아요 기능을 제공합니다.

**Base URL**: `/api/v1`

---

## 🔑 **엔드포인트**

### **프로젝트 좋아요**

#### **1. 프로젝트 좋아요 토글**

**엔드포인트**: `POST /api/v1/projects/{projectId}/like`

**설명**: 프로젝트에 좋아요를 추가하거나 제거 (토글 방식)

**경로 변수**:

- `projectId`: 좋아요할 프로젝트의 ID (1 이상)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "좋아요 처리가 완료되었습니다.",
  "data": {
    "isLiked": true,
    "likeCount": 42
  }
}
```

---

#### **2. 댓글 좋아요 토글**

**엔드포인트**: `POST /api/v1/projects/{projectId}/comments/{commentId}/like`

**설명**: 댓글에 좋아요를 추가하거나 제거 (토글 방식)

**경로 변수**:

- `projectId`: 댓글이 속한 프로젝트의 ID (1 이상)
- `commentId`: 좋아요할 댓글의 ID (1 이상)

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "댓글 좋아요 처리가 완료되었습니다.",
  "data": {
    "isLiked": true,
    "likeCount": 15
  }
}
```

---

## ❌ **에러 코드**

| 코드                | HTTP 상태 | 설명                    |
| ------------------- | --------- | ----------------------- |
| `PROJECT_NOT_FOUND` | 404       | 프로젝트를 찾을 수 없음 |
| `COMMENT_NOT_FOUND` | 404       | 댓글을 찾을 수 없음     |
| `UNAUTHORIZED`      | 401       | 인증 필요               |
| `FORBIDDEN`         | 403       | 권한 없음               |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **프로젝트 좋아요**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/projects/123/like" \
  -H "Authorization: Bearer {access_token}"
```

#### **댓글 좋아요**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/projects/123/comments/456/like" \
  -H "Authorization: Bearer {access_token}"
```

### **JavaScript 예제**

#### **프로젝트 좋아요 토글**

```javascript
const toggleProjectLike = async (projectId) => {
  const response = await fetch(`/api/v1/projects/${projectId}/like`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.json();
};
```

#### **댓글 좋아요 토글**

```javascript
const toggleCommentLike = async (projectId, commentId) => {
  const response = await fetch(
    `/api/v1/projects/${projectId}/comments/${commentId}/like`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  return response.json();
};
```

---

## 📊 **좋아요 정책**

### **좋아요 제한사항**

- **자신의 콘텐츠**: 자신의 프로젝트/댓글에 좋아요 불가
- **중복 좋아요**: 토글 방식으로 중복 방지
- **삭제된 콘텐츠**: 삭제된 프로젝트/댓글에 좋아요 불가

### **좋아요 통계**

- **실시간 업데이트**: 좋아요 수 즉시 반영
- **캐싱**: 인기 콘텐츠의 좋아요 수 캐싱
- **정기 동기화**: 데이터 일관성 유지

---

**💡 좋아요 관련 문제가 발생하면 개발팀에 문의하세요!**
