package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllDataSourcesResponse;
import org.springframework.stereotype.Component;

/**
 * dataSource 웹 DTO와 dataSource 도메인 DTO를 변환하는 매퍼
 */
@Component
public class DataSourceWebMapper {
    /**
     * 도메인 계층의 DataSourceResponse DTO를 웹 계층의 DataSourceWebResponse DTO로 변환합니다.
     *
     * @param dataSourceResponse 변환할 도메인 DataSourceResponse 객체
     * @return 변환된 웹 DataSourceWebResponse 객체
     */
    public AllDataSourcesWebResponse.DataSourceWebResponse toWebDto(AllDataSourcesResponse.DataSourceResponse dataSourceResponse) {
        return new AllDataSourcesWebResponse.DataSourceWebResponse(
                dataSourceResponse.id(),
                dataSourceResponse.value(),
                dataSourceResponse.label()
        );
    }

    /**
     * 도메인 계층의 전체 데이터 소스 응답 DTO를 웹 계층의 전체 데이터 소스 응답 DTO로 변환합니다.
     *
     * @param allDataSourcesResponse 변환할 도메인 전체 데이터 소스 응답 DTO
     * @return 변환된 웹 계층 전체 데이터 소스 응답 DTO
     */
    public AllDataSourcesWebResponse toWebDto(AllDataSourcesResponse allDataSourcesResponse) {
        return new AllDataSourcesWebResponse(
                allDataSourcesResponse.dataSources()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
