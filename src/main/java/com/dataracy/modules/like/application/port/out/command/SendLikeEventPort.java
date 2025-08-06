package com.dataracy.modules.like.application.port.out.command;

import com.dataracy.modules.like.domain.enums.TargetType;

public interface SendLikeEventPort {
    void sendLikeEvent(TargetType targetType, Long targetId, boolean previouslyLiked);
}
