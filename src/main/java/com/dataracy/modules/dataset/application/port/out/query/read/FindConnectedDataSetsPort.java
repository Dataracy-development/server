package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FindConnectedDataSetsPort {
    /**
 * 지정한 프로젝트에 연결된 데이터셋들을 페이지 단위로 조회합니다.
 *
 * 각 페이지 항목은 데이터셋 정보와 해당 데이터셋이 연결된 프로젝트 수를 포함한
 * DataWithProjectCountDto로 구성됩니다.
 *
 * @param projectId 조회 대상 프로젝트의 식별자
 * @param pageable  요청한 페이지와 정렬·크기 정보를 담은 Pageable 객체
 * @return 지정된 페이지 조건에 맞는 DataWithProjectCountDto의 페이지 결과
 */
    Page<DataWithProjectCountDto> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);

    /**
 * 주어진 데이터셋 ID 목록에 대응하는 데이터셋과 각 데이터셋에 연결된 프로젝트 수를 조회하여 반환합니다.
 *
 * 입력한 ID들에 해당하는 데이터셋 각각에 대해 DataWithProjectCountDto(데이터셋 정보 + 연결된 프로젝트 수)를 포함한 리스트를 반환합니다.
 * 요청한 ID 중 존재하지 않는 항목이 있으면 해당 항목은 결과에 포함되지 않을 수 있습니다.
 *
 * @param ids 조회할 데이터셋의 ID 목록
 * @return 각 데이터셋과 연결된 프로젝트 수 정보를 담은 DataWithProjectCountDto 목록
 */
    List<DataWithProjectCountDto> findConnectedDataSetsAssociatedWithProjectByIds(List<Long> ids);
}
