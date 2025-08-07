package com.dataracy.modules.comment.application.service.validate;

import com.dataracy.modules.comment.application.port.in.query.validate.ValidateCommentUseCase;
import com.dataracy.modules.comment.application.port.out.validate.ValidateCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ValidateCommentService implements ValidateCommentUseCase {
    private final ValidateCommentPort validateCommentPort;

    /**
     * 주어진 댓글 ID에 해당하는 댓글이 존재하는지 검증합니다.
     *
     * 댓글이 존재하지 않을 경우 {@code CommentException}이 발생합니다.
     *
     * @param commentId 검증할 댓글의 ID
     * @throws CommentException 댓글이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateComment(Long commentId) {
        Instant startTime = LoggerFactory.service().logStart("ValidateCommentUseCase", "주어진 댓글 ID에 해당하는 댓글 존재 서비스 시작 commentId=" + commentId);
        boolean isValidated = validateCommentPort.existsByCommentId(commentId);

        if (!isValidated) {
            LoggerFactory.service().logWarning("ValidateCommentUseCase", "해당 댓글이 존재하지 않습니다. commentId=" + commentId);
            throw new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);
        }
        LoggerFactory.service().logSuccess("ValidateCommentUseCase", "주어진 댓글 ID에 해당하는 댓글 존재 서비스 종료 commentId=" + commentId, startTime);
    }
}
