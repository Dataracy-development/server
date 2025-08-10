package com.dataracy.modules.dataset.application.port.in.query.search;

import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;

import java.util.List;

public interface SearchSimilarDataSetsUseCase {
    /**
     * 주어진 데이터 ID를 기준으로 유사한 데이터셋을 최대 지정된 개수만큼 반환합니다.
     *
     * @param dataId 유사한 데이터셋을 찾기 위한 기준 데이터의 ID
     * @param size 반환할 유사 데이터셋의 최대 개수
     * @return 유사한 데이터셋의 리스트
     */
    List<SimilarDataResponse> searchSimilarDataSets(Long dataId, int size);
}
