package com.dataracy.modules.dataset.application.worker;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionDlqPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataEsProjectionWorker {
    private final ManageDataProjectionTaskPort manageDataProjectionTaskPort;
    private final LoadDataProjectionTaskPort loadDataProjectionTaskPort;
    private final ManageDataProjectionDlqPort manageDataProjectionDlqPort;

    // ES 어댑터들 (Qualifier로 ES 구현 주입)
    private final SoftDeleteDataPort softDeleteDataEsPort;
    private final UpdateDataDownloadPort updateDataDownloadEsPort;

    private static final int BATCH = 100;
    private static final int MAX_RETRY = 8;

    public DataEsProjectionWorker(
            ManageDataProjectionTaskPort manageDataProjectionTaskPort,
            LoadDataProjectionTaskPort loadDataProjectionTaskPort,
            ManageDataProjectionDlqPort manageDataProjectionDlqPort,
            @Qualifier("softDeleteDataEsAdapter") SoftDeleteDataPort softDeleteDataEsPort,
            @Qualifier("updateDataDownloadEsAdapter") UpdateDataDownloadPort updateDataDownloadEsPort
    ) {
        this.manageDataProjectionTaskPort = manageDataProjectionTaskPort;
        this.loadDataProjectionTaskPort = loadDataProjectionTaskPort;
        this.manageDataProjectionDlqPort = manageDataProjectionDlqPort;
        this.softDeleteDataEsPort = softDeleteDataEsPort;
        this.updateDataDownloadEsPort = updateDataDownloadEsPort;
    }

    private long backoffSeconds(int retryCount) {
        // 1,2,4,8,16,32,64,120(캡)
        if (retryCount >= 8) return 120;
        long shift = Math.max(0, retryCount - 1);
        return 1L << Math.min(shift, 6);
    }

    /**
     * 5초마다 Projection Task를 가져와 개별 Task 단위로 처리
     * 각 Task는 REQUIRES_NEW 트랜잭션으로 실행 → 실패해도 나머지 성공 건은 커밋 유지
     */
    @Transactional
    @Scheduled(fixedDelayString = "PT3S")
    public void run() {
        List<DataEsProjectionTaskEntity> tasks = loadDataProjectionTaskPort.findBatchForWork(
                LocalDateTime.now(),
                List.of(DataEsProjectionType.PENDING, DataEsProjectionType.RETRYING),
                PageRequest.of(0, BATCH)
        );

        for (DataEsProjectionTaskEntity t : tasks) {
            ((DataEsProjectionWorker) AopContext.currentProxy()).processTask(t);
        }
    }

    /**
     * Task 단위 처리 메서드
     * propagation = REQUIRES_NEW → 기존 트랜잭션과 분리하여 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTask(DataEsProjectionTaskEntity t) {
        try {
            // 소프트 삭제/복원
            if (t.getSetDeleted() != null) {
                if (t.getSetDeleted()) {
                    softDeleteDataEsPort.deleteData(t.getDataId());
                } else {
                    softDeleteDataEsPort.restoreData(t.getDataId());
                }
            }

            // 다운로드 수 증가
            Integer delta = t.getDeltaDownload();
            if (delta != null && delta > 0) {
                updateDataDownloadEsPort.increaseDownloadCount(t.getDataId());
            }

            // 성공 → 큐 삭제
            manageDataProjectionTaskPort.delete(t.getId());

        } catch (Exception ex) {
            int next = t.getRetryCount() + 1;
            if (next >= MAX_RETRY) {
                manageDataProjectionDlqPort.save(
                        t.getDataId(),
                        t.getDeltaDownload(),
                        t.getSetDeleted(),
                        truncate(ex.getMessage(), 2000)
                );
                manageDataProjectionTaskPort.delete(t.getId());
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

    private String truncate(String s, int n) {
        if (s == null) return null;
        return s.length() <= n ? s : s.substring(0, n);
    }
}
