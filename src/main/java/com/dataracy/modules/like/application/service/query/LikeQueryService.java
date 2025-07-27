package com.dataracy.modules.like.application.service.query;

import com.dataracy.modules.like.application.port.in.FindTargetIdsUseCase;
import com.dataracy.modules.like.application.port.in.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.application.port.query.LikeQueryRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeQueryService implements ValidateTargetLikeUseCase, FindTargetIdsUseCase {

    private final LikeQueryRepositoryPort likeQueryRepositoryPort;

    @Override
    public boolean hasUserLikedTarget(Long userId, Long targetId, TargetType targetType) {
        return likeQueryRepositoryPort.isLikedTarget(userId, targetId, targetType);
    }

    @Override
    public List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType) {
        return likeQueryRepositoryPort.findLikedTargetIds(userId, targetIds, targetType);
    }
}
