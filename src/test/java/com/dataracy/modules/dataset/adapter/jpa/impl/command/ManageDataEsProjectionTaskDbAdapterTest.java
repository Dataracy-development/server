package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ManageDataEsProjectionTaskDbAdapterTest {

    @Mock
    private DataEsProjectionTaskRepository repo;

    @InjectMocks
    private ManageDataEsProjectionTaskDbAdapter adapter;

    @Captor
    private ArgumentCaptor<DataEsProjectionTaskEntity> captor;

    @Test
    void enqueueSetDeletedShouldSaveEntity() {
        // given
        Long dataId = 1L;
        boolean deleted = true;

        // when
        adapter.enqueueSetDeleted(dataId, deleted);

        // then
        then(repo).should().save(captor.capture());
        DataEsProjectionTaskEntity saved = captor.getValue();

        assertThat(saved.getDataId()).isEqualTo(dataId);
        assertThat(saved.getSetDeleted()).isTrue();
    }

    @Test
    void enqueueDownloadDeltaShouldSaveEntity() {
        // given
        Long dataId = 1L;
        int delta = 5;

        // when
        adapter.enqueueDownloadDelta(dataId, delta);

        // then
        then(repo).should().save(captor.capture());
        DataEsProjectionTaskEntity saved = captor.getValue();

        assertThat(saved.getDataId()).isEqualTo(dataId);
        assertThat(saved.getDeltaDownload()).isEqualTo(delta);
    }

    @Test
    void deleteShouldDeleteById() {
        // given
        Long taskId = 10L;

        // when
        adapter.delete(taskId);

        // then
        then(repo).should().deleteById(taskId);
    }
}
