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
         * Data 도메인 객체와 보조 정보(업로더 닉네임·프로필 이미지, 레이블, 연결된 프로젝트 수)를 결합하여
         * ConnectedDataResponse DTO 인스턴스를 생성하여 반환합니다.
         *
         * @param data 변환할 Data 도메인 객체
         * @param username 업로더의 표시 이름(닉네임)
         * @param userProfileImageUrl 업로더의 프로필 이미지 URL
         * @param topicLabel 데이터의 주제 레이블
         * @param dataTypeLabel 데이터의 유형 레이블
         * @param countConnectedProjects 해당 데이터와 연결된 프로젝트 수
         * @return 구성된 ConnectedDataResponse DTO
         */
    public ConnectedDataResponse toResponseDto(
            Data data,
            String username,
            String userProfileImageUrl,
            String topicLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new ConnectedDataResponse(
                data.getId(),
                data.getTitle(),
                data.getUserId(),
                username,
                userProfileImageUrl,
                topicLabel,
                dataTypeLabel,
                data.getStartDate(),
                data.getEndDate(),
                data.getDataThumbnailUrl(),
                data.getDownloadCount(),
                data.getSizeBytes(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getCreatedAt(),
                countConnectedProjects
        );
    }

    /**
     * Data 도메인 객체를 최근 최소 정보 응답 DTO(RecentMinimalDataResponse)로 변환합니다.
     *
     * 반환되는 DTO는 데이터의 ID, 제목, 업로더 ID/닉네임/프로필 이미지 URL, 썸네일 URL, 생성일시만 포함합니다.
     *
     * @param data 변환할 Data 도메인 객체
     * @param username 업로더의 표시 이름(닉네임)
     * @param userProfileImageUrl 업로더의 프로필 이미지 URL(없을 경우 null 가능)
     * @return ID, 제목, 업로더 정보, 썸네일 URL, 생성일시를 담은 RecentMinimalDataResponse
     */
    public RecentMinimalDataResponse toResponseDto(
            Data data,
            String username,
            String userProfileImageUrl
    ) {
        return new RecentMinimalDataResponse(
                data.getId(),
                data.getTitle(),
                data.getUserId(),
                username,
                userProfileImageUrl,
                data.getDataThumbnailUrl(),
                data.getCreatedAt()
        );
    }

    /**
     * Data 도메인 객체와 사용자·라벨 정보를 결합해 인기 데이터셋 조회용 PopularDataResponse를 생성합니다.
     *
     * 전달된 Data 객체의 식별자·제목·기간·설명·썸네일·다운로드수·크기(bytes)·메타데이터(행/열 수)·생성일과
     * 추가로 제공된 username, topicLabel, dataSourceLabel, dataTypeLabel, 연결된 프로젝트 수를 포함한 DTO를 반환합니다.
     *
     * @param data 변환할 Data 도메인 객체
     * @param username 데이터와 연관된 사용자 이름
     * @param userProfileImageUrl 데이터셋 업로더 프로필 이미지 URL
     * @param topicLabel 데이터의 주제 라벨
     * @param dataSourceLabel 데이터의 출처(데이터 소스) 라벨
     * @param dataTypeLabel 데이터의 타입 라벨
     * @param countConnectedProjects 해당 데이터와 연결된 프로젝트 수
     * @return 인기 데이터셋 정보를 담은 PopularDataResponse
     */
    public PopularDataResponse toResponseDto(
            Data data,
            String username,
            String userProfileImageUrl,
            String topicLabel,
            String dataSourceLabel,
            String dataTypeLabel,
            Long countConnectedProjects
    ) {
        return new PopularDataResponse(
                data.getId(),
                data.getTitle(),
                data.getUserId(),
                username,
                userProfileImageUrl,
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

    /**
     * Data 도메인과 작성자·라벨 정보를 결합해 상세 조회용 DataDetailResponse를 생성합니다.
     *
     * 해당 DTO는 데이터의 메타(행/열/프리뷰), 생성일 및 작성자 관련 표시 정보를 포함합니다.
     *
     * @param userProfileImageUrl 작성자 프로필 이미지 URL (없을 경우 null 허용)
     * @param userIntroductionText 작성자 소개 문구 (없을 경우 null 허용)
     * @return 데이터 상세 정보를 담은 {@link DataDetailResponse} 인스턴스
     */
    public DataDetailResponse toResponseDto(
            Data data,
            String username,
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
                data.getUserId(),
                username,
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
                data.getSizeBytes(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getMetadata().getPreviewJson(),
                data.getCreatedAt()
        );
    }
}
