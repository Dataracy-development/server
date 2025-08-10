package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;

import java.util.List;

public interface GetRecentMinimalDataSetsUseCase {
    /**
     * 지정된 개수만큼 최근 데이터셋의 최소 정보를 조회합니다.
     *
     * @param size 반환할 최근 데이터셋의 최대 개수
     * @return 최근 데이터셋의 최소 정보가 담긴 응답 객체 리스트
     */
    List<RecentMinimalDataResponse> getRecentDataSets(int size);
}
