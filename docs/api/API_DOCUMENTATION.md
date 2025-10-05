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

- **Content-Type**: `application/json`
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
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

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
  "code": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다.",
  "data": null
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

### **프로젝트 관련 에러**

| 코드                    | HTTP 상태 | 설명                    |
| ----------------------- | --------- | ----------------------- |
| `PROJECT_NOT_FOUND`     | 404       | 프로젝트를 찾을 수 없음 |
| `PROJECT_ACCESS_DENIED` | 403       | 프로젝트 접근 권한 없음 |
| `PROJECT_ALREADY_LIKED` | 409       | 이미 좋아요한 프로젝트  |

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
  "password": "password123",
  "nickname": "사용자명",
  "authorLevelId": 1,
  "occupationId": 2,
  "topicIds": [1, 2, 3],
  "visitSourceId": 1
}
```

#### **비밀번호 재설정**

```http
PUT /api/v1/password/reset
Content-Type: application/json

{
  "token": "reset_token_here",
  "newPassword": "new_password123"
}
```

### **사용자 (User)**

#### **프로필 조회**

```http
GET /api/v1/user
Authorization: Bearer {token}
```

#### **프로필 수정**

```http
PUT /api/v1/user
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "nickname": "새로운닉네임",
  "authorLevelId": 2,
  "occupationId": 3,
  "topicIds": [1, 2, 4],
  "visitSourceId": 2,
  "introductionText": "자기소개",
  "profileImageFile": [파일]
}
```

#### **회원 탈퇴**

```http
DELETE /api/v1/user
Authorization: Bearer {token}
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
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "title": "프로젝트 제목",
  "description": "프로젝트 설명",
  "analysisPurpose": "분석 목적",
  "dataSource": "데이터 출처",
  "dataType": "CSV",
  "thumbnailImage": [파일],
  "datasetIds": [1, 2, 3]
}
```

#### **프로젝트 수정**

```http
PUT /api/v1/projects/{id}
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "title": "수정된 제목",
  "description": "수정된 설명",
  // ... 기타 필드
}
```

#### **프로젝트 삭제**

```http
DELETE /api/v1/projects/{id}
Authorization: Bearer {token}
```

#### **프로젝트 좋아요**

```http
POST /api/v1/projects/{id}/like
Authorization: Bearer {token}
```

#### **프로젝트 좋아요 취소**

```http
DELETE /api/v1/projects/{id}/like
Authorization: Bearer {token}
```

#### **유사 프로젝트 조회**

```http
GET /api/v1/projects/{id}/similar?limit=5
```

### **데이터셋 (Dataset)**

#### **데이터셋 목록 조회**

```http
GET /api/v1/datasets?page=0&size=20&sort=createdAt,desc
```

#### **데이터셋 상세 조회**

```http
GET /api/v1/datasets/{id}
```

#### **데이터셋 업로드**

```http
POST /api/v1/datasets
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "title": "데이터셋 제목",
  "description": "데이터셋 설명",
  "dataType": "CSV",
  "dataSource": "데이터 출처",
  "file": [파일]
}
```

#### **데이터셋 다운로드**

```http
GET /api/v1/datasets/{id}/download
Authorization: Bearer {token}
```

### **댓글 (Comment)**

#### **댓글 목록 조회**

```http
GET /api/v1/projects/{projectId}/comments?page=0&size=20
```

#### **댓글 작성**

```http
POST /api/v1/projects/{projectId}/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "댓글 내용",
  "parentCommentId": 123
}
```

#### **댓글 수정**

```http
PUT /api/v1/projects/{projectId}/comments/{commentId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "수정된 댓글 내용"
}
```

#### **댓글 삭제**

```http
DELETE /api/v1/projects/{projectId}/comments/{commentId}
Authorization: Bearer {token}
```

#### **댓글 좋아요**

```http
POST /api/v1/projects/{projectId}/comments/{commentId}/like
Authorization: Bearer {token}
```

### **검색 (Search)**

#### **통합 검색**

```http
GET /api/v1/search?q=키워드&type=project&page=0&size=20
```

#### **실시간 검색**

```http
GET /api/v1/search/real-time?q=키워드&limit=10
```

#### **인기 검색어**

```http
GET /api/v1/search/popular?limit=10
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
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"

# Postman Collection
# https://api.dataracy.store/postman-collection.json
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
