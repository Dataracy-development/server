package com.dataracy.modules.dataset.application.mapper.search;

import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 필터링된 데이터셋 도메인 DTO와 필터링된 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class FilteredDataDtoMapper {
    /**
     * 도메인 모델 Data와 주제, 데이터 소스, 데이터 유형 라벨 및 연결된 프로젝트 수를 받아 DataFilterResponse DTO로 변환합니다.
     *
     * @param data 변환할 데이터 도메인 객체
     * @param topicLabel 데이터의 주제 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param dataTypeLabel 데이터 유형 라벨
     * @param countConnectedProjects 데이터와 연결된 프로젝트 수
     * @return DataFilterResponse로 변환된 DTO 객체
     */
    public FilteredDataResponse toResponseDto(
            Data data,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new FilteredDataResponse(
                data.getId(),
                data.getTitle(),
                topicLabel,
                dataSourceLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getThumbnailUrl(),
                data.getDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }
}
