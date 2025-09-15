package com.dataracy.modules.project.application.port.out.query.extractor;

import java.util.Set;

public interface ExtractProjectOwnerPort {
    /**
     * 지정된 프로젝트 ID에 해당하는 소유자(사용자) ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 프로젝트 소유자의 사용자 ID
     */
    Long findUserIdByProjectId(Long projectId);

    /**
     * 삭제된 프로젝트를 포함하여 주어진 프로젝트 ID에 연결된 사용자 ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 연결된 사용자 ID, 해당 프로젝트가 없으면 null
     */
    Long findUserIdIncludingDeleted(Long projectId);

    /**
     * 지정된 프로젝트 ID에 연결된 데이터 ID들의 집합을 반환합니다.
     *
     * @param projectId 데이터 ID를 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 데이터 ID들의 집합
     */
    Set<Long> findDataIdsByProjectId(Long projectId);
}
