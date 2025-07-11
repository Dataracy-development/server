package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.domain.model.Project;

/**
 * 프로젝트 db 포트
 */
public interface ProjectRepositoryPort {

    /**
     * 프로젝트 엔티티를 영속 저장소에 저장합니다.
     *
     * @param project 저장할 프로젝트 객체
     */
    Long saveProject(Project project);

    /**
     * 주어진 프로젝트 ID에 해당하는 Project 객체를 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 고유 식별자
     * @return 해당 ID의 Project 객체
     */
    Project findProjectById(Long projectId);

    void updateFile(Long projectId, String imageUrl);
}
