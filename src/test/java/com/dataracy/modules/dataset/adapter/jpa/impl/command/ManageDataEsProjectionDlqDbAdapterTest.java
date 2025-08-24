package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionDlqEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionDlqRepository;
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
class ManageDataEsProjectionDlqDbAdapterTest {

    @Mock
    private DataEsProjectionDlqRepository repo;

    @InjectMocks
    private ManageDataEsProjectionDlqDbAdapter adapter;

    @Captor
    private ArgumentCaptor<DataEsProjectionDlqEntity> captor;

    @Test
    void saveShouldPersistEntity() {
        // given
        Long dataId = 1L;
        Integer deltaDownload = 2;
        Boolean setDeleted = true;
        String lastError = "error";

        // when
        adapter.save(dataId, deltaDownload, setDeleted, lastError);

        // then
        then(repo).should().save(captor.capture());
        DataEsProjectionDlqEntity savedEntity = captor.getValue();

        assertThat(savedEntity.getDataId()).isEqualTo(dataId);
        assertThat(savedEntity.getDeltaDownload()).isEqualTo(deltaDownload);
        assertThat(savedEntity.getSetDeleted()).isEqualTo(setDeleted);
        assertThat(savedEntity.getLastError()).isEqualTo(lastError);
    }
}
