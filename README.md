# 🏆 **핵심 성과 지표**

| 영역                | 지표                      | 달성도       | 사용 도구                    | 실무적 가치               |
| ------------------- | ------------------------- | ------------ | ---------------------------- | ------------------------- |
| **코드 품질**       | Instruction 82.5%         | ✅ 목표 달성 | JaCoCo (커버리지 분석)       | 안정적인 코드베이스 구축  |
| **분기 커버리지**   | Branch 71.9%              | ✅ 양호      | JaCoCo (분기 테스트)         | 조건문 테스트 완성도      |
| **메서드 커버리지** | Method 85.8%              | ✅ 우수      | JaCoCo (메서드 테스트)       | 비즈니스 로직 검증 완성도 |
| **클래스 커버리지** | Class 96.5%               | ✅ 완벽      | JaCoCo (클래스 테스트)       | 전체 클래스 테스트 완성도 |
| **테스트 안정성**   | 성공률 100%               | ✅ 완벽 달성 | JUnit 5 + Mockito + AssertJ  | 배포 신뢰성 확보          |
| **빌드 안정성**     | Gradle 9.0 호환           | ✅ 완벽 달성 | Gradle Wrapper + Build Scan  | 최신 도구 지원            |
| **보안**            | 취약점 0개                | ✅ 완벽 달성 | SonarQube (보안 취약점 분석) | 보안성 확보               |
| **유지보수성**      | 코드 냄새(Code Smell) 0개 | ✅ 완벽 달성 | SonarQube (정적 분석)        | 장기적 개발 효율성        |
| **코드 스타일**     | 위반 0개                  | ✅ 완벽 달성 | Checkstyle (스타일 검사)     | 일관된 코딩 표준          |
| **자동 포맷팅**     | 100% 통일                 | ✅ 완벽 달성 | Spotless (자동 포맷팅)       | 팀 협업 효율성            |
| **정적 분석**       | 버그 0개                  | ✅ 완벽 달성 | SpotBugs (버그 탐지)         | 런타임 오류 방지          |

---

# 📊 01. Dataracy 플랫폼 개요

**Dataracy(데이터러시)** — _"사례를 보고 → 따라하고 → 이어가는"_ 흐름으로 초심자가 쉽게 시작하고 성장하는 **피드백 중심 데이터 분석 커뮤니티**입니다.

---

## 🚀 **기술적 혁신**

### **1. 아키텍처 설계**

- **DDD + 헥사고날 아키텍처**: 도메인 중심 설계로 비즈니스 로직 명확화
- **CQRS 패턴**: 명령과 조회 분리로 성능 최적화
- **Port & Adapter**: 인프라와 도메인 분리로 유지보수성 향상

### **2. 데이터 처리 전략**

- **Kafka 기반 이벤트 처리**: 비동기 분리로 API 부하 완화, 멱등성 보장으로 중복 메시지 처리 방지
- **Redis 캐싱**: 인메모리 캐싱으로 조회 성능 향상
- **Elasticsearch 검색**: 전문 검색과 유사도 추천으로 사용자 경험 향상
- **Worker 최적화**: 제한된 병렬 처리로 ES 부하 제어 및 성능 향상

### **3. 운영 안정성**

- **Blue-Green 배포**: 무중단 배포로 서비스 연속성 확보
- **분산락**: Redisson 기반 동시성 제어로 데이터 정합성 보장
- **멱등성 보장**: Kafka Consumer에서 Partition + Offset 기반 중복 메시지 처리 방지
- **Worker 안정성**: 분산 락과 제한된 병렬 처리로 다중 인스턴스 환경에서 안정적 동작
- **모니터링**: Prometheus + Grafana로 실시간 시스템 상태 추적

---

## 왜? (문제)

- 무엇을/어떻게 분석할지 **출발점이 막막**함
- **피드백 부재**로 동기 저하 및 성장 정체

## 무엇으로? (해결)

- **사례 중심 학습**: 잘 만든 예시로 시작 허들 제거
- **이어가기(Remix)**: 기존 분석을 나만의 방식으로 확장
- **가벼운 댓글 피드백**: 빠른 상호작용으로 학습 루프 촉진
- **데이터셋 큐레이션·자동 태깅**: 탐색/재사용성 향상

## 🎯 **핵심 기능 & API**

### **사용자 중심 기능**

| 기능             | 설명                                   | 주요 API 엔드포인트                                  |
| ---------------- | -------------------------------------- | ---------------------------------------------------- |
| 🔍 사례 탐색     | 도메인·목적·형태별 필터/검색           | `GET /api/v1/projects/filter`, `/search/real-time`   |
| ➕ 프로젝트 등록 | 템플릿 기반 카드 업로드(구조화 가이드) | `POST /api/v1/projects`, `PUT /api/v1/projects/{id}` |
| 🔁 이어가기      | 기존 프로젝트 리메이크·확장            | `GET /api/v1/projects/{id}/similar`                  |
| 💬 댓글 피드백   | 짧은 질문/의견으로 빠른 교환           | `POST /api/v1/comments`, `PUT /api/v1/comments/{id}` |
| 📈 데이터셋 목록 | 등록된 데이터셋을 선택해 분석          | `POST /api/v1/datasets`, `GET /api/v1/datasets/{id}` |
| 📂 마이페이지    | 포트폴리오·관심·활동 기록 관리         | `GET/PUT /api/v1/user/profile`                       |

### **기술적 구현 영역**

| 모듈             | 구현 기능                             | 주요 API 엔드포인트                                                   |
| ---------------- | ------------------------------------- | --------------------------------------------------------------------- |
| **인증/사용자**  | OAuth2, JWT, 프로필, 비밀번호 관리    | `POST /api/v1/auth/login`, `/token/re-issue`, `/user/password/change` |
| **프로젝트**     | CRUD, 좋아요, 데이터셋 연결           | `POST /api/v1/projects`, `POST /api/v1/likes`                         |
| **데이터셋**     | 파일 업로드, 메타데이터, 검색         | `POST /api/v1/datasets`, `/search/real-time`                          |
| **검색/추천**    | Elasticsearch 기반 통합 검색, 유사도  | `GET /api/v1/projects/{id}/similar`                                   |
| **댓글**         | CRUD, 대댓글, 좋아요, 이벤트 발행     | `POST /api/v1/comments`, `DELETE /api/v1/comments/{id}`               |
| **참조데이터**   | 토픽, 분석목적, 데이터소스 등 관리    | `GET /api/v1/references/topics`, `/analysis-purposes`                 |
| **이메일**       | SendGrid/SES 인증 코드 발송/검증      | `POST /api/v1/email/send-code`, `/verify`                             |
| **파일스토리지** | S3 멀티파트 업로드, PreSigned URL     | `GET /api/v1/files/pre-signed-url`                                    |
| **행동로그**     | MDC 기반 실시간 로그 수집, Kafka 전송 | 자동 수집 (API 없음)                                                  |
| **알림**         | Kafka 이벤트 기반 비동기 알림         | 이벤트 기반 처리 (API 없음)                                           |

### **상세 구현 기능**

#### **🔐 인증/사용자 관리**

- **OAuth2 소셜 로그인**: 카카오, 구글 연동 (`OAuth2LoginSuccessHandler`)
- **자체 회원가입**: 이메일/닉네임 중복 검증, 비밀번호 암호화 (`SignUpUserService`)
- **JWT 토큰 관리**: Access/Refresh 토큰 발급, 재발급, 쿠키 저장 (`AuthCommandService`)
- **프로필 관리**: 닉네임, 직업, 레벨, 관심주제, 프로필 이미지 (`UserCommandService`)
- **비밀번호 관리**: 변경, 확인, 재설정 (소셜 로그인 사용자 제외) (`UserPasswordApi`)
- **분산락 적용**: 회원가입 시 이메일 중복 방지 (`@DistributedLock`)

#### **📊 프로젝트 관리**

- **프로젝트 CRUD**: 생성, 수정, 삭제, 상세 조회 (`ProjectCommandService`)
- **썸네일 업로드**: S3 멀티파트 업로드, URL 업데이트 (`FileCommandUseCase`)
- **데이터셋 연결**: 프로젝트-데이터셋 다대다 관계 관리 (`ProjectDataEntity`)
- **부모-자식 관계**: 이어가기 기능을 위한 계층 구조 (`parentProject`, `childProjects`)
- **좋아요 시스템**: 프로젝트별 좋아요/취소, 카운트 관리 (`LikeCommandService`)
- **조회수 추적**: 중복 방지 로직으로 조회수 증가 (`ProjectReadService`)
- **검색 색인**: Elasticsearch 자동 색인 (`ProjectEsProjectionWorker`)

#### **📁 데이터셋 관리**

- **파일 업로드**: CSV, Excel 파일 업로드 및 검증 (`DataCommandService`)
- **메타데이터 파싱**: 파일 내용 분석, 행/열 수 추출 (`ParseMetadataService`)
- **썸네일 생성**: 데이터셋 미리보기 이미지 자동 생성 (`ThumbnailGenerator`)
- **참조 데이터 연결**: 토픽, 데이터소스, 데이터타입 분류
- **검색 최적화**: Elasticsearch 기반 전문 검색 및 유사도 추천

#### **💬 댓글 시스템**

- **댓글 CRUD**: 작성, 수정, 삭제 (`CommentCommandService`)
- **대댓글**: 한 단계 깊이의 답글 기능 (`parentCommentId`)
- **댓글 좋아요**: 댓글별 좋아요/취소 기능
- **이벤트 발행**: 댓글 작성 시 Kafka 이벤트 발행 (`SendCommentEventPort`)
- **분산락 적용**: 동시성 제어로 데이터 정합성 보장

#### **🔍 검색/추천 시스템**

- **실시간 검색**: 키워드 자동완성 (`ProjectSearchApi`)
- **필터링 검색**: 다중 조건 필터링 (`FilteringProjectWebRequest`)
- **유사도 추천**: Elasticsearch 기반 유사 프로젝트 추천
- **통합 검색**: 프로젝트, 데이터셋, 사용자 통합 검색

#### **📧 이메일 인증**

- **인증 코드 발송**: SendGrid/SES 기반 이메일 전송 (`EmailCommandService`)
- **코드 검증**: Redis 기반 임시 저장 및 검증 (`EmailVerifyApi`)
- **다목적 지원**: 회원가입, 비밀번호 찾기, 재설정 인증
- **보안**: 6자리 랜덤 코드, 만료시간 관리

#### **☁️ 파일 스토리지**

- **S3 멀티파트 업로드**: 대용량 파일 안정적 처리 (`AwsS3FileStorageAdapter`)
- **PreSigned URL**: 임시 다운로드 링크 생성 (`FileApi`)
- **파일 관리**: 업로드, 삭제, 교체 기능 (`FileCommandService`)
- **썸네일 생성**: 이미지 자동 리사이징 및 최적화

#### **📊 행동 로그 시스템**

- **실시간 수집**: MDC 기반 사용자 행동 추적 (`BehaviorLogTrackingFilter`)
- **Kafka 전송**: 비동기 로그 메시지 전송 (`BehaviorLogKafkaProducerAdapter`)
- **Elasticsearch 저장**: 로그 데이터 영구 저장 (`BehaviorLogElasticsearchSaveAdapter`)
- **행동 분석**: 클릭, 네비게이션, 체류시간 추적
- **디바이스 정보**: OS, 브라우저, IP, User-Agent 수집

