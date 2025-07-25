package com.dataracy.modules.reference.application.port.in.occupation;

import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;

/**
 * 전체 Occupation 조회 유스케이스
 */
public interface FindAllOccupationsUseCase {
    /**
 * 모든 직업 정보를 조회합니다.
 *
 * @return 전체 직업 목록을 포함하는 AllOccupationsResponse 객체
 */
    AllOccupationsResponse findAllOccupations();
}
