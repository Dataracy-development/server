package com.dataracy.modules.dataset.application.mapper.read;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;
import com.dataracy.modules.dataset.application.dto.response.read.DataDetailResponse;
import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.domain.model.Data;
import org.springframework.stereotype.Component;

/**
 * 최신 데이터셋 도메인 DTO와 최신 데이터셋 도메인 모델을 변환하는 매퍼
 */
@Component
public class DataReadDtoMapper {
    /**
     * 도메인 모델 객체와 추가 정보를 결합하여 ConnectedDataAssociatedWithProjectResponse DTO로 변환합니다.
     *
     * @param data 데이터셋 도메인 객체
     * @param topicLabel 데이터셋의 주제 레이블
     * @param dataTypeLabel 데이터셋의 유형 레이블
     * @param countConnectedProjects 데이터와 연결된 프로젝트 수
     * @return 변환된 ConnectedDataAssociatedWithProjectResponse DTO
     */
    public ConnectedDataResponse toResponseDto(
            Data data,
            String topicLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new ConnectedDataResponse(
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

    /**
     * 도메인 모델 Data 객체를 DataMinimalSearchResponse DTO로 변환합니다.
     *
     * @param data 변환할 Data 객체
     * @return DataMinimalSearchResponse로 매핑된 DTO 객체
     */
    public RecentMinimalDataResponse toResponseDto(
            Data data
    ) {
        return new RecentMinimalDataResponse(
                data.getId(),
                data.getTitle(),
                data.getThumbnailUrl(),
                data.getCreatedAt()
        );
    }

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
    public PopularDataResponse toResponseDto(
            Data data,
            String username,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new PopularDataResponse(
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
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }

    public DataDetailResponse toResponseDto(
            Data data,
            String nickname,
            String authorLabel,
            String occupationLabel,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel
    ) {
        return new DataDetailResponse(
                data.getId(),
                data.getTitle(),
                nickname,
                authorLabel,
                occupationLabel,
                topicLabel,
                dataSourceLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getAnalysisGuide(),
                data.getThumbnailUrl(),
                data.getDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getMetadata().getPreviewJson(),
                data.getCreatedAt()
        );
    }
}