#### **🔔 실시간 알림**

- **Kafka 이벤트**: 좋아요, 댓글, 팔로우 이벤트 처리
- **비동기 처리**: 이벤트 기반 비동기 알림 시스템
- **이벤트 발행**: 각 모듈에서 이벤트 발행 (`SendLikeEventPort`, `SendCommentEventPort`)

## 누구를 위해?

- 👩 **입문자/취준생**: 포트폴리오 시작과 방향 제시
- 👨‍💻 **주니어 분석가**: 실전 연습 케이스 축적
- 🧑‍🏫 **강사/멘토**: 사례 기반 수업/피드백 운영

## 차별점

- **한국어·직관 UI**로 낮은 진입장벽
- **템플릿 가이드**로 초심자 친화적 작성 경험
- **실무 중심**: 실제 사용 가능한 데이터와 프로젝트
- **커뮤니티 기반**: 피드백과 소통을 통한 학습
- **단계별 성장**: 초심자부터 전문가까지 체계적 지원
- **이어가기 + 댓글**로 구조화된 협업/학습 루프
- **확장성**: 리포트 판매·교육 연계 등 성장 경로

<br/>
<br/>

---

# 📦 02. 기술 스택 (요약)

| 구분                    | 사용 기술                                                                         |
| ----------------------- | --------------------------------------------------------------------------------- |
| **언어/프레임워크**     | Java 17, Spring Boot (Web, Validation, AOP, Actuator, Security, OAuth2 Client)    |
| **설계**                | DDD, 헥사고날 아키텍처(Port & Adapter), CQRS 패턴                                 |
| **DB/ORM**              | MySQL (AWS RDS), JPA, QueryDSL, JPQL, Native Query, Soft Delete                   |
| **캐시/동시성**         | Redis, Redisson(분산락)                                                           |
| **메시징(이벤트)/검색** | Apache Kafka, Elasticsearch, Kibana                                               |
| **인증/보안**           | OAuth2 (Google/Kakao), JWT, Spring Security, 자체 로그인                          |
| **스토리지/메일**       | AWS S3, AWS SES, SendGrid                                                         |
| **배포/운영**           | Docker, Nginx, Blue-Green 배포(`switch.sh`), AWS EC2/RDS/Redis                    |
| **모니터링/로깅**       | Logback + MDC + AOP(행동 로그), Micrometer + Prometheus + Grafana                 |
| **테스트/DX**           | k6, LLM 기반 커스텀 자동 PR 리뷰 + Code Rabbit 리뷰                               |
| **추가 기술**           | SpEL, Jackson, Swagger/OpenAPI, CORS, Global Exception Handler, Custom Validators |

### **Quality Assurance**

- **Code Coverage**: JaCoCo (82.5% Instruction Coverage)
- **Static Analysis**: SonarQube (A등급 품질 게이트 통과)
- **Code Style**: Checkstyle (Main 코드 0개 경고 - 완전 해결)
- **Testing**: JUnit 5, Mockito, AssertJ

<br/>
<br/>

---

# 🛠 03. 아키텍처 설계 (DDD + 헥사고날 / Port & Adapter)

## 📌 핵심 설계 원칙

- **도메인 중심 설계 (DDD)**: 비즈니스 로직을 도메인 계층에 집중
- **헥사고날 아키텍처**: 포트와 어댑터로 인프라와 도메인 분리
- **CQRS 패턴**: 명령과 조회의 책임 분리로 성능 최적화
- **계층별 독립성**: 각 계층의 변경이 다른 계층에 미치는 영향 최소화

<br/>

## 🏗️ 계층 구조

```plaintext
[Adapters]   ┌─────────────────────────────────────────────────────────────────────────┐
Web(API)    │  Web Adapter (Controller, Web DTO, Web DTO <-> Application DTO Mapper)  │
Messaging   │  Kafka Adapter (Producer/Consumer)                                      │
Persistence │  JPA Adapter (Entity, Repository, Impl, Entity <-> Domain Mapper)       │
Query       │  QueryDsl Adapter (Predicates, SortBuilder, Impl)                       │
Search      │  Elasticsearch Adapter (Indexing, Search)                               │
Storage     │  S3 Adapter                                                             │
Email       │  SES/SendGrid Adapter                                                   │
Security    │  OAuth2/JWT Adapter                                                     │
			└───────────────-┐───────────────────────────────▲────────────────────────┘
							│ inbound ports (use-cases)     │ outbound ports (port)
[Application]        ┌───────-▼───────────────────────────--──┘────────--─┐
Use Cases/Services  │  Application Layer (Ports-In, Ports-Out), Service  │
					└──────────────────────────────────────────────────--┘
[Domain]             ┌─────────────────────────────────────────────────--─┐
Model/Rules         │      Domain Layer (도메인 모델, 도메인 규칙 및 정책)      │
					└──────────────────────────────────────────────────--┘
```

<br/>

## 🔌 Port & Adapter 패턴

### **Port (인터페이스)**

- **Inbound Port**: 외부에서 도메인으로 들어오는 요청 (Controller → Service)
- **Outbound Port**: 도메인에서 외부로 나가는 요청 (Service → Repository)

### **Adapter (구현체)**

- **Primary Adapter**: 외부 요청을 받아 도메인으로 전달 (Web Controller)
- **Secondary Adapter**: 도메인 요청을 외부 시스템으로 전달 (JPA Repository)

<br/>

## 🛠 매퍼 전략

- **Entity ↔ Domain Model** _(~/adapter/jpa/mapper)_
- **Domain Model ↔ Application DTO** _(~/application/mapper)_
- **Application DTO ↔ Web DTO** _(~/adapter/web/mapper)_
- _(현재 구현: 수동 매퍼 기반 — 계층 독립성·의존 역전 원칙 유지)_

## 🏗️ 도메인 모델 설계

- **Rich Domain Model**: 도메인 객체에 비즈니스 로직과 규칙을 캡슐화
- **Value Object**: `UserInfo`, `DataMetadata` 등 불변 객체로 도메인 개념 표현
- **Enum 활용**: `RoleType`, `ProviderType`, `ActionType` 등 타입 안전성 보장
- **Soft Delete**: `@Where(clause = "is_deleted = false")`로 논리적 삭제 구현
- **Auditing**: `BaseEntity`로 생성/수정 시간과 사용자 자동 추적

<br/>

## 🔄 요청·이벤트 흐름 예시

```plaintext
[Web Controller]
	→ Ports-In (UseCase)
		→ Application Service
			→ Domain 규칙 검증 / 모델 변환
			→ Ports-Out (JPA/Redis/ES/Kafka 등)
				→ [Adapters]
```

## 기대 효과

- **유지보수성**: 외부 기술 교체(JPA ↔ MyBatis, ES 버전 변경 등) 시 도메인 영향 최소화
- **테스트 용이성**: Ports-Out을 목(Mock)으로 대체하여 도메인/유스케이스 단위 테스트 가능
- **확장성**: 새로운 어댑터(WebSocket, Batch, 외부 API 등) 추가 시 구조 영향 최소
- **운영 안정성**: 계층별 책임이 명확해 문제 발생 시 원인 파악과 대응이 빠름

<br/>
<br/>

---

# 🚀 04. Blue-Green 무중단 배포 (dev - prod 각자 블루 그린 배포)

> **목적**
> 서비스 중단 없이 새 버전을 배포하고, 실패 시 즉시 이전 상태로 복귀할 수 있는 안정적인 배포 체계 구축.

```plaintext
deployment/dev/
├─ docker/ # Blue/Green/Nginx Compose 파일
├─ nginx/ # Nginx 메인/업스트림 설정
├─ blue-green/switch-dev.sh # 배포 전환 스크립트
└─ script/deploy.sh # 원격 배포 진입점
.github/workflows/
├─ build-dev.yml # PR 빌드/이미지 푸시
└─ deploy-dev.yml # develop 브랜치 푸시 시 배포
```

ec2 내 상태 파일: `/home/ubuntu/color-config/current_color_dev` (현재 활성 색상 기록)

---

## 🔄 동작 흐름

1. **develop 브랜치 Push**
2. **GitHub Actions**(`deploy-dev.yml`)

- EC2에서 현재 색상 확인 → 반대 색상으로 Docker 이미지 빌드/푸시
- `deploy.sh` 원격 실행 → `switch-dev.sh` 호출

3. **switch-dev.sh**

- 현재 가동중인 컨테이너의 색상 확인
- 새 컨테이너 기동 (`docker-compose-<color>-dev.yml`, `--pull always`)
- Docker Health(`actuator/health`) 확인
- Nginx 업스트림 파일 수정 후 스택 재시작
- 이전 컨테이너 종료 및 색상 기록 갱신

<br/>

## 🩺 헬스체크 & 전환 조건

- `/actuator/health`가 **healthy** 상태여야 전환 진행
- 실패 시 Nginx 업스트림 변경 없이 중단 → 기존 서비스 유지

<br/>

## ⏳ 롤백

- 상태 파일 기반으로 **반대 색상** 재기동
- 헬스체크 통과 시 즉시 복귀 가능

<br/>

## 📊 모니터링 (권장)

- **App**: 요청 지연(p95/p99), 에러율, 스레드풀 대기, GC
- **Nginx**: 5xx/4xx 비율, 응답시간, 활성 커넥션
- **Kafka/Redis/ES**: 소비 지연, DLQ, 캐시 적중률
- **알림**: 배포 직후 5~10분간 임계치 강화(Grafana/Prometheus → Slack/메일)

---

**요약**:

> 현재 색상 → 반대 색상 기동 → 헬스체크 통과 시 Nginx 업스트림 전환 → 이전 색상 종료 → 색상 상태 갱신.

<br/>
<br/>

---

# 🔎 05. 데이터 처리 & 조회 전략

## 🔄 Kafka ↔ Elasticsearch 파이프라인

```plaintext
[API 요청]
↓ (💾 DB 트랜잭션 커밋)
[Kafka 이벤트 발행]
├─ [🗂 색인 소비자] → Elasticsearch 인덱스 반영
└─ [⚙️ 업무 소비자] → 알림·검색·집계·후속 로직 실행
```

- 색인 토픽: DB 변경사항을 ES에 실시간/준실시간 반영 → 검색·추천·유사도 분석 정확도 유지
  예) 프로젝트/댓글 생성·수정·삭제 시 해당 문서 인덱스 업데이트
- 업무 토픽: 댓글·좋아요·알림 등 비동기 처리, 통계 집계, 후처리 작업
  예) 좋아요 증가 → 카운트 업데이트 + 알림 발송

<br/>

## 조회 분리 전략

- DB 조회: 일반 목록, 필터링, 인기/랭킹 (정합성·즉시 반영 우선, 단순 조건/정렬 적합)
- Elasticsearch 조회: 실시간 자동완성 조회, 유사도 추천, 가중치(score) 기반 랭킹 (텍스트/태그 분석·점수화 최적)

<br/>

## 일관성 & 성능 보장

- 최종적 일관성: DB → 큐 기반 Worker → ES (단기 지연 허용)
- 캐시 활용: Redis로 인기/핫 데이터 캐싱 (TTL + 키 무효화)
- 분산락: Redisson으로 카운트·집계 동시성 제어
- DLQ: Kafka, Elasticsearch 실패 이벤트 격리·재처리(Dead Letter Queue)

<br/>
<br/>

