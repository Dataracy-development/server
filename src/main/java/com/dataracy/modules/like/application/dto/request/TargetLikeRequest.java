package com.dataracy.modules.like.application.dto.request;

/**
 * 좋아요 대상에 대한 요청 DTO.
 *
 * @param targetId 좋아요 대상의 식별자
 * @param targetType 대상 유형 (예: PROJECT, COMMENT 등)
 * @param previouslyLiked 사용자가 과거에 좋아요를 눌렀는지 여부
 */
public record TargetLikeRequest(
        Long targetId,
        String targetType,
        boolean previouslyLiked
) {}
