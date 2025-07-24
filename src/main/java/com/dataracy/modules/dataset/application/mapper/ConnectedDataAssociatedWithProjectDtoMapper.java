package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.ConnectedDataAssociatedWithProjectResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 필터링된 데이터셋 도메인 DTO와 필터링된 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class ConnectedDataAssociatedWithProjectDtoMapper {
    /**
     * 도메인 모델 객체와 추가 정보를 결합하여 ConnectedDataAssociatedWithProjectResponse DTO로 변환합니다.
     *
     * @param data 데이터셋 도메인 객체
     * @param topicLabel 데이터셋의 주제 레이블
     * @param dataTypeLabel 데이터셋의 유형 레이블
     * @param countConnectedProjects 데이터와 연결된 프로젝트 수
     * @return 변환된 ConnectedDataAssociatedWithProjectResponse DTO
     */
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
