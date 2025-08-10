package com.dataracy.modules.project.application.dto.response.search;

/**
 *요청
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
 */
public record SimilarProjectResponse(
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
