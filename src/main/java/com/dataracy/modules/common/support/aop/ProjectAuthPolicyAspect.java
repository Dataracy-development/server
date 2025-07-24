package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.project.application.port.in.FindUserIdByProjectIdUseCase;
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

    @Before("@annotation(com.dataracy.modules.common.support.annotation.AuthorizationProjectEdit) && args(projectId,..)")
    public void checkProjectEditPermission(Long projectId) {
        Long authenticatedUserId = SecurityContextProvider.getAuthenticatedUserId();
        Long ownerId = findUserIdByProjectIdUseCase.findUserIdByProjectId(projectId);

        log.error("프로젝트 작성자만 수정 및 삭제가 가능합니다.");
        if (!ownerId.equals(authenticatedUserId)) {
            throw new ProjectException(ProjectErrorStatus.NOT_MATCH_CREATOR);
        }
    }
}
