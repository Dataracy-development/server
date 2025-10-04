package com.dataracy.modules.reference.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.reference.adapter.web.response.allview.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.AnalysisPurposeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;

class AnalysisPurposeWebMapperTest {
  private final AnalysisPurposeWebMapper mapper = new AnalysisPurposeWebMapper();

  @Test
  @DisplayName("toWebDto(single): 성공 - 필드 매핑")
  void toWebDtoSingleSuccess() {
    // given
    AnalysisPurposeResponse src = new AnalysisPurposeResponse(1L, "v", "l");

    // when
    AnalysisPurposeWebResponse result = mapper.toWebDto(src);

    // then
    assertAll(
        () -> assertThat(result.id()).isEqualTo(1L),
        () -> assertThat(result.value()).isEqualTo("v"),
        () -> assertThat(result.label()).isEqualTo("l"));
  }

  @Test
  @DisplayName("toWebDto(all): 성공 - 리스트 매핑, null/빈 처리")
  void toWebDtoAllSuccessAndNullsafe() {
    // given
    AllAnalysisPurposesResponse src =
        new AllAnalysisPurposesResponse(
            java.util.List.of(
                new AnalysisPurposeResponse(1L, "v1", "l1"),
                new AnalysisPurposeResponse(2L, "v2", "l2")));

    // when
    AllAnalysisPurposesWebResponse result = mapper.toWebDto(src);
    AllAnalysisPurposesWebResponse nullSafe1 = mapper.toWebDto((AllAnalysisPurposesResponse) null);
    AllAnalysisPurposesWebResponse nullSafe2 =
        mapper.toWebDto(new AllAnalysisPurposesResponse(null));

    // then
    assertAll(
        () -> assertThat(result.analysisPurposes()).hasSize(2),
        () -> assertThat(nullSafe1.analysisPurposes()).isEmpty(),
        () -> assertThat(nullSafe2.analysisPurposes()).isEmpty());
  }
}
