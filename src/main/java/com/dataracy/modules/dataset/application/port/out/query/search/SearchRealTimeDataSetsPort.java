package com.dataracy.modules.dataset.application.port.out.query.search;

import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;

import java.util.List;

public interface SearchRealTimeDataSetsPort {
    /**
     * 주어진 키워드로 실시간 데이터를 검색하여 결과를 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 검색된 데이터의 최소 정보 목록
     */
    List<RecentMinimalDataResponse> searchRealTimeDataSets(String keyword, int size);
}
