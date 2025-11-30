# 결제 Outbox 패턴 설계 제안

## 목표
- DB 트랜잭션과 Kafka 이벤트 발행간 일관성 확보 (이중 쓰기 방지)
- 실패/재시도/감사 로깅을 체계적으로 관리
- 결제 생성/취소 이벤트 모두 동일한 아키텍처로 운영

## 아키텍처 개요
1. **도메인 트랜잭션 내부**에서 `payment_event_outbox` 테이블에 이벤트 레코드를 함께 저장  
   - 컬럼 예시: `id`, `aggregate_type`, `aggregate_id`, `event_type`, `payload (JSON)`, `status`, `created_at`, `published_at`
2. **Outbox Relay**가 테이블을 폴링 또는 CDC 기반으로 읽어 Kafka로 발행  
   - 1차 제안: Spring Batch + 스케줄러 (간단)  
   - 장기적으로 Debezium + Kafka Connect를 붙이면 거의 실시간 CDC 가능
3. Kafka 발행이 성공하면 `status = PUBLISHED` 로 업데이트  
4. 실패 시 재시도 정책(Backoff)과 DLQ를 Outbox 레벨에서 제어 가능

## 구현 상세
### 1) Outbox Entity/Repository
```text
PaymentEventOutbox
  - id (PK, UUID)
  - aggregateType (PAYMENT)
  - aggregateId (paymentId)
  - eventType (PAYMENT_CREATED, PAYMENT_CANCEL_REQUESTED 등)
  - payload (JSON)
  - status (PENDING / PUBLISHED / FAILED)
  - retries
  - createdAt / updatedAt / publishedAt
```

### 2) OutboxWriter (도메인 트랜잭션 내부)
```java
@Transactional
public void createPayment(...){
    Payment payment = paymentCommandPort.save(...);
    outboxWriter.save(PaymentEventOutbox.ofPaymentCreated(payment));
}
```

### 3) Relay 옵션
| 방식 | 장점 | 단점 |
|------|------|------|
| Spring Batch (폴링) | 구현 빠름, 의존성 최소 | 지연/중복 관리 직접 구현 |
| Debezium + Kafka Connect | 실시간, 운영 도구 풍부 | 인프라 복잡, 운영비용 증가 |

현재 단계에서는 **Spring Batch + Scheduler**로 PoC를 만들고, 트래픽이 커지면 Debezium으로 전환 권장.

### 4) 멱등성 연동
- Kafka Listener는 기존 `payment_process_log` / `payment_cancel_log` 유지
- Outbox → Kafka 발행 시에도 `eventId`를 그대로 사용해 Listener 멱등성 보장

## 단계적 도입 전략
1. Outbox 테이블 및 Writer, Relay(배치) 도입
2. 결제 생성 이벤트부터 Outbox를 통해 발행하도록 전환
3. 결제 취소 이벤트 Outbox 전환
4. 운영 지표/알림 추가 (Outbox 적체량, 실패 건수)
5. 필요 시 Debezium 기반 CDC로 업그레이드

## 기대 효과
- DB 커밋과 이벤트 발행의 분리로 인한 데이터 불일치 제거
- Kafka 장애 시에도 Outbox 테이블을 재처리하면 안정적으로 복구 가능
- 감사/추적 로그가 Outbox 테이블에 남아 운영 가시성 향상

