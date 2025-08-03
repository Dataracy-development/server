package com.dataracy.modules.project.application.dto.response.support;

public record ChildProjectResponse(
        Long id,
        String title,
        String content,
        String username,
        Long commentCount,
        Long likeCount
) {}
