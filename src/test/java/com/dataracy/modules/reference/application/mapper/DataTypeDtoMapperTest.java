package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataTypeResponse;
import com.dataracy.modules.reference.domain.model.DataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataTypeDtoMapperTest {
    private final DataTypeDtoMapper mapper = new DataTypeDtoMapper();

    @Test
    @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
    void toResponseDtoSingleSuccess() {
        // given
        DataType domain = new DataType(1L, "v", "l");

        // when
        DataTypeResponse dto = mapper.toResponseDto(domain);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.value()).isEqualTo("v");
        assertThat(dto.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toResponseDto(list): 성공 - 리스트 변환")
    void toResponseDtoListSuccess() {
        // given
        List<DataType> domains = List.of(new DataType(1L,"v1","l1"), new DataType(2L,"v2","l2"));

        // when
        AllDataTypesResponse all = mapper.toResponseDto(domains);

        // then
        assertThat(all.dataTypes()).hasSize(2);
        assertThat(all.dataTypes().get(0).id()).isEqualTo(1L);
    }
}
