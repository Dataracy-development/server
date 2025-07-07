package com.dataracy.modules.user.adapter.web.mapper.reference;

import com.dataracy.modules.user.adapter.web.response.reference.AllOccupationsWebResponse;
import com.dataracy.modules.user.application.dto.response.reference.AllOccupationsResponse;
import org.springframework.stereotype.Component;

/**
 * occupation 웹 DTO와 occupation 도메인 DTO를 변환하는 매퍼
 */
@Component
public class OccupationWebMapper {
    // occupation 조회 도메인 응답 DTO -> occupation 조회 웹 응답 DTO
    public AllOccupationsWebResponse.OccupationWebResponse toWebDto(AllOccupationsResponse.OccupationResponse occupationResponse) {
        return new AllOccupationsWebResponse.OccupationWebResponse(
                occupationResponse.id(),
                occupationResponse.value(),
                occupationResponse.label()
        );
    }

    // 전체 occupation 리스트 조회 도메인 응답 DTO -> 전체 occupation 리스트 조회 웹 응답 DTO
    public AllOccupationsWebResponse toWebDto(AllOccupationsResponse allOccupationsResponse) {
        return new AllOccupationsWebResponse(
                allOccupationsResponse.occupations()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
