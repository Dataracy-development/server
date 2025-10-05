package com.dataracy.modules.project.adapter.web.mapper.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;

class ChildProjectWebMapperTest {

  private final ChildProjectWebMapper mapper = new ChildProjectWebMapper();

  @Test
  @DisplayName("성공 → ChildProjectResponse를 ChildProjectWebResponse로 매핑")
  void toWebDto() {
    // given
    ChildProjectResponse responseDto =
        new ChildProjectResponse(
            1L, "child-title", "child-content", 1L, "tester", "https://~~", 10L, 20L);

    // when
    ChildProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(1L),
        () -> assertThat(webResponse.title()).isEqualTo("child-title"),
        () -> assertThat(webResponse.content()).isEqualTo("child-content"),
        () -> assertThat(webResponse.creatorName()).isEqualTo("tester"),
        () -> assertThat(webResponse.commentCount()).isEqualTo(10L),
        () -> assertThat(webResponse.likeCount()).isEqualTo(20L));
  }
}
