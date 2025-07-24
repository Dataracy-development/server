package com.dataracy.modules.project.adapter.web.response;

import java.time.LocalDateTime;

public record ContinueProjectWebResponse(
        Long id,
        String title,
        String username,
        String userThumnbailUrl,
        String fileUrl,
        String topicLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
