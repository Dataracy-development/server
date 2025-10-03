/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import com.dataracy.modules.reference.application.mapper.AuthorLevelDtoMapper;
import com.dataracy.modules.reference.application.port.out.AuthorLevelPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AuthorLevel;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorLevelQueryServiceTest {

  @Mock private AuthorLevelPort authorLevelPort;

  @Mock private AuthorLevelDtoMapper authorLevelDtoMapper;

  @InjectMocks private AuthorLevelQueryService service;

  @Test
  @DisplayName("작성자 레벨 전체 조회 성공")
  void findAllAuthorLevelsSuccess() {
    // given
    List<AuthorLevel> domainList =
        List.of(new AuthorLevel(1L, "v1", "l1"), new AuthorLevel(2L, "v2", "l2"));
    AllAuthorLevelsResponse mapped =
        new AllAuthorLevelsResponse(
            List.of(
                new AuthorLevelResponse(1L, "v1", "l1"), new AuthorLevelResponse(2L, "v2", "l2")));
    given(authorLevelPort.findAllAuthorLevels()).willReturn(domainList);
    given(authorLevelDtoMapper.toResponseDto(domainList)).willReturn(mapped);

    // when
    AllAuthorLevelsResponse result = service.findAllAuthorLevels();

    // then
    assertThat(result).isSameAs(mapped);
    then(authorLevelPort).should().findAllAuthorLevels();
    then(authorLevelDtoMapper).should().toResponseDto(domainList);
  }

  @Test
  @DisplayName("작성자 레벨 단건 조회 성공")
  void findAuthorLevelSuccess() {
    // given
    Long id = 10L;
    AuthorLevel domain = new AuthorLevel(id, "v", "l");
    AuthorLevelResponse mapped = new AuthorLevelResponse(id, "v", "l");
    given(authorLevelPort.findAuthorLevelById(id)).willReturn(Optional.of(domain));
    given(authorLevelDtoMapper.toResponseDto(domain)).willReturn(mapped);

    // when
    AuthorLevelResponse result = service.findAuthorLevel(id);

    // then
    assertThat(result).isSameAs(mapped);
    then(authorLevelPort).should().findAuthorLevelById(id);
    then(authorLevelDtoMapper).should().toResponseDto(domain);
  }

  @Test
  @DisplayName("작성자 레벨 단건 조회 실패 - 없을 때 예외 발생")
  void findAuthorLevelFailWhenNotFound() {
    // given
    Long id = 999L;
    given(authorLevelPort.findAuthorLevelById(id)).willReturn(Optional.empty());

    // when
    ReferenceException ex =
        catchThrowableOfType(() -> service.findAuthorLevel(id), ReferenceException.class);

    // then
    assertThat(ex).isNotNull();
    then(authorLevelPort).should().findAuthorLevelById(id);
    then(authorLevelDtoMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("작성자 레벨 라벨 조회 성공")
  void getLabelByIdSuccess() {
    // given
    Long id = 1L;
    given(authorLevelPort.getLabelById(id)).willReturn(Optional.of("label"));

    // when
    String label = service.getLabelById(id);

    // then
    assertThat(label).isEqualTo("label");
    then(authorLevelPort).should().getLabelById(id);
  }

  @Test
  @DisplayName("작성자 레벨 라벨 조회 실패 - 없을 때 예외 발생")
  void getLabelByIdFailWhenNotFound() {
    // given
    Long id = 404L;
    given(authorLevelPort.getLabelById(id)).willReturn(Optional.empty());

    // when
    ReferenceException ex =
        catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

    // then
    assertThat(ex).isNotNull();
    then(authorLevelPort).should().getLabelById(id);
  }

  @Test
  @DisplayName("작성자 레벨 검증 성공 - 존재할 때")
  void validateAuthorLevelSuccess() {
    // given
    Long id = 1L;
    given(authorLevelPort.existsAuthorLevelById(id)).willReturn(true);

    // when
    service.validateAuthorLevel(id);

    // then
    then(authorLevelPort).should().existsAuthorLevelById(id);
  }

  @Test
  @DisplayName("작성자 레벨 검증 실패 - 없을 때 예외 발생")
  void validateAuthorLevelFailWhenNotFound() {
    // given
    Long id = 2L;
    given(authorLevelPort.existsAuthorLevelById(id)).willReturn(false);

    // when
    ReferenceException ex =
        catchThrowableOfType(() -> service.validateAuthorLevel(id), ReferenceException.class);

    // then
    assertThat(ex).isNotNull();
    then(authorLevelPort).should().existsAuthorLevelById(id);
  }

  @Test
  @DisplayName("작성자 레벨 라벨 다건 조회 성공 및 빈 값 처리")
  void getLabelsByIdsSuccessAndEmptyHandling() {
    // given - empty/null
    assertAll(
        () -> assertThat(service.getLabelsByIds(null)).isEmpty(),
        () -> assertThat(service.getLabelsByIds(List.of())).isEmpty());

    // given - values
    List<Long> ids = List.of(1L, 2L);
    given(authorLevelPort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

    // when
    Map<Long, String> result = service.getLabelsByIds(ids);

    // then
    assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
    then(authorLevelPort).should().getLabelsByIds(ids);
  }
}
