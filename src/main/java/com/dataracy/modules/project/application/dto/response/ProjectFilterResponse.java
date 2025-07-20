package com.dataracy.modules.project.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectFilterResponse(
        Long id,
        String title,
        String content,
        String username,
        String fileUrl,
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt,
        List<ContinueProjectResponse> continueProjects
) {
    public record ContinueProjectResponse(
            Long id,
            String title,
            String content,
            String username,
            Long commentCount,
            Long likeCount
    ){}
}
