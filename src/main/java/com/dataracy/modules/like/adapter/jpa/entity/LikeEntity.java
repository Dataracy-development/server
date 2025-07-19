package com.dataracy.modules.like.adapter.jpa.entity;

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
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(nullable = false)
    private Long userId;

    /**
     * 주어진 대상 ID, 대상 타입, 사용자 ID로 새로운 LikeEntity 인스턴스를 생성합니다.
     *
     * @param targetId 좋아요가 지정된 대상의 ID
     * @param targetType 좋아요 대상의 타입
     * @param userId 좋아요를 누른 사용자의 ID
     * @return 생성된 LikeEntity 객체
     */
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
