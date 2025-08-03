package com.dataracy.modules.project.application.dto.response.search;

public record SimilarProjectResponse(
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
