/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.impl.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExtractDataFileUrlDbAdapterFindTest {

  @Mock private DataJpaRepository repo;

  @InjectMocks private ExtractDataFileUrlDbAdapterFind adapter;

  @Nested
  @DisplayName("업로더 아이디 추출")
  class ExtractUploaderId {

    @Test
    @DisplayName("findUserIdByDataId - 데이터가 없으면 NOT_FOUND_DATA 예외 발생")
    void findUserIdByDataIdShouldThrowWhenNotFound() {
      // given
      given(repo.findById(99L)).willReturn(Optional.empty());

      // when & then
      DataException ex =
          catchThrowableOfType(() -> adapter.findUserIdByDataId(99L), DataException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("findUserIdByDataId - 데이터가 존재하면 userId 반환")
    void findUserIdByDataIdShouldReturnUserId() {
      // given
      DataEntity entity = DataEntity.builder().userId(10L).build();
      given(repo.findById(1L)).willReturn(Optional.of(entity));

      // when
      Long userId = adapter.findUserIdByDataId(1L);

      // then
      assertThat(userId).isEqualTo(10L);
    }

    @Test
    @DisplayName("findUserIdIncludingDeleted - 데이터가 없으면 NOT_FOUND_DATA 예외 발생")
    void findUserIdIncludingDeletedShouldThrowWhenNotFound() {
      // given
      given(repo.findIncludingDeletedData(99L)).willReturn(Optional.empty());

      // when & then
      DataException ex =
          catchThrowableOfType(() -> adapter.findUserIdIncludingDeleted(99L), DataException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("findUserIdIncludingDeleted - 삭제된 데이터도 포함해서 userId 반환")
    void findUserIdIncludingDeletedShouldReturnUserId() {
      // given
      DataEntity entity = DataEntity.builder().userId(20L).build();
      given(repo.findIncludingDeletedData(1L)).willReturn(Optional.of(entity));

      // when
      Long userId = adapter.findUserIdIncludingDeleted(1L);

      // then
      assertThat(userId).isEqualTo(20L);
    }
  }

  @Nested
  @DisplayName("데이터셋 다운로드 URL 추출")
  class ExtractDownloadUrl {

    @Test
    @DisplayName("findDownloadedDataFileUrl - 존재하지 않으면 Optional.empty 반환")
    void findDownloadedDataFileUrlShouldReturnEmptyWhenNotPresent() {
      // given
      given(repo.findDataFileUrlById(1L)).willReturn(Optional.empty());

      // when
      Optional<String> result = adapter.findDownloadedDataFileUrl(1L);

      // then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findDownloadedDataFileUrl - 데이터가 존재하면 url 반환")
    void findDownloadedDataFileUrlShouldReturnUrlWhenPresent() {
      // given
      given(repo.findDataFileUrlById(1L)).willReturn(Optional.of("fileUrl"));

      // when
      Optional<String> result = adapter.findDownloadedDataFileUrl(1L);

      // then
      assertThat(result).contains("fileUrl");
    }
  }
}
