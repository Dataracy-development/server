package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

public record ConnectedProjectResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
