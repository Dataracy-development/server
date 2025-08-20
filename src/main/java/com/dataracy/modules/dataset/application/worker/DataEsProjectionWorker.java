package com.dataracy.modules.dataset.application.worker;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionDlqEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionDlqRepository;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataEsProjectionWorker {
    private final DataEsProjectionTaskRepository queueRepo;
    private final DataEsProjectionDlqRepository dlqRepo;

    // ES 어댑터들 (Qualifier로 ES 구현 주입)
    private final SoftDeleteDataPort softDeleteDataEsPort;
    private final UpdateDataDownloadPort updateDataDownloadEsPort;

    private static final int BATCH = 100;
    private static final int MAX_RETRY = 8;

    public DataEsProjectionWorker(
            DataEsProjectionTaskRepository queueRepo,
            DataEsProjectionDlqRepository dlqRepo,
            @Qualifier("softDeleteDataEsAdapter") SoftDeleteDataPort softDeleteDataEsPort,
            @Qualifier("updateDataDownloadEsAdapter") UpdateDataDownloadPort updateDataDownloadEsPort
    ) {
        this.queueRepo = queueRepo;
        this.dlqRepo = dlqRepo;
        this.softDeleteDataEsPort = softDeleteDataEsPort;
        this.updateDataDownloadEsPort = updateDataDownloadEsPort;
    }

    private long backoffSeconds(int retryCount) {
        // 1,2,4,8,16,32,64,120(캡)
        if (retryCount >= 8) return 120;
        long shift = Math.max(0, retryCount - 1);
        return 1L << Math.min(shift, 6);
    }

    @Transactional
    @Scheduled(fixedDelayString = "PT1S")
    public void run() {
        List<DataEsProjectionTaskEntity> tasks = queueRepo.findBatchForWork(
                LocalDateTime.now(),
                List.of(DataEsProjectionType.PENDING, DataEsProjectionType.RETRYING),
                PageRequest.of(0, BATCH)
        );

        for (var t : tasks) {
            try {
                // 소프트 삭제/복원 우선
                if (t.getSetDeleted() != null) {
                    if (t.getSetDeleted()) {
                        softDeleteDataEsPort.deleteData(t.getDataId());
                    }
                    else {
                        softDeleteDataEsPort.restoreData(t.getDataId());
                    }
                }

                // 다운로드 수 증가
                if (t.getDeltaDownload() > 0) {
                    updateDataDownloadEsPort.increaseDownloadCount(t.getDataId());
                }

                // 성공 → 큐 삭제
                queueRepo.delete(t);

            } catch (Exception ex) {
                int next = t.getRetryCount() + 1;
                if (next >= MAX_RETRY) {
                    dlqRepo.save(DataEsProjectionDlqEntity.builder()
                            .dataId(t.getDataId())
                            .deltaDownload(t.getDeltaDownload())
                            .setDeleted(t.getSetDeleted())
                            .lastError(truncate(ex.getMessage(), 2000))
                            .build());
                    queueRepo.delete(t);
                } else {
                    t.setStatus(DataEsProjectionType.RETRYING);
                    t.setRetryCount(next);
                    t.setLastError(truncate(ex.getMessage(), 2000));
                    t.setNextRunAt(LocalDateTime.now().plusSeconds(backoffSeconds(next)));
                    // Dirty Checking → 커밋 시 UPDATE
                }
                LoggerFactory.elastic().logError("data_index", "ES 반영 실패 dataId=" + t.getDataId(), ex);
            }
        }
    }

    private String truncate(String s, int n) {
        if (s == null) return null;
        return s.length() <= n ? s : s.substring(0, n);
    }
}
