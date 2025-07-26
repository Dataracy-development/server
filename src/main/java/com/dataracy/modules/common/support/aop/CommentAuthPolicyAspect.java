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
