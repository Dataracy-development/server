package com.dataracy.modules.like.adapter.web.mapper;

import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import org.springframework.stereotype.Component;

@Component
public class LikeWebMapper {
    /**
     * TargetLikeWebRequest 객체를 TargetLikeRequest 애플리케이션 DTO로 변환합니다.
     *
     * @param webRequest 변환할 웹 요청 객체
     * @return 변환된 TargetLikeRequest DTO
     */
    public TargetLikeRequest toApplicationDto(TargetLikeWebRequest webRequest) {
        return new TargetLikeRequest(
                webRequest.targetId(),
                webRequest.targetType(),
                webRequest.previouslyLiked()
        );
    }
}
