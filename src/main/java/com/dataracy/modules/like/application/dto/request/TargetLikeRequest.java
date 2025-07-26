package com.dataracy.modules.like.application.dto.request;

import com.dataracy.modules.like.domain.enums.TargetType;

public record TargetLikeRequest(
        Long targetId,
        TargetType targetType,
        Boolean isLiked
) {}
