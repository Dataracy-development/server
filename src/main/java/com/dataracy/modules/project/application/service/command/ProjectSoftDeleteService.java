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

    /**
     * ProjectSoftDeleteService의 생성자입니다.
     *
     * 데이터베이스와 Elasticsearch에 대한 소프트 삭제 작업을 처리하는 두 포트 구현체를 주입받아 초기화합니다.
     */
    public ProjectSoftDeleteService(
            @Qualifier("softDeleteProjectDbAdapter") SoftDeleteProjectPort softDeleteProjectDbPort,
            @Qualifier("softDeleteProjectEsAdapter") SoftDeleteProjectPort softDeleteProjectEsPort
    ) {
        this.softDeleteProjectDbPort = softDeleteProjectDbPort;
        this.softDeleteProjectEsPort = softDeleteProjectEsPort;
    }

    /**
     * 프로젝트를 소프트 삭제 처리하여 데이터베이스와 Elasticsearch 인덱스 모두에서 삭제 상태로 동기화합니다.
     *
     * @param projectId 삭제 처리할 프로젝트의 ID
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
     * 프로젝트를 소프트 삭제 상태에서 복원합니다.
     *
     * 데이터베이스와 Elasticsearch 인덱스에서 지정한 프로젝트를 복원하여 정상 상태로 되돌립니다.
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
