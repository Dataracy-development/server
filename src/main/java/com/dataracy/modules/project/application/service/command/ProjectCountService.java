package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectCommentPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectLikePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ProjectCountService implements
        IncreaseCommentCountUseCase,
        DecreaseCommentCountUseCase,
        IncreaseLikeCountUseCase,
        DecreaseLikeCountUseCase
{
    private final UpdateProjectCommentPort updateProjectCommentDbPort;
    private final UpdateProjectLikePort updateProjectLikeDbPort;
    private final ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    // Use Case 상수 정의
    private static final String INCREASE_COMMENT_COUNT_USE_CASE = "IncreaseCommentCountUseCase";
    private static final String DECREASE_COMMENT_COUNT_USE_CASE = "DecreaseCommentCountUseCase";
    private static final String INCREASE_LIKE_COUNT_USE_CASE = "IncreaseLikeCountUseCase";
    private static final String DECREASE_LIKE_COUNT_USE_CASE = "DecreaseLikeCountUseCase";

    /**
     * 프로젝트의 댓글 및 좋아요 카운트 동기화에 필요한 포트를 주입 받아 서비스 인스턴스를 생성합니다.
     *
     * 주입되는 포트들은 데이터베이스의 카운트 변경을 반영하고(댓글/좋아요), 변경된 델타를 프로젝션 동기화 작업으로 전달합니다.
     */
    public ProjectCountService(
            @Qualifier("updateProjectCommentDbAdapter") UpdateProjectCommentPort updateProjectCommentDbPort,
            @Qualifier("updateProjectLikeDbAdapter") UpdateProjectLikePort updateProjectLikeDbPort,
            ManageProjectProjectionTaskPort manageProjectProjectionTaskPort
    ) {
        this.updateProjectCommentDbPort = updateProjectCommentDbPort;
        this.updateProjectLikeDbPort = updateProjectLikeDbPort;
        this.manageProjectProjectionTaskPort = manageProjectProjectionTaskPort;
    }

    /**
         * 프로젝트의 댓글 수를 1 증가시키고 변경을 프로젝션 동기화 작업으로 등록합니다.
         *
         * 영구 저장소의 댓글 수를 증가시키고(데이터베이스) 동일 트랜잭션 내에서 프로젝션 동기화용 델타(+1)를 큐에 등록합니다.
         * 이 메서드는 프로젝트별 분산 잠금과 트랜잭션 경계 내에서 실행되어 동시성과 일관성을 보장합니다.
         *
         * @param projectId 증가할 대상 프로젝트의 ID
         */
    @Override
    @DistributedLock(
            key = "'lock:project:comment-count:' + #projectId",
            waitTime = 300L,
            leaseTime = 1500L,
            retry = 2
    )
    @Transactional
    public void increaseCommentCount(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart(INCREASE_COMMENT_COUNT_USE_CASE, "프로젝트 댓글 수 증가 서비스 시작 projectId=" + projectId);

        // DB만 확정 (JPA/Jpql 구현체에서 +1)
        updateProjectCommentDbPort.increaseCommentCount(projectId);

        // 같은 트랜잭션에서 큐 적재 → 워커가 ES에 반영/재시도
        manageProjectProjectionTaskPort.enqueueCommentDelta(projectId, +1);

        LoggerFactory.service().logSuccess(INCREASE_COMMENT_COUNT_USE_CASE, "프로젝트 댓글 수 증가 서비스 종료 projectId=" + projectId, startTime);

    }

    /**
     * 지정한 프로젝트의 댓글 수를 1만큼 감소시키고 변경을 영속화한 뒤 프로젝션(검색 인덱스 등)에 반영할 델타를 등록합니다.
     *
     * 메서드는 분산 락과 트랜잭션 내에서 실행되어 동일 프로젝트에 대한 동시성 충돌을 방지하고,
     * 데이터베이스의 카운트를 감소시킨 후 프로젝션 동기화를 위한 델타를 큐에 등록합니다.
     *
     * @param projectId 감소할 대상 프로젝트의 ID
     */
    @Override
    @DistributedLock(
            key = "'lock:project:comment-count:' + #projectId",
            waitTime = 300L,
            leaseTime = 1500L,
            retry = 2
    )
    @Transactional
    public void decreaseCommentCount(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart(DECREASE_COMMENT_COUNT_USE_CASE, "프로젝트 댓글 수 감소 서비스 시작 projectId=" + projectId);

        updateProjectCommentDbPort.decreaseCommentCount(projectId);
        manageProjectProjectionTaskPort.enqueueCommentDelta(projectId, -1);

        LoggerFactory.service().logSuccess(DECREASE_COMMENT_COUNT_USE_CASE, "프로젝트 댓글 수 감소 서비스 종료 projectId=" + projectId, startTime);
    }

    /**
         * 지정한 프로젝트의 좋아요 수를 1 증가시키고, DB에 반영한 뒤 프로젝션 동기화용 델타(+1)를 큐에 등록한다.
         *
         * 메서드는 프로젝트별 분산 락을 획득하고 트랜잭션 내에서 실행되어 동시성과 원자성을 보장한다.
         *
         * @param projectId 좋아요 수를 증가시킬 프로젝트의 ID
         */
    @Override
    @DistributedLock(
            key = "'lock:project:like-count:' + #projectId",
            waitTime = 300L,
            leaseTime = 1500L,
            retry = 2
    )
    @Transactional
    public void increaseLikeCount(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart(INCREASE_LIKE_COUNT_USE_CASE, "프로젝트 좋아요 수 증가 서비스 시작 projectId=" + projectId);

        updateProjectLikeDbPort.increaseLikeCount(projectId);     // DB만 확정
        manageProjectProjectionTaskPort.enqueueLikeDelta(projectId, +1);  // 큐 적재

        LoggerFactory.service().logSuccess(INCREASE_LIKE_COUNT_USE_CASE, "프로젝트 좋아요 수 증가 서비스 종료 projectId=" + projectId, startTime);
    }

    /**
     * 지정한 프로젝트의 좋아요 수를 1 감소시키고, 데이터베이스와 Elasticsearch 인덱스의 값을 동기화합니다.
     *
     * @param projectId 좋아요 수를 감소시킬 프로젝트의 ID
     */
    @Override
    @DistributedLock(
            key = "'lock:project:like-count:' + #projectId",
            waitTime = 300L,
            leaseTime = 1500L,
            retry = 2
    )
    @Transactional
    public void decreaseLikeCount(Long projectId) {
        Instant startTime = LoggerFactory.service().logStart(DECREASE_LIKE_COUNT_USE_CASE, "프로젝트 좋아요 수 감소 서비스 시작 projectId=" + projectId);

        updateProjectLikeDbPort.decreaseLikeCount(projectId);
        manageProjectProjectionTaskPort.enqueueLikeDelta(projectId, -1);

        LoggerFactory.service().logSuccess(DECREASE_LIKE_COUNT_USE_CASE, "프로젝트 좋아요 수 감소 서비스 종료 projectId=" + projectId, startTime);
    }
}
