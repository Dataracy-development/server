package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.DataPopularSearchResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 인기있는 데이터셋 도메인 DTO와 인기있는 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class PopularDataSetsDtoMapper {
    public DataPopularSearchResponse toResponseDto(
            Data data,
            String username,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new DataPopularSearchResponse(
                data.getId(),
                data.getTitle(),
                username,
                topicLabel,
                dataSourceLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getThumbnailUrl(),
                data.getDownloadCount(),
                data.getRecentWeekDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }
}
