package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;

import java.util.List;

public interface GetPopularDataSetsPort {
    /**
     * 인기 순으로 데이터셋 목록과 각 데이터셋의 프로젝트 수를 조회합니다.
     *
     * @param size 조회할 데이터셋의 최대 개수
     * @return 인기 기준으로 정렬된 데이터셋 및 프로젝트 수 정보 리스트
     */
    List<DataWithProjectCountDto> getPopularDataSets(int size);
}
