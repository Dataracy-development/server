package com.dataracy.modules.like.application.port.in;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface ValidateTargetLikeUseCase {
    boolean hasUserLikedTarget(Long userId, Long targetId, TargetType targetType);
}
