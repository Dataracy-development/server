package com.dataracy.modules.like.adapter.jpa.impl;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.adapter.jpa.mapper.LikeEntityMapper;
import com.dataracy.modules.like.adapter.jpa.repository.LikeJpaRepository;
import com.dataracy.modules.like.application.port.out.LikeRepositoryPort;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryAdapter implements LikeRepositoryPort {
    private final LikeJpaRepository likeJpaRepository;

    /**
     * 도메인 Like 객체를 JPA 엔티티로 변환하여 데이터베이스에 저장합니다.
     *
     * @param like 저장할 Like 도메인 객체
     */
    @Override
    public void save(Like like) {
        LikeEntity entity = LikeEntityMapper.toEntity(like);
        likeJpaRepository.save(entity);
    }

    /**
     * 주어진 사용자 ID, 타겟 ID, 타겟 타입에 해당하는 좋아요를 취소합니다.
     *
     * 해당 조건에 맞는 좋아요가 존재하지 않으면 LikeException이 발생합니다.
     *
     * @param userId    좋아요를 취소할 사용자 ID
     * @param targetId  좋아요를 취소할 대상의 ID
     * @param targetType 좋아요를 취소할 대상의 타입
     * @throws LikeException 해당 조건에 맞는 좋아요가 없을 때 발생
     */
    @Override
    public void cancelLike(Long userId, Long targetId, TargetType targetType) {
        LikeEntity entity = likeJpaRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
                .orElseThrow(() -> new LikeException(LikeErrorStatus.NOT_FOUND_TARGET_LIKE));
        likeJpaRepository.delete(entity);
    }
}
