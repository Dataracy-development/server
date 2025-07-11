package com.dataracy.modules.reference.application.port.in.occupation;

import com.dataracy.modules.reference.application.dto.response.AllOccupationsResponse;

/**
 * 직업 조회 유스케이스
 */
public interface FindOccupationUseCase {
    AllOccupationsResponse.OccupationResponse findOccupation(Long occupationId);
}
