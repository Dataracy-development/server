package com.dataracy.modules.project.adapter.web.response;

import java.time.LocalDateTime;

public record ProjectDetailWebResponse(
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
        boolean hasChild,
        boolean hasDataSet
) {}
