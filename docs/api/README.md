# 📡 API 문서

## 📋 **API 개요**

### **Base URL**

- **개발**: `https://dev-api.dataracy.co.kr`
- **운영**: `https://api.dataracy.co.kr`
- **로컬**: `http://localhost:8080`

### **API 버전**

- **현재 버전**: `v1`
- **URL 패턴**: `/api/v1/{resource}`

### **지원 형식**

- **Content-Type**: `application/json`, `multipart/form-data`
- **Accept**: `application/json`
- **문자 인코딩**: `UTF-8`

---

## 🔗 **API 목록**

### **인증 (Authentication)**

- [인증 API](./authentication.md) - 로그인, 토큰 관리, OAuth2

### **사용자 (User)**

- [사용자 API](./user.md) - 회원가입, 프로필, 비밀번호 관리

### **프로젝트 (Project)**

- [프로젝트 API](./project.md) - CRUD, 검색, 좋아요, 이어가기

### **데이터셋 (Dataset)**

- [데이터셋 API](./dataset.md) - 업로드, 다운로드, 메타데이터

### **댓글 (Comment)**

- [댓글 API](./comment.md) - CRUD, 좋아요

### **좋아요 (Like)**

- [좋아요 API](./like.md) - 프로젝트/댓글 좋아요

### **파일 (File)**

- [파일 API](./file.md) - 파일 업로드, 다운로드

### **이메일 (Email)**

- [이메일 API](./email.md) - 이메일 인증, 발송

### **참조 데이터 (Reference)**

- [참조 데이터 API](./reference.md) - 토픽, 직업, 분석 목적 등

---

## 🔐 **인증 및 보안**

### **JWT 토큰 인증**

```http
Authorization: Bearer {access_token}
```

### **쿠키 기반 인증**

- **리프레시 토큰**: `refreshToken` 쿠키
- **액세스 토큰**: `accessToken` 쿠키
- **만료 시간**: `accessTokenExpiration` 쿠키

### **OAuth2 소셜 로그인**

- **카카오**: `/oauth2/authorization/kakao`
- **구글**: `/oauth2/authorization/google`

---

## 📊 **공통 응답 형식**

### **성공 응답**

```json
{
  "success": true,
  "data": {
    // 응답 데이터
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### **에러 응답**

```json
{
  "success": false,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "사용자를 찾을 수 없습니다.",
    "details": "요청한 사용자 ID가 존재하지 않습니다."
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### **페이징 응답**

```json
{
  "success": true,
  "data": {
    "content": [
      // 데이터 배열
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
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false,
    "numberOfElements": 20
  }
}
```

---

## ❌ **에러 코드**

### **공통 에러**

| 코드                    | HTTP 상태 | 설명           |
| ----------------------- | --------- | -------------- |
| `INVALID_REQUEST`       | 400       | 잘못된 요청    |
| `UNAUTHORIZED`          | 401       | 인증 필요      |
| `FORBIDDEN`             | 403       | 권한 없음      |
| `NOT_FOUND`             | 404       | 리소스 없음    |
| `INTERNAL_SERVER_ERROR` | 500       | 서버 내부 오류 |

### **사용자 관련 에러**

| 코드                  | HTTP 상태 | 설명                  |
| --------------------- | --------- | --------------------- |
| `USER_NOT_FOUND`      | 404       | 사용자를 찾을 수 없음 |
| `DUPLICATED_EMAIL`    | 409       | 중복된 이메일         |
| `DUPLICATED_NICKNAME` | 409       | 중복된 닉네임         |
| `INVALID_PASSWORD`    | 400       | 잘못된 비밀번호       |

---

## 🔧 **개발자 도구**

### **Swagger UI**

- **개발**: `https://dev-api.dataracy.co.kr/swagger-ui.html`
- **운영**: `https://api.dataracy.co.kr/swagger-ui.html`
- **로컬**: `http://localhost:8080/swagger-ui.html`

### **API 테스트**

```bash
# cURL 예제
curl -X GET "https://api.dataracy.co.kr/api/v1/projects" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### **Rate Limiting**

- **일반 사용자**: 1000 requests/hour
- **인증된 사용자**: 5000 requests/hour
- **관리자**: 10000 requests/hour

---

## 📈 **성능 가이드**

### **응답 시간 목표**

- **일반 조회**: < 200ms
- **검색**: < 500ms
- **파일 업로드**: < 5s
- **복잡한 분석**: < 30s

### **캐싱 전략**

- **프로젝트 목록**: 5분 캐시
- **사용자 프로필**: 10분 캐시
- **검색 결과**: 1분 캐시

### **페이징 권장사항**

- **기본 페이지 크기**: 20
- **최대 페이지 크기**: 100
- **정렬**: `createdAt,desc` (기본값)

---

**💡 각 API의 상세 내용은 해당 문서를 참고하세요!**
