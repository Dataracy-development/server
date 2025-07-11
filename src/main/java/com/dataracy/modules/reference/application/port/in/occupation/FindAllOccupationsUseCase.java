package com.dataracy.modules.reference.application.port.in.occupation;

import com.dataracy.modules.reference.application.dto.response.AllOccupationsResponse;

/**
 * 전체 Occupation 조회 유스케이스
 */
public interface FindAllOccupationsUseCase {
    AllOccupationsResponse allOccupations();
}
