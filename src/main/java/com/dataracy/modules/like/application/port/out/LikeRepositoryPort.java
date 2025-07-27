package com.dataracy.modules.like.application.port.out;

import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.model.Like;

public interface LikeRepositoryPort {
    void save(Like like);
    void cancelLike(Long targetId, TargetType targetType);
}
