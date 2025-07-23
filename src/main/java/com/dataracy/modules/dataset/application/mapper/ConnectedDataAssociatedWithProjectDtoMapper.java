package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.ConnectedDataAssociatedWithProjectResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 필터링된 데이터셋 도메인 DTO와 필터링된 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class ConnectedDataAssociatedWithProjectDtoMapper {
    public ConnectedDataAssociatedWithProjectResponse toResponseDto(
            Data data,
            String topicLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new ConnectedDataAssociatedWithProjectResponse(
                data.getId(),
                data.getTitle(),
                topicLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getThumbnailUrl(),
                data.getDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }
}
