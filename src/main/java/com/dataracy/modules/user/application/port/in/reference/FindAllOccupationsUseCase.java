package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.application.dto.response.reference.AllOccupationsResponse;

/**
 * 전체 Occupation 조회 유스케이스
 */
public interface FindAllOccupationsUseCase {
    AllOccupationsResponse allOccupations();
}
