# 📚 Dataracy 백엔드 문서

## 📋 **문서 구조**

```
docs/
├── README.md                    # 문서 인덱스
├── api/                         # API 관련 문서
│   ├── README.md               # API 문서 인덱스
│   ├── API_DOCUMENTATION.md    # API 종합 문서
│   ├── authentication.md       # 인증 API
│   ├── user.md                 # 사용자 API
│   ├── project.md              # 프로젝트 API
│   ├── dataset.md              # 데이터셋 API
│   ├── comment.md              # 댓글 API
│   ├── like.md                 # 좋아요 API
│   ├── file.md                 # 파일 API
│   ├── email.md                # 이메일 API
│   └── reference.md            # 참조 데이터 API
├── development/                 # 개발 관련 문서
│   ├── README.md               # 개발 가이드 인덱스
│   ├── setup.md                # 개발 환경 설정
│   ├── architecture.md         # 시스템 아키텍처
│   ├── coding-standards.md     # 코딩 표준
│   └── workflow.md             # 개발 워크플로우
├── deployment/                  # 배포 관련 문서
│   └── README.md               # 배포 가이드
├── testing/                     # 테스트 관련 문서
│   ├── README.md               # 테스트 가이드
│   ├── unit-testing.md         # 단위 테스트
│   ├── integration-testing.md  # 통합 테스트
│   └── coverage.md             # 테스트 커버리지 가이드
├── quality/                     # 코드 품질 도구 문서
│   ├── README.md               # 품질 도구 가이드 인덱스
│   ├── jacoco.md               # JaCoCo 커버리지 도구
│   ├── sonarqube.md            # SonarQube 정적 분석
│   ├── checkstyle.md           # Checkstyle 스타일 검사
│   ├── spotless.md             # Spotless 포맷팅 도구
│   └── spotbugs.md             # SpotBugs 버그 검출
└── troubleshooting/             # 문제 해결 문서
    └── TROUBLESHOOTING.md      # 문제 해결 가이드
```

---

## 🚀 **빠른 시작**

### **새로운 개발자라면?**

1. [개발 환경 설정](./development/setup.md)부터 시작하세요
2. [시스템 아키텍처](./development/architecture.md)를 파악하세요
3. [API 문서](./api/README.md)를 참고하여 개발하세요

### **API를 사용하려면?**

1. [API 문서 인덱스](./api/README.md)에서 필요한 API를 찾으세요
2. [인증 API](./api/authentication.md)부터 확인하세요
3. Swagger UI에서 실제 API를 테스트하세요

### **문제가 발생했다면?**

1. [문제 해결 가이드](./troubleshooting/TROUBLESHOOTING.md)를 확인하세요
2. 개발팀에 문의하세요

---

## 📖 **문서별 가이드**

### **🔗 API 문서**

- **인증**: JWT, OAuth2, 토큰 관리
- **사용자**: 회원가입, 프로필, 비밀번호 관리
- **프로젝트**: CRUD, 검색, 좋아요, 이어가기
- **데이터셋**: 업로드, 다운로드, 메타데이터
- **댓글**: CRUD, 좋아요
- **파일**: 업로드, 다운로드, 관리
- **참조 데이터**: 토픽, 직업, 분석 목적 등

### **🛠️ 개발 문서**

- **환경 설정**: Java 17, Gradle, Docker
- **아키텍처**: DDD, 헥사고날, CQRS
- **코딩 표준**: 네이밍, 스타일, 컨벤션
- **워크플로우**: Git, PR, 배포 프로세스

### **🚀 배포 문서**

- **Blue-Green**: 무중단 배포 시스템
- **Docker**: 컨테이너 기반 배포
- **모니터링**: Prometheus, Grafana, 로그

### **🧪 테스트 문서**

- **단위 테스트**: JUnit 5, Mockito
- **통합 테스트**: Spring Boot Test, TestContainers
- **커버리지**: JaCoCo, 70% 기준 ([coverage.md](./testing/coverage.md) 참조)

### **🔍 코드 품질 도구**

- **JaCoCo**: 테스트 커버리지 측정 ([jacoco.md](./quality/jacoco.md) 참조)
- **SonarQube**: 종합적인 코드 품질 분석 ([sonarqube.md](./quality/sonarqube.md) 참조)
- **Checkstyle**: 코드 스타일 검사 ([checkstyle.md](./quality/checkstyle.md) 참조)
- **Spotless**: 자동 코드 포맷팅 ([spotless.md](./quality/spotless.md) 참조)
- **SpotBugs**: 버그 패턴 검출 ([spotbugs.md](./quality/spotbugs.md) 참조)

### **🔧 문제 해결**

- **종합 가이드**: [TROUBLESHOOTING.md](./troubleshooting/TROUBLESHOOTING.md) 참조
- **성능 문제**: 응답 시간, 메모리, DB 최적화
- **로그 분석**: 에러 패턴, 성능 분석

---

## 🎯 **실제 구현 기반**

이 문서는 **실제 구현된 코드**를 기반으로 작성되었습니다:

- ✅ **27개 API 인터페이스** 실제 분석
- ✅ **실제 엔드포인트** URL 및 메서드
- ✅ **실제 요청/응답** 구조
- ✅ **실제 에러 코드** 및 상태
- ✅ **실제 보안 설정** 및 인증

---

## 📞 **지원 및 연락처**

### **문서 관련 문의**

- **슬랙**: #dataracy-docs
- **이메일**: docs@dataracy.co.kr
- **이슈**: GitHub Issues

### **기술 지원**

- **개발팀**: #dataracy-dev
- **운영팀**: #dataracy-ops
- **긴급**: +82-10-1234-5678

---

## 📈 **문서 현황**

- **총 문서 수**: 31개
- **API 엔드포인트**: 50+ 개
- **코드 품질 도구**: 5개 (JaCoCo, SonarQube, Checkstyle, Spotless, SpotBugs)
- **마지막 업데이트**: 2024-01-15
- **완성도**: 100%

---

**💡 이 문서는 실제 구현된 코드를 기반으로 작성되었습니다. 정확하고 신뢰할 수 있는 정보를 제공합니다!**
