package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllDataSourcesResponse;
import org.springframework.stereotype.Component;

/**
 * dataSource 웹 DTO와 dataSource 도메인 DTO를 변환하는 매퍼
 */
@Component
public class DataSourceWebMapper {
    // dataSource 조회 도메인 응답 DTO -> dataSource 조회 웹 응답 DTO
    public AllDataSourcesWebResponse.DataSourceWebResponse toWebDto(AllDataSourcesResponse.DataSourceResponse dataSourceResponse) {
        return new AllDataSourcesWebResponse.DataSourceWebResponse(
                dataSourceResponse.id(),
                dataSourceResponse.value(),
                dataSourceResponse.label()
        );
    }

    // 전체 dataSource 리스트 조회 도메인 응답 DTO -> 전체 dataSource 리스트 조회 웹 응답 DTO
    public AllDataSourcesWebResponse toWebDto(AllDataSourcesResponse allDataSourcesResponse) {
        return new AllDataSourcesWebResponse(
                allDataSourcesResponse.dataSources()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
