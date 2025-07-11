package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;
import org.springframework.stereotype.Component;

/**
 * analysisPurpose 웹 DTO와 analysisPurpose 도메인 DTO를 변환하는 매퍼
 */
@Component
public class AnalysisPurposeWebMapper {
    /**
     * 도메인 계층의 AnalysisPurposeResponse 객체를 웹 계층의 AnalysisPurposeWebResponse 객체로 변환합니다.
     *
     * @param analysisPurposeResponse 변환할 도메인 AnalysisPurposeResponse 객체
     * @return 변환된 웹 AnalysisPurposeWebResponse 객체
     */
    public AllAnalysisPurposesWebResponse.AnalysisPurposeWebResponse toWebDto(AllAnalysisPurposesResponse.AnalysisPurposeResponse analysisPurposeResponse) {
        return new AllAnalysisPurposesWebResponse.AnalysisPurposeWebResponse(
                analysisPurposeResponse.id(),
                analysisPurposeResponse.value(),
                analysisPurposeResponse.label()
        );
    }

    /**
     * 도메인 계층의 전체 분석 목적 리스트 응답 DTO를 웹 계층의 응답 DTO로 변환합니다.
     *
     * @param allAnalysisPurposesResponse 도메인 계층의 전체 분석 목적 리스트 응답 DTO
     * @return 변환된 웹 계층의 전체 분석 목적 리스트 응답 DTO
     */
    public AllAnalysisPurposesWebResponse toWebDto(AllAnalysisPurposesResponse allAnalysisPurposesResponse) {
        return new AllAnalysisPurposesWebResponse(
                allAnalysisPurposesResponse.analysisPurposes()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
