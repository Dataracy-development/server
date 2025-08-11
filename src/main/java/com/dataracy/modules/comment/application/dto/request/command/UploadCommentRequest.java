package com.dataracy.modules.comment.application.dto.request.command;

/**
 * 댓글 업로드 애플리케이션 요청 DTO
 *
 * @param content 댓글 내용
 * @param parentCommentId 부모 댓글 ID(대댓글인 경우)
 */
public record UploadCommentRequest(
        String content,
        Long parentCommentId
) {}
