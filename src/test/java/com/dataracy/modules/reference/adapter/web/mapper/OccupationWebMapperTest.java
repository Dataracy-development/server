/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.reference.adapter.web.response.allview.AllOccupationsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.OccupationWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.OccupationResponse;

class OccupationWebMapperTest {
  private final OccupationWebMapper mapper = new OccupationWebMapper();

  @Test
  @DisplayName("toWebDto(single): 성공 - 필드 매핑")
  void toWebDtoSingleSuccess() {
    // given
    OccupationResponse src = new OccupationResponse(1L, "v", "l");

    // when
    OccupationWebResponse result = mapper.toWebDto(src);

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
    AllOccupationsResponse src =
        new AllOccupationsResponse(
            java.util.List.of(
                new OccupationResponse(1L, "v1", "l1"), new OccupationResponse(2L, "v2", "l2")));

    // when
    AllOccupationsWebResponse result = mapper.toWebDto(src);
    AllOccupationsWebResponse nullSafe1 = mapper.toWebDto((AllOccupationsResponse) null);
    AllOccupationsWebResponse nullSafe2 = mapper.toWebDto(new AllOccupationsResponse(null));

    // then
    assertAll(
        () -> assertThat(result.occupations()).hasSize(2),
        () -> assertThat(nullSafe1.occupations()).isEmpty(),
        () -> assertThat(nullSafe2.occupations()).isEmpty());
  }
}
