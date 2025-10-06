# ❤️ 좋아요 API

## 📋 **개요**

좋아요 관련 API는 프로젝트와 댓글에 대한 좋아요 기능을 제공합니다.

**Base URL**: `/api/v1`

---

## 🔑 **엔드포인트**

### **좋아요 처리**

#### **1. 타겟 좋아요 토글**

**엔드포인트**: `POST /api/v1/likes`

**설명**: 프로젝트 또는 댓글에 좋아요를 추가하거나 제거 (토글 방식)

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "targetId": 123,
  "targetType": "PROJECT",
  "previouslyLiked": false
}
```

**응답** (좋아요 추가):

```json
{
  "httpStatus": 200,
  "code": "LIKE-001",
  "message": "프로젝트에 대한 좋아요 처리에 성공했습니다.",
  "data": null
}
```

**응답** (좋아요 취소):

```json
{
  "httpStatus": 200,
  "code": "LIKE-002",
  "message": "프로젝트에 대한 좋아요 취소 처리에 성공했습니다.",
  "data": null
}
```

---

#### **2. 댓글 좋아요 예제**

**요청 본문** (댓글 좋아요):

```json
{
  "targetId": 456,
  "targetType": "COMMENT",
  "previouslyLiked": false
}
```

**응답** (댓글 좋아요 추가):

```json
{
  "httpStatus": 200,
  "code": "LIKE-003",
  "message": "댓글에 대한 좋아요 처리에 성공했습니다.",
  "data": null
}
```

**응답** (댓글 좋아요 취소):

```json
{
  "httpStatus": 200,
  "code": "LIKE-004",
  "message": "댓글에 대한 좋아요 취소 처리에 성공했습니다.",
  "data": null
}
```

---

## ❌ **에러 코드**

| 코드       | HTTP 상태 | 설명                                            | Enum 이름               |
| ---------- | --------- | ----------------------------------------------- | ----------------------- |
| `LIKE-001` | 500       | 해당 좋아요 리소스를 찾을 수 없습니다           | `NOT_FOUND_TARGET_LIKE` |
| `LIKE-002` | 500       | 프로젝트에 대한 좋아요 처리에 실패했습니다      | `FAIL_LIKE_PROJECT`     |
| `LIKE-003` | 500       | 프로젝트에 대한 좋아요 취소 처리에 실패했습니다 | `FAIL_UNLIKE_PROJECT`   |
| `LIKE-004` | 500       | 댓글에 대한 좋아요 처리에 실패했습니다          | `FAIL_LIKE_COMMENT`     |
| `LIKE-005` | 500       | 댓글에 대한 좋아요 취소 처리에 실패했습니다     | `FAIL_UNLIKE_COMMENT`   |
| `LIKE-006` | 403       | 작성자만 좋아요 및 취소가 가능합니다            | `NOT_MATCH_CREATOR`     |
| `LIKE-007` | 400       | 잘못된 좋아요 타겟 유형입니다                   | `INVALID_TARGET_TYPE`   |
| `AUTH-011` | 401       | 유효하지 않은 액세스 토큰입니다                 | `INVALID_ACCESS_TOKEN`  |

---

## ✅ **성공 응답 코드**

| 코드       | HTTP 상태 | 설명                                            | Enum 이름        |
| ---------- | --------- | ----------------------------------------------- | ---------------- |
| `LIKE-001` | 200       | 프로젝트에 대한 좋아요 처리에 성공했습니다      | `LIKE_PROJECT`   |
| `LIKE-002` | 200       | 프로젝트에 대한 좋아요 취소 처리에 성공했습니다 | `UNLIKE_PROJECT` |
| `LIKE-003` | 200       | 댓글에 대한 좋아요 처리에 성공했습니다          | `LIKE_COMMENT`   |
| `LIKE-004` | 200       | 댓글에 대한 좋아요 취소 처리에 성공했습니다     | `UNLIKE_COMMENT` |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **프로젝트 좋아요**

```bash
curl -X POST "https://api.dataracy.store/api/v1/likes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "targetId": 123,
    "targetType": "PROJECT",
    "previouslyLiked": false
  }'
```

#### **댓글 좋아요**

```bash
curl -X POST "https://api.dataracy.store/api/v1/likes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "targetId": 456,
    "targetType": "COMMENT",
    "previouslyLiked": false
  }'
```

### **JavaScript 예제**

#### **프로젝트 좋아요 토글**

```javascript
const toggleProjectLike = async (projectId, previouslyLiked) => {
  const response = await fetch("/api/v1/likes", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({
      targetId: projectId,
      targetType: "PROJECT",
      previouslyLiked: previouslyLiked,
    }),
  });

  return response.json();
};
```

#### **댓글 좋아요 토글**

```javascript
const toggleCommentLike = async (commentId, previouslyLiked) => {
  const response = await fetch("/api/v1/likes", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({
      targetId: commentId,
      targetType: "COMMENT",
      previouslyLiked: previouslyLiked,
    }),
  });

  return response.json();
};
```

---

## 📊 **좋아요 정책**

### **좋아요 제한사항**

- **자신의 콘텐츠**: 자신의 프로젝트/댓글에 좋아요 불가 (`NOT_MATCH_CREATOR`)
- **중복 좋아요**: 토글 방식으로 중복 방지 (Unique Constraint: `targetId`, `targetType`, `userId`)
- **삭제된 콘텐츠**: 삭제된 프로젝트/댓글에 좋아요 불가 (검증 로직)

### **동시성 제어**

- **분산 락**: `@DistributedLock` 적용
- **락 키**: `lock:like:{targetType}:{targetId}:user:{userId}`
- **대기 시간**: 500ms
- **임대 시간**: 3000ms
- **재시도**: 3회

### **이벤트 처리**

- **Kafka 이벤트**: 좋아요/취소 시 비동기 이벤트 발행
- **실시간 업데이트**: 좋아요 수 즉시 반영
- **Elasticsearch 동기화**: 프로젝트/댓글 좋아요 수 실시간 반영

### **지원하는 타겟 타입**

- **PROJECT**: 프로젝트 좋아요
- **COMMENT**: 댓글 좋아요

---

**💡 좋아요 관련 문제가 발생하면 개발팀에 문의하세요!**
