# 도메인 설계 문서 (DDD + 헥사고날 + 이벤트 드리븐)

## 📋 도메인 분리 전략

### 1. 도메인 식별 (Bounded Context)

과제 요구사항의 데이터 모델을 기반으로 다음과 같이 **4개의 핵심 도메인**으로 분리:

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ MeetingRoom     │────▶│ Reservation     │────▶│  Payment Domain │     │  User Domain    │
│   Domain        │     │   Domain        │     │   (결제)         │     │   (사용자)       │
│   (회의실)       │     │   (예약)         │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
```

#### 1.1 MeetingRoom Domain (회의실 도메인)

**책임:**

- 회의실 정보 관리 (이름, 수용 인원, 시간당 요금)
- 회의실 목록 조회
- 회의실 상태 관리 (사용 가능/불가능)

**핵심 엔티티:**

- `MeetingRoom` (Aggregate Root)
  - id, name, capacity, hourlyRate (시간당 요금), status

**이벤트:**

- `RoomCreatedEvent`
- `RoomUpdatedEvent`
- `RoomDeletedEvent`

---

#### 1.2 Reservation Domain (예약 도메인) ⭐ **핵심 도메인**

**책임:**

- 예약 생성/조회/취소 (CRUD)
- 예약 시간 중복 방지 (동일 회의실)
- 예약 시간 검증 (시작 시간 < 종료 시간, 정시/30분 단위)
- 동시성 제어 (Deadlock 방지)
- 예약 상태 관리 (PENDING, CONFIRMED, CANCELLED)
- 결제 상태 관리 (결제 대기, 결제 완료, 결제 실패)
- 총 결제 금액 계산 및 관리

**핵심 엔티티:**

- `Reservation` (Aggregate Root)
  - id, meetingRoomId, userId, startTime, endTime,
    paymentStatus (결제상태: PENDING, COMPLETED, FAILED),
    totalAmount (총 결제금액),
    status (예약상태: PENDING, CONFIRMED, CANCELLED),
    createdAt, updatedAt
  - **비즈니스 규칙:**
    - 시간 중복 검증 (동일 회의실)
    - 시간 단위 검증 (00분 또는 30분)
    - 시작 < 종료 시간 검증
    - 총 결제금액 = (종료시간 - 시작시간) × 시간당 요금

**이벤트:**

- `ReservationCreatedEvent` → Payment 도메인으로 전송
- `ReservationConfirmedEvent` (결제 완료 후)
- `ReservationCancelledEvent` (결제 실패 또는 사용자 취소)
- `ReservationTimeConflictDetectedEvent` (중복 감지 시)

**도메인 서비스:**

- `ReservationValidationService`: 시간 중복 검증, 시간 단위 검증
- `ReservationLockService`: 동시성 제어 (Optimistic Lock 또는 DB Lock)

---

#### 1.3 Payment Domain (결제 도메인)

**책임:**

- 결제 처리 (다중 결제사 통합) - Strategy Pattern
- 결제 상태 조회
- 결제사별 웹훅 수신 및 처리
- 결제 실패 시 예약 취소 이벤트 발행
- 결제사 정보 관리 (API 엔드포인트, 인증정보)

**핵심 엔티티:**

- `Payment` (Aggregate Root)

  - id, reservationId, amount, paymentType (결제사 타입),
    status (결제 상태), externalPaymentId (외부 결제 ID),
    createdAt, updatedAt

- `PaymentProvider` (Aggregate Root 또는 Value Object)
  - id, name (결제사명), apiEndpoint (API 엔드포인트),
    credentials (인증정보 - 암호화 저장), isActive

**Strategy Pattern:**

- `PaymentStrategy` (인터페이스)
  - `TossPaymentStrategy`
  - `KakaoPaymentStrategy`
  - `NaverPaymentStrategy`

**이벤트:**

- `PaymentRequestedEvent` (예약 생성 시 수신)
- `PaymentCompletedEvent` → Reservation 도메인으로 전송
- `PaymentFailedEvent` → Reservation 도메인으로 전송
- `PaymentWebhookReceivedEvent` (외부 결제사 웹훅 수신)

**도메인 서비스:**

- `PaymentStrategyFactory`: 결제사별 Strategy 선택 (PaymentProvider 기반)
- `PaymentWebhookHandler`: 웹훅 처리 및 이벤트 발행
- `PaymentProviderService`: 결제사 정보 조회 및 관리

---

#### 1.4 User Domain (사용자 도메인)

**책임:**

- 사용자 정보 관리
- 사용자 인증/인가 (나중에 security 모듈과 연동)

**핵심 엔티티:**

- `User` (Aggregate Root)
  - id, name, email, role, status, createdAt, updatedAt

**이벤트:**

- `UserCreatedEvent`
- `UserUpdatedEvent`

**참고:** 초기에는 간단하게 구현하고, 나중에 security 모듈과 통합

---

## 🏗 헥사고날 아키텍처 구조

각 도메인은 헥사고날 아키텍처로 구성:

```
modules/{domain}/
├── domain/                          # 도메인 계층 (핵심 비즈니스 로직)
│   ├── model/                      # 도메인 모델
│   │   ├── {Entity}.java           # Aggregate Root
│   │   ├── {ValueObject}.java      # Value Object
│   │   └── {DomainEvent}.java      # 도메인 이벤트
│   ├── service/                    # 도메인 서비스
│   │   └── {DomainService}.java
│   ├── repository/                 # 도메인 리포지토리 인터페이스 (포트)
│   │   └── {Repository}.java
│   └── exception/                  # 도메인 예외
│       └── {DomainException}.java
│
├── application/                    # 애플리케이션 계층 (Use Case)
│   ├── port/
│   │   ├── in/                     # 인바운드 포트 (Use Case 인터페이스)
│   │   │   ├── {UseCase}.java
│   │   │   └── dto/
│   │   └── out/                    # 아웃바운드 포트 (외부 의존성 인터페이스)
│   │       ├── {ExternalService}.java
│   │       └── {EventPublisher}.java
│   └── service/                    # Use Case 구현체
│       └── {UseCaseImpl}.java
│
└── adapter/                        # 어댑터 계층 (인프라스트럭처)
    ├── in/                         # 인바운드 어댑터 (인터페이스)
    │   ├── web/                    # REST API
    │   │   ├── {Controller}.java
    │   │   └── dto/
    │   └── event/                   # 이벤트 리스너
    │       └── {EventListener}.java
    └── out/                        # 아웃바운드 어댑터 (구현체)
        ├── persistence/            # JPA 리포지토리
        │   ├── {RepositoryImpl}.java
        │   └── entity/
        ├── external/               # 외부 API 클라이언트
        │   └── {ExternalClient}.java
        └── event/                  # 이벤트 발행
            └── {EventPublisherImpl}.java
