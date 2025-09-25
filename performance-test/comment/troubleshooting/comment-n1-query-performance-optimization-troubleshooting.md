# Comment 도메인 N+1 쿼리 최적화 트러블 슈팅

## 문제 상황

Comment 도메인의 `findComments` 메서드에서 N+1 쿼리 문제가 발생하고 있었습니다. 프로젝트의 루트 댓글 목록을 조회할 때, 각 댓글마다 답글 수를 서브쿼리로 조회하는 방식으로 구현되어 있어서 댓글 수가 증가할수록 쿼리 수가 기하급수적으로 증가하는 문제가 있었습니다.

### 초기 코드 분석

```java
@Override
public Page<FindCommentWithReplyCountResponse> findComments(Long projectId, Pageable pageable) {
    QCommentEntity child = new QCommentEntity("child");

    List<Tuple> tuples = queryFactory
            .select(
                    comment,
                    JPAExpressions
                            .select(child.count())
                            .from(child)
                            .where(child.parentCommentId.eq(comment.id))
            )
            .from(comment)
            .where(
                    CommentFilterPredicate.projectIdEq(projectId),
                    CommentFilterPredicate.isRootComment()
            )
            .orderBy(CommentSortBuilder.createdAtDesc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    // ... 나머지 로직
}
```

이 방식의 문제점은 댓글 10개를 조회할 때 11개의 쿼리(1개 메인 + 10개 서브쿼리)가 실행되고, 댓글 100개를 조회할 때는 101개의 쿼리가 실행된다는 점이었습니다.

## 문제 원인 분석

### N+1 쿼리 패턴

- **메인 쿼리**: 루트 댓글 목록 조회 (1개)
- **서브쿼리**: 각 댓글의 답글 수 조회 (N개)
- **총 쿼리 수**: 1 + N개

### 성능 영향

- 댓글 수가 증가할수록 쿼리 수가 선형적으로 증가
- 데이터베이스 연결 풀 고갈 가능성
- 응답 시간 지연
- 확장성 문제

## 해결 방안 설계

### 배치 처리 접근법

서브쿼리 대신 배치 처리 방식을 도입하여 쿼리 수를 일정하게 유지하는 방향으로 설계했습니다.

### 최적화 전략

1. **1단계**: 루트 댓글 조회 (1개 쿼리)
2. **2단계**: 모든 댓글의 답글 수를 배치로 조회 (1개 쿼리)
3. **3단계**: 메모리에서 DTO 조합
4. **4단계**: 총 개수 조회 (1개 쿼리)

## 구현 과정

### 최적화된 코드

```java
@Override
public Page<FindCommentWithReplyCountResponse> findComments(Long projectId, Pageable pageable) {
    Instant startTime = LoggerFactory.query().logQueryStart("CommentEntity", "[findComments] 댓글당 답글 수를 포함한 댓글 목록 조회 시작. projectId=" + projectId);
    int queryCount = 0;

    // 1단계: 루트 댓글 조회 (1개 쿼리)
    List<CommentEntity> commentEntities = queryFactory
            .selectFrom(comment)
            .where(
                    CommentFilterPredicate.projectIdEq(projectId),
                    CommentFilterPredicate.isRootComment()
            )
            .orderBy(CommentSortBuilder.createdAtDesc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    queryCount++; // 메인 쿼리

    // 2단계: 배치로 답글 수 조회 (1개 쿼리)
    List<Long> commentIds = commentEntities.stream().map(CommentEntity::getId).toList();
    Map<Long, Long> replyCounts = getReplyCountsBatch(commentIds);
    queryCount++; // 배치 쿼리

    // 3단계: DTO 조합 (메모리에서 처리)
    List<FindCommentWithReplyCountResponse> contents = commentEntities.stream()
            .map(entity -> new FindCommentWithReplyCountResponse(
                    CommentEntityMapper.toDomain(entity),
                    replyCounts.getOrDefault(entity.getId(), 0L)
            ))
            .toList();

    // 4단계: 총 개수 조회 (1개 쿼리)
    long total = Optional.ofNullable(
            queryFactory
                    .select(comment.count())
                    .from(comment)
                    .where(
                            CommentFilterPredicate.projectIdEq(projectId),
                            CommentFilterPredicate.isRootComment()
                    )
                    .fetchOne()
    ).orElse(0L);
    queryCount++; // 카운트 쿼리

    LoggerFactory.query().logQueryEnd("CommentEntity", "[findComments] 댓글당 답글 수를 포함한 댓글 목록 조회 종료. projectId=" + projectId + ", queryCount=" + queryCount, startTime);
    return new PageImpl<>(contents, pageable, total);
}

/**
 * 배치로 답글 수를 조회합니다.
 */
private Map<Long, Long> getReplyCountsBatch(List<Long> commentIds) {
    if (commentIds.isEmpty()) return Collections.emptyMap();

    QCommentEntity child = new QCommentEntity("child");
    return queryFactory
            .select(child.parentCommentId, child.id.count())
            .from(child)
            .where(child.parentCommentId.in(commentIds))
            .groupBy(child.parentCommentId)
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                    tuple -> tuple.get(child.parentCommentId),
                    tuple -> tuple.get(child.id.count())
            ));
}
```

