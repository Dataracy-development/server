package com.dataracy.modules.project.application.dto.response;

import java.time.LocalDateTime;

public record ConnectedProjectAssociatedWithDataResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
