package com.dataracy.modules.like.application.service.command;

import com.dataracy.modules.comment.application.port.in.ValidateCommentUseCase;
import com.dataracy.modules.common.support.lock.DistributedLock;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.TargetLikeUseCase;
import com.dataracy.modules.like.application.port.in.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.application.port.out.LikeRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import com.dataracy.modules.project.application.port.in.ValidateProjectUseCase;
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

    private final ValidateProjectUseCase validateProjectUseCase;
    private final ValidateCommentUseCase validateCommentUseCase;

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

        if (!requestDto.isLiked()){
            Like like = Like.of(
                    null,
                    requestDto.targetId(),
                    targetType,
                    userId
            );
            try {
                likeRepositoryPort.save(like);
            } catch (Exception e) {
                switch (targetType) {
                    case PROJECT -> throw new LikeException(LikeErrorStatus.FAIL_LIKE_PROJECT);
                    case COMMENT -> throw new LikeException(LikeErrorStatus.FAIL_LIKE_COMMENT);
                };
            }
        } else {
            try {
                likeRepositoryPort.cancelLike(userId, requestDto.targetId(), targetType);
            } catch (Exception e) {
                log.error("Database error while saving like: {}", e.getMessage());
                switch (targetType) {
                    case PROJECT -> throw new LikeException(LikeErrorStatus.FAIL_UNLIKE_PROJECT);
                    case COMMENT -> throw new LikeException(LikeErrorStatus.FAIL_UNLIKE_COMMENT);
                };
            }
        }

        return targetType;
    }
}
