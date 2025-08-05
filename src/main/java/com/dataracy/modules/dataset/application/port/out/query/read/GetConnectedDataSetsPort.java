package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetConnectedDataSetsPort {
    /**
 * 특정 프로젝트와 연결된 데이터셋 목록을 페이지네이션하여 반환합니다.
 *
 * @param projectId 데이터셋 연결 정보를 조회할 프로젝트의 식별자
 * @param pageable 결과 페이지네이션을 위한 정보
 * @return 각 데이터셋과 해당 데이터셋이 연결된 프로젝트 수를 포함하는 페이지 결과
 */
    Page<DataWithProjectCountDto> getConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);
}
