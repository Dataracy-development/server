package com.dataracy.modules.like.application.port.in;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface ValidateTargetLikeUseCase {
    boolean isValidateTarget(Long projectId, TargetType targetType);
}
