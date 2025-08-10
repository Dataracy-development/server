package com.dataracy.modules.project.application.dto.response.search;

import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @param id
 * @param title
 * @param content
 * @param username
 * @param projectThumbnailUrl
 * @param topicLabel
 * @param analysisPurposeLabel
 * @param dataSourceLabel
 * @param authorLevelLabel
 * @param commentCount
 * @param likeCount
 * @param viewCount
 * @param createdAt
 * @param childProjects
 */
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
