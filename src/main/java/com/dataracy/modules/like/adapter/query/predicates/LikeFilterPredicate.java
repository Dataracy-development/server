package com.dataracy.modules.like.adapter.query.predicates;

import com.dataracy.modules.like.domain.enums.TargetType;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

import static com.dataracy.modules.like.adapter.jpa.entity.QLikeEntity.likeEntity;

public class LikeFilterPredicate {
private LikeFilterPredicate() {}

    public static BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : likeEntity.userId.eq(userId);
    }

    public static BooleanExpression targetIdEq(Long targetId) {
        return targetId == null ? null : likeEntity.targetId.eq(targetId);
    }

    public static BooleanExpression targetTypeEq(TargetType targetType) {
        return targetType == null ? null : likeEntity.targetType.eq(targetType);
    }

    public static BooleanExpression containTargetIdEq(List<Long> targetIds) {
        return targetIds == null ? null : likeEntity.targetId.in(targetIds);
    }
}
