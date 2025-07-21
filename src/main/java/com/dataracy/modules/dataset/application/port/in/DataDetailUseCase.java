package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.DataDetailResponse;

public interface DataDetailUseCase {
    /**
 * 주어진 데이터 ID에 해당하는 데이터의 상세 정보를 반환합니다.
 *
 * @param dataId 상세 정보를 조회할 데이터의 ID
 * @return 데이터의 상세 정보를 담은 DataDetailResponse 객체
 */
DataDetailResponse getDataDetail(Long dataId);
}
