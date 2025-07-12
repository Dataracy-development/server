package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import com.dataracy.modules.reference.domain.model.VisitSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * VisitSource 도메인 DTO와 VisitSource 도메인 모델을 변환하는 매퍼
 */
@Component
public class VisitSourceDtoMapper {
    /**
     * VisitSource 도메인 모델 객체를 VisitSourceResponse DTO로 변환합니다.
     *
     * @param visitSource 변환할 VisitSource 도메인 모델 객체
     * @return VisitSourceResponse로 변환된 DTO 객체
     */
    public VisitSourceResponse toResponseDto(VisitSource visitSource) {
        return new VisitSourceResponse(
                visitSource.id(),
                visitSource.value(),
                visitSource.label()
        );
    }

    // 전체 VisitSource 리스트 조회 도메인 모델 -> 전체 VisitSource 리스트 조회 도메인 응답 DTO
    public AllVisitSourcesResponse toResponseDto(List<VisitSource> visitSources) {
        return new AllVisitSourcesResponse(
                visitSources.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
