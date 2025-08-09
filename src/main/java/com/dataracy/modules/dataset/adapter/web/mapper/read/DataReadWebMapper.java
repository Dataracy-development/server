package com.dataracy.modules.dataset.adapter.web.mapper.read;

import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import org.springframework.stereotype.Component;

@Component
public class DataReadWebMapper {
    /**
     * 데이터셋의 상세 정보를 담은 애플리케이션 계층 DTO를 웹 계층의 상세 응답 객체로 변환합니다.
     *
     * @param responseDto 데이터셋의 상세 정보가 포함된 DTO
     * @return 데이터셋 상세 정보를 포함하는 웹 응답 객체
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
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.previewJson(),
                responseDto.createdAt()
        );
    }

    /**
     * DataGroupCountResponse 객체를 DataGroupCountWebResponse 객체로 변환합니다.
     *
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
     *
     * 데이터셋의 기본 정보와 연결된 프로젝트 수를 웹 응답 객체로 매핑합니다.
     *
     * @param responseDto 데이터셋 및 연결 프로젝트 수 정보를 포함한 DTO
     * @return 매핑된 데이터셋 연결 정보 웹 응답 객체
     */
    public ConnectedDataWebResponse toWebDto(ConnectedDataResponse responseDto) {
        return new ConnectedDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }

    /**
     * 최근 최소 데이터 정보를 웹 응답 DTO로 변환합니다.
     *
     * @param responseDto 최근 최소 데이터 정보를 담은 도메인 DTO
     * @return 데이터셋 ID, 제목, 썸네일 URL, 생성일시를 포함하는 웹 응답 DTO
     */
    public RecentMinimalDataWebResponse toWebDto(RecentMinimalDataResponse responseDto) {
        return new RecentMinimalDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.dataThumbnailUrl(),
                responseDto.createdAt()
        );
    }

    /**
     * 인기 데이터 검색 도메인 DTO를 웹 응답 DTO로 변환합니다.
     *
     * 데이터셋의 식별자, 제목, 사용자명, 주제, 데이터 소스 및 타입 라벨, 날짜 범위, 설명, 썸네일 URL, 다운로드 수, 행/열 개수, 생성일, 연결된 프로젝트 수를 포함한 정보를 매핑하여 반환합니다.
     *
     * @param responseDto 인기 데이터 검색 도메인 DTO
     * @return 인기 데이터 검색 웹 응답 DTO
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
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
