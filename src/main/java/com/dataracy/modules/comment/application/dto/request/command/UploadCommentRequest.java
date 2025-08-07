package com.dataracy.modules.comment.application.dto.request.command;

public record UploadCommentRequest(
        String content,
        Long parentCommentId
) {}
