package com.dataracy.modules.project.application.dto.response.search;

/**
 *요청
 * @param id
 * @param title
 * @param username
 * @param projectThumbnailUrl
 */
public record RealTimeProjectResponse(
        Long id,
        String title,
        String username,
        String projectThumbnailUrl
) {}
