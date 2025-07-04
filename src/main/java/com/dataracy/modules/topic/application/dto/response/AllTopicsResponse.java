package com.dataracy.modules.topic.application.dto.response;

import java.util.List;

/**
 * 토픽 리스트 조회를 위한 도메인 응답 DTO
 * @param topics 토픽 리스트
 */
public record AllTopicsResponse(List<TopicResponse> topics) {
    public record TopicResponse(Long id, String value, String label
    ) {}
}
