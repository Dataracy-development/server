# 📚 참조 데이터 API

## 📋 **개요**

참조 데이터 관련 API는 시스템에서 사용하는 기본 데이터(토픽, 직업, 분석 목적 등)를 제공합니다.

**Base URL**: `/api/v1/references`

---

## 🔑 **엔드포인트**

### **토픽 (Topic)**

#### **1. 전체 토픽 조회**

**엔드포인트**: `GET /api/v1/references/topics`

**설명**: 데이터베이스에서 전체 토픽 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "머신러닝",
      "description": "머신러닝 관련 토픽"
    },
    {
      "id": 2,
      "name": "데이터 시각화",
      "description": "데이터 시각화 관련 토픽"
    },
    {
      "id": 3,
      "name": "딥러닝",
      "description": "딥러닝 관련 토픽"
    }
  ],
  "message": "전체 토픽 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **직업 (Occupation)**

#### **1. 전체 직업 조회**

**엔드포인트**: `GET /api/v1/references/occupations`

**설명**: 데이터베이스에서 전체 직업 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "데이터 분석가",
      "description": "데이터 분석 업무 담당"
    },
    {
      "id": 2,
      "name": "데이터 사이언티스트",
      "description": "데이터 사이언스 업무 담당"
    },
    {
      "id": 3,
      "name": "AI 엔지니어",
      "description": "AI 시스템 개발 담당"
    }
  ],
  "message": "전체 직업 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **작성자 레벨 (Author Level)**

#### **1. 전체 작성자 레벨 조회**

**엔드포인트**: `GET /api/v1/references/author-levels`

**설명**: 데이터베이스에서 전체 작성자 레벨 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "초급자",
      "description": "데이터 분석 초급자"
    },
    {
      "id": 2,
      "name": "중급자",
      "description": "데이터 분석 중급자"
    },
    {
      "id": 3,
      "name": "고급자",
      "description": "데이터 분석 고급자"
    }
  ],
  "message": "전체 작성자 레벨 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **방문 경로 (Visit Source)**

#### **1. 전체 방문 경로 조회**

**엔드포인트**: `GET /api/v1/references/visit-sources`

**설명**: 데이터베이스에서 전체 방문 경로 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "검색엔진",
      "description": "검색엔진을 통한 유입"
    },
    {
      "id": 2,
      "name": "소셜미디어",
      "description": "소셜미디어를 통한 유입"
    },
    {
      "id": 3,
      "name": "직접입력",
      "description": "URL 직접 입력"
    }
  ],
  "message": "전체 방문 경로 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **데이터 타입 (Data Type)**

#### **1. 전체 데이터 타입 조회**

**엔드포인트**: `GET /api/v1/references/data-types`

**설명**: 데이터베이스에서 전체 데이터 타입 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "CSV",
      "description": "쉼표로 구분된 값",
      "extension": ".csv"
    },
    {
      "id": 2,
      "name": "JSON",
      "description": "JavaScript Object Notation",
      "extension": ".json"
    },
    {
      "id": 3,
      "name": "XLSX",
      "description": "Excel 파일",
      "extension": ".xlsx"
    }
  ],
  "message": "전체 데이터 유형 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **데이터 출처 (Data Source)**

#### **1. 전체 데이터 출처 조회**

**엔드포인트**: `GET /api/v1/references/data-sources`

**설명**: 데이터베이스에서 전체 데이터 출처 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "공공데이터",
      "description": "정부 공공데이터"
    },
    {
      "id": 2,
      "name": "기상청",
      "description": "기상청 데이터"
    },
    {
      "id": 3,
      "name": "국토교통부",
      "description": "국토교통부 데이터"
    }
  ],
  "message": "전체 데이터 출처 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### **분석 목적 (Analysis Purpose)**

#### **1. 전체 분석 목적 조회**

**엔드포인트**: `GET /api/v1/references/analysis-purposes`

**설명**: 데이터베이스에서 전체 분석 목적 목록을 조회

**응답**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "예측",
      "description": "미래 값 예측 분석"
    },
    {
      "id": 2,
      "name": "분류",
      "description": "데이터 분류 분석"
    },
    {
      "id": 3,
      "name": "클러스터링",
      "description": "데이터 클러스터링 분석"
    }
  ],
  "message": "전체 분석 목적 리스트 조회",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## ❌ **에러 코드**

| 코드                       | HTTP 상태 | 설명                       |
| -------------------------- | --------- | -------------------------- |
| `REFERENCE_DATA_NOT_FOUND` | 404       | 참조 데이터를 찾을 수 없음 |
| `INVALID_REFERENCE_TYPE`   | 400       | 잘못된 참조 데이터 타입    |
| `DATABASE_ERROR`           | 500       | 데이터베이스 오류          |

---

## 💡 **사용 예제**

### **cURL 예제**

#### **전체 토픽 조회**

```bash
curl -X GET "https://api.dataracy.co.kr/api/v1/references/topics" \
  -H "Content-Type: application/json"
```

#### **전체 직업 조회**

```bash
curl -X GET "https://api.dataracy.co.kr/api/v1/references/occupations" \
  -H "Content-Type: application/json"
```

### **JavaScript 예제**

#### **참조 데이터 조회**

```javascript
const getReferenceData = async (type) => {
  const response = await fetch(`/api/v1/references/${type}`);
  return response.json();
};
```

#### **모든 참조 데이터 조회**

```javascript
const getAllReferenceData = async () => {
  const [
    topics,
    occupations,
    authorLevels,
    visitSources,
    dataTypes,
    dataSources,
    analysisPurposes,
  ] = await Promise.all([
    getReferenceData("topics"),
    getReferenceData("occupations"),
    getReferenceData("author-levels"),
    getReferenceData("visit-sources"),
    getReferenceData("data-types"),
    getReferenceData("data-sources"),
    getReferenceData("analysis-purposes"),
  ]);

  return {
    topics: topics.data,
    occupations: occupations.data,
    authorLevels: authorLevels.data,
    visitSources: visitSources.data,
    dataTypes: dataTypes.data,
    dataSources: dataSources.data,
    analysisPurposes: analysisPurposes.data,
  };
};
```

---

## 📊 **참조 데이터 정책**

### **캐싱 정책**

- **캐시 시간**: 1시간
- **캐시 저장소**: Redis
- **캐시 키**: `reference:{type}`

### **데이터 업데이트**

- **수동 업데이트**: 관리자만 가능
- **자동 갱신**: 없음
- **버전 관리**: 없음

### **데이터 구조**

- **ID**: 고유 식별자
- **Name**: 표시명
- **Description**: 설명 (선택사항)
- **Extension**: 파일 확장자 (데이터 타입만)

---

**💡 참조 데이터 관련 문제가 발생하면 개발팀에 문의하세요!**
