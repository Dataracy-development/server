# 🏗️ 시스템 아키텍처

## 📋 **개요**

Dataracy 백엔드는 DDD(Domain-Driven Design)와 헥사고날 아키텍처를 기반으로 설계된 Spring Boot 애플리케이션입니다.

---

## 🎯 **아키텍처 원칙**

### **DDD (Domain-Driven Design)**

- **도메인 중심**: 비즈니스 로직을 도메인 계층에 집중
- **언어 통일**: 도메인 전문가와 개발자 간 공통 언어 사용
- **경계 컨텍스트**: 각 모듈별 명확한 경계 설정

### **헥사고날 아키텍처**

- **포트와 어댑터**: 인프라와 도메인 분리
- **의존성 역전**: 도메인이 인프라에 의존하지 않음
- **테스트 용이성**: 외부 의존성 모킹 가능

### **CQRS 패턴**

- **명령과 조회 분리**: Command와 Query 분리
- **성능 최적화**: 조회 전용 모델 사용
- **확장성**: 각각 독립적 확장 가능

---

## 🏛️ **도메인 설계**

### **핵심 도메인 모델**

#### **User (사용자)**

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {
    private Long id;
    private ProviderType provider;        // LOCAL, KAKAO, GOOGLE
    private String providerId;
    private RoleType role;               // ROLE_USER, ROLE_ADMIN
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
    private String introductionText;
    private boolean isAdTermsAgreed;

    // 타 어그리거트는 ID로 간접 참조
    private Long authorLevelId;
    private Long occupationId;
    private List<Long> topicIds;
    private Long visitSourceId;

    private boolean isDeleted;

    // 도메인 로직
    public boolean isPasswordMatch(PasswordEncoder encoder, String rawPassword);
    public void validatePasswordChangable();
    public UserInfo toUserInfo();
    public static User of(/* 16개 파라미터 */);
}
```

#### **Project (프로젝트)**

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project {
    private Long id;
    private String title;
    private Long userId;

    // 참조 데이터 ID들
    private Long analysisPurposeId;
    private Long dataSourceId;
    private Long authorLevelId;
    private Long topicId;

    // 이어가기 기능
    private Boolean isContinue;
    private Long parentProjectId;
    private String content;
    private String thumbnailUrl;

    // 타 어그리거트인 Data는 ID로 간접 참조
    private List<Long> dataIds;

    // 통계 정보
    private Long commentCount;
    private Long likeCount;
    private Long viewCount;

    private List<Project> childProjects;
    private Boolean isDeleted;
    private LocalDateTime createdAt;

    // 방어적 복사
    public List<Long> getDataIds();
    public List<Project> getChildProjects();

    // 도메인 로직
    public void updateThumbnailUrl(String thumbnailUrl);
}
```

