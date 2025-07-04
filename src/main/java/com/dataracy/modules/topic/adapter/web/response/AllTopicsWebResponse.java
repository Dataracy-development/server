package com.dataracy.modules.topic.adapter.web.response;

import java.util.List;

/**
 * 토픽 리스트 조회를 위한 웹응답 DTO
 * @param topics 토픽 리스트
 */
public record AllTopicsWebResponse(List<TopicWebResponse> topics) {
    public record TopicWebResponse(Long id, String value, String label
    ) {}
}