```

---

## 📦 구체적인 디렉토리 구조

### MeetingRoom Domain

```
modules/meetingroom/
├── domain/
│   ├── model/
│   │   ├── MeetingRoom.java            # Aggregate Root
│   │   │   # id, name, capacity, hourlyRate, status
│   │   └── MeetingRoomStatus.java      # Value Object (enum)
│   ├── repository/
│   │   └── MeetingRoomRepository.java  # 포트
│   └── exception/
│       └── MeetingRoomNotFoundException.java
│
├── application/
│   ├── port/in/
│   │   ├── GetMeetingRoomListUseCase.java
│   │   └── dto/
│   │       └── MeetingRoomResponse.java
│   └── service/
│       └── GetMeetingRoomListUseCaseImpl.java
│
└── adapter/
    ├── in/web/
    │   ├── MeetingRoomController.java
    │   └── dto/
    │       └── MeetingRoomApiResponse.java
    └── out/persistence/
        ├── MeetingRoomRepositoryImpl.java
        └── entity/
            └── MeetingRoomEntity.java
```

### Reservation Domain

```
modules/reservation/
├── domain/
│   ├── model/
│   │   ├── Reservation.java             # Aggregate Root
│   │   ├── ReservationStatus.java        # Value Object (enum)
│   │   ├── ReservationTime.java          # Value Object
│   │   └── event/
│   │       ├── ReservationCreatedEvent.java
│   │       ├── ReservationConfirmedEvent.java
│   │       └── ReservationCancelledEvent.java
│   ├── service/
│   │   ├── ReservationValidationService.java
│   │   └── ReservationLockService.java
│   ├── repository/
│   │   └── ReservationRepository.java
│   └── exception/
│       ├── ReservationTimeConflictException.java
│       ├── InvalidReservationTimeException.java
│       └── ReservationNotFoundException.java
│
├── application/
│   ├── port/in/
│   │   ├── CreateReservationUseCase.java
│   │   ├── GetReservationUseCase.java
│   │   ├── CancelReservationUseCase.java
│   │   └── dto/
│   │       ├── CreateReservationRequest.java
│   │       └── ReservationResponse.java
│   ├── port/out/
│   │   ├── PaymentEventPublisher.java   # 결제 이벤트 발행
│   │   └── RoomRepository.java           # Room 도메인 조회
│   └── service/
│       ├── CreateReservationUseCaseImpl.java
│       ├── GetReservationUseCaseImpl.java
│       └── CancelReservationUseCaseImpl.java
│
└── adapter/
    ├── in/web/
    │   ├── ReservationController.java
    │   └── dto/
    │       ├── CreateReservationApiRequest.java
    │       └── ReservationApiResponse.java
    ├── in/event/
    │   └── PaymentEventListener.java     # Payment 이벤트 수신
    └── out/persistence/
        ├── ReservationRepositoryImpl.java
        └── entity/
            └── ReservationEntity.java
