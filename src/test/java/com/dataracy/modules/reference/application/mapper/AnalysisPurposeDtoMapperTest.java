package com.dataracy.modules.reference.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

class AnalysisPurposeDtoMapperTest {
  private final AnalysisPurposeDtoMapper mapper = new AnalysisPurposeDtoMapper();

  @Test
  @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
  void toResponseDtoSingleSuccess() {
    // given
    AnalysisPurpose domain = new AnalysisPurpose(1L, "v", "l");

    // when
    AnalysisPurposeResponse dto = mapper.toResponseDto(domain);

    // then
    assertAll(
        () -> assertThat(dto.id()).isEqualTo(1L),
        () -> assertThat(dto.value()).isEqualTo("v"),
        () -> assertThat(dto.label()).isEqualTo("l"));
  }

  @Test
  @DisplayName("toResponseDto(list): 성공 - 리스트 변환")
  void toResponseDtoListSuccess() {
    // given
    List<AnalysisPurpose> domains =
        List.of(new AnalysisPurpose(1L, "v1", "l1"), new AnalysisPurpose(2L, "v2", "l2"));

    // when
    AllAnalysisPurposesResponse all = mapper.toResponseDto(domains);

    // then
    assertAll(
        () -> assertThat(all.analysisPurposes()).hasSize(2),
        () -> assertThat(all.analysisPurposes().get(0).id()).isEqualTo(1L));
  }
}