#### **Data (데이터셋)**

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Data {
    private Long id;
    private String title;
    private Long userId;
    private Long dataSourceId;
    private Long dataTypeId;
    private Long topicId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String analysisGuide;
    private String dataFileUrl;
    private String dataThumbnailUrl;
    private int downloadCount;
    private Long sizeBytes;
    private DataMetadata metadata;
    private LocalDateTime createdAt;

    // 팩토리 메서드
    public static Data of(/* 15개 파라미터 */);
}
```

#### **BehaviorLog (행동 로그)**

```java
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BehaviorLog {
    // 사용자 정보
    private String userId;
    private String anonymousId;

    // 요청 정보
    private String path;
    private HttpMethod httpMethod;
    private String ip;
    private String requestId;
    private String sessionId;
    private String userAgent;
    private String referrer;
    private String nextPath;

    // 행동 정보
    private ActionType action;
    private Long stayTime;

    // 시스템 처리 지표
    private long responseTime;
    private long dbLatency;
    private long externalLatency;

    // 디바이스 정보
    private DeviceType deviceType;
    private String os;
    private String browser;

    // 로그 타입 + 시간
    private LogType logType;
    private String timestamp;

    // 도메인 로직
    public boolean isValid();
    public BehaviorLog withTimestampIfNull();
}
```

---

## 🏢 **모듈 구조**

### **도메인별 모듈**

```
modules/
├── auth/ (60개 파일)        # 인증/인가 도메인
│   ├── domain/             # 도메인 계층
│   │   ├── model/         # JWT, OAuth2 모델
│   │   ├── enums/         # 인증 관련 열거형
│   │   └── exception/     # 인증 예외
│   ├── application/       # 애플리케이션 계층
│   │   ├── port/in/       # 인바운드 포트
│   │   ├── port/out/      # 아웃바운드 포트
│   │   └── service/       # 인증 서비스
│   └── adapter/           # 어댑터 계층
│       ├── web/           # REST API
│       └── persistence/   # JWT, Redis 어댑터
├── user/ (89개 파일)        # 사용자 도메인
├── project/ (158개 파일)    # 프로젝트 도메인
├── dataset/ (149개 파일)    # 데이터셋 도메인
├── comment/ (60개 파일)     # 댓글 도메인
├── like/ (26개 파일)        # 좋아요 도메인
├── behaviorlog/ (23개 파일) # 행동 로그 도메인
├── filestorage/ (18개 파일) # 파일 저장소 도메인
├── reference/ (136개 파일)  # 참조 데이터 도메인
├── email/ (29개 파일)       # 이메일 도메인
├── security/ (10개 파일)    # 보안 도메인
└── common/ (68개 파일)      # 공통 모듈
    ├── config/            # 공통 설정
    ├── exception/         # 공통 예외
    ├── logging/           # 로깅 지원
    └── support/           # 유틸리티
```

### **계층별 구조**

```
각 모듈/
├── domain/                 # 도메인 계층 (핵심)
│   ├── model/             # 도메인 모델 (Entity, VO)
│   ├── enums/             # 도메인 열거형
│   ├── exception/         # 도메인 예외
│   └── status/            # 도메인 상태 코드
├── application/           # 애플리케이션 계층 (유스케이스)
│   ├── port/
│   │   ├── in/           # 인바운드 포트 (인터페이스)
│   │   └── out/          # 아웃바운드 포트 (인터페이스)
│   ├── service/          # 애플리케이션 서비스 (구현체)
│   └── dto/              # 데이터 전송 객체
└── adapter/              # 어댑터 계층 (인프라)
    ├── persistence/      # 데이터 영속성 (JPA, QueryDSL)
    ├── query/           # 쿼리 어댑터 (복잡한 조회)
    └── web/             # 웹 어댑터 (REST API)
```

---

## 🔄 **데이터 플로우**

### **프로젝트 업로드 플로우**

```
1. 클라이언트 요청
   ↓
2. Web Adapter (REST Controller)
   ↓
3. Application Service (유스케이스)
   ↓
4. Domain Service (비즈니스 로직)
   ↓
5. Port Out (인터페이스)
   ↓
6. Persistence Adapter (JPA Repository)
   ↓
7. Database (MySQL)
   ↓
8. File Storage Adapter (S3)
   ↓
9. Search Adapter (Elasticsearch)
   ↓
10. Event Publisher (Kafka)
    ↓
11. Response
```

### **이벤트 처리 플로우**

```
1. 도메인 이벤트 발생
   ↓
2. Event Publisher (Kafka)
   ↓
3. Event Consumer (Kafka Listener)
   ↓
4. Application Service
   ↓
5. Persistence Adapter
   ↓
6. Database Update
   ↓
7. Search Index Update (Elasticsearch)
   ↓
