package com.dataracy.modules.like.application.port.out.command;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface SendLikeEventPort {
    /**
     * 지정된 대상에 대한 좋아요 이벤트를 전송합니다.
     *
     * @param targetType 이벤트가 발생한 대상의 유형
     * @param targetId 이벤트가 발생한 대상의 고유 식별자
     * @param previouslyLiked 사용자가 이전에 해당 대상을 좋아요 했는지 여부
     */
    void sendLikeEvent(TargetType targetType, Long targetId, boolean previouslyLiked);
}
