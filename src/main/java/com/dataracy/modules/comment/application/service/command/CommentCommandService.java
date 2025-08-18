package com.dataracy.modules.comment.application.service.command;

import com.dataracy.modules.comment.application.dto.request.command.ModifyCommentRequest;
import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;
import com.dataracy.modules.comment.application.port.in.command.content.DeleteCommentUseCase;
import com.dataracy.modules.comment.application.port.in.command.content.ModifyCommentUseCase;
import com.dataracy.modules.comment.application.port.in.command.content.UploadCommentUseCase;
import com.dataracy.modules.comment.application.port.out.command.create.UploadCommentPort;
import com.dataracy.modules.comment.application.port.out.command.delete.DeleteCommentPort;
import com.dataracy.modules.comment.application.port.out.command.event.SendCommentEventPort;
import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentPort;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements
        UploadCommentUseCase,
        ModifyCommentUseCase,
        DeleteCommentUseCase
{
    private final ReadCommentPort readCommentPort;
    private final UploadCommentPort uploadCommentPort;
    private final UpdateCommentPort updateCommentPort;
    private final DeleteCommentPort deleteCommentPort;

    private final SendCommentEventPort sendCommentEventPort;

    /**
     * 프로젝트에 댓글을 등록합니다.
     * 요청 DTO에 부모 댓글 ID가 있을 경우, 해당 부모 댓글의 존재 여부와 대댓글 제한(1단계까지만 허용)을 검증합니다.
     * 부모 댓글이 유효하지 않거나, 대댓글 제한을 위반하면 예외가 발생합니다.
     * 댓글 등록 후, 댓글 수 싱크를 위해 프로젝트 ID를 기반으로 댓글 등록 이벤트를 발행합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param userId 댓글 작성자의 ID
     * @param requestDto 댓글 등록 요청 정보
     * @throws CommentException 부모 댓글이 존재하지 않거나 대댓글 제한을 위반한 경우 발생합니다.
     */
    @Override
    @Transactional
    public UploadCommentResponse uploadComment(Long projectId, Long userId, UploadCommentRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("UploadCommentUseCase", "댓글 작성 서비스 시작 projectId=" + projectId);

        Long parentId = requestDto.parentCommentId();

        if (parentId != null) {
            Comment parent = readCommentPort.findCommentById(parentId)
                    .orElseThrow(() -> {
                        LoggerFactory.service().logWarning("UploadCommentUseCase", "답글 작성에 대하여 해당 부모 댓글이 존재하지 않습니다. commentId=" + parentId);
                        return new CommentException(CommentErrorStatus.NOT_FOUND_PARENT_COMMENT);
                    });
            if (parent.getParentCommentId() != null) {
                LoggerFactory.service().logWarning("UploadCommentUseCase", "답글에 대하여 다시 답글을 작성할 순 없습니다. commentId=" + parent.getParentCommentId());
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

        Comment savedComment = uploadCommentPort.uploadComment(comment);
        sendCommentEventPort.sendCommentUploadedEvent(savedComment.getProjectId());

        LoggerFactory.service().logSuccess("UploadCommentUseCase", "댓글 작성 서비스 종료 projectId=" + projectId, startTime);
        return new UploadCommentResponse(savedComment.getId());
    }

    /**
     * 프로젝트 내 특정 댓글의 내용을 수정합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID입니다.
     * @param commentId 수정할 댓글의 ID입니다.
     * @param requestDto 댓글 수정에 필요한 정보가 담긴 요청 객체입니다.
     */
    @Override
    @Transactional
    public void modifyComment(Long projectId, Long commentId, ModifyCommentRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("ModifyCommentUseCase", "댓글 수정 서비스 시작 projectId=" + projectId + ", commentId=" + commentId);
        updateCommentPort.modifyComment(projectId, commentId, requestDto);
        LoggerFactory.service().logSuccess("ModifyCommentUseCase", "댓글 수정 서비스 종료 projectId=" + projectId + ", commentId=" + commentId, startTime);
    }

    /**
     * 프로젝트 내에서 특정 댓글을 삭제하고, 댓글 삭제 이벤트를 발행합니다.
     *
     * @param projectId 댓글이 속한 프로젝트의 ID
     * @param commentId 삭제할 댓글의 ID
     */
    @Override
    @Transactional
    public void deleteComment(Long projectId, Long commentId) {
        Instant startTime = LoggerFactory.service().logStart("DeleteCommentUseCase", "댓글 삭제 서비스 시작 projectId=" + projectId + ", commentId=" + commentId);
        deleteCommentPort.deleteComment(projectId, commentId);
        sendCommentEventPort.sendCommentDeletedEvent(projectId);
        LoggerFactory.service().logSuccess("DeleteCommentUseCase", "댓글 삭제 서비스 종료 projectId=" + projectId + ", commentId=" + commentId, startTime);
    }
}
