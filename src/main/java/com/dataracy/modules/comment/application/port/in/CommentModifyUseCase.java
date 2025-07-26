package com.dataracy.modules.comment.application.port.in;

import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;

public interface CommentModifyUseCase {
    void modify(Long projectId, Long commentId, CommentModifyRequest requestDto);
}
