package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.DataGroupCountResponse;

import java.util.List;

public interface GetDataGroupCountUseCase {
    /**
 * 주제 레이블별로 데이터 그룹의 개수를 조회합니다.
 *
 * @return 각 주제 레이블에 해당하는 데이터 그룹 개수 정보를 담은 DataGroupCountResponse 객체의 리스트
 */
    List<DataGroupCountResponse> getDataGroupCountByTopicLabel();
}
