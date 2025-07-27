package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ProjectDetailResponse;

public interface ProjectDetailUseCase {
    /**
 * 주어진 프로젝트 ID와 사용자 ID를 기반으로 프로젝트의 상세 정보를 반환합니다.
 *
 * @param projectId 조회할 프로젝트의 고유 식별자
 * @param userId 프로젝트 정보를 조회하는 사용자의 고유 식별자
 * @return 프로젝트의 상세 정보를 담은 ProjectDetailResponse 객체
 */
ProjectDetailResponse getProjectDetail(Long projectId, Long userId, String viewerId);
}
