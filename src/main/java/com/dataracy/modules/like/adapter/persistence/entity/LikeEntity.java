package com.dataracy.modules.like.adapter.persistence.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

/**
 * like 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "target_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"targetId", "targetType", "userId"}),
        }
)
public class LikeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private TargetType targetType;

    @Column(nullable = false)
    private Long userId;

    public static LikeEntity toEntity(
            Long targetId,
            TargetType targetType,
            Long userId
    ) {
        return LikeEntity.builder()
                .targetId(targetId)
                .targetType(targetType)
                .userId(userId)
                .build();
    }
}
