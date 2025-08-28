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
         * <p>입력 DTO의 주요 필드(예: id, title, creatorId, creatorName, userProfileImageUrl, description,
         * previewJson, createdAt 등)를 그대로 전달하여 대응하는 웹 응답 객체를 생성합니다.</p>
         *
         * @param responseDto 변환할 애플리케이션 계층의 DataDetailResponse
         * @return 변환된 DataDetailWebResponse
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
     * ConnectedDataResponse DTO를 ConnectedDataWebResponse로 변환합니다.
     * 데이터셋의 기본 정보와 연결된 프로젝트 수를 웹 응답 객체로 매핑합니다.
     *
     * @param responseDto 데이터셋 및 연결 프로젝트 수 정보를 포함한 DTO
     * @return 매핑된 데이터셋 연결 정보 웹 응답 객체
     */
    public ConnectedDataWebResponse toWebDto(ConnectedDataResponse responseDto) {
        return new ConnectedDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
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
     * 최근 최소 데이터 응답 DTO를 웹 레이어용 RecentMinimalDataWebResponse로 변환합니다.
     *
     * 변환된 웹 DTO에는 id, title, creatorId, creatorName, dataThumbnailUrl, createdAt가 포함됩니다.
     *
     * @param responseDto 변환 대상 애플리케이션 레이어 RecentMinimalDataResponse
     * @return id, 제목, 작성자(id·이름), 썸네일 URL 및 생성일시를 포함한 RecentMinimalDataWebResponse
     */
    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.dataThumbnailUrl(),
                responseDto.createdAt()
        );
    }

    /**
     * PopularDataResponse를 PopularDataWebResponse로 변환합니다.
     *
     * <p>입력 DTO의 필드(id, title, creatorId, creatorName, topicLabel, dataSourceLabel,
     * dataTypeLabel, startDate, endDate, description, dataThumbnailUrl, downloadCount,
     * sizeBytes, rowCount, columnCount, createdAt, countConnectedProjects)를 그대로 매핑하여
     * PopularDataWebResponse 인스턴스를 생성하여 반환합니다.</p>
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
}
