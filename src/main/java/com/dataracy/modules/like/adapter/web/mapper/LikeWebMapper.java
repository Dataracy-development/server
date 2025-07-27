package com.dataracy.modules.like.adapter.web.mapper;

import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import org.springframework.stereotype.Component;

@Component
public class LikeWebMapper {

    public TargetLikeRequest toApplicationDto(TargetLikeWebRequest webRequest) {
        return new TargetLikeRequest(
                webRequest.targetId(),
                webRequest.targetType(),
                webRequest.previouslyLiked()
        );
    }
}
