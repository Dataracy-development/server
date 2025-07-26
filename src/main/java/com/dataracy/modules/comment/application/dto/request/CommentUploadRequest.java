package com.dataracy.modules.comment.application.dto.request;

public record CommentUploadRequest(
        String content,
        Long parentCommentId
) {}
