package com.dataracy.modules.like.domain.model;

import com.dataracy.modules.like.domain.enums.TargetType;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Like {
    private Long id;
    private Long targetId;
    private TargetType targetType;
    private Long userId;

    /**
     * 주어진 값들로 새로운 Like 인스턴스를 생성합니다.
     *
     * @param id Like 엔티티의 고유 식별자
     * @param targetId 좋아요가 지정된 대상의 식별자
     * @param targetType 좋아요 대상의 타입
     * @param userId 좋아요를 남긴 사용자의 식별자
     * @return 지정된 값들로 생성된 Like 객체
     */
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
