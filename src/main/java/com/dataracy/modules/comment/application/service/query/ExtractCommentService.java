package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.port.in.query.extractor.FindUserIdByCommentIdUseCase;
import com.dataracy.modules.comment.application.port.out.query.extractor.ExtractCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ExtractCommentService implements FindUserIdByCommentIdUseCase {
    private final ExtractCommentPort extractCommentPort;

    /**
     * 주어진 댓글 ID로 해당 댓글 작성자의 사용자 ID를 반환합니다.
     * 댓글이 존재하지 않으면 CommentException이 발생합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글 작성자의 사용자 ID
     * @throws CommentException 댓글이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public Long findUserIdByCommentId(Long commentId) {
        Instant startTime = LoggerFactory.service().logStart("FindUserIdByCommentIdUseCase", "댓글 ID에 해당하는 사용자의 ID 반환 서비스 시작 commentId=" + commentId);
        Long userId = extractCommentPort.findUserIdByCommentId(commentId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindUserIdByCommentIdUseCase", "해당 댓글이 존재하지 않습니다. commentId=" + commentId);
                    return new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);
                });
        LoggerFactory.service().logSuccess("FindUserIdByCommentIdUseCase", "댓글 ID에 해당하는 사용자의 ID 반환 서비스 종료 commentId=" + commentId, startTime);
        return userId;
    }
}
