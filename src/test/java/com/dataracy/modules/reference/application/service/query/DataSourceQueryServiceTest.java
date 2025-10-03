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

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.application.mapper.DataSourceDtoMapper;
import com.dataracy.modules.reference.application.port.out.DataSourcePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.DataSource;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSourceQueryServiceTest {

  @Mock private DataSourcePort dataSourcePort;

  @Mock private DataSourceDtoMapper dataSourceDtoMapper;

  @InjectMocks private DataSourceQueryService service;

  @Test
  @DisplayName("데이터 소스 전체 조회 성공")
  void findAllDataSourcesSuccess() {
    // given
    List<DataSource> domainList =
        List.of(new DataSource(1L, "v1", "l1"), new DataSource(2L, "v2", "l2"));
    AllDataSourcesResponse mapped =
        new AllDataSourcesResponse(
            List.of(
                new DataSourceResponse(1L, "v1", "l1"), new DataSourceResponse(2L, "v2", "l2")));
    given(dataSourcePort.findAllDataSources()).willReturn(domainList);
    given(dataSourceDtoMapper.toResponseDto(domainList)).willReturn(mapped);

    // when
    AllDataSourcesResponse result = service.findAllDataSources();

    // then
    assertThat(result).isSameAs(mapped);
    then(dataSourcePort).should().findAllDataSources();
    then(dataSourceDtoMapper).should().toResponseDto(domainList);
  }

  @Test
  @DisplayName("데이터 소스 단건 조회 성공")
  void findDataSourceSuccess() {
    // given
    Long id = 10L;
    DataSource domain = new DataSource(id, "v", "l");
    DataSourceResponse mapped = new DataSourceResponse(id, "v", "l");
    given(dataSourcePort.findDataSourceById(id)).willReturn(Optional.of(domain));
    given(dataSourceDtoMapper.toResponseDto(domain)).willReturn(mapped);

    // when
    DataSourceResponse result = service.findDataSource(id);

    // then
    assertThat(result).isSameAs(mapped);
    then(dataSourcePort).should().findDataSourceById(id);
    then(dataSourceDtoMapper).should().toResponseDto(domain);
  }

  @Test
  @DisplayName("데이터 소스 단건 조회 실패 - 없을 때 예외 발생")
  void findDataSourceFailWhenNotFound() {
    // given
    Long id = 999L;
    given(dataSourcePort.findDataSourceById(id)).willReturn(Optional.empty());

    // when
    ReferenceException ex =
        catchThrowableOfType(() -> service.findDataSource(id), ReferenceException.class);

    // then
    assertThat(ex).isNotNull();
    then(dataSourcePort).should().findDataSourceById(id);
    then(dataSourceDtoMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("데이터 소스 라벨 조회 성공")
  void getLabelByIdSuccess() {
    // given
    Long id = 1L;
    given(dataSourcePort.getLabelById(id)).willReturn(Optional.of("label"));

    // when
    String label = service.getLabelById(id);

    // then
    assertThat(label).isEqualTo("label");
    then(dataSourcePort).should().getLabelById(id);
  }

  @Test
  @DisplayName("데이터 소스 라벨 조회 실패 - 없을 때 예외 발생")
  void getLabelByIdFailWhenNotFound() {
    // given
    Long id = 404L;
    given(dataSourcePort.getLabelById(id)).willReturn(Optional.empty());

    // when
    ReferenceException ex =
        catchThrowableOfType(() -> service.getLabelById(id), ReferenceException.class);

    // then
    assertThat(ex).isNotNull();
    then(dataSourcePort).should().getLabelById(id);
  }

  @Test
  @DisplayName("데이터 소스 검증 성공 - 존재할 때")
  void validateDataSourceSuccess() {
    // given
    Long id = 1L;
    given(dataSourcePort.existsDataSourceById(id)).willReturn(true);

    // when
    service.validateDataSource(id);

    // then
    then(dataSourcePort).should().existsDataSourceById(id);
  }

  @Test
  @DisplayName("데이터 소스 검증 실패 - 없을 때 예외 발생")
  void validateDataSourceFailWhenNotFound() {
    // given
    Long id = 2L;
    given(dataSourcePort.existsDataSourceById(id)).willReturn(false);

    // when
    ReferenceException ex =
        catchThrowableOfType(() -> service.validateDataSource(id), ReferenceException.class);

    // then
    assertThat(ex).isNotNull();
    then(dataSourcePort).should().existsDataSourceById(id);
  }

  @Test
  @DisplayName("데이터 소스 라벨 다건 조회 성공 및 빈 값 처리")
  void getLabelsByIdsSuccessAndEmptyHandling() {
    // given - empty/null
    assertAll(
        () -> assertThat(service.getLabelsByIds(null)).isEmpty(),
        () -> assertThat(service.getLabelsByIds(List.of())).isEmpty());

    // given - values
    List<Long> ids = List.of(1L, 2L);
    given(dataSourcePort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

    // when
    Map<Long, String> result = service.getLabelsByIds(ids);

    // then
    assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
    then(dataSourcePort).should().getLabelsByIds(ids);
  }
}