---

# 🔍 06. ES 프로젝션 설계 의도 & 효과 분석

## 🧩 왜 이런 구조를 도입했는가?

기존에는 좋아요, 댓글 카운트 업데이트를 위해 Kafka 이벤트를 수신하여 서비스 계층에서 DB와 ES를 동시에 갱신하는 **동기 이중 쓰기**를 사용했다. 하지만 이 방식은

- **부분 실패**(DB 성공, ES 실패) 시 불일치 발생
- **분산 트랜잭션 부재**로 롤백 불가
- **네트워크 지연**으로 인한 API 응답 저하

라는 문제를 만들었다.

따라서 **DB를 단일 진실 공급원(SSOT)** 으로 두고, **ES는 파생 뷰(Projection)** 로 관리하는 구조를 채택했다.
기능 호출 시 동기적으로 DB는 업데이트 되고, ES를 업데이트 할 수 있도록 엔티티 기반 큐와 워커를 기반으로 ES를 관리하는 구조이다.

---

## ⚙️ 무엇을 구현했는가?

1. **Projection Task Queue**

- `ProjectEsProjectionTaskEntity`: 댓글·좋아요·조회수·삭제 상태 변경 요청을 큐에 저장 (deltaComment, deltaLike, deltaView, setDeleted), DB 트랜잭션과 함께 커밋
- `ProjectEsProjectionDlqEntity`: 재시도 초과 시 실패 작업을 격리
- `DataEsProjectionTaskEntity`: 다운로드 수·삭제 상태 변경 요청을 큐에 저장 (deltaDownload, setDeleted), DB 트랜잭션과 함께 커밋
- `DataEsProjectionDlqEntity`: 재시도 초과 시 실패 작업을 격리

2. **Adapter & Repository**

- `ManageProjectEsProjectionTaskDbAdapter`: 큐에 작업 등록 (enqueueCommentDelta, enqueueLikeDelta, enqueueViewDelta, enqueueSetDeleted)
- `ManageDataEsProjectionTaskDbAdapter`: 큐에 작업 등록 (enqueueSetDeleted, enqueueDownloadDelta)
- `LoadProjectEsProjectionTaskDbAdapter`: `PESSIMISTIC_WRITE + SKIP LOCKED` 조회로 중복 처리 방지
- `LoadDataEsProjectionTaskDbAdapter`: `PESSIMISTIC_WRITE + SKIP LOCKED` 조회로 중복 처리 방지
- `ManageProjectEsProjectionDlqDbAdapter`: DLQ 이관 (deltaComment, deltaLike, deltaView, setDeleted, lastError)
- `ManageDataEsProjectionDlqDbAdapter`: DLQ 이관 (deltaDownload, setDeleted, lastError)
- `ProjectEsProjectionTaskRepository`: 배치 조회·즉시 삭제 지원 (`deleteImmediate()` 커스텀 쿼리)
- `DataEsProjectionTaskRepository`: 배치 조회·즉시 삭제 지원 (`deleteById()` JPA 기본 메서드)

3. **Worker**

- `ProjectEsProjectionWorker`:
- `@Scheduled(fixedDelayString = "PT3S")`로 3초마다 폴링
- **분산 락 적용**: `@DistributedLock`으로 여러 인스턴스 간 중복 실행 방지
- **제한된 병렬 처리**: `ThreadPoolTaskExecutor` (Core 10, Max 20, Queue 200)로 최대 20개 작업 동시 처리
- 각 Task를 `REQUIRES_NEW` 트랜잭션으로 실행 → 실패가 다른 Task에 영향 없음
- **지수 백오프**로 재시도, 한도 초과 시 DLQ로 이동
- 성공 시 Task 삭제 → ES와 DB의 최종적 일관성 유지
- 처리 대상: `deltaComment` (댓글 수, 양수=증가, 음수=감소), `deltaLike` (좋아요 수, 양수=증가, 음수=감소), `deltaView` (조회수, 양수만 처리), `setDeleted` (삭제/복원 상태, false=복원, true=삭제)

- `DataEsProjectionWorker`:
- `@Scheduled(fixedDelayString = "PT3S")`로 3초마다 폴링
- **분산 락 적용**: `@DistributedLock`으로 여러 인스턴스 간 중복 실행 방지
- **제한된 병렬 처리**: `ThreadPoolTaskExecutor` (Core 10, Max 20, Queue 200)로 최대 20개 작업 동시 처리
- 각 Task를 `REQUIRES_NEW` 트랜잭션으로 실행 → 실패가 다른 Task에 영향 없음
- **지수 백오프**로 재시도, 한도 초과 시 DLQ로 이동
- 성공 시 Task 삭제 → ES와 DB의 최종적 일관성 유지
- 처리 대상: `deltaDownload` (다운로드 수, 양수만 처리), `setDeleted` (삭제/복원 상태, false=복원, true=삭제)

4. **Service**

- `ProjectCountService`:
- 댓글/좋아요 수를 DB에 먼저 반영
- 같은 트랜잭션에서 Projection Task 큐잉
- `@DistributedLock`으로 프로젝트 단위 동시성 제어

---

## 🚀 어떤 효과가 있었는가?

- **정합성 강화**
  DB와 큐에 동시에 기록 → ES 실패 시에도 큐에 남아 재처리 가능 → 최종적으로 DB와 ES가 일치

- **안정성 확보**
  배치 처리, 재시도/백오프, DLQ 격리로 장애 확산 방지
  **분산 락**으로 다중 인스턴스 환경에서도 단일 Worker만 실행하여 중복 처리 방지

- **운영 편의성**
  DLQ를 통한 원인 분석 및 재처리 가능
  추후 큐 사이즈·DLQ 건수·지연 시간 모니터링으로 상태 가시성 확보

- **성능 개선**
  API 요청 시 ES 반영을 기다리지 않음 → 응답 속도 단축
  ES 반영을 비동기로 처리 → 트래픽 급증에도 확장성 확보
  **제한된 병렬 처리**로 ES 부하를 제어하면서 처리 속도 향상 (순차 처리 → 최대 20개 동시 처리)

<br/>
<br/>

---

# 🗄️ 07. MySQL + JPA + QueryDSL

## 📌 개념

- **MySQL (AWS RDS)**: 안정적인 트랜잭션 처리와 스케일링이 가능한 관계형 데이터베이스
- **Spring Data JPA**: 엔티티 중심의 ORM 매핑과 도메인 주도 설계(DDD)와의 높은 궁합
- **QueryDSL**: 타입 안전한 쿼리 작성, 동적 조건 조합, 복잡한 검색 로직 최적화 도구

<br/>

## ⚙️ 실제 구현 방식

### 1) **Soft Delete 최적화** (`@SQLRestriction` 어노테이션)

```java
@Entity
@SQLRestriction("is_deleted = false") // 자동 필터링
public class ProjectEntity {
	@Column(name = "is_deleted")
	@Builder.Default
	private Boolean isDeleted = false;
}
```

**효과**: 모든 조회에서 **자동으로 삭제된 데이터 제외** → 실수로 삭제된 데이터 조회 방지

### 2) **QueryDSL 기반 복잡 쿼리 최적화** (`SearchProjectQueryDslAdapter`)

```java
// 1단계: ID만 페이징 (N+1 문제 방지)
List<Long> pageIds = queryFactory
	.select(project.id)
	.from(project)
	.where(buildFilterPredicates(request))
	.orderBy(ProjectSortBuilder.fromSortOption(sortType))
	.offset(pageable.getOffset())
	.limit(pageable.getPageSize())
	.fetch();

// 2단계: 필요한 데이터만 fetch join
List<ProjectEntity> parentsWithChildren = queryFactory
	.selectFrom(project)
	.distinct()
	.leftJoin(project.childProjects).fetchJoin() // 1:N 페치조인
	.where(project.id.in(pageIds))
	.fetch();
```

**효과**:

- **N+1 문제 해결**: 페이징과 fetch join 분리
- **메모리 효율성**: 필요한 데이터만 로딩
- **성능 향상**: 복잡한 조인에서도 안정적인 성능

### 3) **동적 필터링 시스템** (`ProjectFilterPredicate`)

```java
private BooleanExpression[] buildFilterPredicates(FilteringProjectRequest request) {
	return new BooleanExpression[] {
		ProjectFilterPredicate.keywordContains(request.keyword()), // title만 검색
		ProjectFilterPredicate.topicIdEq(request.topicId()),
		ProjectFilterPredicate.analysisPurposeIdEq(request.analysisPurposeId()),
		ProjectFilterPredicate.dataSourceIdEq(request.dataSourceId()),
		ProjectFilterPredicate.authorLevelIdEq(request.authorLevelId()),
		ProjectFilterPredicate.notDeleted()
	};
}

// DataFilterPredicate는 title과 description 모두 검색
private BooleanExpression[] buildFilterPredicates(FilteringDataRequest request) {
	return new BooleanExpression[] {
		DataFilterPredicate.keywordContains(request.keyword()), // title + description 검색
		DataFilterPredicate.topicIdEq(request.topicId()),
		DataFilterPredicate.dataSourceIdEq(request.dataSourceId()),
		DataFilterPredicate.dataTypeIdEq(request.dataTypeId()),
		DataDatePredicate.yearBetween(request.year())
	};
}
```

**효과**:

- **타입 안전성**: 컴파일 타임에 쿼리 오류 감지
- **재사용성**: 필터 조건을 모듈화하여 재사용
- **확장성**: 새로운 필터 조건 추가 시 기존 코드 영향 없음

### 4) **배치 처리 기반 집계 최적화** (`SearchDataQueryDslAdapter`)

```java
// 1단계: 메인 쿼리로 데이터 조회
List<DataEntity> dataEntities = queryFactory
	.selectFrom(data)
	.leftJoin(data.metadata).fetchJoin()
	.where(buildFilterPredicates(request))
	.orderBy(DataSortBuilder.fromSortOption(sortType, null))
	.offset(pageable.getOffset())
	.limit(pageable.getPageSize())
	.fetch();

// 2단계: 배치로 프로젝트 수 조회 (N+1 문제 해결)
List<Long> dataIds = dataEntities.stream().map(DataEntity::getId).toList();
Map<Long, Long> projectCounts = getProjectCountsBatch(dataIds);

// 3단계: DTO 조합 및 메모리 정렬
List<DataWithProjectCountDto> contents = dataEntities.stream()
	.map(entity -> new DataWithProjectCountDto(
		DataEntityMapper.toDomain(entity),
		projectCounts.getOrDefault(entity.getId(), 0L)))
	.sorted((a, b) -> {
		if (sortType == DataSortType.UTILIZE) {
			return Long.compare(b.countConnectedProjects(), a.countConnectedProjects());
		}
		return 0;
	})
	.toList();
```

**효과**:

- **N+1 문제 해결**: 배치 쿼리로 프로젝트 수 한 번에 조회
- **성능 최적화**: 메모리 정렬로 복잡한 정렬 조건 처리
- **정확성**: 배치 처리로 정확한 카운트 계산

<br/>

## 🚀 실제 성능 효과

### **Before (단순 JPA)**

- 복잡한 검색 시 N+1 문제 발생
- 페이징과 fetch join 동시 사용으로 성능 저하
- 동적 조건 추가 시 코드 복잡도 증가

### **After (QueryDSL 최적화)**

