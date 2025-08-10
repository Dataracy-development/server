package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;

import java.util.List;

public interface GetPopularDataSetsPort {
    /**
     * 지정한 개수만큼 인기 순으로 데이터셋과 각 데이터셋에 연결된 프로젝트 수를 조회합니다.
     *
     * @param size 반환할 데이터셋의 최대 개수
     * @return 인기 순으로 정렬된 데이터셋과 해당 데이터셋별 프로젝트 수 정보를 담은 리스트
     */
    List<DataWithProjectCountDto> getPopularDataSets(int size);
}
