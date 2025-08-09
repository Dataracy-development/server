package com.dataracy.modules.project.application.dto.response.search;

import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;

import java.time.LocalDateTime;
import java.util.List;

public record FilteredProjectResponse(
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
        Long viewCount,
        LocalDateTime createdAt,
        List<ChildProjectResponse> childProjects
) {}
