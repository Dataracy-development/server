package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.AllDataSourcesResponse;
import com.dataracy.modules.reference.domain.model.DataSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DataSource 도메인 DTO와 DataSource 도메인 모델을 변환하는 매퍼
 */
@Component
public class DataSourceDtoMapper {
    // DataSource 도메인 모델 -> DataSource 도메인 응답 DTO
    public AllDataSourcesResponse.DataSourceResponse toResponseDto(DataSource dataSource) {
        return new AllDataSourcesResponse.DataSourceResponse(
                dataSource.id(),
                dataSource.value(),
                dataSource.label()
        );
    }

    // 전체 DataSource 리스트 조회 도메인 모델 -> 전체 DataSource 리스트 조회 도메인 응답 DTO
    public AllDataSourcesResponse toResponseDto(List<DataSource> dataSources) {
        return new AllDataSourcesResponse(
                dataSources.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
