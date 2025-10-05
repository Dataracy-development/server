package com.dataracy.modules.dataset.application.port.in.query.search;

import java.util.List;

import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;

public interface SearchRealTimeDataSetsUseCase {
  /**
   * 주어진 키워드로 실시간 데이터셋을 검색하여, 지정된 개수만큼 반환합니다.
   *
   * @param keyword 검색에 사용할 키워드
   * @param size 반환할 데이터셋의 최대 개수
   * @return 키워드에 해당하는 실시간 데이터셋 목록
   */
  List<RecentMinimalDataResponse> searchRealTimeDataSets(String keyword, int size);
}
