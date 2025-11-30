# 결제 취소 이벤트 파이프라인 설계

## 목표
- 취소 요청도 결제와 동일하게 카프카 이벤트 기반으로 처리
- 비동기 취소/환불 로직을 감싸고 멱등성, 재시도, DLQ를 동일 패턴으로 유지

## 흐름
1. API에서 `cancelPayment` 호출 → `PaymentCancelRequestMessage` 생성, `payment.cancel` 토픽 발행
2. `PaymentCancelListener`가 이벤트 소비
3. PG 취소 API 호출 후 `Payment#cancel()` → 상태 업데이트
4. 실패 시 지수 백오프 재시도, 한계 초과 시 `payment.cancel.dlq`로 이동

## 멱등성 전략
- `payment_cancel_log` 테이블(event_id, payment_id)
- 리스너는 `tryAcquire` 성공 시만 처리, 완료/실패 시 release

## DLQ
- `payment.cancel.dlq` 토픽 추가
- 재처리 스크립트/배치에서 DLQ 메시지를 다시 `payment.cancel`로 재발행 가능하게 함

