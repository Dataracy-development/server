package com.dataracy.modules.project.application.dto.response.read;

import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 *요청
 * @param id
 * @param title
 * @param username
 * @param userIntroductionText
 * @param authorLevelLabel
 * @param occupationLabel
 * @param topicLabel
 * @param analysisPurposeLabel
 * @param dataSourceLabel
 * @param isContinue
 * @param parentProjectId
 * @param content
 * @param projectThumbnailUrl
 * @param createdAt
 * @param commentCount
 * @param likeCount
 * @param viewCount
 * @param isLiked
 * @param hasChild
 * @param connectedDataSets
 */
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
        List<ProjectConnectedDataResponse> connectedDataSets
) {}
