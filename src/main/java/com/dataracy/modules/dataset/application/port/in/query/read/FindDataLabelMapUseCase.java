package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;

import java.util.Collection;

public interface FindDataLabelMapUseCase {
    /**
     * 주어진 데이터셋 컬렉션을 기반으로 데이터 라벨 매핑 정보를 반환합니다.
     *
     * @param savedDataSets 라벨 매핑을 생성할 데이터셋 컬렉션
     * @return 데이터 라벨 매핑 결과를 담은 DataLabelMapResponse 객체
     */
    DataLabelMapResponse labelMapping(Collection<DataWithProjectCountDto> savedDataSets);
}