### 핵심 개선 사항

1. **쿼리 수 최적화**: 1+N → 3개 쿼리로 고정
2. **배치 처리**: `IN` 절을 사용한 효율적인 답글 수 조회
3. **메모리 처리**: DTO 조합을 메모리에서 처리
4. **쿼리 카운트 로깅**: 성능 모니터링을 위한 쿼리 수 추적

## 성능 테스트 및 검증

### 테스트 환경

- **도구**: k6 성능 테스트
- **시나리오**: Smoke 테스트 (5 VU, 30초)
- **API**: `GET /api/v1/projects/{projectId}/comments`

### 성능 비교 결과

| 메트릭              | 기존 (N+1)   | 최적화 후   | 개선율         |
| ------------------- | ------------ | ----------- | -------------- |
| **평균 응답 시간**  | 13.23ms      | 8.86ms      | **33% 개선**   |
| **95th percentile** | 32.95ms      | 20.95ms     | **36% 개선**   |
| **DB 쿼리 시간**    | 6.71ms       | 4.50ms      | **33% 개선**   |
| **성공률**          | 100%         | 100%        | 동일           |
| **처리량**          | 204.19 req/s | 65.83 req/s | 요청 패턴 차이 |

### 쿼리 수 최적화 효과

```
기존 방식 (N+1 문제):
- 댓글 10개 → 11개 쿼리 (1개 메인 + 10개 서브쿼리)
- 댓글 100개 → 101개 쿼리 (1개 메인 + 100개 서브쿼리)
- 댓글 N개 → 1+N개 쿼리

최적화 후 (배치 처리):
- 댓글 10개 → 3개 쿼리 (메인 + 배치 + 카운트)
- 댓글 100개 → 3개 쿼리 (메인 + 배치 + 카운트)
- 댓글 N개 → 3개 쿼리 (일정)

개선 효과:
- 댓글 10개: 11개 → 3개 (73% 감소)
- 댓글 100개: 101개 → 3개 (97% 감소)
- 댓글 1000개: 1001개 → 3개 (99.7% 감소)
```

## 구현 과정에서의 고민

### 초기 접근 방식 검토

처음에는 기존의 서브쿼리 방식을 그대로 유지하면서 다른 최적화 방법을 찾아보려고 했습니다. 하지만 근본적인 문제인 N+1 패턴 자체를 해결하지 않으면 확장성 문제가 지속될 것이라는 판단이 들었습니다.

### 배치 처리 방식 선택

서브쿼리를 배치 처리로 변경하는 과정에서 몇 가지 고려사항이 있었습니다:

1. **메모리 사용량**: 모든 댓글의 답글 수를 한 번에 메모리에 로드하는 것에 대한 우려
2. **쿼리 복잡성**: `IN` 절과 `GROUP BY`를 사용하는 쿼리의 성능
3. **코드 가독성**: 기존 코드 대비 복잡해지는 구조

하지만 실제 구현해보니 메모리 사용량은 미미했고, 쿼리 성능도 오히려 향상되었습니다.

### 쿼리 카운트 로깅 추가

성능 모니터링을 위해 각 단계별로 쿼리 수를 카운트하고 로그에 출력하도록 했습니다. 이는 향후 성능 이슈 발생 시 빠른 진단에 도움이 될 것입니다.

## 최종 결과 및 효과

### 기술적 개선

- **쿼리 수 최적화**: 1+N → 3개 쿼리로 고정 (최대 99.7% 감소)
- **응답 시간 개선**: 평균 33%, 95th percentile 36% 개선
- **확장성 향상**: 댓글 수 증가에도 일정한 성능 유지
- **DB 부하 감소**: 쿼리 수 고정으로 데이터베이스 연결 풀 효율성 향상

### 비즈니스 가치

- **사용자 경험 향상**: 댓글 조회 응답 시간 단축
- **시스템 안정성**: 데이터베이스 부하 감소
- **확장 가능성**: 대용량 댓글 시스템 대응 가능

### 학습 포인트

이번 최적화를 통해 N+1 쿼리 문제의 심각성과 배치 처리의 효과를 실제로 체험할 수 있었습니다. 단순히 코드를 수정하는 것을 넘어서 성능 테스트를 통한 정량적 검증까지 진행하면서, 성능 최적화의 전체적인 프로세스를 경험할 수 있었습니다.

## 향후 개선 방향

### 추가 최적화 가능성

1. **캐싱 도입**: 자주 조회되는 댓글의 답글 수 캐싱
2. **인덱스 최적화**: `parent_comment_id` 컬럼 인덱스 검토
3. **페이지네이션 최적화**: 커서 기반 페이지네이션 도입 검토

### 모니터링 강화

- **쿼리 성능 모니터링**: APM 도구를 통한 지속적 모니터링
- **알림 설정**: 응답 시간 임계치 초과 시 알림
- **정기적 성능 테스트**: 배포 후 성능 회귀 테스트

이번 트러블 슈팅을 통해 N+1 쿼리 문제의 해결 방법과 성능 최적화의 중요성을 깊이 이해할 수 있었습니다. 앞으로도 이러한 성능 이슈를 사전에 발견하고 해결할 수 있는 개발 역량을 기를 수 있을 것입니다.
