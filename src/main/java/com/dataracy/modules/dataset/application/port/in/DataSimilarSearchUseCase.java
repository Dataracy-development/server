package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;

import java.util.List;

public interface DataSimilarSearchUseCase {
/**
 * 지정된 데이터 ID와 유사한 데이터셋을 최대 size 개수만큼 조회합니다.
 *
 * @param dataId 유사도를 기준으로 검색할 데이터셋의 ID
 * @param size 반환할 유사 데이터셋의 최대 개수
 * @return 유사한 데이터셋의 목록
 */
List<DataSimilarSearchResponse> findSimilarDataSets(Long dataId, int size);
}
