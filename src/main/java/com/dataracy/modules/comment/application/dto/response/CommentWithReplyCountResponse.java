package com.dataracy.modules.comment.application.dto.response;

import com.dataracy.modules.comment.domain.model.Comment;

public record CommentWithReplyCountResponse(
        Comment comment,
        long replyCount
) {}
