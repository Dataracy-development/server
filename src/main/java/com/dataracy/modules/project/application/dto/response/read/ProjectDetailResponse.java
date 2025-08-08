package com.dataracy.modules.project.application.dto.response.read;

import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectDetailResponse(
        Long id,
        String title,
        String username,
        String userIntroductionText,
        String authorLevelLabel,
        String occupationLabel,
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        boolean isContinue,
        Long parentProjectId,
        String content,
        String projectThumbnailUrl,
        LocalDateTime createdAt,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        boolean isLiked,
        boolean hasChild,
        boolean hasDataSet,
        List<ProjectConnectedDataResponse> connectedDataSets
) {}
