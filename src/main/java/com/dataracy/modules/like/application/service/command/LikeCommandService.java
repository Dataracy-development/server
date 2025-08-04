package com.dataracy.modules.like.application.service.command;

import com.dataracy.modules.comment.application.port.in.ValidateCommentUseCase;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.TargetLikeUseCase;
import com.dataracy.modules.like.application.port.out.LikeKafkaProducerPort;
import com.dataracy.modules.like.application.port.out.LikeRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import com.dataracy.modules.project.application.port.in.validate.ValidateProjectUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeCommandService implements
        TargetLikeUseCase
{

    private final LikeRepositoryPort likeRepositoryPort;
    private final LikeKafkaProducerPort likeKafkaProducerPort;

    private final ValidateProjectUseCase validateProjectUseCase;
    private final ValidateCommentUseCase validateCommentUseCase;

    /**
     * 사용자가 프로젝트 또는 댓글에 대해 좋아요 또는 좋아요 취소를 수행합니다.
     *
     * 대상 엔티티의 존재를 검증한 후, 이전 좋아요 여부에 따라 좋아요를 저장하거나 취소하며, 성공 시 해당 이벤트를 Kafka로 발행합니다. 동시성 제어를 위해 대상 타입, 대상 ID, 사용자 ID를 조합한 분산 락이 적용됩니다.
     *
     * @param userId 좋아요 또는 좋아요 취소를 수행하는 사용자의 ID
     * @param requestDto 대상 타입, 대상 ID, 이전 좋아요 여부를 포함한 요청 정보
     * @return 좋아요 또는 좋아요 취소가 적용된 대상의 타입
     * @throws LikeException 데이터베이스 오류 등으로 인해 좋아요 또는 좋아요 취소에 실패한 경우, 대상 타입과 작업에 따라 도메인별 예외가 발생합니다.
     */
    @Override
    @Transactional
    @DistributedLock(
            key = "'lock:like:' + #requestDto.targetType + ':' + #requestDto.targetId() + ':user:' + #userId",
            waitTime = 300L,
            leaseTime = 2000L,
            retry = 2
    )
    public TargetType targetLike(Long userId, TargetLikeRequest requestDto) {

        TargetType targetType = TargetType.of(requestDto.targetType());

        if (targetType.equals(TargetType.PROJECT)) {
            validateProjectUseCase.validateProject(requestDto.targetId());
        } else if (targetType.equals(TargetType.COMMENT)) {
            validateCommentUseCase.validateComment(requestDto.targetId());
        }

        if (requestDto.previouslyLiked()){
            try {
                likeRepositoryPort.cancelLike(userId, requestDto.targetId(), targetType);
                switch (targetType) {
                    case PROJECT -> likeKafkaProducerPort.sendProjectLikeDecreaseEvent(requestDto.targetId());
                    case COMMENT -> likeKafkaProducerPort.sendCommentLikeDecreaseEvent(requestDto.targetId());
                };
            } catch (Exception e) {
                log.error("Database error while saving like: {}", e.getMessage());
                switch (targetType) {
                    case PROJECT -> throw new LikeException(LikeErrorStatus.FAIL_UNLIKE_PROJECT);
                    case COMMENT -> throw new LikeException(LikeErrorStatus.FAIL_UNLIKE_COMMENT);
                };
            }
        } else {
            Like like = Like.of(
                    null,
                    requestDto.targetId(),
                    targetType,
                    userId
            );
            try {
                likeRepositoryPort.save(like);
                switch (targetType) {
                    case PROJECT -> likeKafkaProducerPort.sendProjectLikeIncreaseEvent(requestDto.targetId());
                    case COMMENT -> likeKafkaProducerPort.sendCommentLikeIncreaseEvent(requestDto.targetId());
                };
            } catch (Exception e) {
                switch (targetType) {
                    case PROJECT -> throw new LikeException(LikeErrorStatus.FAIL_LIKE_PROJECT);
                    case COMMENT -> throw new LikeException(LikeErrorStatus.FAIL_LIKE_COMMENT);
                };
            }
        }

        return targetType;
    }
}
