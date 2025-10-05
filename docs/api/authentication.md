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
  "success": true,
  "data": null,
  "message": "로그인이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**쿠키 설정**:

- `refreshToken`: 리프레시 토큰
- `accessToken`: 액세스 토큰
- `accessTokenExpiration`: 액세스 토큰 만료 시간

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
  "success": true,
  "data": null,
  "message": "토큰 재발급이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**새로운 쿠키 설정**:

- `refreshToken`: 새로운 리프레시 토큰
- `accessToken`: 새로운 액세스 토큰
- `accessTokenExpiration`: 새로운 액세스 토큰 만료 시간

---

### **3. OAuth2 소셜 로그인**

#### **카카오 로그인**

**엔드포인트**: `GET /oauth2/authorization/kakao`

**설명**: 카카오 OAuth2 로그인 시작

**응답**: 카카오 로그인 페이지로 리다이렉트

#### **구글 로그인**

**엔드포인트**: `GET /oauth2/authorization/google`

**설명**: 구글 OAuth2 로그인 시작

**응답**: 구글 로그인 페이지로 리다이렉트

---

## 🔒 **보안 정책**

### **토큰 만료 시간**

- **액세스 토큰**: 1시간
- **리프레시 토큰**: 14일

### **토큰 저장 방식**

- **액세스 토큰**: 쿠키 (HttpOnly, Secure)
- **리프레시 토큰**: 쿠키 (HttpOnly, Secure)

### **인증 헤더**

```http
Authorization: Bearer {access_token}
```

---

## ❌ **에러 코드**

| 코드                  | HTTP 상태 | 설명                        |
| --------------------- | --------- | --------------------------- |
| `INVALID_CREDENTIALS` | 401       | 잘못된 이메일 또는 비밀번호 |
| `USER_NOT_FOUND`      | 404       | 사용자를 찾을 수 없음       |
| `INVALID_TOKEN`       | 401       | 유효하지 않은 토큰          |
| `TOKEN_EXPIRED`       | 401       | 토큰이 만료됨               |
| `OAUTH2_ERROR`        | 400       | OAuth2 로그인 오류          |

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
2. 서버에서 인증 처리
3. 성공 시 액세스 토큰과 리프레시 토큰을 쿠키에 설정
4. 클라이언트에서 API 요청 시 쿠키의 액세스 토큰 사용

### **OAuth2 로그인 플로우**

1. 사용자가 소셜 로그인 버튼 클릭
2. OAuth2 제공자(카카오/구글)로 리다이렉트
3. 사용자가 소셜 로그인 완료
4. 콜백 URL로 인증 코드 전달
5. 서버에서 인증 코드로 사용자 정보 조회
6. 기존 사용자면 로그인, 신규 사용자면 회원가입 후 로그인
7. 액세스 토큰과 리프레시 토큰을 쿠키에 설정

### **토큰 재발급 플로우**

1. 액세스 토큰 만료 시 401 에러 발생
2. 클라이언트에서 리프레시 토큰으로 재발급 요청
3. 서버에서 리프레시 토큰 검증
4. 새로운 액세스 토큰과 리프레시 토큰 발급
5. 쿠키에 새로운 토큰 설정

---

**💡 인증 관련 문제가 발생하면 개발팀에 문의하세요!**
