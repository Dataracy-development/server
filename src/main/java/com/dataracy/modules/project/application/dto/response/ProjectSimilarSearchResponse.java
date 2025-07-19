package com.dataracy.modules.project.application.dto.response;

public record ProjectSimilarSearchResponse(
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
        Long viewCount
) {}
