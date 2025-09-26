package com.dataracy.modules.dataset.application.worker;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionDlqPort;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    /**
     * DataEsProjectionWorker 인스턴스를 생성합니다.
     *
     * 생성자에 주입된 포트들은 Elasticsearch 기반 데이터 프로젝션 작업의 로드·처리·DLQ 관리,
     * soft-delete 및 다운로드 카운트 업데이트에 사용됩니다. 특히
     * `softDeleteDataEsPort`와 `updateDataDownloadEsPort`는 각각 ES 구현으로 주입됩니다.
     */
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

    /**
     * 재시도 횟수에 따라 지수형(backoff) 대기 시간을 초 단위로 계산한다.
     *
     * 동작:
     * - retryCount가 8 이상이면 최대값인 120초를 반환한다.
     * - 그 외에는 1, 2, 4, 8, 16, 32, 64의 시퀀스 중 하나를 반환한다.
     *
     * @param retryCount 0 이상의 재시도 카운트(현재까지 시도한 횟수)
     * @return 대기 시간(초)
     */
    private long backoffSeconds(int retryCount) {
        // 1,2,4,8,16,32,64,120(캡)
        if (retryCount >= 8) return 120;
        long shift = Math.max(0, retryCount - 1);
        return 1L << Math.min(shift, 6);
    }

    /**
     * 3초마다 대기중(PENDING) 또는 재시도(RETRYING) 상태의 Projection 작업을 배치로 조회하여 각 작업을 비동기로 처리합니다.
     *
     * 개선사항:
     * - 트랜잭션 경계 제거: 전체 배치에 대한 트랜잭션 경계를 제거하여 성능 향상
     * - 비동기 처리: 각 작업을 비동기로 처리하여 병렬성 향상
     * - 독립성 보장: 각 작업은 여전히 독립적인 트랜잭션에서 실행
     */
    @Scheduled(fixedDelayString = "PT3S")
    public void run() {
        List<DataEsProjectionTaskEntity> tasks = loadDataProjectionTaskPort.findBatchForWork(
                LocalDateTime.now(),
                List.of(DataEsProjectionType.PENDING, DataEsProjectionType.RETRYING),
                PageRequest.of(0, BATCH)
        );

        // 각 작업을 비동기로 처리
        List<CompletableFuture<Void>> futures = tasks.stream()
                .map(this::processTaskAsync)
                .toList();

        // 모든 작업 완료 대기 (선택적)
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(throwable -> {
                    LoggerFactory.elastic().logError("data_index", "비동기 작업 처리 중 예외 발생", throwable);
                    return null;
                });
    }

    /**
     * 단일 데이터 반영 작업을 비동기로 처리합니다.
     *
     * @param t 작업 대상 엔터티
     * @return CompletableFuture<Void> 비동기 처리 결과
     */
    public CompletableFuture<Void> processTaskAsync(DataEsProjectionTaskEntity t) {
        return CompletableFuture.runAsync(() -> processTask(t));
    }

    /**
     * 단일 데이터 반영 작업(큐 항목)을 독립된 새 트랜잭션으로 처리한다.
     *
     * 작업 엔터티에 따라 다음 중 하나 이상의 동작을 수행한다:
     * <ul>
     *   <li>setDeleted 필드가 지정되면 해당 데이터에 대해 소프트 삭제 또는 복원을 수행</li>
     *   <li>deltaDownload가 양수이면 다운로드 카운트를 증가</li>
     *   <li>처리 성공 시 해당 작업 큐 항목을 삭제</li>
     * </ul>
     *
     * 처리 중 예외가 발생하면 내부에서 재시도 로직을 적용한다:
     * <ul>
     *   <li>재시도 횟수가 MAX_RETRY 이상이면 DLQ에 실패 정보를 저장하고 큐 항목을 삭제</li>
     *   <li>아직 재시도 가능하면 작업 상태를 RETRYING으로 업데이트하고 retryCount, lastError, nextRunAt을 설정(커밋 시 영속성 컨텍스트의 더티 체크로 반영)</li>
     * </ul>
     *
     * 이 메서드는 예외를 밖으로 던지지 않고 내부에서 처리하므로 호출자는 예외 처리를 신경 쓸 필요가 없다.
     *
     * @param t 작업 대상 엔터티(데이터 ID, deltaDownload, setDeleted, retry 관련 필드 포함)
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
            if (t.getDeltaDownload() > 0) {
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
