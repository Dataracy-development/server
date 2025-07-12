package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllOccupationsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.OccupationWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import org.springframework.stereotype.Component;

/**
 * occupation 웹 DTO와 occupation 도메인 DTO를 변환하는 매퍼
 */
@Component
public class OccupationWebMapper {
    /**
     * 단일 직업 도메인 응답 DTO를 웹 응답 DTO로 변환합니다.
     *
     * @param occupationResponse 변환할 직업 도메인 응답 DTO
     * @return 변환된 직업 웹 응답 DTO
     */
    public OccupationWebResponse toWebDto(OccupationResponse occupationResponse) {
        return new OccupationWebResponse(
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
