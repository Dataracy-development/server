package com.dataracy.modules.comment.adapter.web.response;

import java.time.LocalDateTime;

public record FindCommentWebResponse(
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
