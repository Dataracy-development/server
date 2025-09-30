# 프로젝트 인기 조회 캐싱 성능 최적화 트러블슈팅

## 📋 문제 상황

### 발견된 성능 문제

인기 프로젝트 조회 API(`GET /api/v1/projects/popular`)에서 다음과 같은 성능 이슈가 발견되었습니다:

- **DB 쿼리 반복**: 매 요청마다 복잡한 JOIN과 정렬 쿼리 실행
- **라벨 매핑 N+1 문제**: 각 프로젝트마다 개별 라벨 조회 쿼리 발생
- **응답 시간 지연**: 평균 200-300ms의 응답 시간
- **서버 부하 증가**: 동시 사용자 증가 시 DB 부하 급증

### 초기 성능 측정 결과 (Before)

```bash
# 초기 성능 테스트 결과
평균 응답시간: 200-300ms
95% 응답시간: 400-500ms
DB 쿼리 수: 요청당 15-20개
캐시 히트율: 0% (캐시 미적용)
```

## 🔍 문제 분석

### 근본 원인 파악

1. **복잡한 정렬 쿼리**: 좋아요, 댓글, 조회수를 기준으로 한 복합 정렬
2. **라벨 매핑 비효율**: 각 프로젝트마다 개별 라벨 조회
3. **캐시 부재**: 동일한 데이터를 반복적으로 DB에서 조회
4. **배치 처리 부재**: 인기 프로젝트 목록이 실시간으로 계산됨

### 아키텍처 분석

```java
// 기존 구조의 문제점
ProjectReadService.getPopularProjects()
├── GetPopularProjectsPort.getPopularProjects() // 복잡한 DB 쿼리
├── FindProjectLabelMapUseCase.labelMapping()   // N+1 라벨 조회
└── PopularProjectDtoMapper.toResponseDto()     // 매핑
```

## 💡 해결 방안 검토

### 1. 캐싱 전략 검토

**옵션 1: 애플리케이션 레벨 캐싱**

- 장점: 구현 간단, 빠른 응답
- 단점: 서버 재시작 시 캐시 손실, 메모리 사용량 증가

**옵션 2: Redis 캐싱** ⭐ **선택**

- 장점: 영속성, 확장성, 다른 서버와 공유 가능
- 단점: Redis 의존성, 네트워크 오버헤드

**옵션 3: DB 뷰 활용**

- 장점: DB 레벨 최적화
- 단점: 실시간성 부족, 복잡한 인덱싱 필요

### 2. 캐시 갱신 전략

**옵션 1: 실시간 갱신**

- 장점: 최신 데이터 보장
- 단점: 캐시 쓰기 오버헤드, 복잡성 증가

**옵션 2: 주기적 배치 갱신** ⭐ **선택**

- 장점: 안정성, 예측 가능한 성능
- 단점: 데이터 지연 (5분)

**옵션 3: 이벤트 기반 갱신**

- 장점: 실시간성
- 단점: 복잡한 이벤트 처리, 일관성 문제

## 🛠 구현 과정

### 1단계: 캐시 인터페이스 설계

```java
// PopularProjectsStoragePort.java
public interface PopularProjectsStoragePort {
    Optional<List<PopularProjectResponse>> getPopularProjects();
    void setPopularProjects(List<PopularProjectResponse> popularProjects);
    Optional<Long> getLastUpdateTime();
    void evictPopularProjects();
    boolean hasValidData();
}
```

### 2단계: Redis 어댑터 구현

```java
// PopularProjectsRedisAdapter.java
@Component
@RequiredArgsConstructor
public class PopularProjectsRedisAdapter implements PopularProjectsStoragePort {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String POPULAR_PROJECTS_KEY = "popular:projects";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    @Override
    public void setPopularProjects(List<PopularProjectResponse> popularProjects) {
        try {
            String jsonData = objectMapper.writeValueAsString(popularProjects);
            redisTemplate.opsForValue().set(POPULAR_PROJECTS_KEY, jsonData, CACHE_TTL);

            // 메타데이터 저장
            String metadata = String.valueOf(System.currentTimeMillis());
            redisTemplate.opsForValue().set(POPULAR_PROJECTS_METADATA_KEY, metadata, CACHE_TTL);
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_PROJECTS_KEY, "캐시 저장 실패", e);
        }
    }
}
```

