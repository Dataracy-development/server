package com.dataracy.modules.like.adapter.query;

import com.dataracy.modules.like.adapter.jpa.entity.QLikeEntity;
import com.dataracy.modules.like.adapter.query.predicates.LikeFilterPredicate;
import com.dataracy.modules.like.application.port.query.LikeQueryRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class LikeQueryRepositoryPortAdapter implements LikeQueryRepositoryPort {
    private final JPAQueryFactory queryFactory;

    private final QLikeEntity like = QLikeEntity.likeEntity;

    /**
     * 주어진 사용자 ID, 타겟 ID, 타겟 타입에 대해 사용자가 해당 타겟을 좋아요 했는지 여부를 반환합니다.
     *
     * @param userId     사용자 ID
     * @param targetId   타겟 ID
     * @param targetType 타겟 타입
     * @return 사용자가 해당 타겟을 좋아요 했으면 true, 아니면 false
     */
    @Override
    public boolean isLikedTarget(Long userId, Long targetId, TargetType targetType) {
        Integer result = queryFactory
                .selectOne()
                .from(like)
                .where(
                        LikeFilterPredicate.userIdEq(userId),
                        LikeFilterPredicate.targetIdEq(targetId),
                        LikeFilterPredicate.targetTypeEq(targetType)
                )
                .fetchFirst();

        return result != null;
    }

    /**
     * 지정한 사용자와 타겟 타입에 대해, 주어진 타겟 ID 목록 중 사용자가 좋아요를 누른 타겟 ID 목록을 반환합니다.
     *
     * @param userId     사용자 ID
     * @param targetIds  확인할 타겟 ID 목록
     * @param targetType 타겟 타입
     * @return 사용자가 좋아요를 누른 타겟 ID 목록. 입력값이 null이거나 비어 있으면 빈 리스트를 반환합니다.
     */
    @Override
    public List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType) {
        if (userId == null || targetIds == null || targetIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .select(like.targetId)
                .from(like)
                .where(
                        LikeFilterPredicate.userIdEq(userId),
                        LikeFilterPredicate.containTargetIdEq(targetIds),
                        LikeFilterPredicate.targetTypeEq(targetType)
                )
                .fetch();
    }
}
