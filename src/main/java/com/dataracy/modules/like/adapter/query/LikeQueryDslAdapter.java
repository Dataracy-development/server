package com.dataracy.modules.like.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.adapter.jpa.entity.QLikeEntity;
import com.dataracy.modules.like.adapter.query.predicates.LikeFilterPredicate;
import com.dataracy.modules.like.application.port.out.query.ReadLikePort;
import com.dataracy.modules.like.application.port.out.validate.ValidateLikePort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeQueryDslAdapter implements
        ReadLikePort,
        ValidateLikePort
{
    private final JPAQueryFactory queryFactory;

    // Entity 상수 정의
    private static final String LIKE_ENTITY = "LikeEntity";

    private final QLikeEntity like = QLikeEntity.likeEntity;

    /**
     * 사용자가 특정 타겟(타입 및 ID)에 대해 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param userId    좋아요를 확인할 사용자 ID
     * @param targetId  확인할 타겟의 ID
     * @param targetType 확인할 타겟의 타입
     * @return 사용자가 해당 타겟을 좋아요 했으면 true, 아니면 false
     */
    @Override
    public boolean isLikedTarget(Long userId, Long targetId, TargetType targetType) {
        Instant startTime = LoggerFactory.query().logQueryStart(LIKE_ENTITY, "[isLikedTarget] 사용자가 해당 타겟을 좋아요 했는지 여부 반환 시작. targetType=" + targetType + ", targetId=" + targetId);
        Integer result = queryFactory
                .selectOne()
                .from(like)
                .where(
                        LikeFilterPredicate.userIdEq(userId),
                        LikeFilterPredicate.targetIdEq(targetId),
                        LikeFilterPredicate.targetTypeEq(targetType)
                )
                .fetchFirst();
        LoggerFactory.query().logQueryEnd(LIKE_ENTITY, "[isLikedTarget] 사용자가 해당 타겟을 좋아요 했는지 여부 반환 완료. targetType=" + targetType + ", targetId=" + targetId, startTime);
        return result != null;
    }

    /**
     * 지정한 사용자와 타겟 타입에 대해, 주어진 타겟 ID 목록 중 사용자가 좋아요를 누른 타겟 ID 목록을 반환합니다.
     *
     * @param userId 사용자 ID
     * @param targetIds 확인할 타겟 ID 목록
     * @param targetType 타겟 타입
     * @return 사용자가 좋아요를 누른 타겟 ID 목록. userId, targetIds가 null이거나 targetIds가 비어 있으면 빈 리스트를 반환합니다.
     */
    @Override
    public List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType) {
        Instant startTime = LoggerFactory.query().logQueryStart(LIKE_ENTITY, "[findLikedTargetIds] 사용자가 좋아요를 누른 타겟 ID 목록 반환 시작. targetType=" + targetType);
        if (userId == null || targetIds == null || targetIds.isEmpty()) {
            return List.of();
        }
        List<Long> likedTargetIds = queryFactory
                .select(like.targetId)
                .from(like)
                .where(
                        LikeFilterPredicate.userIdEq(userId),
                        LikeFilterPredicate.containTargetIdEq(targetIds),
                        LikeFilterPredicate.targetTypeEq(targetType)
                )
                .fetch();
        LoggerFactory.query().logQueryEnd(LIKE_ENTITY, "[findLikedTargetIds] 사용자가 좋아요를 누른 타겟 ID 목록 반환 완료. targetType=" + targetType, startTime);
        return likedTargetIds;
    }
}
