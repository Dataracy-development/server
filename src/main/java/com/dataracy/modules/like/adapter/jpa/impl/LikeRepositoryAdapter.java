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

@Repository
@RequiredArgsConstructor
public class LikeRepositoryAdapter implements LikeRepositoryPort {
    private final LikeJpaRepository likeJpaRepository;

    @Override
    public void save(Like like) {
        LikeEntity entity = LikeEntityMapper.toEntity(like);
        likeJpaRepository.save(entity);
    }

    @Override
    public void cancleLike(Long targetId, TargetType targetType) {
        LikeEntity entity = likeJpaRepository.findTargetLike(targetId, targetType)
                .orElseThrow(() -> new LikeException(LikeErrorStatus.NOT_FOUND_TARGET_LIKE));
        likeJpaRepository.delete(entity);
    }
}
