package com.dataracy.modules.like.application.port.in;

import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;

public interface TargetLikeUseCase {
    void targetLike(Long userId, TargetLikeRequest requestDto);
}
