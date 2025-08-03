package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

public record ContinuedProjectResponse(
        Long id,
        String title,
        String username,
        String userThumbnailUrl,
        String fileUrl,
        String topicLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
