package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;

import java.util.List;

public interface DataRealTimeUseCase {
/**
 * 주어진 키워드로 실시간 데이터셋을 검색하여, 지정된 개수만큼 반환합니다.
 *
 * @param keyword 검색에 사용할 키워드
 * @param size 반환할 데이터셋의 최대 개수
 * @return 키워드에 해당하는 실시간 데이터셋 목록
 */
List<DataMinimalSearchResponse> findRealTimeDataSets(String keyword, int size);
}
