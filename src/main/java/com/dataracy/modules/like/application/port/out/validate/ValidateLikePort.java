/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.application.port.out.validate;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface ValidateLikePort {
  /**
   * 사용자가 특정 대상(targetType, targetId)을 좋아요(Like)했는지 여부를 반환합니다.
   *
   * @param userId 좋아요 여부를 확인할 사용자 ID
   * @param targetId 좋아요 대상의 ID
   * @param targetType 좋아요 대상의 타입
   * @return 사용자가 해당 대상을 좋아요했으면 true, 아니면 false
   */
  boolean isLikedTarget(Long userId, Long targetId, TargetType targetType);
}
