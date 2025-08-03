package com.dataracy.modules.project.application.port.in.query.read;

import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindConnectedProjectsUseCase {
    /**
 * 지정된 데이터 ID에 연결된 프로젝트 목록을 페이지네이션하여 반환합니다.
 *
 * @param dataId 연결된 프로젝트를 조회할 데이터의 고유 식별자
 * @param pageable 결과 페이지 및 정렬 정보를 지정하는 객체
 * @return 연결된 프로젝트 정보를 담은 페이지 객체
 */
    Page<ConnectedProjectResponse> findConnectedProjects(Long dataId, Pageable pageable);
}
