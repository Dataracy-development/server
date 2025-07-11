package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.AllTopicsResponse;
import com.dataracy.modules.reference.domain.model.Topic;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 토픽 도메인 DTO와 토픽 도메인 모델을 변환하는 매퍼
 */
@Component
public class TopicDtoMapper {
    /**
     * 단일 토픽 도메인 모델을 토픽 응답 DTO로 변환합니다.
     *
     * @param topic 변환할 토픽 도메인 모델
     * @return 변환된 토픽 응답 DTO
     */
    public AllTopicsResponse.TopicResponse toResponseDto(Topic topic) {
        return new AllTopicsResponse.TopicResponse(
                topic.id(),
                topic.value(),
                topic.label()
        );
    }

    // 전체 토픽 리스트 조회 도메인 모델 -> 전체 토픽 리스트 조회 도메인 응답 DTO
    public AllTopicsResponse toResponseDto(List<Topic> topics) {
        return new AllTopicsResponse(
                topics.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
