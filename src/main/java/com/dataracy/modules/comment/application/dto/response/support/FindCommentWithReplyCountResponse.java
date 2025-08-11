package com.dataracy.modules.comment.application.dto.response.support;

import com.dataracy.modules.comment.domain.model.Comment;

/**
 * 댓글에 달린 답글 수를 함께 반환한 보조 DTO
 *
 * @param comment 댓글 도메인 객체
 * @param replyCount 댓글에 달린 답글 수
 */
public record FindCommentWithReplyCountResponse(
        Comment comment,
        long replyCount
) {}