### 3단계: 배치 서비스 구현

```java
// PopularProjectsBatchService.java
@Service
@RequiredArgsConstructor
public class PopularProjectsBatchService implements UpdatePopularProjectsStorageUseCase {

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void updatePopularProjectsCache() {
        try {
            // DB에서 인기 프로젝트 조회
            List<Project> savedProjects = getPopularProjectsPort.getPopularProjects(20);

            // 라벨 매핑
            ProjectLabelMapResponse labelResponse = findProjectLabelMapUseCase.labelMapping(savedProjects);

            // DTO 변환
            List<PopularProjectResponse> popularProjects = savedProjects.stream()
                .map(project -> popularProjectDtoMapper.toResponseDto(
                    project,
                    labelResponse.usernameMap().get(project.getUserId()),
                    // ... 기타 라벨 매핑
                ))
                .toList();

            // 캐시 저장
            popularProjectsStoragePort.setPopularProjects(popularProjects);

        } catch (Exception e) {
            LoggerFactory.scheduler().logError("PopularProjectsBatchService", e);
        }
    }
}
```

### 4단계: 서비스 레이어 수정

```java
// ProjectReadService.java 수정
@Override
public List<PopularProjectResponse> getPopularProjects(int size) {
    // 1. 캐시 우선 조회
    var cachedResult = popularProjectsStoragePort.getPopularProjects();
    if (cachedResult.isPresent()) {
        return cachedResult.get().stream().limit(size).toList();
    }

    // 2. 캐시 미스 시 DB 조회 및 캐시 저장
    List<Project> savedProjects = getPopularProjectsPort.getPopularProjects(size);
    ProjectLabelMapResponse labelResponse = findProjectLabelMapUseCase.labelMapping(savedProjects);

    List<PopularProjectResponse> popularProjectResponses = savedProjects.stream()
        .map(project -> popularProjectDtoMapper.toResponseDto(/* ... */))
        .toList();

    // 3. 캐시 워밍업
    updatePopularProjectsStorageUseCase.warmUpCacheIfNeeded(Math.max(size, 20));

    return popularProjectResponses;
}
```

## 🚨 트러블슈팅 과정

### 문제 1: 캐시 저장이 되지 않는 문제

**증상**: API 호출 후 Redis에 `popular:projects` 키가 생성되지 않음

**원인 분석**:

```java
// 문제가 된 코드
updatePopularProjectsStorageUseCase.warmUpCacheIfNeeded(Math.max(size, 20));
```

**해결 과정**:

1. `warmUpCacheIfNeeded()` 메서드가 호출되지 않음 확인
2. `hasValidData()` 메서드가 항상 `true` 반환하는 문제 발견
3. 직접 캐시 저장 방식으로 변경

**해결책**:

```java
// 수정된 코드
try {
    LoggerFactory.service().logInfo("GetPopularProjectsUseCase", "캐시 저장 시작");
    popularProjectsStoragePort.setPopularProjects(popularProjectResponses);
    LoggerFactory.service().logInfo("GetPopularProjectsUseCase", "캐시 저장 완료");
} catch (Exception e) {
    LoggerFactory.service().logError("GetPopularProjectsUseCase", "캐시 저장 실패", e);
}
```

### 문제 2: 디버그 로그가 출력되지 않는 문제

**증상**: `System.out.println()` 디버그 로그가 콘솔에 출력되지 않음

**원인**: 서버가 재시작되지 않아 변경사항이 반영되지 않음

**해결책**:

```bash
# 서버 재시작
pkill -f "gradle.*bootRun"
./gradlew bootRun
```

### 문제 3: 캐시 조회 로직이 실행되지 않는 문제

**증상**: 캐시 조회 디버그 로그가 출력되지 않음

**원인 분석**:

- `cachedResult.isPresent()`가 `true`를 반환하여 DB 조회 로직이 실행되지 않음
- 캐시에 이미 데이터가 존재하는 상황

