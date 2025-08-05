package com.dataracy.modules.project.adapter.jpa.impl.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectDataJpaRepository;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ExtractProjectDbAdapter implements ExtractProjectOwnerPort {
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectDataJpaRepository projectDataJpaRepository;

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트의 소유자(사용자) ID를 반환합니다.
     *
     * 프로젝트가 존재하지 않을 경우 {@code ProjectException}이 발생합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 해당 프로젝트의 소유자(사용자) ID
     * @throws ProjectException 프로젝트가 존재하지 않을 때 발생합니다.
     */
    @Override
    public Long findUserIdByProjectId(Long projectId) {
        Instant startTime = LoggerFactory.db().logQueryStart("ProjectEntity", "[findById] 아이디를 통한 프로젝트 조회 시작 projectId=" + projectId);
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("ProjectEntity", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });
        LoggerFactory.db().logQueryEnd("ProjectEntity", "[findById] 아이디를 통한 프로젝트 조회 종료 projectId=" + projectId, startTime);
        return projectEntity.getUserId();
    }

    /**
     * 삭제된 프로젝트를 포함하여 지정된 프로젝트 ID에 연결된 사용자 ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 사용자 ID
     * @throws ProjectException 프로젝트가 존재하지 않을 경우 발생
     */
    @Override
    public Long findUserIdIncludingDeleted(Long projectId) {
        Instant startTime = LoggerFactory.db().logQueryStart("ProjectEntity", "[findIncludingDeleted] 탈퇴한 유저를 포함하여 아이디를 통한 프로젝트 조회 시작 projectId=" + projectId);
        ProjectEntity projectEntity = projectJpaRepository.findIncludingDeleted(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("ProjectEntity", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });
        LoggerFactory.db().logQueryEnd("ProjectEntity", "[findIncludingDeleted] 탈퇴한 유저를 포함하여 아이디를 통한 프로젝트 조회 종료 projectId=" + projectId, startTime);
        return projectEntity.getUserId();
    }

    /**
     * 주어진 프로젝트 ID에 연결된 데이터셋 ID 목록을 조회합니다.
     *
     * @param projectId 데이터셋 ID를 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 데이터셋 ID의 집합
     */
    @Override
    public Set<Long> findDataIdsByProjectId(@Param("projectId") Long projectId) {
        Instant startTime = LoggerFactory.db().logQueryStart("ProjectEntity", "[findDataIdsByProjectId] 아이디를 통하여 프로젝트와 연결된 데이터셋 목록 조회 시작 projectId=" + projectId);
        Set<Long> dataIds = projectDataJpaRepository.findDataIdsByProjectId(projectId);
        LoggerFactory.db().logQueryEnd("ProjectEntity", "[findDataIdsByProjectId] 아이디를 통하여 프로젝트와 연결된 데이터셋 목록 조회 종료 projectId=" + projectId, startTime);
        return dataIds;
    }
}
