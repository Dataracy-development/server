# 🛠️ 개발 가이드

## 📋 **개요**

Dataracy 백엔드 개발을 위한 종합 가이드입니다. 실제 구현된 코드와 아키텍처를 기반으로 작성되었습니다.

---

## 📚 **문서 목차**

### **시작하기**

- [개발 환경 설정](./setup.md) - 로컬 개발 환경 구성
- [시스템 아키텍처](./architecture.md) - DDD + 헥사고날 아키텍처
- [코딩 표준](./coding-standards.md) - 네이밍, 스타일, 컨벤션
- [개발 워크플로우](./workflow.md) - Git, PR, 배포 프로세스

---

## 🚀 **빠른 시작**

### **1. 개발 환경 설정**

```bash
# 1. 프로젝트 클론
git clone https://github.com/your-org/dataracy-server.git
cd dataracy-server

# 2. 환경 변수 설정
cp .env.example .env.local

# 3. 인프라 서비스 시작
docker-compose -f infrastructure/kafka/docker-compose.kafka-local.yml up -d
docker-compose -f infrastructure/redis/redis-compose-local.yml up -d
docker-compose -f infrastructure/elasticsearch/docker-compose.elasticsearch.yml up -d

# 4. 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

### **2. 첫 번째 테스트 실행**

```bash
# 단위 테스트
./gradlew test

# 커버리지 포함 테스트
./test-coverage.sh
```

### **3. API 문서 확인**

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 문서**: [API 문서](../api/README.md)

---

## 🏗️ **프로젝트 구조**

### **모듈별 구조**

```
src/main/java/com/dataracy/modules/
├── auth/                    # 인증/인가 (60개 파일)
│   ├── domain/             # 도메인 계층
│   ├── application/        # 애플리케이션 계층
│   └── adapter/            # 어댑터 계층
├── user/                   # 사용자 관리 (89개 파일)
├── project/                # 프로젝트 관리 (158개 파일)
├── dataset/                # 데이터셋 관리 (149개 파일)
├── comment/                # 댓글 관리 (60개 파일)
├── like/                   # 좋아요 관리 (26개 파일)
├── behaviorlog/            # 행동 로그 (23개 파일)
├── filestorage/            # 파일 저장소 (18개 파일)
├── email/                  # 이메일 관리 (29개 파일)
├── reference/              # 참조 데이터 (136개 파일)
├── security/               # 보안 (10개 파일)
└── common/                 # 공통 모듈 (68개 파일)
```

### **계층별 구조**

```
각 모듈/
├── domain/                 # 도메인 계층
│   ├── model/             # 도메인 모델
│   ├── enums/             # 열거형
│   ├── exception/         # 도메인 예외
│   └── status/            # 상태 코드
├── application/           # 애플리케이션 계층
│   ├── port/
│   │   ├── in/           # 인바운드 포트
│   │   └── out/          # 아웃바운드 포트
│   ├── service/          # 서비스 구현
│   └── dto/              # DTO
└── adapter/              # 어댑터 계층
    ├── persistence/      # 데이터 영속성
    ├── query/           # 쿼리 어댑터
    └── web/             # 웹 어댑터
```

---

## 🎯 **핵심 원칙**

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

## 🔧 **개발 도구**

### **필수 도구**

- **Java**: 17 (Temurin OpenJDK 권장)
- **Gradle**: 8.14.2 (Gradle Wrapper 사용)
- **Docker**: 24.0 이상
- **IDE**: IntelliJ IDEA (권장)

### **코드 품질 도구**

- **Checkstyle**: 코드 스타일 검사 ([상세 가이드](../quality/checkstyle.md))
- **Spotless**: 자동 코드 포맷팅 ([상세 가이드](../quality/spotless.md))
- **JaCoCo**: 테스트 커버리지 (70% 기준) ([상세 가이드](../quality/jacoco.md))
- **SonarQube**: 종합적인 코드 품질 분석 ([상세 가이드](../quality/sonarqube.md))
- **SpotBugs**: 정적 분석 (주석 처리됨, 필요시 활성화) ([상세 가이드](../quality/spotbugs.md))

**📋 전체 코드 품질 도구 가이드**: [quality/README.md](../quality/README.md)

### **테스트 도구**

- **JUnit 5**: 단위 테스트 (명시적 선언)
- **Mockito**: 모킹 프레임워크
- **H2 Database**: 테스트용 인메모리 DB
- **AssertJ**: 플루언트 어설션

---

## 📖 **다음 단계**

1. **[개발 환경 설정](./setup.md)** - 상세한 환경 구성 가이드
2. **[시스템 아키텍처](./architecture.md)** - 아키텍처 설계 원칙
3. **[코딩 표준](./coding-standards.md)** - 코드 작성 규칙
4. **[개발 워크플로우](./workflow.md)** - Git, PR, 배포 프로세스

---

**💡 개발 관련 문의사항은 개발팀에 연락해주세요!**
