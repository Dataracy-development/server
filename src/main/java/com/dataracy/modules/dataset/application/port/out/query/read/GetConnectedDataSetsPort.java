package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetConnectedDataSetsPort {
    /**
     * 지정된 프로젝트와 연결된 데이터셋 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId 연결된 데이터셋을 조회할 프로젝트의 식별자
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 프로젝트와 연결된 데이터셋 및 각 데이터셋의 프로젝트 수를 포함하는 페이지 결과
     */
    Page<DataWithProjectCountDto> getConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);
}
