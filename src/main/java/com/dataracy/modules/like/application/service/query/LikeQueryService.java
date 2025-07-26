package com.dataracy.modules.like.application.service.query;

import com.dataracy.modules.like.application.port.in.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.application.port.query.LikeQueryRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeQueryService implements ValidateTargetLikeUseCase {

    private final LikeQueryRepositoryPort likeQueryRepositoryPort;

    @Override
    public boolean isValidateTarget(Long userId, Long projectId, TargetType targetType) {
        return likeQueryRepositoryPort.isLikedTarget(userId, projectId, targetType);
    }
}
