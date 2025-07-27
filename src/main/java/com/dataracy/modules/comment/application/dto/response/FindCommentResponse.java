package com.dataracy.modules.comment.application.dto.response;

import java.time.LocalDateTime;

public record FindCommentResponse(
        Long id,
        String username,
        String authorLevelLabel,
        String userThumbnailUrl,
        String content,
        Long likeCount,
        Long childCommentCount,
        LocalDateTime createdAt,
        boolean isLiked
) {}