8. Cache Update (Redis)
```

---

## 🛠️ **기술 스택**

### **백엔드 프레임워크**

- **Spring Boot**: 3.2.5
- **Spring Security**: OAuth2, JWT
- **Spring Data JPA**: 데이터 영속성
- **Spring Kafka**: 이벤트 처리
- **Spring AOP**: 관점 지향 프로그래밍

### **데이터베이스**

- **MySQL**: 8.0.33 (주 데이터베이스)
- **Redis**: 7.0 (캐시, 세션)
- **Elasticsearch**: 8.13.4 (검색)
- **H2**: 테스트용

### **메시징**

- **Apache Kafka**: 3.5 (이벤트 스트리밍)
- **Spring Kafka**: Kafka 통합

### **파일 저장소**

- **AWS S3**: 1.12.787 (파일 저장)
- **PreSigned URL**: 직접 업로드/다운로드

### **모니터링**

- **Spring Actuator**: 헬스체크, 메트릭
- **Prometheus**: 메트릭 수집
- **Micrometer**: 메트릭 수집 라이브러리

---

## 🔒 **보안 아키텍처**

### **인증 계층**

```
┌─────────────────────────────────────────────────────────────┐
│                    Security Layer                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   OAuth2    │  │     JWT     │  │   Session   │        │
│  │  (Social)   │  │   (Token)   │  │  (Redis)    │        │
│  │             │  │             │  │             │        │
│  │ - Kakao     │  │ - Access    │  │ - Refresh   │        │
│  │ - Google    │  │ - Refresh   │  │ - Blacklist │        │
│  │ - Local     │  │ - Expiry    │  │ - Rate Limit│        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### **보안 정책**

- **JWT 토큰**: 액세스 토큰 1시간, 리프레시 토큰 14일
- **쿠키 기반**: HttpOnly, Secure 쿠키 사용
- **Rate Limiting**: Redis 기반 요청 제한
- **CORS**: 프론트엔드 도메인만 허용
- **CSRF**: Spring Security CSRF 보호

---

## ⚡ **성능 아키텍처**

### **캐싱 전략**

```
┌─────────────────────────────────────────────────────────────┐
│                    Caching Strategy                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   L1 Cache  │  │   L2 Cache  │  │   L3 Cache  │        │
│  │  (JVM Heap) │  │   (Redis)   │  │   (CDN)     │        │
│  │             │  │             │  │             │        │
│  │ - Entity    │  │ - Session   │  │ - Static    │        │
│  │ - Query     │  │ - API       │  │ - Image     │        │
│  │ - Result    │  │ - Search    │  │ - CSS/JS    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### **비동기 처리**

- **Kafka**: 이벤트 기반 비동기 처리
- **CompletableFuture**: 비동기 프로그래밍
- **@Scheduled**: 배치 처리

### **데이터베이스 최적화**

- **연결 풀**: HikariCP 최적화
- **인덱스**: 복합 인덱스, 부분 인덱스
- **쿼리 최적화**: QueryDSL, 네이티브 쿼리
- **N+1 문제 해결**: 2단계 쿼리, 배치 처리

---

## 📈 **확장성 고려사항**

### **수평 확장**

- **로드 밸런싱**: Nginx 기반
- **세션 클러스터링**: Redis 기반
- **데이터베이스 샤딩**: 사용자 ID 기반
- **캐시 클러스터**: Redis Cluster

### **마이크로서비스 준비**

- **도메인 분리**: 모듈별 독립성
- **API 게이트웨이**: 통합 API 관리
- **서비스 메시**: Istio 준비
- **컨테이너 오케스트레이션**: Kubernetes 준비

---

## 🔧 **모니터링 및 관찰성**

### **모니터링 스택**

- **애플리케이션**: Spring Actuator
- **메트릭**: Prometheus + Micrometer
- **분산 추적**: Zipkin (준비)

### **핵심 메트릭**

- **애플리케이션**: 응답 시간, 처리량, 에러율
- **인프라**: CPU, 메모리, 디스크, 네트워크
- **비즈니스**: 사용자 수, 프로젝트 수, 검색 수
- **보안**: 로그인 시도, API 호출, 에러 로그

---

## 🚀 **배포 아키텍처**

### **Blue-Green 배포**

```
┌─────────────────────────────────────────────────────────────┐
│                    Blue-Green Deployment                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐              ┌─────────────┐              │
│  │   Nginx     │              │   Backend   │              │
│  │   Proxy     │◄─────────────┤   (Blue)    │              │
│  │             │              │   :8081     │              │
│  └─────────────┘              └─────────────┘              │
│         │                                                   │
│         └─────────────────────┐                            │
│                               │                            │
│  ┌─────────────┐              │              ┌─────────────┐│
│  │   Backend   │              │              │   Backend   ││
│  │  (Green)    │◄─────────────┘              │  (Standby)  ││
│  │   :8082     │                             │   :8083     ││
│  └─────────────┘                             └─────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### **CI/CD 파이프라인**

