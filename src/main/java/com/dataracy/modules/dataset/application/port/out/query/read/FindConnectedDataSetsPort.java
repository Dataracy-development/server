package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FindConnectedDataSetsPort {
    /**
     * 특정 프로젝트에 연결된 데이터셋 목록을 페이지네이션하여 조회합니다.
     *
     * @param projectId 연결된 데이터셋을 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 각 데이터셋과 해당 데이터셋이 연결된 프로젝트 수를 포함하는 페이지 결과
     */
    Page<DataWithProjectCountDto> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);

    /**
     * 주어진 데이터셋 ID 목록에 해당하는 데이터셋과 각 데이터셋에 연결된 프로젝트 수 정보를 반환합니다.
     *
     * @param ids 조회할 데이터셋의 ID 목록
     * @return 각 데이터셋과 연결된 프로젝트 수 정보를 담은 DataWithProjectCountDto 리스트
     */
    List<DataWithProjectCountDto> findConnectedDataSetsAssociatedWithProjectByIds(List<Long> ids);
}
