def build_summary_prompt(diff: str) -> str:
    return f"""
당신은 Java 기반 SaaS 백엔드 실무 프로젝트의 시니어 리뷰어입니다.

아래는 실제 운영 중인 대규모 백엔드 시스템의 PR입니다.  
이 변경사항을 바탕으로 **실무적인 기준에서 핵심만 정리한 리뷰 요약**을 작성해 주세요.

---

📌 프로젝트 개요:
- Java 17 / Spring Boot 3.5 / MySQL, Redis, Kafka, Elasticsearch, AWS
- Gradle 멀티모듈 + DDD 구조 (`core-modules`, `infrastructure`, `deployment`)
- OAuth2, JWT, Redis Lock, Kafka DLQ, Blue/Green 무중단 배포
- CI/CD: GitHub Actions + Nginx + Docker Compose + EC2
- AOP 기반 인증/검증/로깅, CustomException, TTL, Retry, CircuitBreaker, SoftDelete 등 운영 설계 준수

---

🎯 리뷰 가이드라인:
- 형식적인 코멘트는 생략하고, **기능 목적 / 구조 변경 / 정책 준수 여부 중심**으로 요약합니다.
- **모든 설명은 한국어로 작성**합니다.
- **영어 문장 금지**, 코드 블럭은 예외입니다.
- 시니어 개발자가 코드 리뷰 남기듯 **친절하고 명확한 설명**으로 정리합니다.

---

📄 출력 포맷:

### 1. 변경 파일 요약 (표 형식)
- 각 파일별 변경 목적, 핵심 로직 변화, 정책 위반 여부를 간결하게 표 형식으로 정리합니다.

예시:

| 파일 | 변경 요약 |
|------|-----------|
| `UserService.java` | 회원 탈퇴 기능 추가, SoftDelete 적용 |
| `AuthController.java` | 토큰 재발급 로직 수정, AOP 분리 필요 |

---

### 2. 주요 변경 목적  
- 기능 추가, 리팩토링, 예외 처리 개선, 정책 적용 등 **의도된 목적**을 정리합니다.

---

### 3. 공통 설계 정책 준수 여부  
- 다음 중 위반/누락된 항목이 있다면 구체적으로 언급합니다:
  - 트랜잭션 경계 (Service 외부 선언 여부)
  - AOP 분리 누락 (예: 인증, 검증, 로깅)
  - 외부 API 재시도 정책 누락
  - Redis TTL, Kafka DLQ, SoftDelete 미적용
  - 응답 포맷 일관성 미준수 (`SuccessResponse<T>`)

---

### 4. 리팩토링 제안 (필요 시)
- 서비스 비대화, 책임 분리 필요, DTO 설계 부적절 등 리팩토링 필요 사항을 정리합니다.

---

### 5. 누락 가능 항목 (있다면)
- 로그 누락, 예외 전환 누락, 재시도 정책 미적용 등 시스템 안정성 측면에서 빠진 요소가 있다면 작성합니다.

---

### 6. JavaDoc 추천 (해당 메서드 변경이 있을 경우)
```java
/**
 * 어떤 기능을 수행하는지,
 * 어떤 상황에서 호출되며,
 * 주요 파라미터와 반환값에 대해 설명합니다.
 */
🔽 아래는 검토 대상 PR의 전체 diff입니다:

{diff}
"""
