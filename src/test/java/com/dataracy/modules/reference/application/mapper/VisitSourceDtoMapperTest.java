package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import com.dataracy.modules.reference.domain.model.VisitSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisitSourceDtoMapperTest {
    private final VisitSourceDtoMapper mapper = new VisitSourceDtoMapper();

    @Test
    @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
    void toResponseDto_single_success() {
        // given
        VisitSource domain = new VisitSource(1L, "v", "l");

        // when
        VisitSourceResponse dto = mapper.toResponseDto(domain);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.value()).isEqualTo("v");
        assertThat(dto.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toResponseDto(list): 성공 - 리스트 변환")
    void toResponseDto_list_success() {
        // given
        List<VisitSource> domains = List.of(new VisitSource(1L,"v1","l1"), new VisitSource(2L,"v2","l2"));

        // when
        AllVisitSourcesResponse all = mapper.toResponseDto(domains);

        // then
        assertThat(all.visitSources()).hasSize(2);
        assertThat(all.visitSources().get(0).id()).isEqualTo(1L);
    }
}
