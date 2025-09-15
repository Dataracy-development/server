package com.dataracy.modules.like.application.port.out.command;

import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.model.Like;

public interface  LikeCommandPort {
    /**
     * 새로운 좋아요 엔티티를 저장합니다.
     *
     * @param like 저장할 Like 도메인 객체
     */
    void save(Like like);

    /**
     * 특정 사용자와 대상에 연결된 좋아요를 취소합니다.
     *
     * @param userId 좋아요를 취소할 사용자의 고유 ID
     * @param targetId 좋아요가 취소될 대상의 고유 ID
     * @param targetType 대상의 유형을 나타내는 값
     */
    void cancelLike(Long userId, Long targetId, TargetType targetType);
}
