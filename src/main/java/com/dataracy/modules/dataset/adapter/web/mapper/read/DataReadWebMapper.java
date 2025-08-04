package com.dataracy.modules.dataset.adapter.web.mapper.read;

import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import org.springframework.stereotype.Component;

@Component
public class DataReadWebMapper {
    /**
     * 애플리케이션 계층의 데이터 상세 응답 DTO를 웹 계층의 응답 객체로 변환합니다.
     *
     * @param responseDto 데이터 상세 정보를 담고 있는 애플리케이션 계층 DTO
     * @return 웹 계층에서 사용할 데이터 상세 응답 객체
     */
    public DataDetailWebResponse toWebDto(DataDetailResponse responseDto) {
        return new DataDetailWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.authorLabel(),
                responseDto.occupationLabel(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.analysisGuide(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.previewJson(),
                responseDto.createdAt()
        );
    }

    /**
     * CountDataGroupResponse 객체를 CountDataGroupWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 애플리케이션 계층의 CountDataGroupResponse 객체
     * @return 웹 계층에서 사용하는 CountDataGroupWebResponse 객체
     */
    public DataGroupCountWebResponse toWebDto(DataGroupCountResponse responseDto) {
        return new DataGroupCountWebResponse(
                responseDto.topicId(),
                responseDto.topicLabel(),
                responseDto.count()
        );
    }

    /**
     * 애플리케이션 계층의 ConnectedDataAssociatedWithProjectResponse DTO를 웹 계층의 ConnectedDataAssociatedWithProjectWebResponse로 변환합니다.
     *
     * @param responseDto 변환할 데이터셋 연결 정보 DTO
     * @return 변환된 웹 응답 객체
     */
    public ConnectedDataWebResponse toWebDto(ConnectedDataResponse responseDto) {
        return new ConnectedDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }

    /**
     * DataMinimalSearchResponse 도메인 DTO를 DataMinimalSearchWebResponse 웹 DTO로 변환합니다.
     *
     * @param responseDto 변환할 최소 데이터 검색 도메인 DTO
     * @return 변환된 최소 데이터 검색 웹 응답 DTO
     */
    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.thumbnailUrl(),
                responseDto.createdAt()
        );
    }

    /**
     * DataPopularSearchResponse 도메인 DTO를 DataPopularSearchWebResponse 웹 응답 DTO로 변환합니다.
     *
     * 데이터셋의 식별자, 제목, 사용자명, 주제/데이터 소스/데이터 타입 라벨, 날짜 범위, 설명, 썸네일 URL, 다운로드 수, 최근 주간 다운로드 수, 행/열 개수, 생성일, 연결된 프로젝트 수를 포함하여 모든 관련 필드를 매핑합니다.
     *
     * @param responseDto 변환할 도메인 계층의 인기 데이터 검색 응답 DTO
     * @return 변환된 웹 계층의 인기 데이터 검색 응답 DTO
     */
    public PopularDataWebResponse toWebDto(PopularDataResponse responseDto) {
        return new PopularDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.username(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
