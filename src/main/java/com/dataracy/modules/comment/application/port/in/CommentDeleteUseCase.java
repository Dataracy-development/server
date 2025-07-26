package com.dataracy.modules.comment.application.port.in;

public interface CommentDeleteUseCase {
    void delete(Long projectId, Long commentId);
}
