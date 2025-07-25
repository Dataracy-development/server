package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.common.support.annotation.AuthorizationProjectEdit;
import com.dataracy.modules.project.application.port.in.FindUserIdByProjectIdUseCase;
import com.dataracy.modules.project.application.port.in.FindUserIdIncludingDeletedProjectUseCase;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
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
public class ProjectAuthPolicyAspect {

    private final FindUserIdByProjectIdUseCase findUserIdByProjectIdUseCase;
    private final FindUserIdIncludingDeletedProjectUseCase findUserIdIncludingDeletedProjectUseCase;

    @Before("@annotation(annotation) && args(projectId,..)")
    public void checkProjectEditPermission(AuthorizationProjectEdit annotation, Long projectId) {
        Long authenticatedUserId = SecurityContextProvider.getAuthenticatedUserId();

        Long ownerId = annotation.restore()
                ? findUserIdIncludingDeletedProjectUseCase.findUserIdIncludingDeleted(projectId)
                : findUserIdByProjectIdUseCase.findUserIdByProjectId(projectId);

        if (!ownerId.equals(authenticatedUserId)) {
            log.error("프로젝트 작성자만 {}할 수 있습니다.",
                    annotation.restore() ? "복원" : "수정 및 삭제");
            throw new ProjectException(ProjectErrorStatus.NOT_MATCH_CREATOR);
        }
    }
}
