package com.dataracy.modules.like.adapter.jpa.repository;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findTargetLike(Long targetId, TargetType targetType);
}
