package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.domain.model.Project;

import java.util.Optional;

/**
 * 프로젝트 db 포트
 */
public interface ProjectRepositoryPort {

    /**
 * 프로젝트 엔티티를 영속 저장소에 저장하고, 저장된 프로젝트의 식별자를 반환합니다.
 *
 * @param project 저장할 프로젝트 객체
 * @return 저장된 프로젝트의 식별자
 */
    Project saveProject(Project project);

    /**
 * 주어진 프로젝트 ID로 프로젝트 엔티티를 조회합니다.
 *
 * @param projectId 조회할 프로젝트의 고유 ID
 * @return 해당 ID에 해당하는 Project 객체
 */
    Optional<Project> findProjectById(Long projectId);

    /**
 * 지정된 프로젝트의 파일(이미지 URL) 정보를 업데이트합니다.
 *
 * @param projectId 파일 정보를 변경할 프로젝트의 식별자
 * @param imageUrl 새로 저장할 이미지 URL
 */
void updateFile(Long projectId, String imageUrl);
}