```

### Payment Domain

```
modules/payment/
├── domain/
│   ├── model/
│   │   ├── Payment.java                 # Aggregate Root
│   │   │   # id, reservationId, amount, paymentType,
│   │   │   # status, externalPaymentId
│   │   ├── PaymentProvider.java         # Aggregate Root
│   │   │   # id, name, apiEndpoint, credentials, isActive
│   │   ├── PaymentStatus.java            # Value Object (enum)
│   │   ├── PaymentType.java              # Value Object (enum)
│   │   ├── event/
│   │   │   ├── PaymentCompletedEvent.java
│   │   │   └── PaymentFailedEvent.java
│   │   └── strategy/                     # Strategy Pattern
│   │       ├── PaymentStrategy.java      # 인터페이스
│   │       ├── TossPaymentStrategy.java
│   │       ├── KakaoPaymentStrategy.java
│   │       └── NaverPaymentStrategy.java
│   ├── service/
│   │   └── PaymentStrategyFactory.java
│   ├── repository/
│   │   └── PaymentRepository.java
│   └── exception/
│       ├── PaymentNotFoundException.java
│       ├── PaymentFailedException.java
│       └── UnsupportedPaymentTypeException.java
│
├── application/
│   ├── port/in/
│   │   ├── ProcessPaymentUseCase.java
│   │   ├── GetPaymentStatusUseCase.java
│   │   ├── HandleWebhookUseCase.java
│   │   └── dto/
│   │       ├── PaymentRequest.java
│   │       └── PaymentResponse.java
│   ├── port/out/
│   │   ├── ReservationEventPublisher.java  # Reservation 이벤트 발행
│   │   └── ExternalPaymentClient.java      # 외부 결제사 API (포트)
│   └── service/
│       ├── ProcessPaymentUseCaseImpl.java
│       ├── GetPaymentStatusUseCaseImpl.java
│       └── HandleWebhookUseCaseImpl.java
│
└── adapter/
    ├── in/web/
    │   ├── PaymentController.java
    │   ├── PaymentWebhookController.java
    │   └── dto/
    │       ├── PaymentApiRequest.java
    │       └── PaymentApiResponse.java
    ├── in/event/
    │   └── ReservationEventListener.java   # Reservation 이벤트 수신
    └── out/
        ├── persistence/
        │   ├── PaymentRepositoryImpl.java
        │   └── entity/
        │       └── PaymentEntity.java
        └── external/
            ├── TossPaymentClient.java      # 어댑터
            ├── KakaoPaymentClient.java
            └── NaverPaymentClient.java
```

### User Domain

```
modules/user/
├── domain/
│   ├── model/
│   │   ├── User.java                    # Aggregate Root
│   │   │   # id, name, email, role, status
│   │   └── UserStatus.java              # Value Object (enum)
│   ├── repository/
│   │   └── UserRepository.java          # 포트
│   └── exception/
│       └── UserNotFoundException.java
│
├── application/
│   ├── port/in/
│   │   ├── GetUserUseCase.java
│   │   └── dto/
│   │       └── UserResponse.java
│   └── service/
│       └── GetUserUseCaseImpl.java
│
└── adapter/
    ├── in/web/
    │   ├── UserController.java
    │   └── dto/
    │       └── UserApiResponse.java
    └── out/persistence/
        ├── UserRepositoryImpl.java
        └── entity/
            └── UserEntity.java
```

---

## 🔄 이벤트 드리븐 아키텍처 플로우

### 시나리오 1: 예약 생성 → 결제 처리

```
1. 사용자 요청
   POST /api/reservations
   { meetingRoomId, userId, startTime, endTime, paymentType }
   ↓
2. Reservation Domain
   - CreateReservationUseCase 실행
   - MeetingRoom 조회 (시간당 요금 확인)
   - 총 결제금액 계산: (endTime - startTime) × hourlyRate
   - 시간 중복 검증 (ReservationValidationService)
   - 동시성 제어 (ReservationLockService)
   - Reservation 엔티티 생성
     (상태: PENDING, paymentStatus: PENDING, totalAmount: 계산된 금액)
   - ReservationCreatedEvent 발행 (totalAmount 포함)
   ↓
