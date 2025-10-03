/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManageDataEsProjectionTaskDbAdapterTest {

  @Mock private DataEsProjectionTaskRepository repo;

  @InjectMocks private ManageDataEsProjectionTaskDbAdapter adapter;

  @Captor private ArgumentCaptor<DataEsProjectionTaskEntity> captor;

  @Test
  @DisplayName("enqueueSetDeleted 호출 시 삭제 플래그가 저장된다")
  void enqueueSetDeletedShouldSaveEntity() {
    // given
    Long dataId = 1L;
    boolean deleted = true;

    // when
    adapter.enqueueSetDeleted(dataId, deleted);

    // then
    then(repo).should().save(captor.capture());
    DataEsProjectionTaskEntity saved = captor.getValue();

    assertAll(
        () -> assertThat(saved.getDataId()).isEqualTo(dataId),
        () -> assertThat(saved.getSetDeleted()).isTrue());
  }

  @Test
  @DisplayName("enqueueDownloadDelta 호출 시 다운로드 델타가 저장된다")
  void enqueueDownloadDeltaShouldSaveEntity() {
    // given
    Long dataId = 1L;
    int delta = 5;

    // when
    adapter.enqueueDownloadDelta(dataId, delta);

    // then
    then(repo).should().save(captor.capture());
    DataEsProjectionTaskEntity saved = captor.getValue();

    assertAll(
        () -> assertThat(saved.getDataId()).isEqualTo(dataId),
        () -> assertThat(saved.getDeltaDownload()).isEqualTo(delta));
  }

  @Test
  @DisplayName("delete 호출 시 해당 ID의 Task 가 삭제된다")
  void deleteShouldDeleteById() {
    // given
    Long taskId = 10L;

    // when
    adapter.delete(taskId);

    // then
    then(repo).should().deleteById(taskId);
  }
}
