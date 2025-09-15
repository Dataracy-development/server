package com.dataracy.modules.project.application.port.in.query.extractor;

public interface FindUserIdIncludingDeletedUseCase {
    /**
     * 삭제된 프로젝트를 포함하여 지정된 프로젝트 ID에 연결된 사용자 ID를 조회합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 프로젝트에 연결된 사용자 ID. 프로젝트가 삭제된 경우에도 반환됩니다.
     */
    Long findUserIdIncludingDeleted(Long projectId);
}
