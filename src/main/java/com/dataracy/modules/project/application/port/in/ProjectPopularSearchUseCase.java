package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ProjectPopularSearchResponse;

import java.util.List;

public interface ProjectPopularSearchUseCase {
    /**
 * 지정한 개수만큼 인기 프로젝트 목록을 조회합니다.
 *
 * @param size 반환할 인기 프로젝트의 최대 개수
 * @return 인기 프로젝트 응답 객체의 리스트
 */
List<ProjectPopularSearchResponse> findPopularProjects(int size);
}
