package com.dataracy.modules.topic.application.mapper;

import com.dataracy.modules.topic.application.dto.response.AllTopicsResponse;
import com.dataracy.modules.topic.domain.model.Topic;

import java.util.List;

/**
 * 토픽 도메인 DTO와 토픽 도메인 모델을 변환하는 매퍼
 */
public class TopicDtoMapper {
    // 토픽 도메인 모델 -> 토픽 도메인 응답 DTO
    public static AllTopicsResponse.TopicResponse toResponseDto(Topic topic) {
        return new AllTopicsResponse.TopicResponse(
                topic.getId(),
                topic.getValue(),
                topic.getLabel()
        );
    }

    // 전체 토픽 리스트 조회 도메인 모델 -> 전체 토픽 리스트 조회 도메인 응답 DTO
    public static AllTopicsResponse toResponseDto(List<Topic> topics) {
        return new AllTopicsResponse(
                topics.stream()
                        .map(TopicDtoMapper::toResponseDto)
                        .toList()
        );
    }
}
