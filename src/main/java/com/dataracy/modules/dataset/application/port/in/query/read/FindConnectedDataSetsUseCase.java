package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindConnectedDataSetsUseCase {
    /**
     * 지정된 프로젝트에 연결된 데이터셋 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 프로젝트에 연결된 데이터셋의 페이지 결과
     */
    Page<ConnectedDataResponse> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);
}
