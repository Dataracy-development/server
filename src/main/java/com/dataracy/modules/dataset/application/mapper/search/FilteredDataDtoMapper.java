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
     * Data 도메인 객체와 라벨 및 연결 프로젝트 수로 FilteredDataResponse DTO를 생성하여 반환합니다.
     *
     * <p>Data 객체의 식별자, 제목, 업로더 정보, 라벨(주제·데이터 소스·데이터 유형)과
     * 시작/종료일, 설명, 썸네일 URL, 다운로드 수, 크기, 행/열 수, 생성일, 연결 프로젝트 수를 매핑합니다.</p>
     *
     * <p>주의: 전달된 data 및 data.getMetadata()는 null이 아니어야 합니다.</p>
     *
     * @param data 변환 대상 Data 도메인 객체
     * @param username 데이터셋 업로더 닉네임
     * @param topicLabel 주제 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param dataTypeLabel 데이터 유형 라벨
     * @param countConnectedProjects 데이터에 연결된 프로젝트 수
     * @return 생성된 FilteredDataResponse DTO
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
