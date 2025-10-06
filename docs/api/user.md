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
  "password": "password123@",
  "passwordConfirm": "password123@",
  "nickname": "사용자명",
  "authorLevelId": 1,
  "occupationId": 2,
  "topicIds": [1, 2, 3],
  "visitSourceId": 1,
  "isAdTermsAgreed": true
}
```

**응답**:

```json
{
  "httpStatus": 201,
  "code": "201",
  "message": "회원가입에 성공했습니다",
  "data": null
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
  "httpStatus": 201,
  "code": "201",
  "message": "회원가입에 성공했습니다",
  "data": null
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

**필드 설명**:

- `nickname`: 닉네임 (2~8자, 필수)
- `authorLevelId`: 작성자 유형 ID (1 이상, 필수)
- `occupationId`: 직업 ID (1 이상, 선택사항)
- `topicIds`: 흥미있는 토픽 ID 리스트 (필수)
- `visitSourceId`: 방문 경로 ID (1 이상, 선택사항)
- `introductionText`: 자기소개 글 (선택사항)

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "회원 정보 수정이 완료되었습니다.",
  "data": null
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
  "httpStatus": 200,
  "code": "200",
  "message": "회원 탈퇴가 완료되었습니다.",
  "data": null
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
  "httpStatus": 200,
  "code": "200",
  "message": "회원 로그아웃이 완료되었습니다.",
  "data": null
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
  "httpStatus": 200,
  "code": "200",
  "message": "유저 정보 조회가 완료되었습니다.",
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
  }
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
  "httpStatus": 200,
  "code": "200",
  "message": "타인유저 정보 조회가 완료되었습니다.",
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
  }
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
  "httpStatus": 200,
  "code": "200",
  "message": "사용할 수 있는 닉네임입니다.",
  "data": null
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
  "resetPasswordToken": "reset_token_here",
  "password": "new_password123@",
  "passwordConfirm": "new_password123@"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "비밀번호를 재설정했습니다.",
  "data": null
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
  "password": "새비밀번호@",
  "passwordConfirm": "새비밀번호@"
}
```

**응답**:

```json
{
  "httpStatus": 200,
  "code": "200",
  "message": "비밀번호를 변경했습니다.",
  "data": null
}
```

---

## ❌ **에러 코드**

| 코드       | HTTP 상태 | 설명                                          | Enum 이름                      |
| ---------- | --------- | --------------------------------------------- | ------------------------------ |
| `USER-001` | 409       | 이미 가입된 계정입니다                        | `ALREADY_SIGN_UP_USER`         |
| `USER-002` | 404       | 해당 유저가 존재하지 않습니다                 | `NOT_FOUND_USER`               |
| `USER-003` | 400       | 비밀번호와 비밀번호 확인은 동일해야합니다     | `NOT_SAME_PASSWORD`            |
| `USER-004` | 409       | 이미 사용 중인 닉네임입니다                   | `DUPLICATED_NICKNAME`          |
| `USER-005` | 409       | 중복된 이메일은 사용할 수 없습니다            | `DUPLICATED_EMAIL`             |
| `USER-006` | 400       | 이전과 동일한 비밀번호입니다                  | `DUPLICATED_PASSWORD`          |
| `AUTH-011` | 401       | 유효하지 않은 액세스 토큰입니다               | `INVALID_ACCESS_TOKEN`         |
| `FILE-001` | 400       | 이미지 파일은 최대 10MB까지 업로드 가능합니다 | `OVER_MAXIMUM_IMAGE_FILE_SIZE` |

---

## ✅ **성공 응답 코드**

| 코드  | HTTP 상태 | 설명                                | Enum 이름                    |
| ----- | --------- | ----------------------------------- | ---------------------------- |
| `201` | 201       | 회원가입에 성공했습니다             | `CREATED_USER`               |
| `200` | 200       | 유저 정보 조회가 완료되었습니다     | `OK_GET_USER_INFO`           |
| `200` | 200       | 타인유저 정보 조회가 완료되었습니다 | `OK_GET_OTHER_USER_INFO`     |
| `200` | 200       | 사용할 수 있는 닉네임입니다         | `OK_NOT_DUPLICATED_NICKNAME` |
| `200` | 200       | 비밀번호를 변경했습니다             | `OK_CHANGE_PASSWORD`         |
| `200` | 200       | 비밀번호를 재설정했습니다           | `OK_RESET_PASSWORD`          |
| `200` | 200       | 회원 정보 수정이 완료되었습니다     | `OK_MODIFY_USER_INFO`        |
| `200` | 200       | 회원 탈퇴가 완료되었습니다          | `OK_WITHDRAW_USER`           |
| `200` | 200       | 회원 로그아웃이 완료되었습니다      | `OK_LOGOUT`                  |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **회원가입**

```bash
curl -X POST "https://api.dataracy.store/api/v1/signup/self" \
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
curl -X PUT "https://api.dataracy.store/api/v1/user" \
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
