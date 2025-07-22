package com.dataracy.modules.dataset.adapter.web.mapper;

import com.dataracy.modules.dataset.adapter.web.request.DataFilterWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.DataPopularSearchWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.DataSimilarSearchWebResponse;
import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
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
     * DataPopularSearchResponse 도메인 객체를 DataPopularSearchWebResponse 웹 응답 객체로 변환합니다.
     *
     * @param responseDto 변환할 도메인 응답 DTO
     * @return 변환된 웹 응답 DTO
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
}
