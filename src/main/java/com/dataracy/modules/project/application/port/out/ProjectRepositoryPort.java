package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.domain.model.Project;

/**
 * 프로젝트 db 포트
 */
public interface ProjectRepositoryPort {

    /**
 * 프로젝트 엔티티를 저장소에 저장한 후, 저장된 프로젝트 객체를 반환합니다.
 *
 * @param project 저장할 프로젝트 인스턴스
 * @return 저장이 완료된 프로젝트 엔티티
 */
    Project saveProject(Project project);

    /**
 * 지정된 프로젝트의 파일(이미지 URL) 정보를 업데이트합니다.
 *
 * @param projectId 파일 정보를 변경할 프로젝트의 식별자
 * @param fileUrl 새로 저장할 이미지 URL
 */
void updateFile(Long projectId, String fileUrl);
}
