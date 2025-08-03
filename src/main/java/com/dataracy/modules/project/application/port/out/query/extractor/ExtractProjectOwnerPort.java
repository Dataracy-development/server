package com.dataracy.modules.project.application.port.out.query.extractor;

import org.springframework.data.repository.query.Param;

import java.util.Set;

/**
 * 프로젝트 db 포트
 */
public interface ExtractProjectOwnerPort {
    /**
     * 지정된 프로젝트 ID에 연결된 사용자 ID를 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 해당 프로젝트에 연결된 사용자 ID
     */
    Long findUserIdByProjectId(Long projectId);

    /**
     * 삭제된 프로젝트를 포함하여 지정된 프로젝트 ID에 연결된 사용자 ID를 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 해당 프로젝트에 연결된 사용자 ID, 존재하지 않으면 null
     */
    Long findUserIdIncludingDeleted(Long projectId);

    Set<Long> findDataIdsByProjectId(@Param("projectId") Long projectId);
}
