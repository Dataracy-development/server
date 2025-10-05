package com.dataracy.modules.dataset.application.port.out.query.search;

import java.util.List;

import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;

public interface SearchRealTimeDataSetsPort {
  /**
   * 키워드에 따라 실시간 데이터셋의 최소 정보를 최대 지정 개수만큼 검색하여 반환합니다.
   *
   * @param keyword 검색할 키워드
   * @param size 반환할 최대 데이터셋 개수
   * @return 키워드와 일치하는 실시간 데이터셋의 최소 정보 목록
   */
  List<RecentMinimalDataResponse> searchRealTimeDataSets(String keyword, int size);
}
