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

    /**
     * 사용자가 특정 대상에 좋아요를 눌렀는지 여부를 반환합니다.
     *
     * @param userId    좋아요를 확인할 사용자 ID
     * @param targetId  좋아요 대상의 ID
     * @param targetType 대상의 타입
     * @return 사용자가 해당 대상을 좋아요했으면 true, 아니면 false
     */
    @Override
    public boolean hasUserLikedTarget(Long userId, Long targetId, TargetType targetType) {
        return likeQueryRepositoryPort.isLikedTarget(userId, targetId, targetType);
    }

    /**
     * 사용자가 지정한 대상 타입과 대상 ID 목록 중에서 좋아요를 누른 대상의 ID 목록을 반환합니다.
     *
     * @param userId      좋아요를 확인할 사용자 ID
     * @param targetIds   확인할 대상 ID 목록
     * @param targetType  대상의 타입
     * @return            사용자가 좋아요를 누른 대상 ID 목록
     */
    @Override
    public List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType) {
        return likeQueryRepositoryPort.findLikedTargetIds(userId, targetIds, targetType);
    }
}
