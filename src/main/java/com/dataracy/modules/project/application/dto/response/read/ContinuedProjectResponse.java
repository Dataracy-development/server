package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

/**
 *요청
 * @param id
 * @param title
 * @param username
 * @param userProfileUrl
 * @param projectThumbnailUrl
 * @param topicLabel
 * @param authorLevelLabel
 * @param commentCount
 * @param likeCount
 * @param viewCount
 * @param createdAt
 */
public record ContinuedProjectResponse(
        Long id,
        String title,
        String username,
        String userProfileUrl,
        String projectThumbnailUrl,
        String topicLabel,
        String authorLevelLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
