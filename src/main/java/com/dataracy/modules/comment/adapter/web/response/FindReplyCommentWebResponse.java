package com.dataracy.modules.comment.adapter.web.response;

import java.time.LocalDateTime;

public record FindReplyCommentWebResponse(
        Long id,
        String username,
        String authorLevelLabel,
        String userThumbnailUrl,
        String content,
        Long likeCount,
        LocalDateTime createdAt
) {}
