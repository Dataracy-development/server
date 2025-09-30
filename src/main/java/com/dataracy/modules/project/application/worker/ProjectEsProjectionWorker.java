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
    
    // Self-injection: Spring 프록시를 통해 REQUIRES_NEW 트랜잭션이 작동하도록 함
    private ProjectEsProjectionWorker self;

    private static final int BATCH = 100;
    private static final int MAX_RETRY = 8;

    /**
     * ProjectEsProjectionWorker에 필요한 포트와 어댑터를 주입하고 필드에 할당하는 생성자.
     *
     * 프로젝트 Elasticsearch 프로젝션 작업의 처리(삭제/복원, 댓글/좋아요/조회수 반영 등)에
     * 필요한 외부 의존성을 초기화합니다.
     */
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
    
    /**
     * Self-injection: 프록시 객체를 주입받아 REQUIRES_NEW 트랜잭션이 작동하도록 합니다.
     * @Lazy를 사용하여 순환 참조 문제를 해결합니다.
     *
     * @param self 현재 빈의 프록시 객체
     */
    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    public void setSelf(ProjectEsProjectionWorker self) {
        this.self = self;
    }

    /**
     * 재시도 횟수에 따른 백오프 간격(초)을 계산한다.
     *
     * 동작:
     * <ul>
     *   <li>retryCount이 8 이상이면 고정 120초를 반환한다.</li>
     *   <li>그렇지 않으면 2^(max(0, retryCount-1)) 초를 반환하되 최대 64초까지 제한된다.</li>
     * </ul>
     *
     * @param retryCount 재시도 누적 횟수
     * @return 대기할 시간(초)
     */
    private long backoffSeconds(int retryCount) {
        if (retryCount >= 8) return 120;
        long shift = Math.max(0, retryCount - 1);
        return 1L << Math.min(shift, 6);
    }

    /**
     * 3초마다 Projection Task를 가져와 개별 Task 단위로 처리
     * 각 Task는 REQUIRES_NEW 트랜잭션으로 실행 → 실패해도 나머지 성공 건은 커밋 유지
     * Self-injection을 통해 프록시 객체를 사용하여 REQUIRES_NEW 트랜잭션이 작동하도록 합니다.
     */
    @Transactional
    @Scheduled(fixedDelayString = "PT3S")
    public void run() {
        List<ProjectEsProjectionTaskEntity> tasks =
                loadProjectProjectionTaskPort.findBatchForWork(LocalDateTime.now(),
                        List.of(ProjectEsProjectionType.PENDING, ProjectEsProjectionType.RETRYING),
                        PageRequest.of(0, BATCH));

        for (ProjectEsProjectionTaskEntity t : tasks) {
            self.processTask(t);
        }
    }

    /**
     * 단일 프로젝트 ES 프로젝션 작업을 독립된 트랜잭션에서 처리한다.
     *
     * 주요 동작:
     * - soft-delete/restore, 댓글/좋아요/조회수 델타를 ES에 적용한다.
     * - 성공 시 해당 작업을 삭제한다.
     * - 예외 발생 시 재시도 횟수를 증가시키고 재시도 한도를 초과하면 DLQ로 저장한 뒤 작업을 삭제한다.
     * - 재시도 시에는 상태를 RETRYING으로 설정하고 nextRunAt을 backoff 정책에 따라 갱신한다.
     * - 발생한 오류는 로깅된다.
     *
     * @param t 처리할 프로젝트 프로젝션 작업 엔티티 (ProjectEsProjectionTaskEntity)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTask(ProjectEsProjectionTaskEntity t) {
        try {
            // 소프트 삭제/복원
            if (t.getSetDeleted() != null) {
                if (Boolean.TRUE.equals(t.getSetDeleted())) {
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
