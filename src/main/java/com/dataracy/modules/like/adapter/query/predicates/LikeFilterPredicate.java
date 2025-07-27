package com.dataracy.modules.like.adapter.query.predicates;

import com.dataracy.modules.like.domain.enums.TargetType;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

import static com.dataracy.modules.like.adapter.jpa.entity.QLikeEntity.likeEntity;

public class LikeFilterPredicate {
/**
 * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private LikeFilterPredicate() {}

    /**
     * 주어진 userId와 일치하는 LikeEntity를 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param userId 필터링할 사용자 ID
     * @return userId가 일치하는 조건의 BooleanExpression, userId가 null이면 null 반환
     */
    public static BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : likeEntity.userId.eq(userId);
    }

    /**
     * 주어진 targetId와 일치하는 LikeEntity의 targetId 필터 조건을 생성합니다.
     *
     * @param targetId 필터링할 대상의 ID
     * @return targetId가 일치하는 조건의 BooleanExpression, targetId가 null이면 null 반환
     */
    public static BooleanExpression targetIdEq(Long targetId) {
        return targetId == null ? null : likeEntity.targetId.eq(targetId);
    }

    /**
     * 주어진 targetType과 일치하는 LikeEntity를 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param targetType 필터링할 대상의 TargetType
     * @return targetType이 null이 아니면 해당 targetType과 일치하는 BooleanExpression, null이면 아무 필터도 적용하지 않음
     */
    public static BooleanExpression targetTypeEq(TargetType targetType) {
        return targetType == null ? null : likeEntity.targetType.eq(targetType);
    }

    /**
     * targetId가 주어진 ID 목록에 포함되는지 확인하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param targetIds 포함 여부를 확인할 targetId의 리스트
     * @return targetId가 targetIds에 포함될 경우의 BooleanExpression, targetIds가 null이면 null 반환
     */
    public static BooleanExpression containTargetIdEq(List<Long> targetIds) {
        return targetIds == null ? null : likeEntity.targetId.in(targetIds);
    }
}