- **2단계 쿼리**로 N+1 문제 해결
- **서브쿼리**로 복잡한 집계 최적화
- **모듈화된 필터**로 유지보수성 향상

**성능 개선**: QueryDSL을 통한 복잡한 검색 쿼리 최적화 및 N+1 문제 해결

<br/>
<br/>

---

# ⚡ 08. Redis 캐싱

## 📌 적용 목적

- **Redis**: 인메모리 기반의 고성능 데이터 저장소로, 주로 캐싱, 세션, 토큰 등 저장, 카운터, 랭킹 처리 등에 활용
- **Spring Data Redis**: Redis 연동을 단순화하고 직렬화/역직렬화를 자동 처리하는 Spring 모듈 (수동 캐시 관리)

<br/>

## ⚙️ 적용 방식

1. **읽기 성능 최적화**

- **조회 빈도가 높은 데이터**(예: 인기 프로젝트 목록, 인기 데이터셋 목록)를 Redis에 캐싱
- TTL(Time-To-Live) 기반 자동 만료 적용

2. **데이터 갱신**

- **배치 기반 주기적 갱신**: 10분마다 DB에서 최신 데이터 조회 후 캐시 갱신
- **TTL 기반 자동 만료**: 10분 TTL로 자동 갱신하여 캐시 정합성 유지

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
	RedisTemplate<String, Object> template = new RedisTemplate<>();
	template.setConnectionFactory(connectionFactory);

	// Key: String, Value: JSON 직렬화
	StringRedisSerializer keySerializer = new StringRedisSerializer();
	GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

	template.setKeySerializer(keySerializer);
	template.setValueSerializer(valueSerializer);
	template.setHashKeySerializer(keySerializer);
	template.setHashValueSerializer(valueSerializer);

	return template;
}
```

**효과**:

- **타입 안전성**: JSON 직렬화로 복잡한 객체도 안전하게 저장
- **호환성**: 다른 언어/시스템과의 데이터 교환 가능
- **디버깅 용이성**: Redis CLI에서도 JSON 형태로 확인 가능

### 2) **행동 로그 세션 관리** (`BehaviorLogActionAspect`)

```java
@Before("@annotation(trackNavigation)")
public void handleTrackNavigation(JoinPoint joinPoint, TrackNavigation trackNavigation) {
	String anonymousId = MDC.get(MdcKey.ANONYMOUS_ID);
	String sessionId = MDC.get(MdcKey.SESSION_ID);
	String path = MDC.get(MdcKey.PATH);

	long now = System.currentTimeMillis();
	String redisKey = buildRedisKey(anonymousId, sessionId);
	String redisValue = redisTemplate.opsForValue().get(redisKey);

	String lastPath = null;
	Long stayTime = null;

	// 이전 경로와 머문 시간 계산
	if (redisValue != null && redisValue.contains(",")) {
		String[] parts = redisValue.split(",");
		if (parts.length >= 2) {
			lastPath = parts[0];
			try {
				long lastTime = Long.parseLong(parts[1]);
				stayTime = now - lastTime;
			} catch (NumberFormatException e) {
				log.warn("Invalid timestamp in Redis value: {}", parts[1]);
			}
		}
	}

	// 현재 경로와 시간 저장 (10분 TTL)
	redisTemplate.opsForValue().set(redisKey, path + "," + now, Duration.ofMinutes(10));

	MDC.put(MdcKey.REFERRER, lastPath);
	MDC.put(MdcKey.NEXT_PATH, path);
	if (stayTime != null) {
		MDC.put(MdcKey.STAY_TIME, String.valueOf(stayTime));
	}
}

private String buildRedisKey(String anonymousId, String sessionId) {
	return "behavior:last:" + (anonymousId != null ? anonymousId : sessionId);
}
```

**효과**:

- **사용자 행동 추적**: 페이지 간 이동 패턴과 체류 시간 분석
- **세션 관리**: 익명 사용자도 일관된 행동 추적
- **자동 만료**: TTL로 메모리 효율성 확보

### 3) **토큰 블랙리스트 관리** (`BlackListRedisAdapter`)

```java
public void setBlackListToken(String token, long expirationMillis) {
	redisTemplate.opsForValue().set(getBlackListKey(token), "logout", Duration.ofMillis(expirationMillis));
	LoggerFactory.redis().logSaveOrUpdate(token, "블랙 리스트 처리를 위한 토큰 레디스 저장에 성공했습니다.");
}

private String getBlackListKey(String token) {
	return "blacklist:" + token;
}

public boolean isBlacklisted(String token) {
	boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(getBlackListKey(token)));
	if (isBlacklisted) {
		LoggerFactory.redis().logExist(token, "블랙리스트 토큰 확인");
	}
	return isBlacklisted;
}
```

**효과**:

- **즉시 무효화**: 로그아웃 시 토큰 즉시 차단
- **보안 강화**: 탈취된 토큰의 재사용 방지
- **자동 정리**: TTL로 만료된 토큰 자동 삭제

### 4) **조회수 중복 방지** (`ProjectViewCountRedisAdapter`)

```java
private static final String VIEW_COUNT_PREFIX = "viewCount:";
private static final String VIEW_COUNT_KEY_FORMAT = "viewCount:%s:%s";
private static final Duration TTL = Duration.ofMinutes(5);

public void increaseViewCount(Long projectId, String viewerId, String targetType) {
	String dedupKey = String.format("viewDedup:%s:%s:%s", targetType, projectId, viewerId);
	Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", TTL);

	if (Boolean.TRUE.equals(wasSet)) {
		String countKey = String.format(VIEW_COUNT_KEY_FORMAT, targetType, projectId);
		redisTemplate.opsForValue().increment(countKey);
		LoggerFactory.redis().logSaveOrUpdate(
			VIEW_COUNT_PREFIX + targetType + ":" + projectId,
			"해당 프로젝트를 조회하였습니다. projectId=" + projectId);
	}
}
```

**효과**:

- **중복 방지**: 5분 내 동일 사용자 조회 1회만 카운트
- **성능 최적화**: Redis의 원자적 연산 활용
- **데이터 정확성**: 조회수 왜곡 방지

### 5) **인기 데이터 캐싱** (실무 검증 전략)

#### **A. 캐시 대상 및 전략**

```java
// 1) 인기 프로젝트 목록 캐싱
@Component
public class PopularProjectsRedisAdapter {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String POPULAR_PROJECTS_KEY = "popular:projects";
    private static final String POPULAR_PROJECTS_METADATA_KEY = "popular:projects:metadata";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10); // 10분 TTL

    public Optional<List<PopularProjectResponse>> getPopularProjects() {
        String cachedData = redisTemplate.opsForValue().get(POPULAR_PROJECTS_KEY);
        if (cachedData == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.readValue(cachedData,
            new TypeReference<List<PopularProjectResponse>>() {}));
    }

    public void setPopularProjects(List<PopularProjectResponse> popularProjects) {
        String jsonData = objectMapper.writeValueAsString(popularProjects);
        redisTemplate.opsForValue().set(POPULAR_PROJECTS_KEY, jsonData, CACHE_TTL);

        // 메타데이터도 함께 저장 (마지막 업데이트 시간)
        String metadata = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set(POPULAR_PROJECTS_METADATA_KEY, metadata, CACHE_TTL);
    }
}

// 2) 인기 데이터셋 목록 캐싱
@Component
public class PopularDataSetsRedisAdapter {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String POPULAR_DATASETS_KEY = "popular:datasets";
    private static final String POPULAR_DATASETS_METADATA_KEY = "popular:datasets:metadata";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10); // 10분 TTL

    public Optional<List<PopularDataResponse>> getPopularDataSets() {
        String cachedData = redisTemplate.opsForValue().get(POPULAR_DATASETS_KEY);
        if (cachedData == null) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.readValue(cachedData,
            new TypeReference<List<PopularDataResponse>>() {}));
    }

    public void setPopularDataSets(List<PopularDataResponse> popularDataSets) {
        String jsonData = objectMapper.writeValueAsString(popularDataSets);
        redisTemplate.opsForValue().set(POPULAR_DATASETS_KEY, jsonData, CACHE_TTL);

        // 메타데이터도 함께 저장 (마지막 업데이트 시간)
        String metadata = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set(POPULAR_DATASETS_METADATA_KEY, metadata, CACHE_TTL);
    }
}
```

#### **B. 실제 DTO 설계**

```java
// 인기 프로젝트 응답 DTO
public record PopularProjectResponse(
    Long id,
    String title,
    String content,
    Long creatorId,
    String creatorName,
    String userProfileImageUrl,
    String projectThumbnailUrl,
    String topicLabel,
    String analysisPurposeLabel,
    String dataSourceLabel,
    String authorLevelLabel,
    Long commentCount,
    Long likeCount,
    Long viewCount
) {}

// 인기 데이터셋 응답 DTO
public record PopularDataResponse(
    Long id,
    String title,
    Long creatorId,
    String creatorName,
    String userProfileImageUrl,
    String topicLabel,
    String dataSourceLabel,
    String dataTypeLabel,
    LocalDate startDate,
    LocalDate endDate,
    String description,
    String dataThumbnailUrl,
    Integer downloadCount,
    Long sizeBytes,
    Integer rowCount,
    Integer columnCount,
    LocalDateTime createdAt,
    Long countConnectedProjects
) {}
```

#### **C. Redis 설정**

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key: String, Value: JSON 직렬화
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
```

### 6) **배치 기반 주기적 갱신** (실무 최적화)

#### **A. 배치 서비스 구현**

```java
// 1) 인기 프로젝트 배치 갱신
@Service
@RequiredArgsConstructor
public class PopularProjectsBatchService {
    private final PopularProjectsRedisAdapter popularProjectsRedisAdapter;
    private final ReadProjectQueryDslAdapter readProjectQueryDslAdapter;
    private final PopularProjectDtoMapper popularProjectDtoMapper;

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void updatePopularProjects() {
        // DB에서 최신 인기 프로젝트 조회
        List<Project> popularProjects = getPopularProjectsPort.getPopularProjects(20);
        List<PopularProjectResponse> responses = popularProjects.stream()
            .map(project -> popularProjectDtoMapper.toResponseDto(project, ...))
            .toList();

        // Redis 캐시 갱신
        popularProjectsStoragePort.setPopularProjects(responses);
    }
}

// 2) 인기 데이터셋 배치 갱신
@Service
@RequiredArgsConstructor
public class PopularDataSetsBatchService {
    private final PopularDataSetsRedisAdapter popularDataSetsRedisAdapter;
    private final ReadDataQueryDslAdapter readDataQueryDslAdapter;
    private final DataReadDtoMapper dataReadDtoMapper;

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void updatePopularDataSets() {
        // DB에서 최신 인기 데이터셋 조회
        List<DataWithProjectCountDto> popularDataSets = getPopularDataSetsPort.getPopularDataSets(20);
        List<PopularDataResponse> responses = popularDataSets.stream()
            .map(wrapper -> dataReadDtoMapper.toResponseDto(wrapper.data(), ...))
            .toList();

        // Redis 캐시 갱신
        popularDataSetsStoragePort.setPopularDataSets(responses);
    }
}

// 3) 수동 캐시 삭제 (필요시)
public void evictPopularProjects() {
    redisTemplate.delete(POPULAR_PROJECTS_KEY);
    redisTemplate.delete(POPULAR_PROJECTS_METADATA_KEY);
}
```

#### **B. 실제 동작 시나리오**

