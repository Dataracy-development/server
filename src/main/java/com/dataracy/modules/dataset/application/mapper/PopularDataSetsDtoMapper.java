package com.dataracy.modules.dataset.application.mapper;

import com.dataracy.modules.dataset.application.dto.response.DataPopularSearchResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 인기있는 데이터셋 도메인 DTO와 인기있는 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class PopularDataSetsDtoMapper {
    /**
     * 도메인 모델 Data와 추가 정보를 결합하여 DataPopularSearchResponse DTO로 변환합니다.
     *
     * @param data 변환할 데이터셋 도메인 객체
     * @param username 데이터셋과 연관된 사용자 이름
     * @param topicLabel 데이터셋의 주제 라벨
     * @param dataSourceLabel 데이터셋의 데이터 소스 라벨
     * @param dataTypeLabel 데이터셋의 데이터 타입 라벨
     * @param countConnectedProjects 해당 데이터셋과 연결된 프로젝트 수
     * @return DataPopularSearchResponse로 매핑된 응답 DTO
     */
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
