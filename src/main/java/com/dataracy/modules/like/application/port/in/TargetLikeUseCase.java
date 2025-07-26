package com.dataracy.modules.like.application.port.in;

import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.domain.enums.TargetType;

public interface TargetLikeUseCase {
    void targetLike(Long userId, TargetLikeRequest requestDto, TargetType targetType);
}
