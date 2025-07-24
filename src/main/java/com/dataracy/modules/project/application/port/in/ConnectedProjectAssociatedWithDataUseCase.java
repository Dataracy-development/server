package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ConnectedProjectAssociatedWithDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConnectedProjectAssociatedWithDataUseCase {
/**
 * 지정된 데이터 ID에 연결된 프로젝트 목록을 페이지 단위로 조회합니다.
 *
 * @param dataId    연결된 프로젝트를 조회할 데이터의 식별자
 * @param pageable  페이지네이션 정보를 담은 객체
 * @return          연결된 프로젝트 응답 객체의 페이지
 */
Page<ConnectedProjectAssociatedWithDataResponse> findConnectedProjects(Long dataId, Pageable pageable);
}