```java
// 시나리오 1: 사용자가 인기 프로젝트 목록 조회
// 1. ProjectReadService.getPopularProjects() 호출
// 2. PopularProjectsRedisAdapter.getPopularProjects() → Redis에서 조회
// 3. 캐시 히트: 즉시 반환, 캐시 미스: DB 조회 후 캐시 웜업

// 시나리오 2: 사용자가 프로젝트에 좋아요
// 1. LikeCommandService.likeProject() 호출
// 2. DB에서 좋아요 수 증가 (ProjectCountService)
// 3. 캐시는 그대로 유지 (TTL 기반 자동 갱신)
// 4. 다음 배치 실행 시 최신 데이터로 갱신

// 시나리오 3: 5분 후 배치 갱신
// 1. @Scheduled PopularProjectsBatchService.updatePopularProjects() 실행
// 2. getPopularProjectsPort.getPopularProjects(20) → DB에서 최신 데이터 조회
// 3. popularProjectDtoMapper.toResponseDto() → DTO 변환
// 4. PopularProjectsRedisAdapter.setPopularProjects() → Redis 캐시 갱신 (10분 TTL)
```

#### **C. 배치 기반 전략의 장점**

```java
// 배치 기반 주기적 갱신의 실무적 장점
public class BatchCacheStrategy {
    // 1. 단순성: 복잡한 무효화 로직 불필요
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void updateCache() {
        // DB에서 최신 데이터 조회 후 캐시 갱신
    }

    // 2. 안정성: 무효화 실패 위험 없음
    // 3. 성능: 무효화 오버헤드 없음
    // 4. 일관성: 항상 일정한 주기로 갱신
    // 5. 예측 가능성: 배치 실행 시간 예측 가능
}
```

**효과**:

- **단순성**: 복잡한 무효화 로직 불필요로 유지보수성 향상
- **안정성**: 무효화 실패로 인한 캐시 불일치 위험 제거
- **성능**: 무효화 오버헤드 없이 순수 캐시 성능 확보
- **일관성**: 5분 주기로 일관된 데이터 갱신 보장
- **예측 가능성**: 배치 실행 시간이 예측 가능하여 모니터링 용이
- **확장성**: 트래픽 증가에도 안정적인 캐시 운영

<br/>

## 🚀 실제 성능 효과

### **Before (DB 직접 조회)**

- 매번 DB 접근으로 지연 발생
- 복잡한 조인 쿼리로 인한 성능 저하
- 트래픽 증가 시 DB 부하 급증

### **After (배치 기반 Redis 캐싱)**

- **인메모리 접근**: ms 단위 빠른 응답 시간
- **배치 갱신**: DB 부하 분산으로 안정적 성능
- **TTL 자동 만료**: 메모리 효율성 및 데이터 일관성 확보
- **단순성**: 복잡한 무효화 로직 없이 안정적 운영
- **예측 가능성**: 5분 주기 배치로 모니터링 용이

**성능 개선**: 캐시 적중 시 99% 응답 시간 단축, DB 부하 90% 감소

<br/>
<br/>

---

# ⚡ 09. Redis 기반 인증·세션 관리

## 📌 적용 목적

- 인증·인가 과정에서 **고속 검증**과 **실시간 토큰 상태 관리**를 위해 Redis 활용
- 로그아웃 시 토큰 즉시 무효화, 재발급 시 검증 등 **보안성 강화** 목표

<br/>

## ⚙️ 구현 방식

1. **리프레시 토큰 저장**

- 로그인 성공 시 `refresh:user:<userId>` 키로 Redis에 저장, TTL은 만료 시간과 동일
- 재발급 시 Redis에 저장된 토큰과 비교해 유효성 검증

2. **토큰 블랙리스트 처리**

- 로그아웃 시 Access Token을 `blacklist:<token>`에 저장, TTL은 남은 유효기간
- JWT 검증 시 블랙리스트 조회 → 존재 시 인증 거부
  -> 보안 등급이 높은 서비스에서 필요. 현재 프로젝트에서는 실무적으로 과한 기능이라 생각하여 유스케이스로 개발만 해두고 비활성화 처리 해둠.

3. **TTL 기반 자동 만료**

- Redis 자체 TTL 기능으로 만료 토큰 자동 제거
- 별도의 정리 배치 불필요

4. **고속 인증 검증**

- DB 대신 인메모리 Redis에서 상태 확인 → ms 단위 응답

<br/>

## 기대 효과

- 인증 처리 속도 단축 → 사용자 경험 개선
- 유지보수 단순화 (TTL 자동 삭제)

<br/>
<br/>

---

# 🔒 10. Redisson 기반 분산락

## 📌 적용 목적

- 다중 서버·멀티 스레드 환경에서 **동시성 충돌(Race Condition)** 방지
- 좋아요·댓글 카운트 집계, Kafka 이벤트 소비 등 **데이터 무결성이 중요한 작업** 보호

<br/>

## ⚙️ 구현 방식

1. **AOP 기반 락 적용**

- `@DistributedLock` 어노테이션으로 메서드 단위 락 제어
- SpEL 지원 → 자원 식별 키를 동적으로 생성 가능
- `waitTime`, `leaseTime`, `retry` 등 파라미터로 세밀한 제어 가능

2. **Redisson Lock Manager**

- `tryLock()` 기반으로 락 획득 시도 → 실패 시 재시도 로직 포함
- **자동 해제(leaseTime)** 설정으로 데드락 방지
- 예외 유형별(`BusinessException`, `CommonException`) 세분화 처리

3. **안전한 해제**

- `lock.isHeldByCurrentThread()` 확인 후 `unlock()` 호출
- 해제 실패 시 로깅 및 예외 처리

<br/>

## 🔄 동작 흐름

1. 서비스 메서드에 `@DistributedLock(key = "'lock:post:' + #postId")`와 같이 적용
2. AOP가 메서드 호출 전 락 키 생성 → Redisson을 통해 락 획득 시도
3. 락을 획득하면 비즈니스 로직 실행
4. 작업 완료 후 락 해제(또는 leaseTime 만료 시 자동 해제)

<br/>

## 기대 효과

- **데이터 정합성 보장**: 중복 집계·중복 소비 방지
- **확장성 확보**: 서버 인스턴스 수 증가 시에도 안전한 동시성 제어
- **운영 안정성**: 재시도·자동 해제로 데드락 위험 최소화

<br/>
<br/>

---

<br/>
<br/>

---

# 👀 11. 조회수(View Count) 처리 전략

## 🧩 문제 상황

조회수가 발생할 때마다 곧바로 **DB 업데이트**를 수행하면,

- 트래픽이 많은 경우 **DB 부하 급증**
- 단순 증가 연산이지만 매번 트랜잭션이 발생 → 성능 저하
- 순간적으로 많은 요청이 몰리면 락 경합과 지연 발생

이라는 문제가 생긴다.

즉, 조회수는 **정합성보다는 성능과 집계 효율**이 중요한 데이터이므로, **즉시 DB 반영**보다 **임시 저장 + 배치 동기화** 전략이 적합하다.

---

## ⚙️ 구현 방식

1. **Redis 캐싱 & Deduplication**

- `viewDedup:{targetType}:{projectId}:{viewerId}` 키를 사용해 **5분 TTL** 동안 동일 사용자의 중복 조회수 증가를 방지.
- 최초 조회일 때만 `viewCount:{targetType}:{projectId}` 카운트를 +1 증가.
- Redis의 빠른 쓰기 성능을 활용하여 고빈도 요청을 흡수.

2. **스케줄러 기반 워커** (`ProjectViewCountWorker`)

- `@Scheduled(fixedDelay = 20 * 1000)` 주기로 Redis의 viewCount 키를 스캔.
- 각 키의 카운트를 **원자적 pop(getDel)** 하여 가져오고,
- 값이 있으면 DB `viewCount`를 증가시키고,
- 동시에 **Projection Task** 큐에 등록 → ES에도 반영됨.
- 개별 처리 중 오류가 발생해도 로깅 후 다음 키를 계속 처리 → 장애가 전체 동작에 영향 주지 않음.

3. **DB & ES 동기화**

- DB에는 최종 집계된 viewCount 반영.
- ES에는 Projection Worker를 통해 비동기 업데이트 → 검색·추천에서 최신 데이터 활용 가능.

---

## 🚀 효과

- **DB 부하 감소**
  잦은 조회 이벤트를 Redis에서 집계 → 주기적으로 DB에 반영 → 트랜잭션 횟수 감소.

- **중복 방지**
  TTL 기반 deduplication → 동일 사용자의 짧은 시간 내 반복 조회는 카운트에 반영되지 않음 → 데이터 왜곡 방지.

- **최종적 일관성**
  Redis → DB → ES 순서로 동기화 → 약간의 지연은 있지만, DB/ES의 상태는 최종적으로 일치.

- **운영 효율성**
  워커 기반 배치 처리 → 모니터링·알림·재처리 체계와 쉽게 통합 가능.

<br/>
<br/>

---

# 📡 12. Kafka 기반 이벤트 처리 (Kafka 중심 요약)

## 📌 목적

- **비동기 분리**로 API 부하 완화
- **Key=리소스 ID** 파티셔닝으로 동일 리소스 **순서 보장**
- **재시도·DLQ**로 장애 격리 및 안정 운영

---

## ⚙️ 메시지 & 토픽 설계 (적용 사례)

- **메시지 타입**
- **도메인 이벤트**: `DataUploadEvent{ dataId, dataFileUrl, originalFilename }` (JSON)
- **경량 트리거**: `Long` (예: `projectId`, `commentId`)
- **키 정책**: 항상 `key = 리소스 ID` → 동일 ID는 동일 파티션으로 직렬 처리
- **대표 토픽**
- 업로드 이벤트: `data-uploaded`
- 좋아요 증감: `project-like-increase` / `project-like-decrease`
- 댓글 수 변경 트리거: `comment-uploaded-topic` / `comment-deleted-topic`

---

## 📨 프로듀서 (구현 기준)

- **DataKafkaProducerAdapter — 업로드 이벤트**

- Key / Value: `dataId` / `DataUploadEvent(JSON)`
- Topic: `spring.kafka.producer.extract-metadata.topic` → `data-uploaded`

- **LikeKafkaProducerAdapter — 좋아요 증감**

- Key / Value: `targetId` / `Long`
- Topic(프로젝트): `spring.kafka.producer.project-like-(increase|decrease).topic` → `project-like-*-topic`
- Topic(댓글): `spring.kafka.producer.comment-like-(increase|decrease).topic` → `comment-like-*-topic`

- **CommentKafkaProducerAdapter — 댓글 수 변경**
- Key / Value: `projectId` / `Long`
- Topic: `spring.kafka.producer.comment-(upload|delete).topic` → `comment-*-topic`

## 📥 컨슈머 (구현 기준)

- **DataKafkaConsumerAdapter.consume — 파일 첨부 및 업로드 → 메타데이터 파싱/저장**

- Payload: `DataUploadEvent`
- Topic: `spring.kafka.consumer.extract-metadata.topic` → `data-uploaded`

- **ProjectKafkaConsumerAdapter.consumeCommentUpload — 프로젝트 댓글 수 +1**

- Payload: `Long projectId`
- Topic: `spring.kafka.consumer.comment-upload.topic` → `comment-uploaded-topic`

- **ProjectKafkaConsumerAdapter.consumeCommentDelete — 프로젝트 댓글 수 -1**

