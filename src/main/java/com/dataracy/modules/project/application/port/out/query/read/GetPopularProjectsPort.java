package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

public interface GetPopularProjectsPort {
    /**
 * 지정한 개수만큼 인기 프로젝트를 조회하여 반환합니다.
 *
 * @param size 반환할 인기 프로젝트의 최대 개수
 * @return 인기 프로젝트 목록
 */
    List<Project> getPopularProjects(int size);
}
