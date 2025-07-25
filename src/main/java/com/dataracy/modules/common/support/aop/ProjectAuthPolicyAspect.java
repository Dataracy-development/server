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

    /**
     * 프로젝트의 수정, 삭제 또는 복원 작업에 대해 현재 인증된 사용자가 프로젝트 작성자인지 확인합니다.
     *
     * @param annotation 프로젝트 편집 권한 검증에 사용되는 어노테이션 정보
     * @param projectId 권한 검증 대상 프로젝트의 ID
     * @throws ProjectException 인증된 사용자가 프로젝트 작성자가 아닐 경우 발생하며, 작업이 차단됩니다.
     */
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
