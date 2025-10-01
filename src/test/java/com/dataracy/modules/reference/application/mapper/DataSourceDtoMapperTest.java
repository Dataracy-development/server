package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.domain.model.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DataSourceDtoMapper 테스트")
class DataSourceDtoMapperTest {

    private final DataSourceDtoMapper mapper = new DataSourceDtoMapper();

    @Test
    @DisplayName("단일 DataSource를 DataSourceResponse로 변환 성공")
    void toResponseDto_ShouldConvertSingleDataSource() {
        // Given
        DataSource dataSource = new DataSource(1L, "government", "정부 기관");

        // When
        DataSourceResponse response = mapper.toResponseDto(dataSource);

        // Then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.id()).isEqualTo(1L),
                () -> assertThat(response.value()).isEqualTo("government"),
                () -> assertThat(response.label()).isEqualTo("정부 기관")
        );
    }

    @Test
    @DisplayName("DataSource 리스트를 AllDataSourcesResponse로 변환 성공")
    void toResponseDto_ShouldConvertDataSourceList() {
        // Given
        DataSource dataSource1 = new DataSource(1L, "government", "정부 기관");
        DataSource dataSource2 = new DataSource(2L, "private", "민간 기업");
        List<DataSource> dataSources = List.of(dataSource1, dataSource2);

        // When
        AllDataSourcesResponse response = mapper.toResponseDto(dataSources);

        // Then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.dataSources()).hasSize(2),
                () -> assertThat(response.dataSources().get(0).id()).isEqualTo(1L),
                () -> assertThat(response.dataSources().get(0).value()).isEqualTo("government"),
                () -> assertThat(response.dataSources().get(1).id()).isEqualTo(2L),
                () -> assertThat(response.dataSources().get(1).value()).isEqualTo("private")
        );
    }

    @Test
    @DisplayName("빈 DataSource 리스트를 AllDataSourcesResponse로 변환 성공")
    void toResponseDto_ShouldConvertEmptyDataSourceList() {
        // Given
        List<DataSource> dataSources = List.of();

        // When
        AllDataSourcesResponse response = mapper.toResponseDto(dataSources);

        // Then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.dataSources()).isEmpty()
        );
    }
}