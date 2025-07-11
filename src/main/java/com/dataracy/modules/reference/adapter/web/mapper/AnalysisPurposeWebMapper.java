package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;
import org.springframework.stereotype.Component;

/**
 * analysisPurpose 웹 DTO와 analysisPurpose 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AnalysisPurposeWebMapper {
    // analysisPurpose 조회 도메인 응답 DTO -> analysisPurpose 조회 웹 응답 DTO
    public AllAnalysisPurposesWebResponse.AnalysisPurposeWebResponse toWebDto(AllAnalysisPurposesResponse.AnalysisPurposeResponse analysisPurposeResponse) {
        return new AllAnalysisPurposesWebResponse.AnalysisPurposeWebResponse(
                analysisPurposeResponse.id(),
                analysisPurposeResponse.value(),
                analysisPurposeResponse.label()
        );
    }

    // 전체 analysisPurpose 리스트 조회 도메인 응답 DTO -> 전체 analysisPurpose 리스트 조회 웹 응답 DTO
    public AllAnalysisPurposesWebResponse toWebDto(AllAnalysisPurposesResponse allAnalysisPurposesResponse) {
        return new AllAnalysisPurposesWebResponse(
                allAnalysisPurposesResponse.analysisPurposes()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
