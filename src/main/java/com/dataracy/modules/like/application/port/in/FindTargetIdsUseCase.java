package com.dataracy.modules.like.application.port.in;

import com.dataracy.modules.like.domain.enums.TargetType;

import java.util.List;

public interface FindTargetIdsUseCase {
    List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType);
}
