package com.dataracy.modules.reference.application.dto.response.allview;

import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;

import java.util.List;

/**
 * occupation 리스트 조회를 위한 도메인 응답 DTO
 * @param occupations occupation 리스트
 */
public record AllOccupationsResponse(List<OccupationResponse> occupations) {
}
