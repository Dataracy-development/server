package com.dataracy.modules.project.application.port.out.query.read;

import com.dataracy.modules.project.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindContinuedProjectsPort {
    /**
 * 특정 프로젝트를 기준으로 이어지는 프로젝트들을 페이지 형태로 반환합니다.
 *
 * @param projectId 기준이 되는 프로젝트의 ID
 * @param pageable 페이지네이션 정보
 * @return 기준 프로젝트 이후에 이어지는 프로젝트들의 페이지 결과
 */
    Page<Project> findContinuedProjects(Long projectId, Pageable pageable);
}