- Payload: `Long projectId`
- Topic: `spring.kafka.consumer.comment-delete.topic` → `comment-deleted-topic`

- **ProjectKafkaConsumerAdapter.consumeLikeIncrease — 프로젝트 좋아요 수 +1**

- Payload: `Long projectId`
- Topic: `spring.kafka.consumer.project-like-increase.topic` → `project-like-increase-topic`

- **ProjectKafkaConsumerAdapter.consumeLikeDecrease — 프로젝트 좋아요 수 -1**
- Payload: `Long projectId`
- Topic: `spring.kafka.consumer.project-like-decrease.topic` → `project-like-decrease-topic`

> 처리 실패 시 예외 재던지기 → 컨테이너 **재시도/백오프 → DLQ** 정책 적용.

---

## 🛡️ 신뢰성 운영

- **재시도/백오프**: 일시 오류는 자동 재시도, 한계 초과 시 **DLQ**로 격리
- **멱등성 보장**: Partition + Offset 기반 중복 메시지 처리 방지
  - Redis 키 기반 중복 체크 (`kafka:idempotent:topic:partition:offset`)
  - `processIdempotently()` 공통 메서드로 모든 Consumer에 적용
  - 중복 메시지 감지 시 즉시 스킵, 처리 실패 시 Redis 키 삭제로 재시도 가능
- **수동 커밋 모드**: `AckMode.MANUAL_IMMEDIATE`로 처리 성공 후에만 offset 커밋
- **Consumer 설정 최적화**: `max.poll.records=200`, `max.poll.interval.ms` 조정으로 안정성 확보
- **모니터링 핵심 지표**: **Lag**, **DLQ 적재량**, **처리 지연/오류율**, **소비 스루풋**

---

## 🔄 대표 흐름

`Producer(키=ID로 발행) → Broker(파티션/보존/복제) → Consumer(재시도/백오프) → 실패 시 DLQ`

<br/>
<br/>

---

# 🔍 13. 검색 & 분석 (Elasticsearch)

## 🎯 적용 목적

- 프로젝트에 대한 **전문 검색**, **유사 프로젝트 추천**, **실시간 검색어 대응**을 위해 Elasticsearch를 사용합니다.
- RDB 조회는 정합성 우선(목록·필터), ES는 **유사도·가중치 랭킹/자동완성/고속 검색**을 담당합니다.

<br/>

## 🗂️ 인덱스/도큐먼트

- **인덱스**: `project_index`
- **문서**: 프로젝트 기본 메타(제목/내용/작성자 등) + 통계(댓글/좋아요/조회수) + 상태(`isDeleted`) + 시간(`createdAt`) 등

<br/>

## 🔄 색인 & 증분 업데이트

- **색인 어댑터**: 프로젝트 문서를 `project_index`에 저장/갱신.
- **증분 업데이트**: 댓글 수 변화는 **Painless 스크립트**로 증가/감소 처리
- `commentCount == null` → 초기화, 0 이하 방지
- **목표**: API 트랜잭션 이후 **준실시간 반영**으로 검색/추천 정확도 유지.

<br/>

## 🔎 검색 시나리오

### 1) 실시간 검색/자동완성

- **쿼리**: `multi_match`(fuzziness **AUTO**)로 **title^3**, **username^2** 가중치 적용하여 제목과 업로더 검색
- **필터**: `isDeleted=false`
- **정렬**: `createdAt` 내림차순
- **결과**: 가벼운 응답 모델(예: id, title, username, thumbnail)로 반환 → 목록/자동완성/실시간 검색에 즉시 사용

### 2) 유사 프로젝트 추천

- **쿼리 조합**:
- `more_like_this`(title, content)
- `term` 가중치: `topicId`(1.5), `analysisPurposeId`(1.3)
- `must_not`: 자기 자신(`id`) 제외
- `filter`: `isDeleted=false`
- **정렬**: 최신성 반영(`createdAt` desc)
- **결과**: 제목/요약/작성자/썸네일/통계 필드를 포함한 추천 리스트

---

## 🔀 조회 분리 전략

- **DB(JPA/QueryDSL)**: 단순 목록/필터, 즉시 일관성 필요한 화면
- **Elasticsearch**: 전문 검색, 유사도 추천, 가중치(score) 랭킹, 자동완성

---

## ✅ 기대 효과

- **검색 품질 향상**: 내용 기반 유사도 + 도메인 가중치로 랭킹 최적화
- **실시간성**: 색인/증분 업데이트로 최신 지표 반영
- **성능**: 대량 데이터에서도 ms~수십 ms 수준 응답

<br/>
<br/>

---

# 📝 14. 공통 로깅 (LoggerFactory 기반)

## 적용 목적

- 계층 및 모듈별 **일관된 로깅 표준** 제공
- 로그 포맷·레벨·태그를 통일해 **문제 추적과 분석** 효율화
- API, DB, Kafka, Elasticsearch, Redis, 분산락 등 다양한 영역에서 **재사용 가능**

---

## 설계 개요

- **`BaseLogger`**: 모든 로거의 기본 로깅 기능(info/debug/warn/error) 추상화
- **전문화 Logger 클래스**: API, Service, Domain, Persistence, Kafka, Elasticsearch, Redis, 분산락 등 기능별 로깅 지원
- **`LoggerFactory`**: 전역에서 각 Logger **싱글톤** 제공
  → 호출 계층에 맞는 Logger를 선택적으로 사용 가능

---

## 특징

- **계층별 로깅 분리**로 로그 필터링 및 분석 용이
- **시간·소요 시간 기록** 등 성능 측정에 필요한 부가정보 포함
- **도메인·인프라 로깅 통합 관리**로 유지보수성 향상
- 새로운 로깅 영역이 필요할 경우 **손쉬운 확장 가능**

<br/>
<br/>

---

## 🔐 15. 보안 & 인증 (OAuth2 + 자체 로그인 + JWT)

### ⚙️ 구성

- **소셜 로그인**: Google, Kakao (OAuth2)
- **자체 로그인**: 이메일/비밀번호
- 인증 성공 시 **Access Token + Refresh Token(JWT)** 발급

### 💾 세션 상태 관리

- Refresh Token → `Redis` 저장 (사용자 단위 키, TTL 설정)

### 🔍 필터 체인

- 요청마다 어세스토큰 JWT 인증 검사
- 실패 유형별 401(인증 실패) / 403(권한 부족) 구분 응답

### 🔄 재발급 플로우

- Access 만료 → Refresh 검증 → Access 재발급
- Redis 저장값 불일치/만료 시 재발급 거부

### 🛡 보안 하드닝

- 토큰 스코프·클레임 최소화 (원칙: 필요한 정보만 포함)
- **HTTPS 전제**, 헤더 기반 전달
- 비정상 시도(재발급 과다, 실패 누적) 지표화

### ✅ 효과

- 소셜·자체 로그인 **통합 관리**
- Redis 기반 **즉시 무효화**로 보안성 강화

<br/>
<br/>

---

# 🛡️ 16. AOP 기반 변경 권한 검증 (ex. DataAuthPolicy)

## 🎯 목적

데이터 **편집·삭제·복원** 시 현재 인증 사용자가 해당 데이터의 **생성자**인지 검증하여
비인가 접근을 차단하고 로깅합니다.

---

## ⚙️ 동작 방식 (적용 예시)

1. 🏷️ 메서드에 `@AuthorizationDataEdit` 적용
2. 🔍 AOP 실행:

- 👤 인증 사용자 ID 조회
- 📂 데이터 Owner ID 조회 (복원 시 삭제 데이터 포함)
- 🚫 불일치 시 **경고 로그** 기록 후 `DataException` 발생

---

## 📜 주요 규칙

- ✏️ 기본: 생성자만 편집·삭제 가능
- ♻️ `restore = true`: 생성자만 복원 가능
- 🔑 첫 번째 파라미터는 `dataId`(Long)여야 함

---

## 💻 사용 예시

```java
@AuthorizationDataEdit
public void update(Long dataId) { ... }

@AuthorizationDataEdit(restore = true)
public void restore(Long dataId) { ... }

```

<br/>
<br/>

---

# 🗂 17. 스토리지 & 메일 (S3 + SendGrid)

### 📁 파일 저장

- 이미지/첨부 → `S3` 업로드
- 업로드·삭제·조회 적용, 응답엔 접근 가능한 URL만 노출 (서버 권한 처리)

### 🔑 파일 다운로드

- 파일 다운로드는 보안성을 생각
- 일정시간동안만 다운로드를 할 수 있는 preSigned URL 링크를 제공
- 해당 시간이 지날 시 만료된 URL로 파일 다운로드 불가

### 📧 메일 발송

- `SendGrid API`로 인증·알림 메일 전송
- 템플릿화(제목/본문 변수치환)로 운영 효율성 확보

### ⚠️ 장애 대응

- 외부 I/O 실패 시 재시도 정책 적용
- 로깅 표준화 (요청ID, 수신자, 템플릿, 응답코드) → 문제 추적 용이

### ✅ 효과

- 대용량 파일/메일 전송을 애플리케이션에서 분리 → 성능·안정성 확보
- 운영상 관찰 가능성 유지

<br/>
<br/>

---

# 📊 18. Dataracy – 사용자 행동 로그 기반 관측 시스템

> Real-Time Behavioral Logging & Monitoring with Kafka, Elasticsearch, Redis, Prometheus, Grafana

## 🧩 프로젝트 개요

> **Dataracy**는 사용자 행동(클릭, 이동, 체류 시간 등)을 행동 로그 기록을 통해
> Kafka → Elasticsearch로 비동기 수집하고, Kibana 대시보드 및 Prometheus + Grafana를 통해 실시간 분석/시각화하는 고성능 로그 수집 시스템을 적용한다.

---

## 🧱 시스템 아키텍처

```plaintext
User
└── HTTP Request
	└── 🔍 [Filter] MDC 추적 ID, IP, Path 등 수집
		└── 🎯 [@TrackClick / @TrackNavigation] 사용자 행동 분류
				└── 🕓 DB/외부 API 응답시간 측정 (AOP)
					└── 📦 Kafka Producer 전송
						└── 🧵 Kafka Consumer → Elasticsearch 저장
							└── 📊 Kibana 대시보드 시각화
	└── 📡 Prometheus /actuator/prometheus 수집
		└── 📈 Grafana 실시간 메트릭 분석
```

## 🎯 목표

사용자와 시스템이 **"누가, 언제, 무엇을, 얼마나 걸려서"** 수행했는지를
행동 단위로 기록하여 **운영, 분석, 성능 모니터링, 컴플라이언스 근거 데이터**를 확보합니다.

---

## 🛠 설계

- **MDC 기반 상관관계 ID 부여**
- 요청·응답 전 구간 추적 (Controller~Repository 호출 체인 연결)
- **AOP 계층별 지연 측정**
- API, DB, 외부 I/O 호출 시간 로깅
- **비동기 처리**
- Kafka를 통한 로그 전송 → API 응답 지연 최소화
- **시각화**
- Elasticsearch 색인 + Kibana 대시보드로 사용자 행동 패턴 분석

| 필드명      | 설명                    |
| ----------- | ----------------------- |
| requestId   | 요청 ID                 |
| userId      | 사용자 ID               |
| anonymousId | 익명 사용자 식별자      |
| uri         | 요청 URI                |
| method      | HTTP 메서드             |
| durationMs  | 처리시간(ms)            |
| statusCode  | 결과 코드 (HTTP Status) |
| userAgent   | 클라이언트 User-Agent   |
| ip          | 요청 IP                 |

