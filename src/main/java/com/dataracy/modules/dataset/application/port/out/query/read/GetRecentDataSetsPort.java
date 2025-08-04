package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.domain.model.Data;

import java.util.List;

public interface GetRecentDataSetsPort {
    /**
     * 지정한 개수만큼 최신 데이터셋 목록을 반환합니다.
     *
     * @param size 반환할 데이터셋의 최대 개수
     * @return 최신 데이터셋 리스트
     */
    List<Data> getRecentDataSets(int size);
}
