package com.dataracy.modules.comment.application.port.in;

public interface FindUserIdByCommentIdUseCase {
    Long findUserIdByCommentId(Long commentId);
}
