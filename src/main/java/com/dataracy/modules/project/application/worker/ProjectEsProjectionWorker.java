package com.dataracy.modules.project.application.worker;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionDlqPort;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectCommentPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectLikePort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;
import com.dataracy.modules.project.application.port.out.query.projection.LoadProjectProjectionTaskPort;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProjectEsProjectionWorker {
    private final ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;
    private final ManageProjectProjectionDlqPort manageProjectProjectionDlqPort;
    private final LoadProjectProjectionTaskPort loadProjectProjectionTaskPort;

    private final SoftDeleteProjectPort softDeleteProjectEsPort;
    private final UpdateProjectCommentPort updateProjectCommentEsPort;
    private final UpdateProjectLikePort updateProjectLikeEsPort;
    private final UpdateProjectViewPort updateProjectViewEsPort;

    private static final int BATCH = 100;
    private static final int MAX_RETRY = 8;

    public ProjectEsProjectionWorker(
            ManageProjectProjectionTaskPort manageProjectProjectionTaskPort,
            ManageProjectProjectionDlqPort manageProjectProjectionDlqPort,
            LoadProjectProjectionTaskPort loadProjectProjectionTaskPort,
            @Qualifier("softDeleteProjectEsAdapter") SoftDeleteProjectPort softDeleteProjectEsPort,
            @Qualifier("updateProjectCommentEsAdapter") UpdateProjectCommentPort updateProjectCommentEsPort,
            @Qualifier("updateProjectLikeEsAdapter") UpdateProjectLikePort updateProjectLikeEsPort,
            @Qualifier("updateProjectViewEsAdapter") UpdateProjectViewPort updateProjectViewEsPort
    ) {
        this.manageProjectProjectionTaskPort = manageProjectProjectionTaskPort;
        this.manageProjectProjectionDlqPort = manageProjectProjectionDlqPort;
        this.loadProjectProjectionTaskPort = loadProjectProjectionTaskPort;
        this.softDeleteProjectEsPort = softDeleteProjectEsPort;
        this.updateProjectCommentEsPort = updateProjectCommentEsPort;
        this.updateProjectLikeEsPort = updateProjectLikeEsPort;
        this.updateProjectViewEsPort = updateProjectViewEsPort;
    }

    private long backoffSeconds(int retryCount) {
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
        List<ProjectEsProjectionTaskEntity> tasks =
                loadProjectProjectionTaskPort.findBatchForWork(LocalDateTime.now(),
                        List.of(ProjectEsProjectionType.PENDING, ProjectEsProjectionType.RETRYING),
                        PageRequest.of(0, BATCH));

        for (ProjectEsProjectionTaskEntity t : tasks) {
            processTask(t);
        }
    }

    /**
     * Task 단위 처리 메서드
     * propagation = REQUIRES_NEW → 기존 트랜잭션과 분리하여 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTask(ProjectEsProjectionTaskEntity t) {
        try {
            // 소프트 삭제/복원
            if (t.getSetDeleted() != null) {
                if (t.getSetDeleted()) {
                    softDeleteProjectEsPort.deleteProject(t.getProjectId());
                } else {
                    softDeleteProjectEsPort.restoreProject(t.getProjectId());
                }
            }

            // 댓글 델타
            if (t.getDeltaComment() > 0) {
                updateProjectCommentEsPort.increaseCommentCount(t.getProjectId());
            } else if (t.getDeltaComment() < 0) {
                updateProjectCommentEsPort.decreaseCommentCount(t.getProjectId());
            }

            // 좋아요 델타
            if (t.getDeltaLike() > 0) {
                updateProjectLikeEsPort.increaseLikeCount(t.getProjectId());
            } else if (t.getDeltaLike() < 0) {
                updateProjectLikeEsPort.decreaseLikeCount(t.getProjectId());
            }

            // 조회 델타
            if (t.getDeltaView() > 0) {
                updateProjectViewEsPort.increaseViewCount(t.getProjectId(), t.getDeltaView());
            }

            // 성공 → 큐 삭제
            manageProjectProjectionTaskPort.delete(t.getId());

        } catch (Exception ex) {
            int nextRetry = t.getRetryCount() + 1;
            if (nextRetry >= MAX_RETRY) {
                manageProjectProjectionDlqPort.save(
                        t.getProjectId(),
                        t.getDeltaComment(),
                        t.getDeltaLike(),
                        t.getDeltaView(),
                        t.getSetDeleted(),
                        truncate(ex.getMessage(), 2000)
                );
                manageProjectProjectionTaskPort.delete(t.getId());
            } else {
                t.setStatus(ProjectEsProjectionType.RETRYING);
                t.setRetryCount(nextRetry);
                t.setLastError(truncate(ex.getMessage(), 2000));
                t.setNextRunAt(LocalDateTime.now().plusSeconds(backoffSeconds(nextRetry)));
            }

            LoggerFactory.elastic().logError("project_index",
                    "ES 반영 실패 - projectId=" + t.getProjectId(), ex);
        }
    }

    private String truncate(String msg, int n) {
        if (msg == null) return null;
        return msg.length() <= n ? msg : msg.substring(0, n);
    }
}