## 📈 효과

- **지연 핫스팟 즉시 식별** (DB / ES / 외부 API)
- **기능·성능 회귀 여부 신속 검증**
- **운영 모니터링**

<br/>
<br/>

---

## 🧪 19. 테스트 전략 & 코드 품질 관리

### 🎯 **체계적인 품질 관리 시스템**

> **목표**: 높은 코드 품질과 안정성을 보장하는 체계적인 테스트 및 품질 관리 시스템 구축

---

### 📊 **코드 품질 현황**

| 지표                      | 현재 값      | 목표      | 상태        |
| ------------------------- | ------------ | --------- | ----------- |
| **Instruction 커버리지**  | **82.5%**    | 80% 이상  | ✅ **우수** |
| **Branch 커버리지**       | **71.9%**    | 70% 이상  | ✅ **달성** |
| **Line 커버리지**         | **83.8%**    | 80% 이상  | ✅ **우수** |
| **Method 커버리지**       | **85.8%**    | 80% 이상  | ✅ **우수** |
| **Class 커버리지**        | **96.5%**    | 90% 이상  | ✅ **완벽** |
| **Complexity 커버리지**   | **80.5%**    | 80% 이상  | ✅ **달성** |
| **테스트 실행 시간**      | **1분 41초** | 3분 이하  | ✅ **우수** |
| **테스트 성공률**         | **100%**     | 99% 이상  | ✅ **완벽** |
| **SonarQube 품질 게이트** | **통과**     | 통과 필수 | ✅ **통과** |
| **컴파일러 경고**         | **0개**      | 0개       | ✅ **완벽** |

---

### 🏗️ **테스트 아키텍처**

#### **계층별 테스트 전략**

```java
// 단위 테스트 (Service Layer) - 비즈니스 로직 검증
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataCommandServiceTest {
	@InjectMocks private DataCommandService service;
	@Mock private CreateDataPort createDataPort;

	@Nested
	@DisplayName("데이터 업로드 테스트")
	class UploadDataTest {
		@Test
		@DisplayName("정상적인 데이터 업로드 성공")
		void uploadDataSuccess() {
			// given-when-then 패턴으로 명확한 테스트 구조
		}
	}
}

// 웹 테스트 (Controller Layer) - API 엔드포인트 검증
@WebMvcTest(controllers = TopicController.class)
class TopicControllerTest {
	@Autowired private MockMvc mockMvc;
	@MockBean private FindAllTopicsUseCase findAllTopicsUseCase;

	@Test
	@DisplayName("API 응답 검증: 200 OK와 JSON 구조")
	void findAllTopicsSuccess() throws Exception {
		mockMvc.perform(get("/api/v1/references/topics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.topics[0].id").value(1));
	}
}

// 통합 테스트 (Database Integration) - 실제 DB 연동 검증
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LikeServiceIntegrationTest {
	@Autowired private LikeTargetUseCase likeTargetUseCase;
	@Autowired private UserJpaRepository userJpaRepository;

	@Test
	@DisplayName("프로젝트 좋아요 → DB 저장 및 조회 검증")
	void likeProject_ShouldBeSavedAndRetrievable() {
		// 실제 데이터베이스 연동 테스트
	}
}
```

### 🔧 **품질 관리 도구 통합**

#### **JaCoCo 커버리지 분석**

- **실행 결과**: **✅ 82.5% 커버리지 달성** (목표 70% 초과 달성)
- **구체적 수치**:
- **Instruction Coverage**: 82.5% (38,023/46,099) ✅
- **Branch Coverage**: 71.9% (900/1,251) ✅
- **Line Coverage**: 83.8% (7,471/8,916) ✅
- **Method Coverage**: 85.8% (2,381/2,775) ✅
- **Class Coverage**: 96.5% (605/627) ✅
- **분석 결과**: **총 46,099개 명령어 중 38,023개 테스트 완료**
- **왜 중요한가**:
- **테스트 커버리지 측정**: 어떤 코드가 테스트되지 않았는지 정확히 파악
- **테스트 품질 평가**: 테스트가 얼마나 철저하게 작성되었는지 수치로 확인
- **품질 게이트 통과**: 70% 이상 커버리지로 코드 품질 보장
- **HTML 보고서**: `build/reports/jacoco/test/html/index.html`

#### **SonarQube 정적 분석**

- **실행 결과**: **✅ A등급 달성** - 모든 품질 기준 통과
- **구체적 수치**:
- **Coverage**: 71.9% (기준 70% 이상) ✅
- **Duplicated Lines**: 0.0% (기준 3% 이하) ✅
- **Code Smells**: 0개 (기준 0개) ✅
- **Security Vulnerabilities**: 0개 (기준 0개) ✅
- **Bugs**: 0개 (기준 0개) ✅
- **Reliability Rating**: A 등급 ✅
- **분석 결과**: **완벽한 코드 품질 상태 유지**
- **왜 중요한가**:
- **코드 품질 분석**: 버그, 취약점, 코드 냄새를 자동으로 탐지
- **중복 코드 제거**: 동일한 코드 패턴을 찾아 리팩토링 포인트 제공
- **기술 부채 관리**: 코드 복잡도, 유지보수성 지수를 통한 품질 모니터링
- **웹 대시보드**: `http://localhost:9000` (SonarQube 서버 실행 후)

#### **Checkstyle 코드 스타일 검사**

- **도구**: Checkstyle 10.12.4
- **실제 결과**: **0개 위반** - 모든 스타일 규칙 준수
- **왜 중요한가**:
- **코딩 스타일 강제**: 들여쓰기, 명명 규칙, 줄 길이 등 일관된 스타일 적용
- **문법 오류 방지**: 세미콜론 누락, 잘못된 import 등 기본적인 오류 사전 차단
- **팀 코딩 표준 준수**: 모든 개발자가 동일한 스타일 가이드라인을 따르도록 보장
- **HTML 보고서**: `build/reports/checkstyle/main.html`, `build/reports/checkstyle/test.html`

#### **🚀 우선순위 도구 (추가 권장)**

##### **1. SpotBugs** ✅

- **상태**: **완전 설정 및 실행 완료** - **0개 버그 달성**
- **목적**: 정적 분석을 통한 버그 탐지
- **왜 중요한가**:
- **정적 분석 버그 탐지**: 컴파일 타임에 발견되지 않는 런타임 버그 패턴 탐지
- **코드 품질 향상**: 캡슐화 위반, 배열 직접 노출, null 포인터, 메모리 누수, 잘못된 타입 캐스팅 등 잠재적 문제 발견
- **실무적 무해 패턴 제외**: Spring 프레임워크, Lombok, 테스트 코드의 정상적인 사용 패턴들
- **SonarQube와 차이점**: 더 세밀한 바이트코드 분석으로 특정 버그 패턴 전문 탐지
- **해결된 주요 버그 패턴**:
- **DM_CONVERT_CASE**: 로케일 문제 (toLowerCase() → toLowerCase(Locale.ENGLISH))
- **DM_DEFAULT_ENCODING**: 인코딩 문제 (getBytes() → getBytes(StandardCharsets.UTF_8))
- **CT_CONSTRUCTOR_THROW**: 생성자 예외 문제 (private 생성자로 인스턴스화 방지)
- **EI_EXPOSE_REP**: 내부 표현 노출 문제 (방어적 복사 구현)
- **UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR**: 필드 초기화 문제 (null 체크 추가)
- **HTML 보고서**: `build/reports/spotbugs/main.html`, `build/reports/spotbugs/test.html`

##### **2. Spotless (자동 포맷)** ✅

- **상태**: **완전 설정 및 실행 완료** - **코드 스타일 100% 통일**
- **실행 결과**: **✅ 모든 파일 포맷팅 완료** - 스타일 일관성 달성

**목적**: 코드 자동 포맷팅 및 스타일 통일

**왜 중요한가**:

- **팀 협업 효율성**: 코드 스타일 논의 시간 절약, 일관된 가독성으로 리뷰 시간 단축
- **IDE 독립성**: 어떤 IDE를 사용하든 동일한 코드 스타일 보장
- **자동화**: 수동 포맷팅 작업 제거로 개발 생산성 향상

**적용 파일 타입 및 규칙**:

- **Java**: Google Java Format + Import 정리 + 라이선스 헤더 자동 추가
- **XML/YAML/Gradle**: 들여쓰기 통일 + 공백 정리
- **Markdown/Properties/JSON/SQL**: 공백 정리 + 줄바꿈 통일
- **공통 규칙**: 줄 끝 공백 제거, 파일 끝 개행 문자 통일

**실제 해결된 문제들**:

- **들여쓰기 불일치**: IDE마다 다른 들여쓰기 → Google Java Format으로 통일
- **공백 문제**: 파일 끝/줄 끝 공백 → 자동 제거
- **Import 정리**: 사용하지 않는 import, 순서 불일치 → 자동 정리
- **라이선스 헤더**: 누락된 저작권 정보 → 자동 추가
- **HTML 보고서**: `build/reports/spotless/spotlessJava.html`, `build/reports/spotless/spotlessXml.html` 등

**개발자 워크플로우**:

```bash
# 코드 작성/수정 후
./gradlew spotlessApply

# 커밋 전 필수
./gradlew spotlessCheck  # 검사만 (파일 수정 안함)
./gradlew spotlessApply  # 실제 수정
git add .
git commit -m "기능 추가"
```

**통합 실행 명령어**:

```bash
# 기본 품질 관리 도구 (필수)
./gradlew jacocoTestReport               # JaCoCo 커버리지 분석
./gradlew sonar                          # SonarQube 정적 분석
./gradlew checkstyleMain checkstyleTest  # Checkstyle 코드 스타일 검사

# 우선순위 추가 도구 (권장)
./gradlew spotbugsMainFixed spotbugsTestFixed  # SpotBugs

# Spotless 자동 포맷팅 (개발 워크플로우 필수)
./gradlew spotlessApply                        # 포맷팅 적용 (파일 수정)
./gradlew spotlessCheck                        # 포맷팅 검사만 (수정 안함)
./gradlew spotlessJavaCheck spotlessXmlCheck spotlessYamlCheck spotlessGradleCheck spotlessMarkdownCheck spotlessMiscCheck  # 개별 파일 타입 검사
```

---

### 🛠️ **테스트 데이터 관리**

#### **직접 생성 패턴**

```java
// 도메인 모델 직접 생성
Data data = Data.of(
    1L, "Test Data", 1L, 1L, 1L, 1L,
    LocalDate.now(), LocalDate.now(),
    "Description", "Analysis Guide", "dataFile.csv", "thumbnail.jpg",
    0, 1024L, DataMetadata.of(1L, 10, 5, "{\"preview\": \"sample\"}"),
    LocalDateTime.now()
);
```

#### **테스트 명명 규칙**

- **클래스**: `{ClassName}Test`, `{ClassName}IntegrationTest`
- **메서드**: `{methodName}_{상황}_{예상결과}` 패턴
- **@DisplayName**: 한국어로 명확한 의도 표현

#### **AssertJ 활용**

```java
// 개선된 예외 처리 방식
Throwable ex = catchThrowableOfType(() -> service.method(), SomeException.class);
assertThat(ex).isNotNull();
assertThat(ex.getMessage()).contains("예상 메시지");
```

