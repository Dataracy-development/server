package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;
import com.dataracy.modules.reference.domain.model.Occupation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OccupationDtoMapperTest {
    private final OccupationDtoMapper mapper = new OccupationDtoMapper();

    @Test
    @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
    void toResponseDto_single_success() {
        // given
        Occupation domain = new Occupation(1L, "v", "l");

        // when
        OccupationResponse dto = mapper.toResponseDto(domain);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.value()).isEqualTo("v");
        assertThat(dto.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toResponseDto(list): 성공 - 리스트 변환")
    void toResponseDto_list_success() {
        // given
        List<Occupation> domains = List.of(new Occupation(1L,"v1","l1"), new Occupation(2L,"v2","l2"));

        // when
        AllOccupationsResponse all = mapper.toResponseDto(domains);

        // then
        assertThat(all.occupations()).hasSize(2);
        assertThat(all.occupations().get(0).id()).isEqualTo(1L);
    }
}
