package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.domain.model.Topic;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 토픽 도메인 DTO와 토픽 도메인 모델을 변환하는 매퍼
 */
@Component
public class TopicDtoMapper {
    /**
     * 단일 Topic 도메인 모델을 TopicResponse DTO로 변환합니다.
     *
     * @param topic 변환할 Topic 도메인 객체
     * @return 변환된 TopicResponse DTO
     */
    public TopicResponse toResponseDto(Topic topic) {
        return new TopicResponse(
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
