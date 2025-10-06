# 🔐 인증 API

## 📋 **개요**

인증 관련 API는 사용자 로그인, 토큰 관리, OAuth2 소셜 로그인을 제공합니다.

**Base URL**: `/api/v1/auth`

---

## 🔑 **엔드포인트**

### **1. 자체 로그인**

**엔드포인트**: `POST /api/v1/auth/login`

**설명**: 이메일과 비밀번호를 이용한 자체 로그인

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "자체 로그인에 성공하였습니다.",
  "data": null
}
```

**쿠키 설정**:

- `refreshToken`: 리프레시 토큰 (HttpOnly, Secure)

---

### **2. 토큰 재발급**

**엔드포인트**: `POST /api/v1/auth/token/re-issue`

**설명**: 리프레시 토큰을 이용한 토큰 재발급

**요청 쿠키**:

```http
Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "토큰 재발급에 성공하였습니다.",
  "data": null
}
```

**새로운 쿠키 설정**:

- `refreshToken`: 새로운 리프레시 토큰 (HttpOnly, Secure)

---

### **3. OAuth2 소셜 로그인**

#### **카카오 로그인**

**엔드포인트**: `GET /oauth2/authorization/kakao`

**설명**: 카카오 OAuth2 로그인 시작

**요청**: 브라우저에서 직접 접근

**응답**: 카카오 로그인 페이지로 리다이렉트

**스코프**: `profile_nickname`, `account_email`

#### **구글 로그인**

**엔드포인트**: `GET /oauth2/authorization/google`

**설명**: 구글 OAuth2 로그인 시작

**요청**: 브라우저에서 직접 접근

**응답**: 구글 로그인 페이지로 리다이렉트

**스코프**: `email`, `profile`

---

## 🔒 **보안 정책**

### **토큰 만료 시간**

- **액세스 토큰**: 1시간 (환경변수: `ACCESS_TOKEN_EXPIRATION_TIME`)
- **리프레시 토큰**: 14일 (환경변수: `REFRESH_TOKEN_EXPIRATION_TIME`)
- **레지스터 토큰**: 10분 (환경변수: `REGISTER_TOKEN_EXPIRATION_TIME`)
- **리셋 토큰**: 10분 (환경변수: `RESET_TOKEN_EXPIRATION_TIME`)

### **토큰 저장 방식**

- **액세스 토큰**: Authorization 헤더 (`Bearer {access_token}`)
- **리프레시 토큰**: 쿠키 (HttpOnly, Secure)

### **인증 헤더**

```http
Authorization: Bearer {access_token}
```

### **레이트 리미팅**

- **로그인 API**: IP별 10 requests/minute (Redis 기반)
- **사용자별 + IP별**: 정상/의심 사용자 구분
- **정상 사용자**: 60 requests/minute
- **의심 사용자**: 5 requests/minute

---

## ❌ **에러 코드**

| 코드       | HTTP 상태 | 설명                                                        | Enum 이름                            |
| ---------- | --------- | ----------------------------------------------------------- | ------------------------------------ |
| `AUTH-001` | 401       | 로그인이 필요한 요청입니다                                  | `NOT_AUTHENTICATED`                  |
| `AUTH-002` | 401       | 요청 헤더에 액세스 토큰이 없습니다                          | `NOT_FOUND_ACCESS_TOKEN_IN_HEADER`   |
| `AUTH-004` | 400       | 이메일 또는 비밀번호를 확인해주세요                         | `BAD_REQUEST_LOGIN`                  |
| `AUTH-005` | 401       | 토큰이 만료되었습니다                                       | `EXPIRED_TOKEN`                      |
| `AUTH-006` | 401       | 액세스 토큰이 만료되었습니다. 토큰을 재발급해주세요         | `EXPIRED_ACCESS_TOKEN`               |
| `AUTH-007` | 401       | 리프레시 토큰이 만료되었습니다. 다시 로그인해주세요         | `EXPIRED_REFRESH_TOKEN`              |
| `AUTH-010` | 401       | 토큰이 유효하지 않습니다                                    | `INVALID_TOKEN`                      |
| `AUTH-011` | 401       | 유효하지 않은 액세스 토큰입니다                             | `INVALID_ACCESS_TOKEN`               |
| `AUTH-021` | 400       | 지원되지 않는 소셜 로그인 유형입니다. (구글, 카카오만 가능) | `NOT_SUPPORTED_SOCIAL_PROVIDER_TYPE` |
| `AUTH-022` | 429       | 요청 횟수가 초과되었습니다                                  | `RATE_LIMIT_EXCEEDED`                |
| `USER-002` | 404       | 해당 유저가 존재하지 않습니다                               | `NOT_FOUND_USER`                     |

---

## ✅ **성공 응답 코드**

| 코드  | HTTP 상태 | 설명                         | Enum 이름           |
| ----- | --------- | ---------------------------- | ------------------- |
| `200` | 200       | 자체 로그인에 성공하였습니다 | `OK_SELF_LOGIN`     |
| `200` | 200       | 토큰 재발급에 성공하였습니다 | `OK_RE_ISSUE_TOKEN` |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **로그인**

```bash
curl -X POST "https://api.dataracy.store/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }' \
  -c cookies.txt
```

#### **토큰 재발급**

```bash
curl -X POST "https://api.dataracy.store/api/v1/auth/token/re-issue" \
  -b cookies.txt \
  -c cookies.txt
```

### **JavaScript 예제**

#### **로그인**

```javascript
const login = async (email, password) => {
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify({ email, password }),
  });

  return response.json();
};
```

#### **토큰 재발급**

```javascript
const refreshToken = async () => {
  const response = await fetch("/api/v1/auth/token/re-issue", {
    method: "POST",
    credentials: "include",
  });

  return response.json();
};
```

---

## 🔄 **인증 플로우**

### **일반 로그인 플로우**

1. 사용자가 이메일/비밀번호로 로그인 요청
2. 서버에서 인증 처리 (레이트 리미팅 적용)
3. 성공 시 리프레시 토큰을 쿠키에 설정
4. 클라이언트에서 API 요청 시 Authorization 헤더에 액세스 토큰 사용

### **OAuth2 로그인 플로우**

1. 사용자가 소셜 로그인 버튼 클릭
2. OAuth2 제공자(카카오/구글)로 리다이렉트
3. 사용자가 소셜 로그인 완료
4. 콜백 URL로 인증 코드 전달
5. 서버에서 인증 코드로 사용자 정보 조회
6. **신규 사용자**: 레지스터 토큰을 쿠키에 설정 후 온보딩 페이지로 리다이렉트
7. **기존 사용자**: 리프레시 토큰을 쿠키에 설정 후 메인 페이지로 리다이렉트

### **토큰 재발급 플로우**

1. 액세스 토큰 만료 시 401 에러 발생
2. 클라이언트에서 쿠키의 리프레시 토큰으로 재발급 요청
3. 서버에서 리프레시 토큰 검증 (Redis에서 확인)
4. 새로운 리프레시 토큰 발급
5. 쿠키에 새로운 리프레시 토큰 설정

---

**💡 인증 관련 문제가 발생하면 개발팀에 문의하세요!**
