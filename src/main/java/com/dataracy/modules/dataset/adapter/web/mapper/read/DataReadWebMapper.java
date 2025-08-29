package com.dataracy.modules.dataset.adapter.web.mapper.read;

import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import org.springframework.stereotype.Component;

/**
 * 데이터 조회 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class DataReadWebMapper {
    /**
     * 애플리케이션 계층의 DataDetailResponse를 웹 계층의 DataDetailWebResponse로 변환합니다.
     *
     * <p>원본 DTO의 각 필드(id, title, username, userProfileImageUrl, userIntroductionText,
     * authorLabel, occupationLabel, topicLabel, dataSourceLabel, dataTypeLabel, startDate, endDate,
     * description, analysisGuide, dataThumbnailUrl, downloadCount, sizeBytes, rowCount, columnCount,
     * previewJson, createdAt)를 그대로 전달하여 웹 응답 객체를 생성합니다.</p>
     *
     * @param responseDto 변환할 애플리케이션 계층의 데이터셋 상세 응답 DTO
     * @return 변환된 웹 계층의 데이터셋 상세 응답 객체
     */
    public DataDetailWebResponse toWebDto(DataDetailResponse responseDto) {
        return new DataDetailWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.userIntroductionText(),
                responseDto.authorLabel(),
                responseDto.occupationLabel(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.analysisGuide(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.sizeBytes(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.previewJson(),
                responseDto.createdAt()
        );
    }

    /**
     * DataGroupCountResponse 객체를 DataGroupCountWebResponse 객체로 변환합니다.
     * 주어진 주제별 데이터 개수 정보를 웹 응답 객체로 매핑합니다.
     *
     * @param responseDto 주제별 데이터 개수 정보를 담은 애플리케이션 계층 DTO
     * @return 주제 ID, 주제명, 개수를 포함하는 웹 계층 응답 객체
     */
    public DataGroupCountWebResponse toWebDto(DataGroupCountResponse responseDto) {
        return new DataGroupCountWebResponse(
                responseDto.topicId(),
                responseDto.topicLabel(),
                responseDto.count()
        );
    }

    /**
     * ConnectedDataResponse를 ConnectedDataWebResponse로 변환합니다.
     *
     * 응답 DTO의 기본 데이터셋 정보(작성자 프로필 이미지 포함)와 연결된 프로젝트 수를
     * 웹 응답 객체로 매핑하여 반환합니다.
     *
     * @param responseDto 변환할 도메인 응답 DTO
     * @return 변환된 ConnectedDataWebResponse
     */
    public ConnectedDataWebResponse toWebDto(ConnectedDataResponse responseDto) {
        return new ConnectedDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.sizeBytes(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }

    /**
     * RecentMinimalDataResponse를 RecentMinimalDataWebResponse로 변환합니다.
     *
     * <p>변환된 웹 응답에는 다음 필드가 포함됩니다: id, title, creatorId, creatorName,
     * userProfileImageUrl, dataThumbnailUrl, createdAt.</p>
     *
     * @param responseDto 변환할 애플리케이션 계층의 최근 최소 데이터 응답 DTO
     * @return 위에 열거된 필드를 포함한 RecentMinimalDataWebResponse
     */
    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.dataThumbnailUrl(),
                responseDto.createdAt()
        );
    }

    /**
     * 인기 데이터 애플리케이션 응답을 웹 응답 DTO로 변환합니다.
     *
     * <p>다음 필드를 PopularDataWebResponse로 매핑하여 새 인스턴스를 반환합니다:
     * id, title, creatorId, creatorName, userProfileImageUrl, topicLabel, dataSourceLabel,
     * dataTypeLabel, startDate, endDate, description, dataThumbnailUrl, downloadCount,
     * sizeBytes, rowCount, columnCount, createdAt, countConnectedProjects.</p>
     *
     * @param responseDto 인기 데이터 검색 애플리케이션 응답 DTO
     * @return 매핑된 인기 데이터 웹 응답 DTO
     */
    public PopularDataWebResponse toWebDto(PopularDataResponse responseDto) {
        return new PopularDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.sizeBytes(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }

    public UserDataWebResponse toWebDto(UserDataResponse responseDto) {
        return new UserDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.sizeBytes(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
