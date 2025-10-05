# 👤 사용자 API

## 📋 **개요**

사용자 관련 API는 회원가입, 프로필 관리, 비밀번호 관리, 사용자 검증을 제공합니다.

**Base URL**: `/api/v1/user`, `/api/v1/users`, `/api/v1`

---

## 🔑 **엔드포인트**

### **회원가입 (SignUp)**

#### **1. 자체 회원가입**

**엔드포인트**: `POST /api/v1/signup/self`

**설명**: 이메일과 비밀번호를 이용한 자체 회원가입

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "사용자명",
  "authorLevelId": 1,
  "occupationId": 2,
  "topicIds": [1, 2, 3],
  "visitSourceId": 1
}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "회원가입이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 소셜 회원가입 (온보딩)**

**엔드포인트**: `POST /api/v1/signup/oauth`

**설명**: 소셜 로그인 후 온보딩 과정에서 추가 정보 입력

**요청 헤더**:

```http
Content-Type: application/json
Cookie: registerToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**요청 본문**:

```json
{
  "nickname": "사용자명",
  "authorLevelId": 1,
  "occupationId": 2,
  "topicIds": [1, 2, 3],
  "visitSourceId": 1
}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "소셜 회원가입이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **사용자 관리 (Command)**

#### **1. 회원 정보 수정**

**엔드포인트**: `PUT /api/v1/user`

**설명**: 현재 로그인한 사용자의 정보 수정

**요청 헤더**:

```http
Content-Type: multipart/form-data
Authorization: Bearer {access_token}
```

**요청 본문** (multipart/form-data):

```
profileImageFile: [파일] (선택사항)
webRequest: {
  "nickname": "새로운닉네임",
  "authorLevelId": 2,
  "occupationId": 3,
  "topicIds": [1, 2, 4],
  "visitSourceId": 2,
  "introductionText": "자기소개"
}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "회원 정보 수정이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 회원 탈퇴**

**엔드포인트**: `DELETE /api/v1/user`

**설명**: 현재 로그인한 사용자의 소프트 탈퇴

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "회원 탈퇴가 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **3. 로그아웃**

**엔드포인트**: `POST /api/v1/user/logout`

**설명**: 현재 로그인한 사용자의 로그아웃

**요청 헤더**:

```http
Authorization: Bearer {access_token}
Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "로그아웃이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **사용자 조회 (Read)**

#### **1. 내 프로필 조회**

**엔드포인트**: `GET /api/v1/user`

**설명**: 현재 로그인한 사용자의 프로필 정보 조회

**요청 헤더**:

```http
Authorization: Bearer {access_token}
```

**응답**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "사용자명",
    "profileImageUrl": "https://example.com/profile.jpg",
    "authorLevel": {
      "id": 1,
      "name": "초급자"
    },
    "occupation": {
      "id": 2,
      "name": "데이터 분석가"
    },
    "topics": [
      { "id": 1, "name": "머신러닝" },
      { "id": 2, "name": "데이터 시각화" }
    ],
    "visitSource": {
      "id": 1,
      "name": "검색엔진"
    },
    "introductionText": "안녕하세요!",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "message": "프로필 조회가 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 다른 사용자 프로필 조회**

**엔드포인트**: `GET /api/v1/users/{userId}`

**설명**: 특정 사용자의 공개 프로필 정보 조회

**경로 변수**:

- `userId`: 조회할 사용자 ID

**응답**:

```json
{
  "success": true,
  "data": {
    "id": 2,
    "nickname": "다른사용자",
    "profileImageUrl": "https://example.com/profile2.jpg",
    "authorLevel": {
      "id": 2,
      "name": "중급자"
    },
    "occupation": {
      "id": 3,
      "name": "데이터 사이언티스트"
    },
    "topics": [
      { "id": 3, "name": "딥러닝" },
      { "id": 4, "name": "NLP" }
    ],
    "introductionText": "데이터 사이언스 전문가입니다.",
    "createdAt": "2024-01-10T10:30:00Z"
  },
  "message": "사용자 프로필 조회가 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **사용자 검증 (Validate)**

#### **1. 닉네임 중복 확인**

**엔드포인트**: `POST /api/v1/nickname/check`

**설명**: 닉네임 중복 여부 확인

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "nickname": "사용자명"
}
```

**응답**:

```json
{
  "success": true,
  "data": {
    "available": true
  },
  "message": "사용 가능한 닉네임입니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **비밀번호 관리 (Password)**

#### **1. 비밀번호 재설정**

**엔드포인트**: `PUT /api/v1/password/reset`

**설명**: 토큰을 이용한 비밀번호 재설정

**요청 헤더**:

```http
Content-Type: application/json
```

**요청 본문**:

```json
{
  "token": "reset_token_here",
  "newPassword": "new_password123"
}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "비밀번호 재설정에 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

#### **2. 비밀번호 변경**

**엔드포인트**: `PUT /api/v1/user/password/change`

**설명**: 현재 로그인한 사용자의 비밀번호 변경

**요청 헤더**:

```http
Content-Type: application/json
Authorization: Bearer {access_token}
```

**요청 본문**:

```json
{
  "newPassword": "새비밀번호"
}
```

**응답**:

```json
{
  "success": true,
  "data": null,
  "message": "비밀번호 변경이 성공했습니다.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## ❌ **에러 코드**

| 코드                  | HTTP 상태 | 설명                          |
| --------------------- | --------- | ----------------------------- |
| `USER_NOT_FOUND`      | 404       | 사용자를 찾을 수 없음         |
| `DUPLICATED_EMAIL`    | 409       | 중복된 이메일                 |
| `DUPLICATED_NICKNAME` | 409       | 중복된 닉네임                 |
| `INVALID_PASSWORD`    | 400       | 잘못된 비밀번호               |
| `PASSWORD_MISMATCH`   | 400       | 현재 비밀번호가 일치하지 않음 |
| `INVALID_TOKEN`       | 401       | 유효하지 않은 토큰            |
| `FILE_UPLOAD_FAILURE` | 500       | 파일 업로드 실패              |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **회원가입**

```bash
curl -X POST "https://api.dataracy.co.kr/api/v1/signup/self" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "nickname": "사용자명",
    "authorLevelId": 1,
    "occupationId": 2,
    "topicIds": [1, 2, 3],
    "visitSourceId": 1
  }'
```

#### **프로필 수정**

```bash
curl -X PUT "https://api.dataracy.co.kr/api/v1/user" \
  -H "Authorization: Bearer {access_token}" \
  -F "webRequest={\"nickname\":\"새로운닉네임\",\"authorLevelId\":2};type=application/json" \
  -F "profileImageFile=@profile.jpg"
```

### **JavaScript 예제**

#### **닉네임 중복 확인**

```javascript
const checkNickname = async (nickname) => {
  const response = await fetch("/api/v1/nickname/check", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ nickname }),
  });
  const result = await response.json();
  return result.success;
};
```

#### **프로필 수정**

```javascript
const updateProfile = async (profileData, profileImage) => {
  const formData = new FormData();
  formData.append("webRequest", JSON.stringify(profileData));
  if (profileImage) {
    formData.append("profileImageFile", profileImage);
  }

  const response = await fetch("/api/v1/user", {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: formData,
  });

  return response.json();
};
```

---

**💡 사용자 관련 문제가 발생하면 개발팀에 문의하세요!**
