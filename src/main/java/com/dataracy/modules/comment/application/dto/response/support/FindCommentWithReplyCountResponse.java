package com.dataracy.modules.comment.application.dto.response.support;

import com.dataracy.modules.comment.domain.model.Comment;

public record FindCommentWithReplyCountResponse(
        Comment comment,
        long replyCount
) {}
