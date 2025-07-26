package com.dataracy.modules.comment.application.service.command;

import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;
import com.dataracy.modules.comment.application.port.in.CommentDeleteUseCase;
import com.dataracy.modules.comment.application.port.in.CommentModifyUseCase;
import com.dataracy.modules.comment.application.port.in.CommentUploadUseCase;
import com.dataracy.modules.comment.application.port.out.CommentRepositoryPort;
import com.dataracy.modules.comment.application.port.query.CommentQueryRepositoryPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements
        CommentUploadUseCase,
        CommentModifyUseCase,
        CommentDeleteUseCase
{
    private final CommentQueryRepositoryPort commentQueryRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;

    @Override
    public void upload(Long projectId, Long userId, CommentUploadRequest requestDto) {
        Long parentId = requestDto.parentCommentId();

        if (parentId != null) {
            Comment parent = commentQueryRepositoryPort.findCommentById(parentId)
                    .orElseThrow(() -> new CommentException(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT));
            if (parent.getParentCommentId() != null) {
                throw new CommentException(CommentErrorStatus.FORBIDDEN_REPLY_COMMENT);
            }
        }

        Comment comment = Comment.of(
                null,
                projectId,
                userId,
                requestDto.content(),
                parentId,
                0L,
                null
        );

        commentRepositoryPort.upload(comment);
    }

    @Override
    public void modify(Long projectId, Long commentId, Long userId, CommentModifyRequest requestDto) {
        commentRepositoryPort.modify(projectId, commentId, requestDto);
    }

    @Override
    public void delete(Long projectId, Long commentId) {
        commentRepositoryPort.delete(projectId, commentId);
    }
}
