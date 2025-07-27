package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
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
 * 지정된 프로젝트의 파일(이미지 URL) 정보를 새로운 URL로 갱신합니다.
 *
 * @param projectId 파일 정보를 변경할 프로젝트의 식별자
 * @param fileUrl 새로 저장할 이미지 URL
 */
void updateFile(Long projectId, String fileUrl);

    /**
 * 지정한 프로젝트 ID에 해당하는 프로젝트의 존재 여부를 확인합니다.
 *
 * @param projectId 존재 여부를 확인할 프로젝트의 ID
 * @return 프로젝트가 존재하면 true, 존재하지 않으면 false
 */
boolean existsProjectById(Long projectId);

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
/**
 * 지정된 프로젝트 ID에 해당하는 프로젝트를 주어진 수정 요청 데이터로 변경합니다.
 *
 * @param projectId 수정할 프로젝트의 ID
 * @param requestDto 프로젝트 수정에 필요한 데이터가 담긴 요청 객체
 */
void modify(Long projectId, ProjectModifyRequest requestDto);

/**
 * 지정된 프로젝트 ID에 해당하는 프로젝트를 삭제합니다.
 *
 * @param projectId 삭제할 프로젝트의 ID
 */
void delete(Long projectId);
/**
 * 삭제된 프로젝트를 지정된 ID로 복원합니다.
 *
 * @param projectId 복원할 프로젝트의 ID
 */
void restore(Long projectId);

    void increaseViewCount(Long projectId, Long count);
}
