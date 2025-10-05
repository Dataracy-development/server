package com.dataracy.modules.common.support.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.dataracy.modules.comment.application.port.in.query.extractor.FindUserIdByCommentIdUseCase;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationCommentEdit;
import com.dataracy.modules.security.handler.SecurityContextProvider;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class CommentAuthPolicyAspect {
  private final FindUserIdByCommentIdUseCase findUserIdByCommentIdUseCase;

  /**
   * 댓글 수정 또는 삭제 요청 시, 현재 인증된 사용자가 해당 댓글의 작성자인지 검증합니다. 인증된 사용자가 댓글 작성자가 아닐 경우, 댓글 수정 또는 삭제를 차단하고 예외를
   * 발생시킵니다.
   *
   * @param annotation 댓글 수정 권한 검증을 위한 어노테이션
   * @param commentId 권한 검증 대상 댓글의 ID
   * @throws CommentException 인증된 사용자가 댓글 작성자가 아닐 때 발생
   */
  @Before("@annotation(annotation) && args(commentId,..)")
  public void checkCommentEditPermission(AuthorizationCommentEdit annotation, Long commentId) {
    Long authenticatedUserId = SecurityContextProvider.getAuthenticatedUserId();
    Long ownerId = findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId);

    if (!ownerId.equals(authenticatedUserId)) {
      LoggerFactory.common().logWarning("Comment", "댓글 작성자만 수정 및 삭제 할 수 있습니다.");
      throw new CommentException(CommentErrorStatus.NOT_MATCH_CREATOR);
    }
  }
}
