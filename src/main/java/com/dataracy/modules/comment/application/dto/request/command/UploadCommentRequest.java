package com.dataracy.modules.comment.application.dto.request.command;

/**
 *
 * @param content
 * @param parentCommentId
 */
public record UploadCommentRequest(
        String content,
        Long parentCommentId
) {}
