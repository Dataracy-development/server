package com.dataracy.modules.like.application.port.in.validate;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface ValidateTargetLikeUseCase {
    /**
 * 사용자가 특정 타입의 타겟을 이미 좋아요 했는지 여부를 반환합니다.
 *
 * @param userId    확인할 사용자의 ID
 * @param targetId  확인할 타겟의 ID
 * @param targetType 타겟의 타입
 * @return 사용자가 해당 타겟을 좋아요 했으면 true, 아니면 false
 */
boolean hasUserLikedTarget(Long userId, Long targetId, TargetType targetType);
}
