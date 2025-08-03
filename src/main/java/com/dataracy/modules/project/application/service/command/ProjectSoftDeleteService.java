package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.in.command.content.DeleteProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.RestoreProjectUseCase;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ProjectSoftDeleteService implements
        DeleteProjectUseCase,
        RestoreProjectUseCase
{
    private final SoftDeleteProjectPort softDeleteProjectDbPort;
    private final SoftDeleteProjectPort softDeleteProjectEsPort;

    public ProjectSoftDeleteService(
            @Qualifier("softDeleteProjectDbAdapter") SoftDeleteProjectPort softDeleteProjectDbPort,
            @Qualifier("softDeleteProjectEsAdapter") SoftDeleteProjectPort softDeleteProjectEsPort
    ) {
        this.softDeleteProjectDbPort = softDeleteProjectDbPort;
        this.softDeleteProjectEsPort = softDeleteProjectEsPort;
    }

    /**
     * 프로젝트를 삭제 상태로 변경합니다.
     *
     * 데이터베이스에서 프로젝트를 삭제 처리하고, Elasticsearch 인덱스에서도 삭제 상태로 동기화합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID
     */
    @Override
    @Transactional
    public void deleteProject(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart("DeleteProjectUseCase", "프로젝트 소프트 delete 삭제 서비스 시작 projectId=" + projectId);
        softDeleteProjectDbPort.deleteProject(projectId);
        softDeleteProjectEsPort.deleteProject(projectId);
        LoggerFactory.service().logSuccess("DeleteProjectUseCase", "프로젝트 소프트 delete 삭제 서비스 종료 projectId=" + projectId, startTime);
    }

    /**
     * 프로젝트를 복원 상태로 변경합니다.
     *
     * 데이터베이스에서 삭제된 프로젝트를 복원하고, Elasticsearch 인덱스에서도 복원 상태로 반영합니다.
     *
     * @param projectId 복원할 프로젝트의 ID
     */
    @Override
    @Transactional
    public void restoreProject(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart("RestoreProjectUseCase", "프로젝트 소프트 delete 복원 서비스 시작 projectId=" + projectId);
        softDeleteProjectDbPort.restoreProject(projectId);
        softDeleteProjectEsPort.restoreProject(projectId);
        LoggerFactory.service().logSuccess("RestoreProjectUseCase", "프로젝트 소프트 delete 복원 서비스 종료 projectId=" + projectId, startTime);
    }
}
