package com.dataracy.modules.like.application.dto.request;

/**
 *
 * @param targetId
 * @param targetType
 * @param previouslyLiked
 */
public record TargetLikeRequest(
        Long targetId,
        String targetType,
        Boolean previouslyLiked
) {}
