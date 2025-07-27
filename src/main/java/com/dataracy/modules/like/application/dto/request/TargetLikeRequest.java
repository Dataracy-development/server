package com.dataracy.modules.like.application.dto.request;

public record TargetLikeRequest(
        Long targetId,
        String targetType,
        Boolean previouslyLiked
) {}
