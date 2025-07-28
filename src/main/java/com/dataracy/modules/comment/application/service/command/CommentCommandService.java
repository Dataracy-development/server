package com.dataracy.modules.comment.application.service.command;

import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;
import com.dataracy.modules.comment.application.port.in.CommentDeleteUseCase;
import com.dataracy.modules.comment.application.port.in.CommentModifyUseCase;
import com.dataracy.modules.comment.application.port.in.CommentUploadUseCase;
import com.dataracy.modules.comment.application.port.out.CommentKafkaProducerPort;
import com.dataracy.modules.comment.application.port.out.CommentRepositoryPort;
import com.dataracy.modules.comment.application.port.query.CommentQueryRepositoryPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements
        CommentUploadUseCase,
        CommentModifyUseCase,
        CommentDeleteUseCase
{
    private final CommentQueryRepositoryPort commentQueryRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;
    private final CommentKafkaProducerPort commentKafkaProducerPort;

    /**
     * 프로젝트에 새로운 댓글을 등록합니다.
     *
     * 부모 댓글 ID가 제공된 경우, 해당 부모 댓글의 존재 여부와 대댓글 제한(1단계까지만 허용)을 검증합니다.
     * 부모 댓글이 존재하지 않거나 이미 대댓글인 경우 예외가 발생합니다.
     * 댓글 등록 후, 프로젝트 ID를 기반으로 댓글 등록 이벤트를 발행합니다.
     *
     * @param projectId 댓글이 등록될 프로젝트의 ID
     * @param userId 댓글을 작성하는 사용자의 ID
     * @param requestDto 댓글 등록 요청 정보
     * @throws CommentException 부모 댓글이 존재하지 않거나 대댓글 제한을 위반한 경우 발생합니다.
     */
    @Override
    @Transactional
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

        Comment savedComment = commentRepositoryPort.upload(comment);
        commentKafkaProducerPort.sendCommentUploadedEvent(savedComment.getProjectId());
    }

    /**
     * 주어진 프로젝트와 댓글 ID에 해당하는 댓글의 내용을 수정합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param commentId 수정할 댓글의 ID
     * @param requestDto 댓글 수정 요청 정보
     */
    @Override
    @Transactional
    public void modify(Long projectId, Long commentId, CommentModifyRequest requestDto) {
        commentRepositoryPort.modify(projectId, commentId, requestDto);
    }

    /**
     * 프로젝트 내에서 특정 댓글을 삭제하고, 삭제 이벤트를 발행합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param commentId 삭제할 댓글의 ID
     */
    @Override
    @Transactional
    public void delete(Long projectId, Long commentId) {
        commentRepositoryPort.delete(projectId, commentId);
        commentKafkaProducerPort.sendCommentDeletedEvent(projectId);
    }
}
