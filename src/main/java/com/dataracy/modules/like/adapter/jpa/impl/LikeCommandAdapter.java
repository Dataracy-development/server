package com.dataracy.modules.like.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.adapter.jpa.mapper.LikeEntityMapper;
import com.dataracy.modules.like.adapter.jpa.repository.LikeJpaRepository;
import com.dataracy.modules.like.application.port.out.command.LikeCommandPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeCommandAdapter implements LikeCommandPort {
    private final LikeJpaRepository likeJpaRepository;

    /**
     * Like 도메인 객체를 JPA 엔티티로 변환하여 데이터베이스에 저장합니다.
     *
     * @param like 저장할 Like 도메인 객체
     */
    @Override
    public void save(Like like) {
        LikeEntity entity = LikeEntityMapper.toEntity(like);
        LikeEntity savedLike = likeJpaRepository.save(entity);
        LoggerFactory.db().logSave("LikeEntity", String.valueOf(savedLike.getId()), "타겟 좋아요가 완료되었습니다. likeId=" + savedLike.getId());
    }

    /**
     * 주어진 사용자 ID, 타겟 ID, 타겟 타입에 해당하는 좋아요를 취소합니다.
     * 해당 조건에 맞는 좋아요가 존재하지 않을 경우 LikeException이 발생합니다.
     *
     * @param userId 좋아요를 취소할 사용자 ID
     * @param targetId 좋아요를 취소할 대상의 ID
     * @param targetType 좋아요를 취소할 대상의 타입
     * @throws LikeException 해당 조건에 맞는 좋아요가 존재하지 않을 때 발생
     */
    @Override
    public void cancelLike(Long userId, Long targetId, TargetType targetType) {
        LikeEntity entity = likeJpaRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("LikeEntity", "해당 타겟 좋아요 리소스가 존재하지 않습니다. targetType=" + targetType + ", targetId=" + targetId);
                    return new LikeException(LikeErrorStatus.NOT_FOUND_TARGET_LIKE);
                });
        likeJpaRepository.delete(entity);
        LoggerFactory.db().logDelete("LikeEntity", String.valueOf(entity.getId()), "타겟 좋아요 취소가 완료되었습니다.");
    }
}
