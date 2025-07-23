package com.dataracy.modules.dataset.application.port.query;

import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.response.CountDataGroupResponse;
import com.dataracy.modules.dataset.application.dto.response.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DataQueryRepositoryPort {
/**
 * 주어진 식별자에 해당하는 데이터를 조회합니다.
 *
 * @param dataId 조회할 데이터의 식별자
 * @return 데이터가 존재하면 해당 Data 객체를 Optional로 감싸서 반환하며, 존재하지 않으면 빈 Optional을 반환합니다.
 */
Optional<Data> findDataById(Long dataId);

/**
 * 지정된 ID에 해당하는 데이터와 그 메타데이터를 조회합니다.
 *
 * @param dataId 조회할 데이터의 고유 식별자
 * @return 데이터와 메타데이터가 존재하면 해당 Data 객체를 포함한 Optional, 없으면 빈 Optional
 */
Optional<Data> findDataWithMetadataById(Long dataId);

    /**
 * 인기 순으로 데이터셋 목록과 각 데이터셋의 프로젝트 수를 조회합니다.
 *
 * @param size 조회할 데이터셋의 최대 개수
 * @return 인기 기준으로 정렬된 데이터셋 및 프로젝트 수 정보 리스트
 */
List<DataWithProjectCountDto> findPopularDataSets(int size);

    /**
 * 필터 조건, 페이지네이션, 정렬 기준에 따라 데이터셋을 검색하여 결과를 페이지 형태로 반환합니다.
 *
 * @param request 데이터셋 필터링 조건이 포함된 요청 객체
 * @param pageable 페이지네이션 정보
 * @param sortType 데이터셋 정렬 기준
 * @return 데이터셋 및 프로젝트 수 정보를 포함하는 페이지 결과
 */
Page<DataWithProjectCountDto> searchByFilters(DataFilterRequest request, Pageable pageable, DataSortType sortType);

    /**
 * 지정한 개수만큼 최신 데이터셋 목록을 반환합니다.
 *
 * @param size 반환할 데이터셋의 최대 개수
 * @return 최신 데이터셋 리스트
 */
List<Data> findRecentDataSets(int size);
    /**
 * 데이터셋을 특정 기준으로 그룹화하여 각 그룹별 개수를 반환합니다.
 *
 * @return 그룹별 데이터셋 개수 정보를 담은 CountDataGroupResponse 리스트
 */
List<CountDataGroupResponse> countDataGroups();
}
