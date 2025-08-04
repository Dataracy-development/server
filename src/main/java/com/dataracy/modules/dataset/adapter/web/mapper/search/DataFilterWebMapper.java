package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import org.springframework.stereotype.Component;

@Component
public class DataFilterWebMapper {

    /**
     * DataFilterWebRequest 객체를 DataFilterRequest 도메인 DTO로 변환합니다.
     *
     * @param webRequest 데이터 필터링 기준이 담긴 웹 요청 DTO
     * @return 필터링 조건이 반영된 DataFilterRequest 도메인 DTO
     */
    public FilteringDataRequest toApplicationDto(FilteringDataWebRequest webRequest) {
        return new FilteringDataRequest(
                webRequest.keyword(),
                webRequest.sortType(),
                webRequest.topicId(),
                webRequest.dataSourceId(),
                webRequest.dataTypeId(),
                webRequest.year()
        );
    }

    /**
     * DataFilterResponse 객체를 DataFilterWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 DataFilterResponse 객체
     * @return 변환된 DataFilterWebResponse 객체
     */
    public FilteredDataWebResponse toWebDto(FilteredDataResponse responseDto) {
        return new FilteredDataWebResponse(
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
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
