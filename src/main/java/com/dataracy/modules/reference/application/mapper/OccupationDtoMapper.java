package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import com.dataracy.modules.reference.domain.model.Occupation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Occupation 도메인 DTO와 Occupation 도메인 모델을 변환하는 매퍼
 */
@Component
public class OccupationDtoMapper {
    /**
     * Occupation 도메인 모델을 OccupationResponse DTO로 변환합니다.
     *
     * @param occupation 변환할 Occupation 도메인 객체
     * @return OccupationResponse DTO 객체
     */
    public OccupationResponse toResponseDto(Occupation occupation) {
        return new OccupationResponse (
                occupation.id(),
                occupation.value(),
                occupation.label()
        );
    }

    // 전체 Occupation 리스트 조회 도메인 모델 -> 전체 Occupation 리스트 조회 도메인 응답 DTO
    public AllOccupationsResponse toResponseDto(List<Occupation> occupations) {
        return new AllOccupationsResponse(
                occupations.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
