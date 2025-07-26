package com.dataracy.modules.like.application.service.command;

import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.TargetLikeUseCase;
import com.dataracy.modules.like.application.port.out.LikeRepositoryPort;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeCommandService implements TargetLikeUseCase {

    private final LikeRepositoryPort likeRepositoryPort;

    @Override
    @Transactional
    public void targetLike(Long userId, TargetLikeRequest requestDto) {
        if (!requestDto.isLiked()){
            Like like = Like.of(
                    null,
                    requestDto.targetId(),
                    requestDto.targetType(),
                    userId
            );
            try {
                likeRepositoryPort.save(like);
            } catch (Exception e) {
                switch (requestDto.targetType()) {
                    case PROJECT -> throw new LikeException(LikeErrorStatus.FAIL_LIKE_PROJECT);
                    case COMMENT -> throw new LikeException(LikeErrorStatus.FAIL_LIKE_COMMENT);
                };
            }
        } else {
            try {
                likeRepositoryPort.cancleLike(requestDto.targetId(), requestDto.targetType());
            } catch (Exception e) {
                switch (requestDto.targetType()) {
                    case PROJECT -> throw new LikeException(LikeErrorStatus.FAIL_UNLIKE_PROJECT);
                    case COMMENT -> throw new LikeException(LikeErrorStatus.FAIL_UNLIKE_COMMENT);
                };
            }
        }
    }
}
