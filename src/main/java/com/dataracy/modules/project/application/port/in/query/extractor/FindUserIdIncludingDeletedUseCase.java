package com.dataracy.modules.project.application.port.in.query.extractor;

public interface FindUserIdIncludingDeletedUseCase {
    /**
     * 삭제된 프로젝트를 포함하여 주어진 프로젝트 ID에 연결된 사용자 ID를 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 해당 프로젝트에 연결된 사용자 ID
     */
    Long findUserIdIncludingDeleted(Long projectId);
}
