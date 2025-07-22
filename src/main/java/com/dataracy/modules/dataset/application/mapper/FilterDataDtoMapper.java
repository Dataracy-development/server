package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.DataFilterResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 필터링된 데이터셋 도메인 DTO와 필터링된 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class FilterDataDtoMapper {
    public DataFilterResponse toResponseDto(
            Data data,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new DataFilterResponse(
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
                data.getRecentWeekDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }
}
