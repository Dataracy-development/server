package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseCommentCountUseCase;
import com.dataracy.modules.project.application.port.in.command.count.IncreaseLikeCountUseCase;
import com.dataracy.modules.project.application.port.out.command.projection.EnqueueProjectProjectionPort;
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
    private final EnqueueProjectProjectionPort enqueueProjectProjectionPort;

    /**
     * 프로젝트의 댓글 및 좋아요 수를 동기화하기 위한 서비스 인스턴스를 생성합니다.
     *
     * @param updateProjectCommentDbPort 프로젝트 댓글 수를 데이터베이스에 반영하는 포트
     * @param updateProjectLikeDbPort 프로젝트 좋아요 수를 데이터베이스에 반영하는 포트
     */
    public ProjectCountService(
            @Qualifier("updateProjectCommentDbAdapter") UpdateProjectCommentPort updateProjectCommentDbPort,
            @Qualifier("updateProjectLikeDbAdapter") UpdateProjectLikePort updateProjectLikeDbPort,
            EnqueueProjectProjectionPort enqueueProjectProjectionPort
    ) {
        this.updateProjectCommentDbPort = updateProjectCommentDbPort;
        this.updateProjectLikeDbPort = updateProjectLikeDbPort;
        this.enqueueProjectProjectionPort = enqueueProjectProjectionPort;
    }

    /**
     * 지정한 프로젝트의 댓글 수를 1 증가시킵니다.
     *
     * 데이터베이스와 Elasticsearch 인덱스의 댓글 수를 모두 1씩 증가시켜 동기화합니다.
     *
     * @param projectId 댓글 수를 증가시킬 프로젝트의 ID
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
        Instant startTime = LoggerFactory.service().logStart("IncreaseCommentCountUseCase", "프로젝트 댓글 수 증가 서비스 시작 projectId=" + projectId);

        // DB만 확정 (JPA/Jpql 구현체에서 +1)
        updateProjectCommentDbPort.increaseCommentCount(projectId);

        // 같은 트랜잭션에서 큐 적재 → 워커가 ES에 반영/재시도
        enqueueProjectProjectionPort.enqueueCommentDelta(projectId, +1);

        LoggerFactory.service().logSuccess("IncreaseCommentCountUseCase", "프로젝트 댓글 수 증가 서비스 종료 projectId=" + projectId, startTime);

    }

    /**
     * 프로젝트의 댓글 수를 1 감소시키고, 데이터베이스와 Elasticsearch 인덱스에 동기화합니다.
     *
     * @param projectId 댓글 수를 감소시킬 대상 프로젝트의 ID
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
        Instant startTime = LoggerFactory.service().logStart("DecreaseCommentCountUseCase", "프로젝트 댓글 수 감소 서비스 시작 projectId=" + projectId);

        updateProjectCommentDbPort.decreaseCommentCount(projectId);
        enqueueProjectProjectionPort.enqueueCommentDelta(projectId, -1);

        LoggerFactory.service().logSuccess("DecreaseCommentCountUseCase", "프로젝트 댓글 수 감소 서비스 종료 projectId=" + projectId, startTime);
    }

    /**
     * 지정한 프로젝트의 좋아요 수를 1 증가시키고, 데이터베이스와 Elasticsearch 인덱스의 값을 동기화합니다.
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
        Instant startTime = LoggerFactory.service().logStart("IncreaseLikeCountUseCase", "프로젝트 좋아요 수 증가 서비스 시작 projectId=" + projectId);

        updateProjectLikeDbPort.increaseLikeCount(projectId);     // DB만 확정
        enqueueProjectProjectionPort.enqueueLikeDelta(projectId, +1);  // 큐 적재

        LoggerFactory.service().logSuccess("IncreaseLikeCountUseCase", "프로젝트 좋아요 수 증가 서비스 종료 projectId=" + projectId, startTime);
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
        Instant startTime = LoggerFactory.service().logStart("DecreaseLikeCountUseCase", "프로젝트 좋아요 수 감소 서비스 시작 projectId=" + projectId);

        updateProjectLikeDbPort.decreaseLikeCount(projectId);
        enqueueProjectProjectionPort.enqueueLikeDelta(projectId, -1);

        LoggerFactory.service().logSuccess("DecreaseLikeCountUseCase", "프로젝트 좋아요 수 감소 서비스 종료 projectId=" + projectId, startTime);
    }
}
