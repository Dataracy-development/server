package com.dataracy.modules.like.adapter.jpa.mapper;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.model.Like;

public final class LikeEntityMapper {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private LikeEntityMapper() {}

    private static Like toDomain(LikeEntity entity) {
        if (entity == null) return null;

        return Like.of(
                entity.getId(),
                entity.getTargetId(),
                entity.getTargetType(),
                entity.getUserId()
        );
    }

    public static LikeEntity toEntity(Like like) {
        if (like == null) return null;

        return LikeEntity.of(
                like.getTargetId(),
                like.getTargetType(),
                like.getUserId()
        );
    }
}
