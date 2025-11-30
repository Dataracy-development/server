## ✅ PR 유형

어떤 변경 사항이 있었나요?

- [x] 새로운 기능 추가

---

## 📝 작업 내용

이번 PR에서 작업한 내용을 간략히 설명해주세요(이미지 첨부 가능)

### Security Context에서 userId 추출 구현 (#46)

- **ReservationController 수정**: Security Context에서 실제 인증된 사용자 ID 추출
- **권한 검증 추가**: `getReservationsByUserId`, `cancelReservation`에서 요청 userId와 인증 userId 일치 여부 확인
- **테스트 업데이트**: Security Context 설정 및 권한 검증 테스트 추가

### 주요 구현 내용

1. **SecurityContextProvider 활용**
   - `SecurityContextProvider.getAuthenticatedUserId()` 사용하여 인증된 사용자 ID 추출
   - `createReservation`: Security Context에서 userId 자동 추출
   - `getReservationsByUserId`: 요청 userId와 인증 userId 일치 여부 검증
   - `cancelReservation`: 요청 userId와 인증 userId 일치 여부 검증

2. **권한 검증 로직**
   - 다른 사용자의 예약 조회/취소 시도 시 `UNAUTHORIZED` 예외 발생
   - 보안 강화 및 데이터 무결성 보장

3. **테스트 업데이트**
   - `@BeforeEach`에서 `SecurityContextProvider.setupSecurityContextForTest()` 호출
   - `@AfterEach`에서 `SecurityContextProvider.clearSecurityContext()` 호출
   - 권한 검증 실패 테스트 케이스 추가

---

## ✏️ 관련 이슈

본인이 작업한 내용이 어떤 Issue Number와 관련이 있는지만 작성해주세요

- Resolves : #46

---

## 🧪 테스트

- [x] 단위 테스트 작성 및 통과
- [x] 전체 테스트 실행 (모든 테스트 통과)
- [x] Spotless 포맷팅 적용

---

## 📊 변경 사항

### 수정된 파일
- `ReservationController.java`: Security Context에서 userId 추출 및 권한 검증 추가
- `ReservationControllerTest.java`: Security Context 설정 및 권한 검증 테스트 추가

### 개선 사항
- ✅ TODO 제거: 임시로 사용하던 `1L` 대신 실제 인증된 사용자 ID 사용
- ✅ 보안 강화: 다른 사용자의 예약 조회/취소 시도 방지
- ✅ 테스트 커버리지 향상: 권한 검증 시나리오 테스트 추가
