package com.dataracy.modules.reference.application.dto.response.allview;

import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;

import java.util.List;

/**
 * 토픽 리스트 조회를 위한 애플리케이션 응답 DTO
 *
 * @param topics 토픽 리스트
 */
public record AllTopicsResponse(List<TopicResponse> topics) {
}
