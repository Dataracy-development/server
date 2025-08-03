package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

public record ProjectDetailResponse(
        Long id,
        String title,
        String username,
        String authorLevelLabel,
        String occupationLabel,
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        boolean isContinue,
        Long parentProjectId,
        String content,
        String fileUrl,
        LocalDateTime createdAt,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        boolean isLiked,
        boolean hasChild,
        boolean hasDataSet
) {}
