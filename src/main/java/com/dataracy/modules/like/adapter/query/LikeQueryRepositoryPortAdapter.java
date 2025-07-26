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
