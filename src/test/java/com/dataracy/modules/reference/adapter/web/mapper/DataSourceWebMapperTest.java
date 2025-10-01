package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DataSourceWebMapperTest {

    private DataSourceWebMapper dataSourceWebMapper;

    @BeforeEach
    void setUp() {
        dataSourceWebMapper = new DataSourceWebMapper();
    }

    @Test
    @DisplayName("toWebDto - DataSourceResponse를 DataSourceWebResponse로 변환한다")
    void toWebDto_WhenDataSourceResponse_ConvertsToDataSourceWebResponse() {
        // given
        DataSourceResponse dataSourceResponse = new DataSourceResponse(1L, "GOV", "정부기관");

        // when
        DataSourceWebResponse result = dataSourceWebMapper.toWebDto(dataSourceResponse);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(1L),
                () -> assertThat(result.value()).isEqualTo("GOV"),
                () -> assertThat(result.label()).isEqualTo("정부기관")
        );
    }

    @Test
    @DisplayName("toWebDto - AllDataSourcesResponse를 AllDataSourcesWebResponse로 변환한다")
    void toWebDto_WhenAllDataSourcesResponse_ConvertsToAllDataSourcesWebResponse() {
        // given
        List<DataSourceResponse> dataSources = List.of(
                new DataSourceResponse(1L, "GOV", "정부기관"),
                new DataSourceResponse(2L, "CORP", "기업"),
                new DataSourceResponse(3L, "ACAD", "학술기관")
        );
        AllDataSourcesResponse allDataSourcesResponse = new AllDataSourcesResponse(dataSources);

        // when
        AllDataSourcesWebResponse result = dataSourceWebMapper.toWebDto(allDataSourcesResponse);

        // then
        assertAll(
                () -> assertThat(result.dataSources()).hasSize(3),
                () -> assertThat(result.dataSources().get(0).id()).isEqualTo(1L),
                () -> assertThat(result.dataSources().get(0).value()).isEqualTo("GOV"),
                () -> assertThat(result.dataSources().get(0).label()).isEqualTo("정부기관"),
                () -> assertThat(result.dataSources().get(1).id()).isEqualTo(2L),
                () -> assertThat(result.dataSources().get(1).value()).isEqualTo("CORP"),
                () -> assertThat(result.dataSources().get(1).label()).isEqualTo("기업"),
                () -> assertThat(result.dataSources().get(2).id()).isEqualTo(3L),
                () -> assertThat(result.dataSources().get(2).value()).isEqualTo("ACAD"),
                () -> assertThat(result.dataSources().get(2).label()).isEqualTo("학술기관")
        );
    }

    @Test
    @DisplayName("toWebDto - AllDataSourcesResponse가 null인 경우 빈 리스트를 반환한다")
    void toWebDto_WhenAllDataSourcesResponseIsNull_ReturnsEmptyList() {
        // when
        AllDataSourcesWebResponse result = dataSourceWebMapper.toWebDto((AllDataSourcesResponse) null);

        // then
        assertThat(result.dataSources()).isEmpty();
    }

    @Test
    @DisplayName("toWebDto - AllDataSourcesResponse의 dataSources가 null인 경우 빈 리스트를 반환한다")
    void toWebDto_WhenDataSourcesIsNull_ReturnsEmptyList() {
        // given
        AllDataSourcesResponse allDataSourcesResponse = new AllDataSourcesResponse(null);

        // when
        AllDataSourcesWebResponse result = dataSourceWebMapper.toWebDto(allDataSourcesResponse);

        // then
        assertThat(result.dataSources()).isEmpty();
    }

    @Test
    @DisplayName("toWebDto - AllDataSourcesResponse의 dataSources가 빈 리스트인 경우 빈 리스트를 반환한다")
    void toWebDto_WhenDataSourcesIsEmpty_ReturnsEmptyList() {
        // given
        AllDataSourcesResponse allDataSourcesResponse = new AllDataSourcesResponse(List.of());

        // when
        AllDataSourcesWebResponse result = dataSourceWebMapper.toWebDto(allDataSourcesResponse);

        // then
        assertThat(result.dataSources()).isEmpty();
    }

    @Test
    @DisplayName("toWebDto - DataSourceResponse의 모든 필드가 null인 경우에도 변환한다")
    void toWebDto_WhenDataSourceResponseFieldsAreNull_ConvertsToDataSourceWebResponse() {
        // given
        DataSourceResponse dataSourceResponse = new DataSourceResponse(null, null, null);

        // when
        DataSourceWebResponse result = dataSourceWebMapper.toWebDto(dataSourceResponse);

        // then
        assertAll(
                () -> assertThat(result.id()).isNull(),
                () -> assertThat(result.value()).isNull(),
                () -> assertThat(result.label()).isNull()
        );
    }
}