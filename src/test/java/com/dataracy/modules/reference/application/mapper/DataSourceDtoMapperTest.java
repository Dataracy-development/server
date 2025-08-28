package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.domain.model.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataSourceDtoMapperTest {
    private final DataSourceDtoMapper mapper = new DataSourceDtoMapper();

    @Test
    @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
    void toResponseDtoSingleSuccess() {
        // given
        DataSource domain = new DataSource(1L, "v", "l");

        // when
        DataSourceResponse dto = mapper.toResponseDto(domain);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.value()).isEqualTo("v");
        assertThat(dto.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toResponseDto(list): 성공 - 리스트 변환")
    void toResponseDtoListSuccess() {
        // given
        List<DataSource> domains = List.of(new DataSource(1L,"v1","l1"), new DataSource(2L,"v2","l2"));

        // when
        AllDataSourcesResponse all = mapper.toResponseDto(domains);

        // then
        assertThat(all.dataSources()).hasSize(2);
        assertThat(all.dataSources().get(0).id()).isEqualTo(1L);
    }
}
