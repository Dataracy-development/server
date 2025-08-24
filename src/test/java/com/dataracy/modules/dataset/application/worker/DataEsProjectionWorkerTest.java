package com.dataracy.modules.dataset.application.worker;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionDlqPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataEsProjectionWorkerTest {

    @InjectMocks
    private DataEsProjectionWorker worker;

    @Mock private ManageDataProjectionTaskPort taskPort;
    @Mock private LoadDataProjectionTaskPort loadPort;
    @Mock private ManageDataProjectionDlqPort dlqPort;
    @Mock private SoftDeleteDataPort softDeletePort;
    @Mock private UpdateDataDownloadPort dlPort;

    private DataEsProjectionTaskEntity task(Long id, Long dataId, boolean deleted, int delta) {
        DataEsProjectionTaskEntity e = new DataEsProjectionTaskEntity();
        e.setId(id);
        e.setDataId(dataId);
        e.setSetDeleted(deleted);
        e.setDeltaDownload(delta);
        e.setRetryCount(0);
        e.setStatus(DataEsProjectionType.PENDING);
        e.setNextRunAt(LocalDateTime.now());
        return e;
    }

    @Test
    void processTaskSuccessDelete() {
        DataEsProjectionTaskEntity t = task(1L, 10L, true, 0);
        worker.processTask(t);
        then(softDeletePort).should().deleteData(10L);
        then(taskPort).should().delete(1L);
    }

    @Test
    void processTaskSuccessDownload() {
        DataEsProjectionTaskEntity t = task(2L, 11L, false, 2);
        worker.processTask(t);
        then(dlPort).should().increaseDownloadCount(11L);
        then(taskPort).should().delete(2L);
    }

    @Test
    void processTaskFailureMovesToDlqAfterMaxRetry() {
        DataEsProjectionTaskEntity t = task(3L,12L,true,0);
        t.setRetryCount(7);
        willThrow(new RuntimeException("fail")).given(softDeletePort).deleteData(12L);

        worker.processTask(t);

        then(dlqPort).should().save(eq(12L), anyInt(), any(), any());
        then(taskPort).should().delete(3L);
    }

    @Test
    void runShouldLoadAndProcess() {
        DataEsProjectionTaskEntity t = task(4L,13L,true,0);
        given(loadPort.findBatchForWork(any(), any(), any(PageRequest.class))).willReturn(List.of(t));
        worker.run();
        then(taskPort).should().delete(4L);
    }
}
