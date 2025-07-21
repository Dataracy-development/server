package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataPopularSearchResponse;

import java.util.List;

public interface DataPopularSearchUseCase {
    /**
 * 지정한 개수만큼 인기 있는 데이터셋 목록을 조회합니다.
 *
 * @param size 반환할 인기 데이터셋의 최대 개수
 * @return 인기 데이터셋 응답 객체의 리스트
 */
List<DataPopularSearchResponse> findPopularDataSets(int size);
}
