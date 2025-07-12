package com.dataracy.modules.reference.application.port.in.occupation;

import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;

/**
 * 전체 Occupation 조회 유스케이스
 */
public interface FindAllOccupationsUseCase {
    /**
     * 모든 직업 정보를 조회하여 반환합니다.
     *
     * @return 전체 직업 목록이 포함된 응답 객체
     */
    AllOccupationsResponse allOccupations();
}