3. Payment Domain (이벤트 리스너)
   - ReservationEventListener.onReservationCreated()
   - ProcessPaymentUseCase 실행
   - PaymentProvider 조회 (API 엔드포인트, 인증정보)
   - PaymentStrategyFactory로 결제사 Strategy 선택
   - 외부 결제사 API 호출 (PaymentProvider 정보 사용)
   - Payment 엔티티 생성 (상태: PROCESSING)
   ↓
4. 결제 완료 시
   - PaymentCompletedEvent 발행
   ↓
5. Reservation Domain (이벤트 리스너)
   - PaymentEventListener.onPaymentCompleted()
   - Reservation 상태를 CONFIRMED로 변경
   - Reservation paymentStatus를 COMPLETED로 변경
```

### 시나리오 2: 결제 웹훅 수신

```
1. 외부 결제사 → Payment Webhook
   POST /api/payments/webhook/{paymentType}
   ↓
2. Payment Domain
   - HandleWebhookUseCase 실행
   - 웹훅 검증 및 파싱
   - Payment 상태 업데이트
   ↓
3. 이벤트 발행
   - PaymentCompletedEvent 또는 PaymentFailedEvent
   ↓
4. Reservation Domain (이벤트 리스너)
   - PaymentEventListener 처리
   - Reservation 상태 업데이트 (CONFIRMED 또는 CANCELLED)
```

---

## 🔐 동시성 제어 전략

### Reservation Domain에서 동시성 제어

**옵션 1: Optimistic Lock (권장)**

```java
@Entity
public class Reservation {
    @Version
    private Long version;  // Optimistic Lock

    // 예약 생성 시 version 체크
}
```

**옵션 2: Pessimistic Lock (DB Lock)**

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Reservation> findByRoomIdAndTimeRange(...);
```

**옵션 3: 분산 락 (Redis)**

- 나중에 필요시 Redisson 추가

---

## 📝 도메인 간 의존성

### 의존성 방향 (포트-어댑터 패턴)

```
Reservation Domain
    ↓ (포트: MeetingRoomRepository)
MeetingRoom Domain

Reservation Domain
    ↓ (포트: UserRepository)
User Domain

Reservation Domain
    ↓ (이벤트: ReservationCreatedEvent)
Payment Domain

Payment Domain
    ↓ (이벤트: PaymentCompletedEvent)
Reservation Domain
```

**중요:** 도메인 간 직접 의존성 없음! 이벤트로만 통신!

---

## 🎯 핵심 설계 원칙

1. **도메인 독립성**: 각 도메인은 독립적으로 개발/테스트 가능
2. **이벤트 기반 통신**: 도메인 간 직접 호출 없이 이벤트로 통신
3. **포트-어댑터 패턴**: 외부 의존성은 포트로 추상화
4. **Strategy Pattern**: Payment 도메인에서 결제사별 전략 구현
5. **동시성 제어**: Reservation 도메인에서 Optimistic Lock 또는 DB Lock 사용

---

## 🚀 구현 순서 제안

1. **User Domain** (가장 단순)
   - 사용자 정보 관리 (간단한 CRUD)
   - 나중에 security 모듈과 통합
2. **MeetingRoom Domain** (단순)
   - 회의실 목록 조회
   - 회의실 정보 관리 (이름, 수용인원, 시간당 요금)
3. **Reservation Domain** (핵심)
   - 예약 CRUD
   - 시간 검증 로직 (중복 방지, 정시/30분 단위)
   - 총 결제금액 계산 (MeetingRoom의 hourlyRate 사용)
   - 동시성 제어
   - 결제 상태 관리
4. **Payment Domain** (복잡)

   - PaymentProvider 관리 (결제사 정보, API 엔드포인트, 인증정보)
   - Strategy Pattern 구현
   - 웹훅 처리
   - 이벤트 발행/수신

5. **이벤트 통합**
   - Spring Events 또는 Message Queue (Kafka) 사용
   - 도메인 간 이벤트 연결

---

## 📌 추가 고려사항

### 이벤트 버스 구현

- **옵션 1**: Spring Events (간단, 동기)
- **옵션 2**: Kafka (복잡, 비동기, 확장성)
- **초기**: Spring Events로 시작 → 나중에 Kafka로 전환 가능

### 트랜잭션 관리

- 각 도메인은 독립적인 트랜잭션
- 이벤트 발행은 트랜잭션 커밋 후 (Outbox Pattern 고려)

### 테스트 전략

- 도메인 로직: 단위 테스트 (Mock 사용)
- Use Case: 통합 테스트 (TestContainers)
- 이벤트: 이벤트 발행/수신 테스트
