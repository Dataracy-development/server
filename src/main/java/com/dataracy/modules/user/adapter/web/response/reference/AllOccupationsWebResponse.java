package com.dataracy.modules.user.adapter.web.response.reference;

import java.util.List;

/**
 * occupation 리스트 조회를 위한 웹응답 DTO
 * @param occupations occupation 리스트
 */
public record AllOccupationsWebResponse(List<OccupationWebResponse> occupations) {
    public record OccupationWebResponse(
            Long id,
            String value,
            String label
    ) {}
}
