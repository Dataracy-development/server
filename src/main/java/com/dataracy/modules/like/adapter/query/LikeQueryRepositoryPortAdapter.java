package com.dataracy.modules.like.adapter.query;

import com.dataracy.modules.like.adapter.jpa.entity.QLikeEntity;
import com.dataracy.modules.like.adapter.query.predicates.LikeFilterPredicate;
import com.dataracy.modules.like.application.port.query.LikeQueryRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


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
}
