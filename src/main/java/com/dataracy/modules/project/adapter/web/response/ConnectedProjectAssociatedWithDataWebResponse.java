package com.dataracy.modules.project.adapter.web.response;

import java.time.LocalDateTime;

public record ConnectedProjectAssociatedWithDataWebResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
