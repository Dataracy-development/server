package com.dataracy.modules.like.application.service.command;

import com.dataracy.modules.comment.application.port.in.query.validate.ValidateCommentUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.command.LikeTargetUseCase;
import com.dataracy.modules.like.application.port.out.command.LikeCommandPort;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import com.dataracy.modules.project.application.port.in.validate.ValidateProjectUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LikeCommandService implements LikeTargetUseCase {
    private final LikeCommandPort likeCommandPort;
    private final SendLikeEventPort sendLikeEventPort;

    // Use Case 상수 정의
    private static final String LIKE_TARGET_USE_CASE = "LikeTargetUseCase";

    private final ValidateProjectUseCase validateProjectUseCase;
    private final ValidateCommentUseCase validateCommentUseCase;

    /**
     * 사용자가 프로젝트 또는 댓글에 대해 좋아요 또는 좋아요 취소를 수행합니다.
     * 대상 엔티티의 존재를 검증한 후, 이전 좋아요 여부에 따라 좋아요를 저장하거나 취소하며, 성공 시 해당 이벤트를 발행합니다.
     * 
     * 동시성 제어를 위해 분산 락을 적용하여 같은 사용자가 동시에 좋아요/취소를 요청할 때 데이터 일관성을 보장합니다.
     *
     * @param userId 좋아요 또는 좋아요 취소를 요청하는 사용자의 ID
     * @param requestDto 대상 타입, 대상 ID, 이전 좋아요 여부를 포함한 요청 정보
     * @return 처리된 대상의 타입
     * @throws LikeException 좋아요 또는 좋아요 취소 과정에서 실패 시, 대상 및 작업에 따라 도메인별 예외가 발생합니다.
     */
    @Override
    @DistributedLock(
            key = "'lock:like:' + #requestDto.targetType + ':' + #requestDto.targetId() + ':user:' + #userId",
            waitTime = 500L,
            leaseTime = 3000L,
            retry = 3
    )
    @Transactional
    public TargetType likeTarget(Long userId, TargetLikeRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart(LIKE_TARGET_USE_CASE, requestDto.targetType() + " 좋아요 서비스 시작 targetId=" + requestDto.targetId());
        TargetType targetType = TargetType.of(requestDto.targetType());

        validateTarget(targetType, requestDto.targetId());

        if (requestDto.previouslyLiked()) {
            processCancelLike(userId, requestDto.targetId(), targetType);
        } else {
            processAddLike(userId, requestDto.targetId(), targetType);
        }
        
        LoggerFactory.service().logSuccess(LIKE_TARGET_USE_CASE, requestDto.targetType() + " 좋아요 서비스 종료 targetId=" + requestDto.targetId(), startTime);
        return targetType;
    }
    
    private void validateTarget(TargetType targetType, Long targetId) {
        if (targetType.equals(TargetType.PROJECT)) {
            validateProjectUseCase.validateProject(targetId);
        } else if (targetType.equals(TargetType.COMMENT)) {
            validateCommentUseCase.validateComment(targetId);
        }
    }
    
    private void processCancelLike(Long userId, Long targetId, TargetType targetType) {
        try {
            likeCommandPort.cancelLike(userId, targetId, targetType);
            sendLikeEventPort.sendLikeEvent(targetType, targetId, true);
        } catch (Exception e) {
            handleLikeError(targetType, targetId, true);
        }
    }
    
    private void processAddLike(Long userId, Long targetId, TargetType targetType) {
        Like like = Like.of(null, targetId, targetType, userId);
        try {
            likeCommandPort.save(like);
            sendLikeEventPort.sendLikeEvent(targetType, targetId, false);
        } catch (Exception e) {
            handleLikeError(targetType, targetId, false);
        }
    }
    
    private void handleLikeError(TargetType targetType, Long targetId, boolean isCancelling) {
        switch (targetType) {
            case PROJECT -> {
                String message = isCancelling ? "프로젝트 좋아요 취소 실패. targetId=" : "프로젝트 좋아요 실패. targetId=";
                LoggerFactory.service().logWarning(LIKE_TARGET_USE_CASE, message + targetId);
                throw new LikeException(isCancelling ? LikeErrorStatus.FAIL_UNLIKE_PROJECT : LikeErrorStatus.FAIL_LIKE_PROJECT);
            }
            case COMMENT -> {
                String message = isCancelling ? "댓글 좋아요 취소 실패. targetId=" : "댓글 좋아요 실패. targetId=";
                LoggerFactory.service().logWarning(LIKE_TARGET_USE_CASE, message + targetId);
                throw new LikeException(isCancelling ? LikeErrorStatus.FAIL_UNLIKE_COMMENT : LikeErrorStatus.FAIL_LIKE_COMMENT);
            }
        }
    }
}
