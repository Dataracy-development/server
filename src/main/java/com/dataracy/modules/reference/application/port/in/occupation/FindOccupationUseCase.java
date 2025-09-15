package com.dataracy.modules.reference.application.port.in.occupation;

import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;

public interface FindOccupationUseCase {
    /**
     * 주어진 직업 ID로 직업 정보를 조회하여 반환합니다.
     *
     * @param occupationId 조회할 직업의 고유 ID
     * @return 해당 ID에 해당하는 직업 정보가 담긴 OccupationResponse 객체
     */
    OccupationResponse findOccupation(Long occupationId);
}
