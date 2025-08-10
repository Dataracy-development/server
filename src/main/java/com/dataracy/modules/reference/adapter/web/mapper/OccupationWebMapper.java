package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllOccupationsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.OccupationWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * 도메인 전체 직업 응답 DTO를 웹 전체 직업 응답 DTO로 변환합니다.
     *
     * @param allOccupationsResponse 변환할 도메인 전체 직업 응답 DTO
     * @return 변환된 웹 전체 직업 응답 DTO. 입력값이나 내부 리스트가 null인 경우 빈 리스트를 포함합니다.
     */
    public AllOccupationsWebResponse toWebDto(AllOccupationsResponse allOccupationsResponse) {
        if (allOccupationsResponse == null || allOccupationsResponse.occupations() == null) {
            return new AllOccupationsWebResponse(List.of());
        }

        return new AllOccupationsWebResponse(
                allOccupationsResponse.occupations()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
