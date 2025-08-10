package com.dataracy.modules.like.application.port.out.query;

import com.dataracy.modules.like.domain.enums.TargetType;

import java.util.List;

public interface ReadLikePort {
    /**
     * 사용자가 지정한 대상 ID 목록 중에서 좋아요를 누른 대상의 ID 목록을 반환합니다.
     *
     * @param userId      좋아요를 확인할 사용자 ID
     * @param targetIds   확인할 대상 ID 목록
     * @param targetType  대상의 타입
     * @return            사용자가 좋아요를 누른 대상 ID 목록
     */
    List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType);
}
