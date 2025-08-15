package com.dataracy.modules.project.application.worker;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.EsProjectionDlqEntity;
import com.dataracy.modules.project.adapter.jpa.entity.EsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.EsProjectionDlqRepository;
import com.dataracy.modules.project.adapter.jpa.repository.EsProjectionTaskRepository;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectCommentPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectLikePort;
import com.dataracy.modules.project.domain.enums.EsProjectionStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EsProjectionWorker {
    private final EsProjectionTaskRepository queueRepo;
    private final EsProjectionDlqRepository dlqRepo;

    // 이미 가진 ES 어댑터 (painless 스크립트로 증감)
    private final SoftDeleteProjectPort softDeleteProjectEsPort;
    private final UpdateProjectCommentPort updateProjectCommentEsPort;
    private final UpdateProjectLikePort updateProjectLikeEsPort;

    private static final int BATCH = 100;
    private static final int MAX_RETRY = 8;

    public EsProjectionWorker(
            EsProjectionTaskRepository esProjectionTaskRepository,
            EsProjectionDlqRepository esProjectionDlqRepository,
            @Qualifier("softDeleteProjectEsAdapter") SoftDeleteProjectPort softDeleteProjectEsPort,
            @Qualifier("updateProjectCommentEsAdapter") UpdateProjectCommentPort updateProjectCommentEsPort,
            @Qualifier("updateProjectLikeEsAdapter") UpdateProjectLikePort updateProjectLikeEsPort
    ) {
        this.queueRepo = esProjectionTaskRepository;
        this.dlqRepo = esProjectionDlqRepository;
        this.softDeleteProjectEsPort = softDeleteProjectEsPort;
        this.updateProjectCommentEsPort = updateProjectCommentEsPort;
        this.updateProjectLikeEsPort = updateProjectLikeEsPort;
    }

    private long backoffSeconds(int retryCount) {
        // 1,2,4,8,16,32,64,120(캡)
        return Math.min(120, 1L << Math.min(retryCount, 6));
    }

    @Transactional
    @Scheduled(fixedDelayString = "PT1S")
    public void run() {
        List<EsProjectionTaskEntity> tasks =
                queueRepo.findBatchForWork(LocalDateTime.now(),
                        List.of(EsProjectionStatus.PENDING, EsProjectionStatus.RETRYING),
                        PageRequest.of(0, BATCH));

        for (EsProjectionTaskEntity t : tasks) {
            try {
                // 소프트 삭제/복원 먼저
                if (t.getSetDeleted() != null) {
                    if (t.getSetDeleted()) {
                        softDeleteProjectEsPort.deleteProject(t.getProjectId());   // isDeleted=true 설정
                    } else {
                        softDeleteProjectEsPort.restoreProject(t.getProjectId());  // isDeleted=false 설정(※ upsert 권장)
                    }
                }

                // 댓글 델타 적용 (있으면)
                if (t.getDeltaComment() > 0) {
                    updateProjectCommentEsPort.increaseCommentCount(t.getProjectId());
                } else if (t.getDeltaComment() < 0) {
                    updateProjectCommentEsPort.decreaseCommentCount(t.getProjectId());
                }

                // 좋아요 델타 적용 (있으면)
                if (t.getDeltaLike() > 0) {
                    // 네가 이미 가진 ES 어댑터 사용
                    updateProjectLikeEsPort.increaseLikeCount(t.getProjectId());
                } else if (t.getDeltaLike() < 0) {
                    updateProjectLikeEsPort.decreaseLikeCount(t.getProjectId());
                }

                // 성공 → 큐 삭제
                queueRepo.delete(t);

            } catch (Exception ex) {
                // 실패 → 재시도 or DLQ
                int nextRetry = t.getRetryCount() + 1;
                if (nextRetry >= MAX_RETRY) {
                    dlqRepo.save(EsProjectionDlqEntity.builder()
                            .projectId(t.getProjectId())
                            .deltaComment(t.getDeltaComment())
                            .deltaLike(t.getDeltaLike())
                            .error(truncate(ex.getMessage(), 2000))
                            .build());
                    queueRepo.delete(t);
                } else {
                    t.setStatus(EsProjectionStatus.RETRYING);
                    t.setRetryCount(nextRetry);
                    t.setLastError(truncate(ex.getMessage(), 2000));
                    t.setNextRunAt(LocalDateTime.now().plusSeconds(backoffSeconds(nextRetry)));
                    // JPA dirty checking으로 update
                }

                LoggerFactory.elastic().logError("project_index",
                        "ES 반영 실패 - projectId=" + t.getProjectId(), ex);
            }
        }
    }

    private String truncate(String msg, int n) {
        if (msg == null) return null;
        return msg.length() <= n ? msg : msg.substring(0, n);
    }
}