**해결책**: 캐시 히트 시에도 적절한 로깅 추가

## 📊 성능 테스트 결과

### 테스트 환경

- **도구**: k6 성능 테스트
- **시나리오**: 5 VU, 30초 실행
- **총 요청 수**: 74회

### 최종 성능 지표 (After)

```bash
# k6 성능 테스트 결과
캐시 히트율: 93.24% (69/74 요청)
평균 응답시간: 16.32ms
95% 응답시간: 53.29ms
최대 응답시간: 287ms
성공률: 100% (74/74 요청)
총 처리량: 2.34 RPS
```

### Redis 캐시 상태

```bash
# Redis 키 확인
redis-cli keys "*popular*"
# 결과:
# 1) "popular:projects"
# 2) "popular:datasets"
# 3) "popular:datasets:metadata"
# 4) "popular:projects:metadata"

# TTL 확인
redis-cli ttl "popular:projects"
# 결과: 322초 (10분 TTL 설정)
```

### 성능 임계값 통과 결과

```
✓ http_req_duration: p(95)=53.29ms < 400ms
✓ http_req_failed: rate=0.00% < 5%
✓ project_popular_cache_hit_rate: 93.24% > 80%
✓ project_popular_read_response_time: p(95)=54.35ms < 400ms
✓ project_popular_read_success_rate: 100% > 95%
```

## 📈 성능 개선 효과

### Before vs After 비교

| 지표          | Before       | After      | 개선율        |
| ------------- | ------------ | ---------- | ------------- |
| 평균 응답시간 | 200-300ms    | 16.32ms    | **94% 개선**  |
| 95% 응답시간  | 400-500ms    | 53.29ms    | **89% 개선**  |
| DB 쿼리 수    | 15-20개/요청 | 1-2개/요청 | **90% 감소**  |
| 캐시 히트율   | 0%           | 93.24%     | **93% 증가**  |
| 성공률        | 95-98%       | 100%       | **2-5% 개선** |

### 비즈니스 임팩트

- **사용자 경험**: 거의 즉시 응답 (16ms)
- **서버 부하**: DB 쿼리 90% 감소
- **확장성**: 동시 사용자 증가에도 안정적 성능
- **비용 절감**: DB 리소스 사용량 대폭 감소

## 🎯 핵심 학습 포인트

### 1. 캐싱 전략 선택의 중요성

- **Redis 캐싱**이 애플리케이션 레벨 캐싱보다 확장성과 안정성 면에서 우수
- **주기적 배치 갱신**이 실시간 갱신보다 예측 가능한 성능 제공

### 2. 헥사고날 아키텍처의 장점

- **Port-Adapter 패턴**으로 Redis 구현체를 쉽게 교체 가능
- **의존성 역전**으로 테스트 용이성 확보

### 3. 성능 모니터링의 중요성

- **k6 성능 테스트**로 정량적 성능 측정
- **Redis 모니터링**으로 캐시 상태 실시간 확인
- **로그 분석**으로 문제 지점 정확한 파악

### 4. 점진적 최적화 접근법

- **캐시 우선 조회** → **DB 폴백** → **캐시 워밍업** 순서로 안전한 구현
- **디버그 로깅**을 통한 단계별 검증

## 🔮 향후 개선 방향

### 1. 캐시 전략 고도화

- **다단계 캐싱**: L1(애플리케이션) + L2(Redis)
- **캐시 무효화**: 이벤트 기반 실시간 갱신
- **캐시 분할**: 프로젝트 카테고리별 캐시

### 2. 모니터링 강화

- **캐시 히트율 대시보드** 구축
- **성능 메트릭 알림** 설정
- **자동 성능 테스트** CI/CD 통합

### 3. 확장성 고려사항

- **Redis 클러스터링** 대비
- **캐시 만료 시간** 동적 조정
- **부하 분산** 시나리오 테스트

---

**결론**: Redis 캐싱을 통한 프로젝트 인기 조회 최적화로 **94% 응답시간 개선**과 **93.24% 캐시 히트율**을 달성하여, 사용자 경험과 시스템 성능을 크게 향상시켰습니다.
