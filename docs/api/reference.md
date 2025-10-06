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
  "httpStatus": 200,
  "code": "200",
  "message": "도메인 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "머신러닝"
    },
    {
      "id": 2,
      "name": "데이터 시각화"
    },
    {
      "id": 3,
      "name": "딥러닝"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "직업 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "데이터 분석가"
    },
    {
      "id": 2,
      "name": "데이터 사이언티스트"
    },
    {
      "id": 3,
      "name": "AI 엔지니어"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "작성자 유형 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "초급자"
    },
    {
      "id": 2,
      "name": "중급자"
    },
    {
      "id": 3,
      "name": "고급자"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "방문 경로 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "검색엔진"
    },
    {
      "id": 2,
      "name": "소셜미디어"
    },
    {
      "id": 3,
      "name": "직접입력"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "데이터 유형 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "CSV"
    },
    {
      "id": 2,
      "name": "JSON"
    },
    {
      "id": 3,
      "name": "XLSX"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "데이터 출처 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "공공데이터"
    },
    {
      "id": 2,
      "name": "기상청"
    },
    {
      "id": 3,
      "name": "국토교통부"
    }
  ]
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
  "httpStatus": 200,
  "code": "200",
  "message": "분석 목적 리스트 조회에 성공했습니다.",
  "data": [
    {
      "id": 1,
      "name": "예측"
    },
    {
      "id": 2,
      "name": "분류"
    },
    {
      "id": 3,
      "name": "클러스터링"
    }
  ]
}
```

---

## ❌ **에러 코드**

| 코드            | HTTP 상태 | 설명                                                 | Enum 이름                    |
| --------------- | --------- | ---------------------------------------------------- | ---------------------------- |
| `REFERENCE-001` | 404       | 해당하는 토픽명이 없습니다. 올바른 값을 입력해주세요 | `NOT_FOUND_TOPIC_NAME`       |
| `REFERENCE-002` | 404       | 해당 작성자 유형이 존재하지 않습니다                 | `NOT_FOUND_AUTHOR_LEVEL`     |
| `REFERENCE-003` | 404       | 해당 직업이 존재하지 않습니다                        | `NOT_FOUND_OCCUPATION`       |
| `REFERENCE-004` | 404       | 해당 방문 경로가 존재하지 않습니다                   | `NOT_FOUND_VISIT_SOURCE`     |
| `REFERENCE-005` | 404       | 해당 분석 목적이 존재하지 않습니다                   | `NOT_FOUND_ANALYSIS_PURPOSE` |
| `REFERENCE-006` | 404       | 해당 데이터 출처가 존재하지 않습니다                 | `NOT_FOUND_DATA_SOURCE`      |
| `REFERENCE-007` | 404       | 해당 데이터 유형이 존재하지 않습니다                 | `NOT_FOUND_DATA_TYPE`        |

---

## ✅ **성공 응답 코드**

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

## 💡 **사용 예제**

### **cURL 예제**

#### **전체 토픽 조회**

```bash
curl -X GET "https://api.dataracy.store/api/v1/references/topics" \
  -H "Content-Type: application/json"
```

#### **전체 직업 조회**

```bash
curl -X GET "https://api.dataracy.store/api/v1/references/occupations" \
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

### **데이터 구조**

- **ID**: 고유 식별자 (`topic_id`, `data_source_id`, `data_type_id` 등)
- **Value**: 내부 값 (Unique Constraint)
- **Label**: 표시명 (사용자에게 보여지는 이름)

### **데이터 관리**

- **데이터베이스**: MySQL JPA Entity 기반
- **캐싱**: 현재 미구현 (직접 DB 조회)
- **업데이트**: 관리자만 가능 (수동)
- **버전 관리**: 없음

### **엔티티 구조**

- **TopicEntity**: `topic_id`, `value`, `label`
- **DataSourceEntity**: `data_source_id`, `value`, `label`
- **DataTypeEntity**: `data_type_id`, `value`, `label`, `extension`
- **OccupationEntity**: `occupation_id`, `value`, `label`
- **AuthorLevelEntity**: `author_level_id`, `value`, `label`
- **VisitSourceEntity**: `visit_source_id`, `value`, `label`
- **AnalysisPurposeEntity**: `analysis_purpose_id`, `value`, `label`

---

**💡 참조 데이터 관련 문제가 발생하면 개발팀에 문의하세요!**
