package com.dataracy.modules.project.application.dto.response.read;

import java.time.LocalDateTime;

/**
 *
 * @param id
 * @param title
 * @param username
 * @param topicLabel
 * @param commentCount
 * @param likeCount
 * @param viewCount
 * @param createdAt
 */
public record ConnectedProjectResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        Long commentCount,
        Long likeCount,
        Long viewCount,
        LocalDateTime createdAt
) {}
