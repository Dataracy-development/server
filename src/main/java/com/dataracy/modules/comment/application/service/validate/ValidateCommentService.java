package com.dataracy.modules.comment.application.service.validate;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.comment.application.port.in.query.validate.ValidateCommentUseCase;
import com.dataracy.modules.comment.application.port.out.validate.ValidateCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateCommentService implements ValidateCommentUseCase {
  private final ValidateCommentPort validateCommentPort;

  // Use Case 상수 정의
  private static final String VALIDATE_COMMENT_USE_CASE = "ValidateCommentUseCase";

  // 메시지 상수 정의
  private static final String COMMENT_NOT_FOUND_MESSAGE = "해당 댓글이 존재하지 않습니다. commentId=";

  /**
   * 주어진 댓글 ID에 해당하는 댓글의 존재 여부를 검증합니다. 댓글이 존재하지 않을 경우 CommentException을 발생시킵니다.
   *
   * @param commentId 존재 여부를 확인할 댓글의 ID
   * @throws CommentException 댓글이 존재하지 않을 때 발생
   */
  @Override
  @Transactional(readOnly = true)
  public void validateComment(Long commentId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                VALIDATE_COMMENT_USE_CASE, "주어진 댓글 ID에 해당하는 댓글 존재 서비스 시작 commentId=" + commentId);
    boolean isValidated = validateCommentPort.existsByCommentId(commentId);

    if (!isValidated) {
      LoggerFactory.service()
          .logWarning(VALIDATE_COMMENT_USE_CASE, COMMENT_NOT_FOUND_MESSAGE + commentId);
      throw new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);
    }
    LoggerFactory.service()
        .logSuccess(
            VALIDATE_COMMENT_USE_CASE,
            "주어진 댓글 ID에 해당하는 댓글 존재 서비스 종료 commentId=" + commentId,
            startTime);
  }
}
