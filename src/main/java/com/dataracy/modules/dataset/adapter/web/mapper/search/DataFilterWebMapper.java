package com.dataracy.modules.dataset.adapter.web.mapper.search;

import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import org.springframework.stereotype.Component;

/**
 * 데이터 필터링 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class DataFilterWebMapper {
    /**
     * FilteringDataWebRequest 웹 요청 DTO를 FilteringDataRequest 애플리케이션 도메인 DTO로 변환합니다.
     *
     * @param webRequest 데이터 필터링 조건이 포함된 웹 요청 DTO
     * @return 필터링 조건이 반영된 FilteringDataRequest 도메인 DTO
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
     * 애플리케이션 계층의 FilteredDataResponse를 웹 계층의 FilteredDataWebResponse로 변환합니다.
     *
     * <p>responseDto의 모든 필드(id, title, creatorId, creatorName, userProfileImageUrl, topicLabel 등)를
     * 대응하는 웹 응답 필드로 그대로 매핑하여 새 FilteredDataWebResponse 인스턴스를 생성합니다.</p>
     *
     * @param responseDto 변환할 애플리케이션 계층의 FilteredDataResponse
     * @return 웹 응답에 사용되는 FilteredDataWebResponse 인스턴스
     */
    public FilteredDataWebResponse toWebDto(FilteredDataResponse responseDto) {
        return new FilteredDataWebResponse(
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
}
