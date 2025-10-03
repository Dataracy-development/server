/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import com.dataracy.modules.reference.domain.model.AuthorLevel;

class AuthorLevelDtoMapperTest {
  private final AuthorLevelDtoMapper mapper = new AuthorLevelDtoMapper();

  @Test
  @DisplayName("toResponseDto(single): 성공 - 도메인에서 DTO로 매핑")
  void toResponseDtoSingleSuccess() {
    // given
    AuthorLevel domain = new AuthorLevel(1L, "v", "l");

    // when
    AuthorLevelResponse dto = mapper.toResponseDto(domain);

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
    List<AuthorLevel> domains =
        List.of(new AuthorLevel(1L, "v1", "l1"), new AuthorLevel(2L, "v2", "l2"));

    // when
    AllAuthorLevelsResponse all = mapper.toResponseDto(domains);

    // then
    assertAll(
        () -> assertThat(all.authorLevels()).hasSize(2),
        () -> assertThat(all.authorLevels().get(0).id()).isEqualTo(1L));
  }
}