```
1. 코드 푸시 (GitHub)
   ↓
2. GitHub Actions (CI)
   ↓
3. 테스트 실행 (JUnit, Integration)
   ↓
4. 코드 품질 검사 (Checkstyle, JaCoCo)
   ↓
5. Docker 이미지 빌드
   ↓
6. 이미지 푸시 (Docker Hub)
   ↓
7. Blue-Green 배포
   ↓
8. 헬스체크 및 트래픽 전환
   ↓
9. 이전 버전 정리
```

---

## 🔄 **이벤트 아키텍처**

### **이벤트 스트리밍**

```
┌─────────────────────────────────────────────────────────────┐
│                    Event Streaming                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Producer  │  │    Kafka    │  │  Consumer   │        │
│  │             │  │             │  │             │        │
│  │ - Project   │  │ - Topics    │  │ - Search    │        │
│  │ - Data      │  │ - Partitions│  │ - Analytics │        │
│  │ - Comment   │  │ - Replicas  │  │ - Cache     │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### **주요 이벤트**

- **프로젝트 생성**: `project-created`
- **데이터셋 업로드**: `dataset-uploaded`
- **댓글 작성**: `comment-created`
- **좋아요**: `like-toggled`
- **행동 로그**: `behavior-logged`

---

## 📊 **데이터 아키텍처**

### **데이터 저장 전략**

- **MySQL**: 트랜잭션 데이터, 관계형 데이터
- **Redis**: 캐시, 세션, 실시간 데이터
- **Elasticsearch**: 검색, 분석, 로그
- **S3**: 파일, 이미지, 대용량 데이터

### **데이터 일관성**

- **ACID**: MySQL 트랜잭션
- **이벤트 소싱**: 상태 변경 이벤트
- **분산락**: Redisson 기반 동시성 제어
- **보상 트랜잭션**: Saga 패턴

---

## 🔧 **개발 도구**

### **빌드 도구**

- **Gradle**: 8.14.2
- **Java**: 17
- **QueryDSL**: 타입 안전 쿼리

### **코드 품질**

- **Checkstyle**: 코드 스타일
- **Spotless**: 자동 포맷팅
- **JaCoCo**: 커버리지 (70% 기준)
- **SpotBugs**: 정적 분석

### **테스트**

- **JUnit 5**: 단위 테스트
- **Mockito**: 모킹
- **H2 Database**: 테스트용 인메모리 DB
- **AssertJ**: 플루언트 어설션

---

## 🚀 **미래 계획**

### **단기 계획 (3개월)**

- **성능 최적화**: 쿼리 최적화, 캐싱 강화
- **모니터링 강화**: APM 도입, 알림 시스템
- **보안 강화**: WAF 도입, 보안 스캔 자동화

### **중기 계획 (6개월)**

- **마이크로서비스**: 도메인별 서비스 분리
- **클라우드 마이그레이션**: AWS/GCP 이전
- **CI/CD 고도화**: GitOps, 자동 배포

### **장기 계획 (1년)**

- **AI/ML 통합**: 추천 시스템, 자동 태깅
- **글로벌 확장**: CDN, 다국가 지원
- **플랫폼화**: API 마켓플레이스, 서드파티 연동

---

**💡 아키텍처 관련 문의사항은 개발팀에 연락해주세요!**
