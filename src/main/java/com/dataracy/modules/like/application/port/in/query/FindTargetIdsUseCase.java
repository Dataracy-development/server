package com.dataracy.modules.like.application.port.in.query;

import com.dataracy.modules.like.domain.enums.TargetType;

import java.util.List;

public interface FindTargetIdsUseCase {
    /**
     * 지정한 사용자가 주어진 대상 ID 목록 중에서 좋아요를 누른 대상의 ID 목록을 반환합니다.
     *
     * @param userId     사용자의 고유 식별자
     * @param targetIds  확인할 대상의 ID 목록
     * @param targetType 대상의 유형
     * @return           사용자가 좋아요를 누른 대상의 ID 목록
     */
    List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType);
}
