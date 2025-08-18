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
     * 데이터 도메인 객체와 주제/유형 레이블, 연결된 프로젝트 수를 결합하여 ConnectedDataResponse DTO로 변환합니다.
     *
     * @param data 변환할 데이터 도메인 객체
     * @param topicLabel 데이터의 주제 레이블
     * @param dataTypeLabel 데이터의 유형 레이블
     * @param countConnectedProjects 데이터와 연결된 프로젝트의 개수
     * @return 데이터 정보와 주제/유형 레이블, 연결된 프로젝트 수를 포함하는 ConnectedDataResponse DTO
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
                data.getDataThumbnailUrl(),
                data.getDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }

    /**
     * Data 도메인 객체를 최근 최소 정보 데이터 응답 DTO로 변환합니다.
     *
     * 데이터의 ID, 제목, 썸네일 URL, 생성일시만을 포함하는 RecentMinimalDataResponse 객체를 생성하여 반환합니다.
     *
     * @param data 최소 정보로 변환할 Data 도메인 객체
     * @return ID, 제목, 썸네일 URL, 생성일시만 포함된 RecentMinimalDataResponse DTO
     */
    public RecentMinimalDataResponse toResponseDto(
            Data data
    ) {
        return new RecentMinimalDataResponse(
                data.getId(),
                data.getTitle(),
                data.getDataThumbnailUrl(),
                data.getCreatedAt()
        );
    }

    /**
     * 데이터셋 도메인 객체와 관련 라벨 및 연결 프로젝트 수를 받아 인기 데이터셋 응답 DTO로 변환합니다.
     *
     * 데이터셋, 사용자 이름, 주제/데이터 소스/데이터 타입 라벨, 연결된 프로젝트 수를 조합하여
     * 인기 데이터셋 정보를 담은 PopularDataResponse 객체를 생성합니다.
     *
     * @param username 데이터셋과 연관된 사용자 이름
     * @param topicLabel 데이터셋의 주제 라벨
     * @param dataSourceLabel 데이터셋의 데이터 소스 라벨
     * @param dataTypeLabel 데이터셋의 데이터 타입 라벨
     * @param countConnectedProjects 해당 데이터셋과 연결된 프로젝트 수
     * @return 인기 데이터셋 정보를 담은 PopularDataResponse DTO
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
                data.getDataThumbnailUrl(),
                data.getDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }

    /**
     * Data 도메인 객체와 작성자 및 데이터셋 관련 라벨 정보를 활용하여 상세 데이터 조회용 DataDetailResponse DTO로 변환합니다.
     *
     * @param nickname 데이터셋 작성자의 닉네임
     * @param authorLabel 데이터셋 작성자 라벨
     * @param occupationLabel 데이터셋 작성자 직업 라벨
     * @param topicLabel 데이터셋 주제 라벨
     * @param dataSourceLabel 데이터 출처 라벨
     * @param dataTypeLabel 데이터 유형 라벨
     * @return 데이터의 상세 정보를 담은 DataDetailResponse DTO
     */
    public DataDetailResponse toResponseDto(
            Data data,
            String nickname,
            String userProfileImageUrl,
            String userIntroductionText,
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
                userProfileImageUrl,
                userIntroductionText,
                authorLabel,
                occupationLabel,
                topicLabel,
                dataSourceLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getAnalysisGuide(),
                data.getDataThumbnailUrl(),
                data.getDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getMetadata().getPreviewJson(),
                data.getCreatedAt()
        );
    }
}
