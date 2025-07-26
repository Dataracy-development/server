package com.dataracy.modules.comment.application.port.out;

import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.domain.model.Comment;

public interface CommentRepositoryPort {
    void upload(Comment comment);
    void modify(Long projectId, Long commentId, CommentModifyRequest requestDto);
    void delete(Long projectId, Long commentId);}
