package com.dataracy.modules.comment.application.port.query;

import com.dataracy.modules.comment.application.dto.response.CommentWithReplyCountResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentQueryRepositoryPort {
Optional<Comment> findCommentById(Long commentId);
    Page<CommentWithReplyCountResponse> findComments(Long projectId, Pageable pageable);
}
