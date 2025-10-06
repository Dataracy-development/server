# 📧 이메일 API

## 📋 **개요**

이메일 관련 API는 이메일 발송, 인증, 검증 기능을 제공합니다.

**Base URL**: `/api/v1`

---

## 🔑 **엔드포인트**

### **이메일 인증**

#### **1. 이메일 인증 요청**

**엔드포인트**: `POST /api/v1/email/send`

**설명**: 이메일 인증을 위한 인증 코드 발송

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "email": "user@example.com",
  "purpose": "SIGN_UP"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "입력하신 이메일로 인증코드가 발송되었습니다. 인증 코드를 확인해주세요",
  "data": null
}
```

**지원하는 목적 (purpose)**:

- `SIGN_UP`: 회원가입 인증
- `PASSWORD_SEARCH`: 비밀번호 찾기 인증
- `PASSWORD_RESET`: 비밀번호 재설정 인증

---

#### **2. 이메일 인증 확인**

**엔드포인트**: `POST /api/v1/email/verify`

**설명**: 발송된 인증 코드 확인

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "email": "user@example.com",
  "code": "123456",
  "purpose": "SIGN_UP"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "본인 인증이 완료되었습니다",
  "data": {
    "resetPasswordToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**참고**: `PASSWORD_SEARCH` 목적으로 인증 시에만 `resetPasswordToken`이 응답에 포함됩니다.

---

## ❌ **에러 코드**

| 코드        | HTTP 상태 | 설명                                                      | Enum 이름                 |
| ----------- | --------- | --------------------------------------------------------- | ------------------------- |
| `EMAIL-001` | 500       | 인증번호 발송에 실패했습니다. 다시 시도해주세요           | `FAIL_SEND_EMAIL_CODE`    |
| `EMAIL-002` | 400       | 인증번호를 확인해주세요                                   | `FAIL_VERIFY_EMAIL_CODE`  |
| `EMAIL-003` | 400       | 인증 시간이 초과되었거나, 작성한 정보가 올바르지 않습니다 | `EXPIRED_EMAIL_CODE`      |
| `EMAIL-004` | 400       | 이메일 전송 목적이 올바르지 않습니다                      | `INVALID_EMAIL_SEND_TYPE` |

---

## ✅ **성공 응답 코드**

| 코드  | HTTP 상태 | 설명                                                         | Enum 이름                            |
| ----- | --------- | ------------------------------------------------------------ | ------------------------------------ |
| `200` | 200       | 입력하신 이메일로 인증코드가 발송되었습니다                  | `OK_SEND_EMAIL_CODE_SIGN_UP`         |
| `200` | 200       | 입력하신 이메일로 비밀번호 찾기 인증 코드가 발송되었습니다   | `OK_SEND_EMAIL_CODE_PASSWORD_SEARCH` |
| `200` | 200       | 입력하신 이메일로 비밀번호 재설정 인증 코드가 발송되었습니다 | `OK_SEND_EMAIL_CODE_PASSWORD_RESET`  |
| `200` | 200       | 본인 인증이 완료되었습니다                                   | `OK_VERIFY_EMAIL_CODE`               |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **이메일 인증 요청**

```bash
curl -X POST "https://api.dataracy.store/api/v1/email/send" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "purpose": "SIGN_UP"
  }'
```

#### **이메일 인증 확인**

```bash
curl -X POST "https://api.dataracy.store/api/v1/email/verify" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "code": "123456",
    "purpose": "SIGN_UP"
  }'
```

### **JavaScript 예제**

#### **이메일 인증 플로우**

```javascript
const verifyEmail = async (email, purpose = "SIGN_UP") => {
  // 1. 인증 코드 발송
  await fetch("/api/v1/email/send", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email, purpose }),
  });

  // 2. 사용자 입력 대기 (UI에서 처리)
  const code = await getUserInput("인증 코드를 입력하세요:");

  // 3. 인증 코드 확인
  const response = await fetch("/api/v1/email/verify", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email,
      code,
      purpose,
    }),
  });

  return response.json();
};
```

---

## 📊 **이메일 정책**

### **인증 코드 정책**

- **유효 시간**: 5분 (환경변수: `aws.ses.expire-minutes`)
- **코드 형식**: 6자리 숫자 (SecureRandom 기반)
- **저장 위치**: Redis (목적별 키 분리)
- **검증 후 삭제**: 인증 성공 시 Redis에서 자동 삭제

### **지원하는 인증 목적**

- **SIGN_UP**: 회원가입 인증
- **PASSWORD_SEARCH**: 비밀번호 찾기 인증 (리셋 토큰 발급)
- **PASSWORD_RESET**: 비밀번호 재설정 인증

### **Redis 키 구조**

- **회원가입**: `email:verification:signup:{email}`
- **비밀번호 찾기**: `email:verification:password:search:{email}`
- **비밀번호 재설정**: `email:verification:password:reset:{email}`

### **이메일 제공업체**

- **AWS SES**: 이메일 발송 서비스
- **환경변수**: `aws.ses.expire-minutes` (기본값: 5분)

---

**💡 이메일 관련 문제가 발생하면 개발팀에 문의하세요!**
