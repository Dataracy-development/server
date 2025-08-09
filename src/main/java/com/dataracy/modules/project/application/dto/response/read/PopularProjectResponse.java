package com.dataracy.modules.project.application.dto.response.read;

public record PopularProjectResponse(
        Long id,
        String title,
        String content,
        String username,
        String projectThumbnailUrl,
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount
) {}
