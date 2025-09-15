package com.dataracy.modules.like.application.port.in.command;

import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.domain.enums.TargetType;

public interface LikeTargetUseCase {
    /**
     * 사용자가 특정 대상을 좋아요 처리한 후, 해당 대상의 타입을 반환합니다.
     *
     * @param userId 좋아요를 수행하는 사용자의 식별자
     * @param requestDto 좋아요 대상 및 요청 정보를 포함하는 객체
     * @return 좋아요가 적용된 대상의 타입
     */
    TargetType likeTarget(Long userId, TargetLikeRequest requestDto);
}
