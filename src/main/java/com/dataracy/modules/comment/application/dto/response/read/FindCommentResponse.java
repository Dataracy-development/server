package com.dataracy.modules.comment.application.dto.response.read;

import java.time.LocalDateTime;

/**
 *
 * @param id
 * @param username
 * @param authorLevelLabel
 * @param userProfileUrl
 * @param content
 * @param likeCount
 * @param childCommentCount
 * @param createdAt
 * @param isLiked
 */
public record FindCommentResponse(
        Long id,
        String username,
        String authorLevelLabel,
        String userProfileUrl,
        String content,
        Long likeCount,
        Long childCommentCount,
        LocalDateTime createdAt,
        boolean isLiked
) {}
