package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DataTypeWebMapper 테스트")
class DataTypeWebMapperTest {

    private final DataTypeWebMapper mapper = new DataTypeWebMapper();

    @Test
    @DisplayName("단일 DataTypeResponse를 DataTypeWebResponse로 변환 성공")
    void toWebDto_ShouldConvertSingleResponse() {
        // Given
        DataTypeResponse response = new DataTypeResponse(1L, "type1", "Type 1");

        // When
        DataTypeWebResponse webResponse = mapper.toWebDto(response);

        // Then
        assertAll(
                () -> assertThat(webResponse).isNotNull(),
                () -> assertThat(webResponse.id()).isEqualTo(1L),
                () -> assertThat(webResponse.value()).isEqualTo("type1"),
                () -> assertThat(webResponse.label()).isEqualTo("Type 1")
        );
    }

    @Test
    @DisplayName("전체 DataTypesResponse를 AllDataTypesWebResponse로 변환 성공")
    void toWebDto_ShouldConvertAllResponse() {
        // Given
        DataTypeResponse response1 = new DataTypeResponse(1L, "type1", "Type 1");
        DataTypeResponse response2 = new DataTypeResponse(2L, "type2", "Type 2");
        AllDataTypesResponse allResponse = new AllDataTypesResponse(List.of(response1, response2));

        // When
        AllDataTypesWebResponse webResponse = mapper.toWebDto(allResponse);

        // Then
        assertAll(
                () -> assertThat(webResponse).isNotNull(),
                () -> assertThat(webResponse.dataTypes()).hasSize(2),
                () -> assertThat(webResponse.dataTypes().get(0).id()).isEqualTo(1L),
                () -> assertThat(webResponse.dataTypes().get(0).value()).isEqualTo("type1"),
                () -> assertThat(webResponse.dataTypes().get(1).id()).isEqualTo(2L),
                () -> assertThat(webResponse.dataTypes().get(1).value()).isEqualTo("type2")
        );
    }

    @Test
    @DisplayName("빈 리스트로 AllDataTypesResponse 변환 성공")
    void toWebDto_ShouldConvertEmptyResponse() {
        // Given
        AllDataTypesResponse allResponse = new AllDataTypesResponse(List.of());

        // When
        AllDataTypesWebResponse webResponse = mapper.toWebDto(allResponse);

        // Then
        assertAll(
                () -> assertThat(webResponse).isNotNull(),
                () -> assertThat(webResponse.dataTypes()).isEmpty()
        );
    }
}