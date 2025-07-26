package com.dataracy.modules.comment.application.port.in;

import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;

public interface CommentUploadUseCase {
    void upload(Long projectId, Long userId, CommentUploadRequest requestDto);
}
