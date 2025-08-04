package com.dataracy.modules.dataset.application.port.in.query.search;

import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFilteredDataSetsUseCase {
    /**
     * 주어진 필터 조건과 페이지 정보를 기반으로 데이터셋을 필터링하여 페이징된 결과를 반환합니다.
     *
     * @param requestDto 데이터셋 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 정보
     * @return 필터링된 데이터셋 결과의 페이지 객체
     */
    Page<FilteredDataResponse> searchFilteredDataSets(FilteringDataRequest requestDto, Pageable pageable);
}
