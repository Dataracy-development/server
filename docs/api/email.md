# 📧 이메일 API

## 📋 **개요**

이메일 관련 API는 이메일 발송, 인증, 검증 기능을 제공합니다.

**Base URL**: `/api/v1`

---

## 🔑 **엔드포인트**

### **이메일 인증**

#### **1. 이메일 인증 요청**

**엔드포인트**: `POST /api/v1/email/verify`

**설명**: 이메일 인증을 위한 인증 코드 발송

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "email": "user@example.com",
  "type": "SIGNUP"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "인증 이메일이 발송되었습니다.",
  "data": null
}
```

---

#### **2. 이메일 인증 확인**

**엔드포인트**: `POST /api/v1/email/verify/confirm`

**설명**: 발송된 인증 코드 확인

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "email": "user@example.com",
  "verificationCode": "123456",
  "type": "SIGNUP"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "이메일 인증이 완료되었습니다.",
  "data": {
    "verified": true,
    "expiresAt": "2024-01-15T11:30:00Z"
  }
}
```

---

### **이메일 발송**

#### **1. 일반 이메일 발송**

**엔드포인트**: `POST /api/v1/email/send`

**설명**: 템플릿 기반 이메일 발송

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "to": "recipient@example.com",
  "subject": "이메일 제목",
  "template": "WELCOME",
  "variables": {
    "name": "사용자명",
    "verificationUrl": "https://example.com/verify"
  }
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "이메일이 성공적으로 발송되었습니다.",
  "data": {
    "messageId": "msg_123456789",
    "sentAt": "2024-01-15T10:30:00Z"
  }
}
```

---

#### **2. 대량 이메일 발송**

**엔드포인트**: `POST /api/v1/email/send/bulk`

**설명**: 여러 수신자에게 대량 이메일 발송

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "recipients": [
    {
      "email": "user1@example.com",
      "variables": {
        "name": "사용자1"
      }
    },
    {
      "email": "user2@example.com",
      "variables": {
        "name": "사용자2"
      }
    }
  ],
  "subject": "대량 이메일 제목",
  "template": "NEWSLETTER",
  "commonVariables": {
    "companyName": "Dataracy"
  }
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "대량 이메일 발송이 시작되었습니다.",
  "data": {
    "batchId": "batch_123456789",
    "totalRecipients": 2,
    "estimatedCompletion": "2024-01-15T10:35:00Z"
  }
}
```

---

## ❌ **에러 코드**

| 코드                        | HTTP 상태 | 설명                        |
| --------------------------- | --------- | --------------------------- |
| `INVALID_EMAIL_FORMAT`      | 400       | 잘못된 이메일 형식          |
| `EMAIL_ALREADY_VERIFIED`    | 409       | 이미 인증된 이메일          |
| `INVALID_VERIFICATION_CODE` | 400       | 잘못된 인증 코드            |
| `VERIFICATION_CODE_EXPIRED` | 410       | 인증 코드 만료              |
| `EMAIL_SEND_FAILED`         | 500       | 이메일 발송 실패            |
| `RATE_LIMIT_EXCEEDED`       | 429       | 이메일 발송 한도 초과       |
| `INVALID_TEMPLATE`          | 400       | 존재하지 않는 이메일 템플릿 |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **이메일 인증 요청**

```bash
curl -X POST "https://api.dataracy.store/api/v1/email/verify" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "type": "SIGNUP"
  }'
```

#### **이메일 인증 확인**

```bash
curl -X POST "https://api.dataracy.store/api/v1/email/verify/confirm" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "verificationCode": "123456",
    "type": "SIGNUP"
  }'
```

### **JavaScript 예제**

#### **이메일 인증 플로우**

```javascript
const verifyEmail = async (email, type = "SIGNUP") => {
  // 1. 인증 코드 발송
  await fetch("/api/v1/email/verify", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email, type }),
  });

  // 2. 사용자 입력 대기 (UI에서 처리)
  const verificationCode = await getUserInput("인증 코드를 입력하세요:");

  // 3. 인증 코드 확인
  const response = await fetch("/api/v1/email/verify/confirm", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email,
      verificationCode,
      type,
    }),
  });

  return response.json();
};
```

#### **이메일 발송**

```javascript
const sendEmail = async (to, subject, template, variables) => {
  const response = await fetch("/api/v1/email/send", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({
      to,
      subject,
      template,
      variables,
    }),
  });

  return response.json();
};
```

---

## 📊 **이메일 정책**

### **인증 코드 정책**

- **유효 시간**: 10분
- **재발송 제한**: 1분 간격
- **최대 시도**: 5회
- **코드 형식**: 6자리 숫자

### **발송 제한**

- **일일 발송 한도**: 사용자당 100통
- **시간당 발송 한도**: 사용자당 20통
- **대량 발송**: 관리자 권한 필요
- **스팸 방지**: 자동 스팸 필터링

### **지원하는 이메일 템플릿**

- **WELCOME**: 회원가입 환영 이메일
- **PASSWORD_RESET**: 비밀번호 재설정
- **EMAIL_VERIFICATION**: 이메일 인증
- **NEWSLETTER**: 뉴스레터
- **NOTIFICATION**: 알림 이메일
- **PROJECT_SHARE**: 프로젝트 공유

### **이메일 제공업체**

- **SendGrid**: 주요 이메일 발송
- **AWS SES**: 백업 및 대량 발송
- **자동 전환**: 장애 시 자동 전환

---

**💡 이메일 관련 문제가 발생하면 개발팀에 문의하세요!**
