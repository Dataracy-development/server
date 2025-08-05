package com.dataracy.modules.dataset.application.port.in.query.search;

import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFilteredDataSetsUseCase {
    /**
 * 필터링 조건과 페이지네이션 정보를 이용해 데이터셋을 검색하고, 조건에 맞는 데이터셋 목록을 페이징 형태로 반환합니다.
 *
 * @param requestDto 데이터셋 검색에 사용할 필터링 조건이 담긴 요청 객체
 * @param pageable 결과의 페이지 크기 및 정렬 등 페이지네이션 정보
 * @return 조건에 부합하는 데이터셋 목록의 페이징 결과
 */
    Page<FilteredDataResponse> searchFilteredDataSets(FilteringDataRequest requestDto, Pageable pageable);
}
