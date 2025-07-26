package com.dataracy.modules.like.application.port.query;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface LikeQueryRepositoryPort {
    boolean isLikedTarget(Long userId, Long targetId, TargetType targetType);
}
