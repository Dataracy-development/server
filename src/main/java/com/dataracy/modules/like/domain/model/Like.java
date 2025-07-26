package com.dataracy.modules.like.domain.model;

import com.dataracy.modules.like.domain.enums.TargetType;
import lombok.*;

/**
 * like 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Like {
    private Long id;
    private Long targetId;
    private TargetType targetType;
    private Long userId;

    public static Like of(
            Long id,
            Long targetId,
            TargetType targetType,
            Long userId
    ) {
        return Like.builder()
                .id(id)
                .targetId(targetId)
                .targetType(targetType)
                .userId(userId)
                .build();
    }
}
