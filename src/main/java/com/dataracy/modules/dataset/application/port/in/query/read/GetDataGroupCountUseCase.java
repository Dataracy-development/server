package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.DataGroupCountResponse;

import java.util.List;

public interface GetDataGroupCountUseCase {
    /**
     * 주제 레이블별로 그룹화된 데이터 그룹의 개수를 반환합니다.
     *
     * @return 주제 레이블별 데이터 그룹 개수 정보를 담은 CountDataGroupResponse 객체의 리스트
     */
    List<DataGroupCountResponse> getDataGroupCountByTopicLabel();
}
