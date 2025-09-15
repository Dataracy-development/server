package com.dataracy.modules.like.adapter.jpa.repository;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {
    /**
     * 지정된 사용자 ID, 타겟 ID, 타겟 타입에 해당하는 LikeEntity를 조회합니다.
     *
     * @param userId    좋아요를 누른 사용자의 ID
     * @param targetId  좋아요 대상의 ID
     * @param targetType 좋아요 대상의 타입
     * @return 조건에 일치하는 LikeEntity가 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    Optional<LikeEntity> findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, TargetType targetType);
}
