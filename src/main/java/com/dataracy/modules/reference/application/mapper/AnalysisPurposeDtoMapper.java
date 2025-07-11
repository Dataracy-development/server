package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AnalysisPurpose 도메인 DTO와 AnalysisPurpose 도메인 모델을 변환하는 매퍼
 */
@Component
public class AnalysisPurposeDtoMapper {
    // AnalysisPurpose 도메인 모델 -> AnalysisPurpose 도메인 응답 DTO
    public AllAnalysisPurposesResponse.AnalysisPurposeResponse toResponseDto(AnalysisPurpose analysisPurpose) {
        return new AllAnalysisPurposesResponse.AnalysisPurposeResponse(
                analysisPurpose.id(),
                analysisPurpose.value(),
                analysisPurpose.label()
        );
    }

    // 전체 AnalysisPurpose 리스트 조회 도메인 모델 -> 전체 AnalysisPurpose 리스트 조회 도메인 응답 DTO
    public AllAnalysisPurposesResponse toResponseDto(List<AnalysisPurpose> analysisPurposes) {
        return new AllAnalysisPurposesResponse(
                analysisPurposes.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
