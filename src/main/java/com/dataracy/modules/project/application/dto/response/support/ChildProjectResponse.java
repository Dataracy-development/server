package com.dataracy.modules.project.application.dto.response.support;

/**
 *
 * @param id
 * @param title
 * @param content
 * @param username
 * @param commentCount
 * @param likeCount
 */
public record ChildProjectResponse(
        Long id,
        String title,
        String content,
        String username,
        Long commentCount,
        Long likeCount
) {}
