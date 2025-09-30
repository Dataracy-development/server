package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.domain.model.DataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DataTypeDtoMapper 테스트")
class DataTypeDtoMapperTest {

    private final DataTypeDtoMapper mapper = new DataTypeDtoMapper();

    @Test
    @DisplayName("단일 DataType을 DataTypeResponse로 변환 성공")
    void toResponseDto_ShouldConvertSingleDataType() {
        // Given
        DataType dataType = new DataType(1L, "csv", "CSV 파일");

        // When
        DataTypeResponse response = mapper.toResponseDto(dataType);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.value()).isEqualTo("csv");
        assertThat(response.label()).isEqualTo("CSV 파일");
    }

    @Test
    @DisplayName("DataType 리스트를 AllDataTypesResponse로 변환 성공")
    void toResponseDto_ShouldConvertDataTypeList() {
        // Given
        DataType dataType1 = new DataType(1L, "csv", "CSV 파일");
        DataType dataType2 = new DataType(2L, "json", "JSON 파일");
        List<DataType> dataTypes = List.of(dataType1, dataType2);

        // When
        AllDataTypesResponse response = mapper.toResponseDto(dataTypes);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.dataTypes()).hasSize(2);
        assertThat(response.dataTypes().get(0).id()).isEqualTo(1L);
        assertThat(response.dataTypes().get(0).value()).isEqualTo("csv");
        assertThat(response.dataTypes().get(1).id()).isEqualTo(2L);
        assertThat(response.dataTypes().get(1).value()).isEqualTo("json");
    }

    @Test
    @DisplayName("빈 DataType 리스트를 AllDataTypesResponse로 변환 성공")
    void toResponseDto_ShouldConvertEmptyDataTypeList() {
        // Given
        List<DataType> dataTypes = List.of();

        // When
        AllDataTypesResponse response = mapper.toResponseDto(dataTypes);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.dataTypes()).isEmpty();
    }
}