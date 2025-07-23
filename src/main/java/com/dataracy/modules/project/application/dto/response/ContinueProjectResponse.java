package com.dataracy.modules.project.application.dto.response;

import java.time.LocalDateTime;

public record ContinueProjectResponse(
        Long id,
        String title,
        String username,
        String userThumnailUrl,
        String fileUrl,
        String topicLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
