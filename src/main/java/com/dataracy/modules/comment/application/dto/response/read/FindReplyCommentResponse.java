package com.dataracy.modules.comment.application.dto.response.read;

import java.time.LocalDateTime;

/**
 *요청
 * @param id
 * @param username
 * @param authorLevelLabel
 * @param userProfileUrl
 * @param content
 * @param likeCount
 * @param createdAt
 * @param isLiked
 */
public record FindReplyCommentResponse(
        Long id,
        String username,
        String authorLevelLabel,
        String userProfileUrl,
        String content,
        Long likeCount,
        LocalDateTime createdAt,
        boolean isLiked
) {}