---

### 🔧 **자동화된 품질 검증**

#### **CI/CD 파이프라인 통합**

```bash
# 전체 품질 검증 실행
./gradlew clean build jacocoTestReport sonar

# 결과 확인
open build/reports/tests/test/index.html          # 테스트 리포트
open build/reports/jacoco/test/html/index.html    # 커버리지 리포트
```

#### **품질 게이트 자동 통과**

- **커버리지 기준**: 70% 이상 자동 검증
- **코드 냄새**: 0개 유지
- **보안 취약점**: 0개 유지
- **중복 코드**: 3% 이하 유지

---

### ✅ **실무적 성과**

#### **개발 생산성**

- **테스트 실행 시간**: 1분 41초
- **컴파일 시간**: 약 30초
- **품질 검증 시간**: 약 2분 (전체 프로세스)

#### **코드 품질**

- **커버리지**: 82.5% 달성
- **코드 냄새**: 0개
- **컴파일러 경고**: 0개
- **테스트 안정성**: 100% 성공률

#### **유지보수성**

- **직접 생성 패턴**: 도메인 모델의 of() 메서드 활용
- **AssertJ 체이닝**: 테스트 가독성 향상
- **@Nested 구조**: 테스트 그룹화로 관리 효율성 증대

---

### 🎯 **향후 개선 계획**

1. **커버리지 목표**: Branch Coverage 75% 달성
2. **코드 품질**: SonarQube 규칙 강화
3. **보안 검사**: 정기적인 라이브러리 업데이트 및 취약점 모니터링
4. **테스트 확장**: 더 많은 시나리오 추가

<br/>
<br/>

---

## 📊 20. 모니터링 & 알림 (Micrometer + Prometheus + Grafana)

### 📡 수집

- `Micrometer`로 요청 지연, 에러율, 스레드풀, GC, 큐 길이 등 표준 메트릭 노출

### 💾 저장 & 시각화

- `Prometheus` 주기 스크랩 → `Grafana` 대시보드로 배포 전/후 비교 뷰, SLO 패널 제공

### 🔔 알림

- 임계치 초과 시 Slack/메일 등 실시간 통지
  (예: p95 지연, 5xx 비율, Kafka Lag, DLQ 적재량, 색인 지연)
- 배포 직후 **엄격 임계치 윈도우(5~10분)** 운영

### 🛠 운영 포인트

- **블루/그린 컬러·릴리스ID·요청ID**를 메트릭 태그에 포함 → 상관관계 분석
- Kafka/ES/Redis 지표를 앱 지표와 **동일 화면에서 교차 확인**

### ✅ 효과

- 이상 징후를 **신속 감지**
- 지표의 **스토리(원인→영향)**를 한눈에 추적

<br/>
<br/>
---

# 🏆 **프로젝트 최종 성과 & 기술적 우수성**

## 📊 **실제 달성 성과 요약**

### **코드 품질 지표**

- **Instruction Coverage**: 82.5% (목표 70% 초과 달성)
- **Branch Coverage**: 71.9% (목표 70% 달성)
- **Method Coverage**: 85.8% (메서드 테스트 완성도 우수)
- **Class Coverage**: 96.5% (클래스 테스트 완성도 완벽)
- **테스트 성공률**: 100% (완벽 달성)
- **코드 냄새**: 0개 (완벽한 코드 품질 유지)
- **보안 취약점**: 0개 (보안성 확보)
- **컴파일러 경고**: 0개 (깔끔한 코드베이스 구축)

## 🔧 **품질 관리 도구 통합**

### **1. 품질 게이트 설정**

- **커버리지**: 70% 이상 (현재 82.5% 달성)
- **코드 냄새**: 0개
- **보안 취약점**: 0개
- **중복 코드**: 최소화

### **2. 리포트 생성**

- **테스트 리포트**: `build/reports/tests/test/index.html`
- **커버리지 리포트**: `build/reports/jacoco/test/html/index.html`
- **SonarQube 대시보드**: 자동 업데이트

### **3. 자동화된 품질 검증**

```bash
# 전체 품질 검증 실행
./gradlew clean build jacocoTestReport sonar
```

## 🚀 **핵심 기술적 성과**

### **아키텍처 & 설계**

- **DDD + 헥사고날 아키텍처**: 도메인 중심 설계로 비즈니스 로직 명확화
- **CQRS 패턴**: 명령과 조회 분리로 성능 최적화
- **Port & Adapter**: 인프라와 도메인 분리로 유지보수성 향상

### **성능 & 확장성**

- **Kafka 기반 이벤트 처리**: 비동기 분리로 API 부하 완화, 멱등성 보장으로 중복 메시지 처리 방지
- **Redis 캐싱**: 인메모리 캐싱으로 조회 성능 향상
- **Elasticsearch 검색**: 전문 검색과 유사도 추천으로 사용자 경험 향상
- **QueryDSL 최적화**: N+1 문제 해결 및 복잡한 쿼리 최적화
- **Worker 병렬 처리**: 제한된 병렬 처리로 ES 부하 제어 및 성능 향상

### **운영 & 안정성**

- **Blue-Green 배포**: 무중단 배포로 서비스 연속성 확보
- **분산락**: Redisson 기반 동시성 제어로 데이터 정합성 보장
- **멱등성 보장**: Kafka Consumer에서 Partition + Offset 기반 중복 메시지 처리 방지
- **Worker 안정성**: 분산 락과 제한된 병렬 처리로 다중 인스턴스 환경에서 안정적 동작
- **모니터링**: Prometheus + Grafana로 실시간 시스템 상태 추적
- **Gradle 9.0 호환성**: 최신 빌드 도구 지원으로 미래 지향적 개발 환경 구축

<br/>
<br/>

---

# 📚 21. 추가 문서

## 📋 **상세 문서 링크**

### **🔗 API 문서**

- **[API 문서 인덱스](./docs/api/README.md)** - API 개요, Base URL, 인증 방법
- **[API 종합 문서](./docs/api/API_DOCUMENTATION.md)** - 모든 API 엔드포인트 상세 설명
- **[인증 API](./docs/api/authentication.md)** - JWT, OAuth2, 토큰 관리
- **[사용자 API](./docs/api/user.md)** - 회원가입, 프로필, 비밀번호 관리
- **[프로젝트 API](./docs/api/project.md)** - CRUD, 검색, 좋아요, 이어가기
- **[데이터셋 API](./docs/api/dataset.md)** - 업로드, 다운로드, 메타데이터
- **[댓글 API](./docs/api/comment.md)** - CRUD, 좋아요
- **[파일 API](./docs/api/file.md)** - 파일 업로드, 다운로드
- **[이메일 API](./docs/api/email.md)** - 이메일 인증, 발송

### **🛠️ 개발 문서**

- **[개발 가이드](./docs/development/README.md)** - 개발 환경 설정, 프로젝트 구조
- **[시스템 아키텍처](./docs/development/architecture.md)** - DDD, 헥사고날, CQRS 상세
- **[코딩 표준](./docs/development/coding-standards.md)** - 네이밍, 스타일, 컨벤션
- **[개발 환경 설정](./docs/development/setup.md)** - 로컬 개발 환경 구성
- **[개발 워크플로우](./docs/development/workflow.md)** - Git, PR, 배포 프로세스

### **🚀 배포 문서**

- **[배포 가이드](./docs/deployment/README.md)** - Blue-Green 배포, Docker, 환경 설정
- **[환경 구성](./docs/deployment/README.md#환경-구성)** - 로컬/개발/운영 환경
- **[모니터링](./docs/deployment/README.md#모니터링)** - 헬스체크, 메트릭, 로그
- **[롤백 전략](./docs/deployment/README.md#롤백-전략)** - 자동/수동 롤백 방법

### **🧪 테스트 문서**

- **[테스트 가이드](./docs/testing/README.md)** - 테스트 전략, 도구, 실행 방법
- **[단위 테스트](./docs/testing/unit-testing.md)** - JUnit 5, Mockito, AssertJ
- **[통합 테스트](./docs/testing/integration-testing.md)** - Spring Boot Test, TestContainers
- **[테스트 커버리지](./docs/testing/coverage.md)** - JaCoCo, 70% 기준

### **🔍 코드 품질 도구**

- **[품질 도구 가이드](./docs/quality/README.md)** - 모든 품질 도구 종합 가이드
- **[JaCoCo 커버리지](./docs/quality/jacoco.md)** - 테스트 커버리지 측정 및 분석
- **[SonarQube 분석](./docs/quality/sonarqube.md)** - 종합적인 코드 품질 분석
- **[Checkstyle 검사](./docs/quality/checkstyle.md)** - 코딩 표준 및 스타일 검사
- **[Spotless 포맷팅](./docs/quality/spotless.md)** - 자동 코드 포맷팅 및 스타일 통일
- **[SpotBugs 검출](./docs/quality/spotbugs.md)** - 잠재적 버그 패턴 검출

### **🔧 문제 해결**

- **[문제 해결 가이드](./docs/troubleshooting/TROUBLESHOOTING.md)** - 빌드, 테스트, 연결 문제 해결
- **[빌드 문제](./docs/troubleshooting/TROUBLESHOOTING.md#빌드-및-컴파일-문제)** - Gradle, QueryDSL, Lombok 오류
- **[테스트 문제](./docs/troubleshooting/TROUBLESHOOTING.md#테스트-실행-문제)** - 테스트 실패, 커버리지 문제
- **[연결 문제](./docs/troubleshooting/TROUBLESHOOTING.md#데이터베이스-연결-문제)** - DB, Kafka, Redis, ES 연결
- **[성능 문제](./docs/troubleshooting/TROUBLESHOOTING.md#성능-문제)** - 메모리, 응답 시간 최적화

### **📊 성능 테스트**

- **[성능 테스트 시나리오](./performance-test/)** - k6 기반 실제 성능 테스트
- **[로그인 성능 테스트](./performance-test/auth/scenarios/login.test.js)** - 로그인 API 성능 측정
- **[로그인 남용 테스트](./performance-test/auth/scenarios/login-abuse.test.js)** - 보안 취약점 테스트
- **[트러블슈팅 가이드](./performance-test/auth/troubleshooting/)** - 성능 문제 해결 방법

---

## 🎯 **문서 활용 가이드**

### **새로운 개발자라면?**

1. [개발 환경 설정](./docs/development/setup.md)부터 시작
2. [시스템 아키텍처](./docs/development/architecture.md) 파악
3. [API 문서](./docs/api/README.md) 참고하여 개발

### **API를 사용하려면?**

1. [API 문서 인덱스](./docs/api/README.md)에서 필요한 API 찾기
2. [인증 API](./docs/api/authentication.md)부터 확인
3. Swagger UI에서 실제 API 테스트

### **문제가 발생했다면?**

1. [문제 해결 가이드](./docs/troubleshooting/TROUBLESHOOTING.md) 확인
2. 해당 모듈별 문서 참고
3. 개발팀에 문의

### **코드 품질을 관리하려면?**

1. [품질 도구 가이드](./docs/quality/README.md) 참고
2. [코딩 표준](./docs/development/coding-standards.md) 준수
3. 정기적인 품질 검사 실행

---

## 📞 **지원 및 연락처**

- **이메일**: jh981109@gmail.com
- **번호**: 010-5485-1325
