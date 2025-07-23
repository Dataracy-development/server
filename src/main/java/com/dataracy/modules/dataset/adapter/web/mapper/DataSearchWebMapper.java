package com.dataracy.modules.dataset.adapter.web.mapper;

import com.dataracy.modules.dataset.adapter.web.request.DataFilterWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.DataMinimalSearchWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.DataPopularSearchWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.DataSimilarSearchWebResponse;
import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.response.DataMinimalSearchResponse;
import com.dataracy.modules.dataset.application.dto.response.DataPopularSearchResponse;
import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class DataSearchWebMapper {
    /**
     * 도메인 계층의 DataSimilarSearchResponse 객체를 웹 응답용 DataSimilarSearchWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 도메인 DTO 객체
     * @return 변환된 웹 응답 DTO 객체
     */
    public DataSimilarSearchWebResponse toWebDto(DataSimilarSearchResponse responseDto) {
        return new DataSimilarSearchWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataSourceLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.description(),
                responseDto.thumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.recentWeekDownloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
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
    public DataPopularSearchWebResponse toWebDto(DataPopularSearchResponse responseDto) {
        return new DataPopularSearchWebResponse(
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
                responseDto.recentWeekDownloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }

    /**
     * DataFilterWebRequest 객체를 DataFilterRequest 도메인 DTO로 변환합니다.
     *
     * @param webRequest 데이터 필터링 기준이 담긴 웹 요청 DTO
     * @return 필터링 조건이 반영된 DataFilterRequest 도메인 DTO
     */
    public DataFilterRequest toApplicationDto(DataFilterWebRequest webRequest) {
        return new DataFilterRequest(
                webRequest.keyword(),
                webRequest.sortType(),
                webRequest.topicId(),
                webRequest.dataSourceId(),
                webRequest.dataTypeId(),
                webRequest.year()
        );
    }

    /**
     * DataMinimalSearchResponse 도메인 DTO를 DataMinimalSearchWebResponse 웹 DTO로 변환합니다.
     *
     * @param responseDto 변환할 최소 데이터 검색 도메인 DTO
     * @return 변환된 최소 데이터 검색 웹 응답 DTO
     */
    public DataMinimalSearchWebResponse toWebDto(DataMinimalSearchResponse responseDto) {
        return new DataMinimalSearchWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.thumbnailUrl(),
                responseDto.createdAt()
        );
    }
}
