package com.dataracy.modules.project.application.port.in.query.extractor;

public interface FindUserIdUseCase {
    /**
     * 주어진 프로젝트 ID에 해당하는 사용자 ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 사용자 ID
     */
    Long findUserIdByProjectId(Long projectId);
}
