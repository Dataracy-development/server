package com.dataracy.modules.user.application.dto.response.reference;

import java.util.List;

/**
 * occupation 리스트 조회를 위한 도메인 응답 DTO
 * @param occupations occupation 리스트
 */
public record AllOccupationsResponse(List<OccupationResponse> occupations) {
    public record OccupationResponse(Long id, String value, String label
    ) {}
}
