package com.dataracy.modules.like.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;

class LikeWebMapperTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;
  private static final Integer TWENTY_FOUR = 24;
  private static final Integer FOURTEEN = 14;
  private static final Integer EIGHTEEN = 18;

  @Test
  @DisplayName("타겟 좋아요 앱 DTO -> 웹 DTO")
  void toApplicationDtoShouldMapFields() {
    // given
    LikeWebMapper mapper = new LikeWebMapper();
    TargetLikeWebRequest web = new TargetLikeWebRequest(5L, "PROJECT", false);

    // when
    TargetLikeRequest dto = mapper.toApplicationDto(web);

    // then
    assertAll(
        () -> assertThat(dto.targetId()).isEqualTo(5L),
        () -> assertThat(dto.targetType()).isEqualTo("PROJECT"),
        () -> assertThat(dto.previouslyLiked()).isFalse());
  }
}
