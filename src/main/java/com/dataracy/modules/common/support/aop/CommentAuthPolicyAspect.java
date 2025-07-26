package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.comment.application.port.in.FindUserIdByCommentIdUseCase;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.support.annotation.AuthorizationCommentEdit;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentAuthPolicyAspect {

    private final FindUserIdByCommentIdUseCase findUserIdByCommentIdUseCase;

    /**
     * 댓글 수정 또는 삭제 요청 시, 현재 인증된 사용자가 해당 댓글의 작성자인지 확인합니다.
     *
     * 댓글의 작성자와 인증된 사용자가 다를 경우, 댓글 수정 또는 삭제를 허용하지 않고 예외를 발생시킵니다.
     *
     * @param annotation 댓글 수정 권한 검증을 위한 어노테이션
     * @param commentId 권한 검증 대상이 되는 댓글의 ID
     * @throws CommentException 인증된 사용자가 댓글 작성자가 아닐 경우 발생
     */
    @Before("@annotation(annotation) && args(commentId,..)")
    public void checkCommentEditPermission(AuthorizationCommentEdit annotation, Long commentId) {
        Long authenticatedUserId = SecurityContextProvider.getAuthenticatedUserId();
        Long ownerId = findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId);

        if (!ownerId.equals(authenticatedUserId)) {
            log.error("댓글 작성자만 수정 및 삭제 할 수 있습니다.");
            throw new CommentException(CommentErrorStatus.NOT_MATCH_CREATOR);
        }
    }
}
