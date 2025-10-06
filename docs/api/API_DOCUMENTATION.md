# 📡 API 문서

## 📋 **목차**

- [API 개요](#api-개요)
- [인증 및 보안](#인증-및-보안)
- [공통 응답 형식](#공통-응답-형식)
- [에러 코드](#에러-코드)
- [API 엔드포인트](#api-엔드포인트)
- [예제 요청/응답](#예제-요청응답)

---

## 🌐 **API 개요**

### **Base URL**

- **개발**: `https://dev-api.dataracy.store`
- **운영**: `https://api.dataracy.store`
- **로컬**: `http://localhost:8080`

### **API 버전**

- **현재 버전**: `v1`
- **URL 패턴**: `/api/v1/{resource}`

### **지원 형식**

- **Content-Type**: `application/json`, `multipart/form-data`
- **Accept**: `application/json`
- **문자 인코딩**: `UTF-8`

---

## 🔐 **인증 및 보안**

### **JWT 토큰 인증**

```http
Authorization: Bearer {access_token}
```

### **토큰 갱신**

```http
POST /api/v1/auth/token/re-issue
Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**응답**: 쿠키에 새로운 토큰들이 설정됨

### **OAuth2 소셜 로그인**

- **카카오**: `/oauth2/authorization/kakao`
- **구글**: `/oauth2/authorization/google`

---

## 📊 **공통 응답 형식**

### **성공 응답**

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    // 응답 데이터
  }
}
```

### **에러 응답**

```json
{
  "httpStatus": 404,
  "code": "USER-002",
  "message": "해당 유저가 존재하지 않습니다."
}
```

### **페이징 응답**

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "조회에 성공했습니다.",
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

| 코드         | HTTP 상태 | 설명                          | Enum 이름               |
| ------------ | --------- | ----------------------------- | ----------------------- |
| `COMMON-400` | 400       | 잘못된 요청                   | `BAD_REQUEST`           |
| `COMMON-401` | 401       | 인증이 필요합니다             | `UNAUTHORIZED`          |
| `COMMON-403` | 403       | 접근이 금지된 요청입니다      | `FORBIDDEN`             |
| `COMMON-404` | 404       | 요청 경로를 찾을 수 없습니다  | `NOT_FOUND_HANDLER`     |
| `COMMON-500` | 500       | 서버 내부 오류가 발생했습니다 | `INTERNAL_SERVER_ERROR` |

### **인증 관련 에러**

| 코드       | HTTP 상태 | 설명                                | Enum 이름                          |
| ---------- | --------- | ----------------------------------- | ---------------------------------- |
| `AUTH-001` | 401       | 로그인이 필요한 요청입니다          | `NOT_AUTHENTICATED`                |
| `AUTH-002` | 401       | 요청 헤더에 액세스 토큰이 없습니다  | `NOT_FOUND_ACCESS_TOKEN_IN_HEADER` |
| `AUTH-004` | 400       | 이메일 또는 비밀번호를 확인해주세요 | `BAD_REQUEST_LOGIN`                |
| `AUTH-005` | 401       | 토큰이 만료되었습니다               | `EXPIRED_TOKEN`                    |
| `AUTH-006` | 401       | 액세스 토큰이 만료되었습니다        | `EXPIRED_ACCESS_TOKEN`             |
| `AUTH-007` | 401       | 리프레시 토큰이 만료되었습니다      | `EXPIRED_REFRESH_TOKEN`            |
| `AUTH-010` | 401       | 토큰이 유효하지 않습니다            | `INVALID_TOKEN`                    |
| `AUTH-011` | 401       | 유효하지 않은 액세스 토큰입니다     | `INVALID_ACCESS_TOKEN`             |
| `AUTH-022` | 429       | 요청 횟수가 초과되었습니다          | `RATE_LIMIT_EXCEEDED`              |

### **사용자 관련 에러**

| 코드       | HTTP 상태 | 설명                                                  | Enum 이름                                 |
| ---------- | --------- | ----------------------------------------------------- | ----------------------------------------- |
| `USER-001` | 409       | 이미 가입된 계정입니다                                | `ALREADY_SIGN_UP_USER`                    |
| `USER-002` | 404       | 해당 유저가 존재하지 않습니다                         | `NOT_FOUND_USER`                          |
| `USER-003` | 400       | 비밀번호와 비밀번호 확인은 동일해야합니다             | `NOT_SAME_PASSWORD`                       |
| `USER-004` | 409       | 이미 사용 중인 닉네임입니다                           | `DUPLICATED_NICKNAME`                     |
| `USER-005` | 409       | 중복된 이메일은 사용할 수 없습니다                    | `DUPLICATED_EMAIL`                        |
| `USER-006` | 400       | 이전과 동일한 비밀번호입니다                          | `DUPLICATED_PASSWORD`                     |
| `USER-020` | 400       | 이메일 또는 비밀번호를 확인해주세요                   | `BAD_REQUEST_LOGIN`                       |
| `USER-021` | 403       | 비밀번호 재설정 토큰이 유효하지 않거나 만료되었습니다 | `INVALID_OR_EXPIRED_RESET_PASSWORD_TOKEN` |

### **프로젝트 관련 에러**

| 코드          | HTTP 상태 | 설명                                          | Enum 이름                    |
| ------------- | --------- | --------------------------------------------- | ---------------------------- |
| `PROJECT-001` | 500       | 프로젝트 업로드에 실패했습니다                | `FAIL_SAVE_PROJECT`          |
| `PROJECT-002` | 404       | 해당 프로젝트 리소스가 존재하지 않습니다      | `NOT_FOUND_PROJECT`          |
| `PROJECT-003` | 404       | 해당 부모 프로젝트 리소스가 존재하지 않습니다 | `NOT_FOUND_PARENT_PROJECT`   |
| `PROJECT-008` | 403       | 작성자만 수정 및 삭제, 복원이 가능합니다      | `NOT_MATCH_CREATOR`          |
| `PROJECT-010` | 400       | 유효하지 않은 썸네일 파일 URL입니다           | `INVALID_THUMBNAIL_FILE_URL` |

### **데이터셋 관련 에러**

| 코드       | HTTP 상태 | 설명                                                | Enum 이름           |
| ---------- | --------- | --------------------------------------------------- | ------------------- |
| `DATA-001` | 500       | 데이터셋 업로드에 실패했습니다                      | `FAIL_UPLOAD_DATA`  |
| `DATA-002` | 404       | 해당 데이터셋 리소스가 존재하지 않습니다            | `NOT_FOUND_DATA`    |
| `DATA-003` | 400       | 데이터셋 수집 시작일은 종료일보다 이전이어야 합니다 | `BAD_REQUEST_DATE`  |
| `DATA-008` | 403       | 작성자만 수정 및 삭제, 복원이 가능합니다            | `NOT_MATCH_CREATOR` |
| `DATA-010` | 400       | 유효하지 않은 파일 URL입니다                        | `INVALID_FILE_URL`  |

### **댓글 관련 에러**

| 코드          | HTTP 상태 | 설명                                        | Enum 이름                  |
| ------------- | --------- | ------------------------------------------- | -------------------------- |
| `COMMENT-001` | 500       | 피드백 댓글 업로드에 실패했습니다           | `FAIL_SAVE_COMMENT`        |
| `COMMENT-002` | 404       | 해당 피드백 댓글 리소스가 존재하지 않습니다 | `NOT_FOUND_COMMENT`        |
| `COMMENT-003` | 404       | 해당 부모 댓글 리소스가 존재하지 않습니다   | `NOT_FOUND_PARENT_COMMENT` |
| `COMMENT-004` | 403       | 작성자만 수정 및 삭제, 복원이 가능합니다    | `NOT_MATCH_CREATOR`        |
| `COMMENT-005` | 403       | 답글에 다시 답글을 작성할 순 없습니다       | `FORBIDDEN_REPLY_COMMENT`  |
| `COMMENT-006` | 403       | 해당 프로젝트에 작성된 댓글이 아닙니다      | `MISMATCH_PROJECT_COMMENT` |

### **좋아요 관련 에러**

| 코드       | HTTP 상태 | 설명                                            | Enum 이름               |
| ---------- | --------- | ----------------------------------------------- | ----------------------- |
| `LIKE-001` | 500       | 해당 좋아요 리소스를 찾을 수 없습니다           | `NOT_FOUND_TARGET_LIKE` |
| `LIKE-002` | 500       | 프로젝트에 대한 좋아요 처리에 실패했습니다      | `FAIL_LIKE_PROJECT`     |
| `LIKE-003` | 500       | 프로젝트에 대한 좋아요 취소 처리에 실패했습니다 | `FAIL_UNLIKE_PROJECT`   |
| `LIKE-004` | 500       | 댓글에 대한 좋아요 처리에 실패했습니다          | `FAIL_LIKE_COMMENT`     |
| `LIKE-005` | 500       | 댓글에 대한 좋아요 취소 처리에 실패했습니다     | `FAIL_UNLIKE_COMMENT`   |
| `LIKE-006` | 403       | 작성자만 좋아요 및 취소가 가능합니다            | `NOT_MATCH_CREATOR`     |
| `LIKE-007` | 400       | 잘못된 좋아요 타겟 유형입니다                   | `INVALID_TARGET_TYPE`   |

### **파일 관련 에러**

| 코드       | HTTP 상태 | 설명                                           | Enum 이름                      |
| ---------- | --------- | ---------------------------------------------- | ------------------------------ |
| `FILE-001` | 400       | 이미지 파일은 최대 10MB까지 업로드 가능합니다  | `OVER_MAXIMUM_IMAGE_FILE_SIZE` |
| `FILE-002` | 400       | 이미지 파일은 jpg, jpeg, png 형식만 허용됩니다 | `BAD_REQUEST_IMAGE_FILE_TYPE`  |
| `FILE-003` | 400       | 파일은 최대 200MB까지 업로드 가능합니다        | `OVER_MAXIMUM_FILE_SIZE`       |
| `FILE-004` | 400       | 파일은 csv, xlsx, json 형식만 허용됩니다       | `BAD_REQUEST_FILE_TYPE`        |
| `FILE-005` | 500       | 파일 업로드에 실패했습니다                     | `FILE_UPLOAD_FAILURE`          |
| `FILE-006` | 500       | 썸네일 생성에 실패했습니다                     | `THUMBNAIL_GENERATION_FAILURE` |

### **이메일 관련 에러**

| 코드        | HTTP 상태 | 설명                                                      | Enum 이름                 |
| ----------- | --------- | --------------------------------------------------------- | ------------------------- |
| `EMAIL-001` | 500       | 인증번호 발송에 실패했습니다                              | `FAIL_SEND_EMAIL_CODE`    |
| `EMAIL-002` | 400       | 인증번호를 확인해주세요                                   | `FAIL_VERIFY_EMAIL_CODE`  |
| `EMAIL-003` | 400       | 인증 시간이 초과되었거나, 작성한 정보가 올바르지 않습니다 | `EXPIRED_EMAIL_CODE`      |
| `EMAIL-004` | 400       | 이메일 전송 목적이 올바르지 않습니다                      | `INVALID_EMAIL_SEND_TYPE` |

### **참조 데이터 관련 에러**

| 코드            | HTTP 상태 | 설명                                 | Enum 이름                    |
| --------------- | --------- | ------------------------------------ | ---------------------------- |
| `REFERENCE-001` | 404       | 해당하는 토픽명이 없습니다           | `NOT_FOUND_TOPIC_NAME`       |
| `REFERENCE-002` | 404       | 해당 작성자 유형이 존재하지 않습니다 | `NOT_FOUND_AUTHOR_LEVEL`     |
| `REFERENCE-003` | 404       | 해당 직업이 존재하지 않습니다        | `NOT_FOUND_OCCUPATION`       |
| `REFERENCE-004` | 404       | 해당 방문 경로가 존재하지 않습니다   | `NOT_FOUND_VISIT_SOURCE`     |
| `REFERENCE-005` | 404       | 해당 분석 목적이 존재하지 않습니다   | `NOT_FOUND_ANALYSIS_PURPOSE` |
| `REFERENCE-006` | 404       | 해당 데이터 출처가 존재하지 않습니다 | `NOT_FOUND_DATA_SOURCE`      |
| `REFERENCE-007` | 404       | 해당 데이터 유형이 존재하지 않습니다 | `NOT_FOUND_DATA_TYPE`        |

---

## ✅ **성공 응답 코드**

### **공통 성공**

| 코드         | HTTP 상태 | 설명                | Enum 이름    |
| ------------ | --------- | ------------------- | ------------ |
| `COMMON-200` | 200       | 성공입니다          | `OK`         |
| `COMMON-201` | 201       | 생성에 성공했습니다 | `CREATED`    |
| `COMMON-204` | 204       | 성공입니다          | `NO_CONTENT` |

### **인증 관련 성공**

| 코드  | HTTP 상태 | 설명                         | Enum 이름           |
| ----- | --------- | ---------------------------- | ------------------- |
| `200` | 200       | 자체 로그인에 성공하였습니다 | `OK_SELF_LOGIN`     |
| `200` | 200       | 토큰 재발급에 성공하였습니다 | `OK_RE_ISSUE_TOKEN` |

### **사용자 관련 성공**

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

### **프로젝트 관련 성공**

| 코드  | HTTP 상태 | 설명                                                          | Enum 이름                                |
| ----- | --------- | ------------------------------------------------------------- | ---------------------------------------- |
| `201` | 201       | 제출이 완료되었습니다                                         | `CREATED_PROJECT`                        |
| `200` | 200       | 실시간 프로젝트 리스트를 조회하였습니다                       | `FIND_REAL_TIME_PROJECTS`                |
| `200` | 200       | 유사 프로젝트 리스트를 조회하였습니다                         | `FIND_SIMILAR_PROJECTS`                  |
| `200` | 200       | 인기있는 프로젝트 리스트를 조회하였습니다                     | `FIND_POPULAR_PROJECTS`                  |
| `200` | 200       | 필터링된 프로젝트 리스트를 조회하였습니다                     | `FIND_FILTERED_PROJECTS`                 |
| `200` | 200       | 프로젝트 상세 정보를 조회하였습니다                           | `GET_PROJECT_DETAIL`                     |
| `200` | 200       | 해당하는 프로젝트의 이어가기 프로젝트 리스트를 조회하였습니다 | `GET_CONTINUE_PROJECTS`                  |
| `200` | 200       | 해당하는 데이터셋을 이용한 프로젝트 리스트를 조회하였습니다   | `GET_CONNECTED_PROJECTS_ASSOCIATED_DATA` |
| `200` | 200       | 프로젝트 수정이 완료되었습니다                                | `MODIFY_PROJECT`                         |
| `200` | 200       | 프로젝트 삭제가 완료되었습니다                                | `DELETE_PROJECT`                         |
| `200` | 200       | 프로젝트 복원에 완료되었습니다                                | `RESTORE_PROJECT`                        |
| `200` | 200       | 로그인한 회원이 업로드한 프로젝트 목록 조회가 완료되었습니다  | `GET_USER_PROJECTS`                      |
| `200` | 200       | 로그인한 회원이 좋아요한 프로젝트 목록 조회가 완료되었습니다  | `GET_LIKE_PROJECTS`                      |

### **데이터셋 관련 성공**

| 코드  | HTTP 상태 | 설명                                                | Enum 이름                 |
| ----- | --------- | --------------------------------------------------- | ------------------------- |
| `201` | 201       | 제출이 완료되었습니다                               | `CREATED_DATASET`         |
| `200` | 200       | 유사 데이터셋 조회가 완료되었습니다                 | `FIND_SIMILAR_DATASETS`   |
| `200` | 200       | 인기 데이터셋 조회가 완료되었습니다                 | `FIND_POPULAR_DATASETS`   |
| `200` | 200       | 데이터 상세 정보 조회가 완료되었습니다              | `GET_DATA_DETAIL`         |
| `200` | 200       | 데이터셋 필터링이 완료되었습니다                    | `FIND_FILTERED_DATASETS`  |
| `200` | 200       | 최신 데이터셋 목록 조회가 완료되었습니다            | `GET_RECENT_DATASETS`     |
| `200` | 200       | 데이터셋 자동완성을 위한 데이터셋 목록을 조회한다   | `FIND_REAL_TIME_DATASETS` |
| `200` | 200       | 수정이 완료되었습니다                               | `MODIFY_DATASET`          |
| `200` | 200       | 데이터셋 삭제가 완료되었습니다                      | `DELETE_DATASET`          |
| `200` | 200       | 데이터셋 복원에 완료되었습니다                      | `RESTORE_DATASET`         |
| `200` | 200       | 유효기간이 있는 데이터셋 다운로드 URL을 반환된다    | `DOWNLOAD_DATASET`        |
| `200` | 200       | 로그인한 회원이 업로드한 데이터셋 리스트를 조회한다 | `GET_USER_DATASETS`       |

### **댓글 관련 성공**

| 코드  | HTTP 상태 | 설명                                            | Enum 이름            |
| ----- | --------- | ----------------------------------------------- | -------------------- |
| `201` | 201       | 댓글 작성이 완료되었습니다                      | `CREATED_COMMENT`    |
| `200` | 200       | 댓글 수정이 완료되었습니다                      | `MODIFY_COMMENT`     |
| `200` | 200       | 댓글 삭제가 완료되었습니다                      | `DELETE_COMMENT`     |
| `200` | 200       | 프로젝트에 대한 댓글 목록 조회가 완료되었습니다 | `GET_COMMENTS`       |
| `200` | 200       | 댓글에 대한 답글 목록 조회가 완료되었습니다     | `GET_REPLY_COMMENTS` |

### **좋아요 관련 성공**

| 코드       | HTTP 상태 | 설명                                            | Enum 이름        |
| ---------- | --------- | ----------------------------------------------- | ---------------- |
| `LIKE-001` | 200       | 프로젝트에 대한 좋아요 처리에 성공했습니다      | `LIKE_PROJECT`   |
| `LIKE-002` | 200       | 프로젝트에 대한 좋아요 취소 처리에 성공했습니다 | `UNLIKE_PROJECT` |
| `LIKE-003` | 200       | 댓글에 대한 좋아요 처리에 성공했습니다          | `LIKE_COMMENT`   |
| `LIKE-004` | 200       | 댓글에 대한 좋아요 취소 처리에 성공했습니다     | `UNLIKE_COMMENT` |

### **이메일 관련 성공**

| 코드  | HTTP 상태 | 설명                                                         | Enum 이름                            |
| ----- | --------- | ------------------------------------------------------------ | ------------------------------------ |
| `200` | 200       | 입력하신 이메일로 인증코드가 발송되었습니다                  | `OK_SEND_EMAIL_CODE_SIGN_UP`         |
| `200` | 200       | 입력하신 이메일로 비밀번호 찾기 인증 코드가 발송되었습니다   | `OK_SEND_EMAIL_CODE_PASSWORD_SEARCH` |
| `200` | 200       | 입력하신 이메일로 비밀번호 재설정 인증 코드가 발송되었습니다 | `OK_SEND_EMAIL_CODE_PASSWORD_RESET`  |
| `200` | 200       | 본인 인증이 완료되었습니다                                   | `OK_VERIFY_EMAIL_CODE`               |

### **참조 데이터 관련 성공**

| 코드  | HTTP 상태 | 설명                                   | Enum 이름                        |
| ----- | --------- | -------------------------------------- | -------------------------------- |
| `200` | 200       | 도메인 리스트 조회에 성공했습니다      | `OK_TOTAL_TOPIC_LIST`            |
| `200` | 200       | 작성자 유형 리스트 조회에 성공했습니다 | `OK_TOTAL_AUTHOR_LEVEL_LIST`     |
| `200` | 200       | 직업 리스트 조회에 성공했습니다        | `OK_TOTAL_OCCUPATION_LIST`       |
| `200` | 200       | 방문 경로 리스트 조회에 성공했습니다   | `OK_TOTAL_VISIT_SOURCE_LIST`     |
| `200` | 200       | 분석 목적 리스트 조회에 성공했습니다   | `OK_TOTAL_ANALYSIS_PURPOSE_LIST` |
| `200` | 200       | 데이터 출처 리스트 조회에 성공했습니다 | `OK_TOTAL_DATA_SOURCE_LIST`      |
| `200` | 200       | 데이터 유형 리스트 조회에 성공했습니다 | `OK_TOTAL_DATA_TYPE_LIST`        |

---

## 🔗 **API 엔드포인트**

### **인증 (Auth)**

#### **로그인**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### **회원가입**

```http
POST /api/v1/signup/self
Content-Type: application/json

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

#### **비밀번호 재설정**

```http
PUT /api/v1/password/reset
Content-Type: application/json

{
  "resetPasswordToken": "reset_token_here",
  "password": "new_password123@",
  "passwordConfirm": "new_password123@"
}
```

### **사용자 (User)**

#### **프로필 조회**

```http
GET /api/v1/user
Authorization: Bearer {access_token}
```

#### **프로필 수정**

```http
PUT /api/v1/user
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

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

#### **회원 탈퇴**

```http
DELETE /api/v1/user
Authorization: Bearer {access_token}
```

### **프로젝트 (Project)**

#### **프로젝트 목록 조회**

```http
GET /api/v1/projects?page=0&size=20&sort=createdAt,desc
```

#### **프로젝트 상세 조회**

```http
GET /api/v1/projects/{id}
```

#### **프로젝트 생성**

```http
POST /api/v1/projects
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "프로젝트 제목",
  "content": "프로젝트 내용",
  "topicId": 1,
  "analysisPurposeId": 2,
  "dataSourceId": 3,
  "authorLevelId": 4,
  "isContinue": false,
  "parentProjectId": null,
  "dataIds": [1, 2, 3]
}
```

#### **프로젝트 수정**

```http
PUT /api/v1/projects/{id}
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

thumbnailFile: [파일] (선택사항)
webRequest: {
  "title": "수정된 제목",
  "content": "수정된 내용",
  "topicId": 1,
  "analysisPurposeId": 2,
  "dataSourceId": 3,
  "authorLevelId": 4,
  "isContinue": false,
  "parentProjectId": null,
  "dataIds": [1, 2, 3]
}
```

#### **프로젝트 삭제**

```http
DELETE /api/v1/projects/{id}
Authorization: Bearer {access_token}
```

#### **프로젝트 복원**

```http
PUT /api/v1/projects/{id}/restore
Authorization: Bearer {access_token}
```

#### **유사 프로젝트 조회**

```http
GET /api/v1/projects/{id}/similar?size=5
```

#### **실시간 프로젝트 검색**

```http
GET /api/v1/projects/search/real-time?keyword=키워드&size=10
```

### **데이터셋 (Dataset)**

#### **인기 데이터셋 조회**

```http
GET /api/v1/datasets/popular?size=10
```

#### **데이터셋 상세 조회**

```http
GET /api/v1/datasets/{dataId}
```

#### **최근 데이터셋 조회**

```http
GET /api/v1/datasets/recent?size=10
```

#### **데이터셋 업로드**

```http
POST /api/v1/datasets
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

dataFile: [파일]
webRequest: {
  "title": "데이터셋 제목",
  "description": "데이터셋 설명",
  "topicId": 1,
  "dataSourceId": 2,
  "dataTypeId": 3,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "analysisGuide": "분석 가이드"
}
```

#### **데이터셋 수정**

```http
PUT /api/v1/datasets/{dataId}
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

dataFile: [파일] (선택사항)
webRequest: {
  "title": "수정된 제목",
  "description": "수정된 설명",
  "topicId": 1,
  "dataSourceId": 2,
  "dataTypeId": 3,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "analysisGuide": "수정된 분석 가이드"
}
```

#### **데이터셋 삭제**

```http
DELETE /api/v1/datasets/{dataId}
Authorization: Bearer {access_token}
```

#### **필터링된 데이터셋 조회**

```http
GET /api/v1/datasets/filter?keyword=키워드&topicId=1&dataSourceId=2&page=0&size=5
```

#### **실시간 데이터셋 검색**

```http
GET /api/v1/datasets/search/real-time?keyword=키워드&size=10
```

#### **유사 데이터셋 조회**

```http
GET /api/v1/datasets/{dataId}/similar?size=5
```

### **댓글 (Comment)**

#### **댓글 목록 조회**

```http
GET /api/v1/projects/{projectId}/comments?page=0&size=5
```

#### **답글 목록 조회**

```http
GET /api/v1/projects/{projectId}/comments/{commentId}?page=0&size=5&sort=createdAt,asc
```

#### **댓글 작성**

```http
POST /api/v1/projects/{projectId}/comments
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "content": "댓글 내용",
  "parentCommentId": 123
}
```

#### **댓글 수정**

```http
PUT /api/v1/projects/{projectId}/comments/{commentId}
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "content": "수정된 댓글 내용"
}
```

#### **댓글 삭제**

```http
DELETE /api/v1/projects/{projectId}/comments/{commentId}
Authorization: Bearer {access_token}
```

### **좋아요 (Like)**

#### **좋아요 처리**

```http
POST /api/v1/likes
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "targetId": 123,
  "targetType": "PROJECT",
  "previouslyLiked": false
}
```

### **참조 데이터 (Reference)**

#### **토픽 목록 조회**

```http
GET /api/v1/references/topics
```

#### **직업 목록 조회**

```http
GET /api/v1/references/occupations
```

#### **작가 레벨 목록 조회**

```http
GET /api/v1/references/author-levels
```

#### **방문 경로 목록 조회**

```http
GET /api/v1/references/visit-sources
```

#### **데이터 타입 목록 조회**

```http
GET /api/v1/references/data-types
```

#### **데이터 소스 목록 조회**

```http
GET /api/v1/references/data-sources
```

#### **분석 목적 목록 조회**

```http
GET /api/v1/references/analysis-purposes
```

### **파일 (File)**

#### **PreSigned URL 생성**

```http
GET /api/v1/files/pre-signed-url?s3Url=파일URL&expirationSeconds=3600
Authorization: Bearer {access_token}
```

### **이메일 (Email)**

#### **이메일 인증 코드 전송**

```http
POST /api/v1/email/send
Content-Type: application/json

{
  "email": "user@example.com",
  "purpose": "SIGN_UP"
}
```

#### **이메일 인증 코드 확인**

```http
POST /api/v1/email/verify
Content-Type: application/json

{
  "email": "user@example.com",
  "code": "123456",
  "purpose": "SIGN_UP"
}
```

---

## 💡 **예제 요청/응답**

### **1. 사용자 로그인**

**요청:**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답:**

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "로그인이 성공했습니다.",
  "data": null
}
```

### **2. 프로젝트 목록 조회**

**요청:**

```http
GET /api/v1/projects?page=0&size=10&sort=createdAt,desc
```

**응답:**

```json
{
  "httpStatus": 200,
  "code": "SUCCESS",
  "message": "프로젝트 목록 조회에 성공했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "서울시 교통량 분석",
        "description": "서울시 주요 도로의 교통량을 분석한 프로젝트입니다.",
        "thumbnailImageUrl": "https://example.com/thumbnail.jpg",
        "author": {
          "id": 1,
          "nickname": "분석가",
          "profileImageUrl": "https://example.com/profile.jpg"
        },
        "likeCount": 42,
        "viewCount": 156,
        "commentCount": 8,
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "direction": "DESC",
        "property": "createdAt"
      }
    },
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false,
    "numberOfElements": 10
  }
}
```

### **3. 프로젝트 생성**

**요청:**

```http
POST /api/v1/projects
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data

{
  "title": "부동산 가격 예측 모델",
  "description": "머신러닝을 활용한 부동산 가격 예측 모델 개발",
  "analysisPurpose": "PREDICTION",
  "dataSource": "국토교통부",
  "dataType": "CSV",
  "thumbnailImage": [파일],
  "datasetIds": [1, 2]
}
```

**응답:**

```json
{
  "httpStatus": 201,
  "code": "SUCCESS",
  "message": "프로젝트가 성공적으로 생성되었습니다.",
  "data": {
    "id": 123,
    "title": "부동산 가격 예측 모델",
    "description": "머신러닝을 활용한 부동산 가격 예측 모델 개발",
    "thumbnailImageUrl": "https://example.com/thumbnail.jpg",
    "author": {
      "id": 1,
      "nickname": "사용자명",
      "profileImageUrl": "https://example.com/profile.jpg"
    },
    "likeCount": 0,
    "viewCount": 0,
    "commentCount": 0,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

### **4. 에러 응답 예제**

**요청:**

```http
GET /api/v1/projects/999
```

**응답:**

```json
{
  "httpStatus": 404,
  "code": "PROJECT_NOT_FOUND",
  "message": "프로젝트를 찾을 수 없습니다.",
  "data": null
}
```

---

## 🔧 **개발자 도구**

### **Swagger UI**

- **개발**: `https://dev-api.dataracy.store/swagger-ui.html`
- **운영**: `https://api.dataracy.store/swagger-ui.html`
- **로컬**: `http://localhost:8080/swagger-ui.html`

### **API 테스트**

```bash
# cURL 예제
curl -X GET "https://api.dataracy.store/api/v1/projects" \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json"

# Postman Collection
# https://api.dataracy.store/postman-collection.json
```

### **Rate Limiting**

- **로그인 API**: IP별 10 requests/minute (Redis 기반)
- **일반 API**: IP별 제한 없음 (현재 미적용)
- **메모리 기반**: 단일 인스턴스 환경용 (기본값: 10 requests/minute)

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

## 🔄 **API 버전 관리**

### **버전 정책**

- **메이저 버전**: 하위 호환성 깨짐
- **마이너 버전**: 기능 추가 (하위 호환성 유지)
- **패치 버전**: 버그 수정

### **Deprecation 정책**

- **사전 공지**: 3개월 전
- **지원 기간**: 6개월
- **제거**: 9개월 후

---

**💡 API 관련 문의사항은 개발팀에 연락해주세요!**
