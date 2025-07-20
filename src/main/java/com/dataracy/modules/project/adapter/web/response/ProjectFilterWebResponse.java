package com.dataracy.modules.project.adapter.web.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectFilterWebResponse(
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
        List<ChildProjectWebResponse> childProjects
) {}
