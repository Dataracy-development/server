package com.dataracy.modules.dataset.application.worker;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionDlqPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataEsProjectionWorkerTest {

    @InjectMocks
    private DataEsProjectionWorker worker;

    @Mock
    private ManageDataProjectionTaskPort taskPort;

    @Mock
    private LoadDataProjectionTaskPort loadPort;

    @Mock
    private ManageDataProjectionDlqPort dlqPort;

    @Mock
    private SoftDeleteDataPort softDeletePort;

    @Mock
    private UpdateDataDownloadPort dlPort;

    @BeforeEach
    void setUp() {
        // Self-injection 설정 (Spring AOP 프록시를 통한 @Transactional 및 @DistributedLock 작동을 위함)
        worker.setSelf(worker);
    }

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
    @DisplayName("삭제 작업 처리 성공 → softDeletePort 호출 후 task 삭제")
    void processTaskSuccessDelete() {
        // given
        DataEsProjectionTaskEntity t = task(1L, 10L, true, 0);

        // when
        worker.processTask(t);

        // then
        then(softDeletePort).should().deleteData(10L);
        then(taskPort).should().delete(1L);
    }

    @Test
    @DisplayName("다운로드 작업 처리 성공 → downloadPort 호출 후 task 삭제")
    void processTaskSuccessDownload() {
        // given
        DataEsProjectionTaskEntity t = task(2L, 11L, false, 2);

        // when
        worker.processTask(t);

        // then
        then(dlPort).should().increaseDownloadCount(11L);
        then(taskPort).should().delete(2L);
    }

    @Test
    @DisplayName("작업 실패 + 재시도 초과 → DLQ 저장 후 task 삭제")
    void processTaskFailureMovesToDlqAfterMaxRetry() {
        // given
        DataEsProjectionTaskEntity t = task(3L, 12L, true, 0);
        t.setRetryCount(7);
        willThrow(new  RuntimeException("fail"))
                .given(softDeletePort).deleteData(12L);

        // when
        worker.processTask(t);

        // then
        then(dlqPort).should().save(eq(12L), anyInt(), any(), any());
        then(taskPort).should().delete(3L);
    }

    @Test
    @DisplayName("run 호출 시 LoadPort → processTask 실행 후 taskPort 삭제 확인")
    void runShouldLoadAndProcess() throws Exception {
        // given
        DataEsProjectionTaskEntity t = task(4L, 13L, true, 0);
        given(loadPort.findBatchForWork(any(), any(), any(PageRequest.class)))
                .willReturn(List.of(t));

        // when
        worker.run();

        // then
        then(loadPort).should().findBatchForWork(any(), any(), any(PageRequest.class));
        then(softDeletePort).should().deleteData(13L);
        then(taskPort).should().delete(4L);
    }
}
