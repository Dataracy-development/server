package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AnalysisPurpose 도메인 DTO와 AnalysisPurpose 도메인 모델을 변환하는 매퍼
 */
@Component
public class AnalysisPurposeDtoMapper {
    /**
     * AnalysisPurpose 도메인 객체를 AnalysisPurposeResponse DTO로 변환합니다.
     *
     * @param analysisPurpose 변환 대상이 되는 AnalysisPurpose 도메인 객체
     * @return id, value, label 정보를 포함하는 AnalysisPurposeResponse DTO
     */
    public AnalysisPurposeResponse toResponseDto(AnalysisPurpose analysisPurpose) {
        return new AnalysisPurposeResponse(
                analysisPurpose.id(),
                analysisPurpose.value(),
                analysisPurpose.label()
        );
    }

    /**
     * AnalysisPurpose 도메인 모델 리스트를 전체 AnalysisPurpose 응답 DTO로 변환합니다.
     *
     * @param analysisPurposes 변환할 AnalysisPurpose 도메인 모델 리스트
     * @return 변환된 전체 AnalysisPurpose 응답 DTO
     */
    public AllAnalysisPurposesResponse toResponseDto(List<AnalysisPurpose> analysisPurposes) {
        return new AllAnalysisPurposesResponse(
                analysisPurposes.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
