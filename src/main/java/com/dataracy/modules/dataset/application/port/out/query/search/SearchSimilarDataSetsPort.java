package com.dataracy.modules.dataset.application.port.out.query.search;

import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.domain.model.Data;

import java.util.List;

public interface SearchSimilarDataSetsPort {
    /**
 * 기준 데이터와 유사한 데이터셋을 최대 지정 개수만큼 추천합니다.
 *
 * @param data 유사도를 판단할 기준 데이터
 * @param size 추천할 데이터셋의 최대 개수
 * @return 추천된 유사 데이터셋의 목록
 */
    List<SimilarDataResponse> searchSimilarDataSets(Data data, int size);
}
