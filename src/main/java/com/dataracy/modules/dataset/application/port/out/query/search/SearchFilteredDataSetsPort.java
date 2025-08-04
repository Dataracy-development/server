package com.dataracy.modules.dataset.application.port.out.query.search;

import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFilteredDataSetsPort {
    /**
     * 필터 조건, 페이지네이션, 정렬 기준에 따라 데이터셋을 검색하여 결과를 페이지 형태로 반환합니다.
     *
     * @param request 데이터셋 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 정보
     * @param sortType 데이터셋 정렬 기준
     * @return 데이터셋 및 프로젝트 수 정보를 포함하는 페이지 결과
     */
    Page<DataWithProjectCountDto> searchByFilters(FilteringDataRequest request, Pageable pageable, DataSortType sortType);
}
