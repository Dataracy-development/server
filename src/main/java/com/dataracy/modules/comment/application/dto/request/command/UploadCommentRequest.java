package com.dataracy.modules.comment.application.dto.request.command;

/**
 *요청
 * @param content
 * @param parentCommentId
 */
public record UploadCommentRequest(
        String content,
        Long parentCommentId
) {}
