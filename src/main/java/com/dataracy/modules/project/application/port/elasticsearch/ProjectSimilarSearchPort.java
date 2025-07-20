package com.dataracy.modules.project.application.port.elasticsearch;

import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

public interface ProjectSimilarSearchPort {
    /**
 * 주어진 프로젝트와 유사한 프로젝트를 추천합니다.
 *
 * @param project 기준이 되는 프로젝트
 * @param size 추천할 프로젝트의 최대 개수
 * @return 유사한 프로젝트 추천 결과 목록
 */
List<ProjectSimilarSearchResponse> recommendSimilarProjects(Project project, int size);
}
