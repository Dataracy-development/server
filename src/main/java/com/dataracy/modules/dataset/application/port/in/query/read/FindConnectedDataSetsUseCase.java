package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindConnectedDataSetsUseCase {
    /**
 * 특정 프로젝트에 연결된 데이터셋을 페이지네이션하여 조회합니다.
 *
 * @param projectId 데이터셋을 조회할 대상 프로젝트의 식별자
 * @param pageable 결과 페이지 및 정렬 정보를 포함하는 페이지네이션 객체
 * @return 연결된 데이터셋 정보를 담은 페이지 객체
 */
    Page<ConnectedDataResponse> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);
}
