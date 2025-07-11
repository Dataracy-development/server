package com.dataracy.modules.reference.application.dto.response;

import java.util.List;

/**
 * dataSources 리스트 조회를 위한 도메인 응답 DTO
 * @param dataSources dataSources 리스트
 */
public record AllDataSourcesResponse(List<DataSourceResponse> dataSources) {
    public record DataSourceResponse(Long id, String value, String label
    ) {}
}
