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
     * Data 도메인 객체와 주제, 데이터 소스, 데이터 유형 라벨, 연결된 프로젝트 수를 기반으로 FilteredDataResponse DTO를 생성합니다.
     * 데이터의 주요 속성, 메타데이터 정보, 각종 라벨, 연결된 프로젝트 수를 포함한 DTO를 반환합니다.
     *
     * @param data 변환할 Data 도메인 객체
     * @param username 데이터셋 업로더 닉네임
     * @param topicLabel 데이터의 주제 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param dataTypeLabel 데이터 유형 라벨
     * @param countConnectedProjects 데이터와 연결된 프로젝트 수
     * @return 변환된 FilteredDataResponse DTO
     */
    public FilteredDataResponse toResponseDto(
            Data data,
            String username,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new FilteredDataResponse(
                data.getId(),
                data.getTitle(),
                data.getUserId(),
                username,
                topicLabel,
                dataSourceLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getDataThumbnailUrl(),
                data.getDownloadCount(),
                data.getSizeBytes(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }
}
