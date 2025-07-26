package com.dataracy.modules.comment.application.dto.response;

import java.time.LocalDateTime;

public record FindReplyCommentResponse(
        Long id,
        String username,
        String authorLevelLabel,
        String userThumbnailUrl,
        String content,
        Long likeCount,
        LocalDateTime createdAt,
        boolean isLiked
) {}
