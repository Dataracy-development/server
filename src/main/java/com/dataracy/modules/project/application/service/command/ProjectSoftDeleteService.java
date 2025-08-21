package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.in.command.content.DeleteProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.RestoreProjectUseCase;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
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
    private final ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    /**
     * ProjectSoftDeleteService의 생성자입니다.
     *
     * 데이터베이스와 Elasticsearch에 대한 소프트 삭제 작업을 처리하는 두 포트 구현체를 주입받아 초기화합니다.
     */
    public ProjectSoftDeleteService(
            @Qualifier("softDeleteProjectDbAdapter") SoftDeleteProjectPort softDeleteProjectDbPort,
            ManageProjectProjectionTaskPort manageProjectProjectionTaskPort
    ) {
        this.softDeleteProjectDbPort = softDeleteProjectDbPort;
        this.manageProjectProjectionTaskPort = manageProjectProjectionTaskPort;
    }

    /**
         * 지정한 프로젝트를 소프트 삭제(데이터베이스에서 is_deleted=true)하고, 프로젝션(예: Elasticsearch)에서의 삭제 상태 반영 작업을 비동기로 등록합니다.
         *
         * <p>데이터베이스 변경은 트랜잭션 안에서 수행되며, 프로젝션 반영 작업은 큐에 등록되어 별도 워커가 비동기로 처리합니다.</p>
         *
         * @param projectId 소프트 삭제할 프로젝트의 식별자
         */
    @Override
    @Transactional
    public void deleteProject(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart("DeleteProjectUseCase", "프로젝트 소프트 delete 삭제 서비스 시작 projectId=" + projectId);

        // DB만 확정 (프로젝트 is_deleted = true)
        softDeleteProjectDbPort.deleteProject(projectId);

        // ES 작업을 큐에 적재 → 워커가 비동기로 isDeleted=true 설정
        manageProjectProjectionTaskPort.enqueueSetDeleted(projectId, true);

        LoggerFactory.service().logSuccess("DeleteProjectUseCase", "프로젝트 소프트 delete 삭제 서비스 종료 projectId=" + projectId, startTime);
    }

    /**
     * 지정한 프로젝트를 소프트 삭제 상태에서 복원합니다.
     *
     * 트랜잭션 내에서 데이터베이스의 is_deleted 플래그를 false로 되돌리고,
     * 복원 상태를 반영하도록 프로젝션(예: Elasticsearch)에 대한 비동기 작업을 큐에 등록합니다.
     *
     * @param projectId 복원할 프로젝트의 ID
     */
    @Override
    @Transactional
    public void restoreProject(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart("RestoreProjectUseCase", "프로젝트 소프트 delete 복원 서비스 시작 projectId=" + projectId);

        // DB만 확정 (프로젝트 is_deleted = false)
        softDeleteProjectDbPort.restoreProject(projectId);

        // ES 작업 큐 → isDeleted=false
        manageProjectProjectionTaskPort.enqueueSetDeleted(projectId, false);

        LoggerFactory.service().logSuccess("RestoreProjectUseCase", "프로젝트 소프트 delete 복원 서비스 종료 projectId=" + projectId, startTime);
    }
}
