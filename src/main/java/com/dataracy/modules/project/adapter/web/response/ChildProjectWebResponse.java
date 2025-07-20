package com.dataracy.modules.project.adapter.web.response;

public record ChildProjectWebResponse(
        Long id,
        String title,
        String content,
        String username,
        Long commentCount,
        Long likeCount
) {}
