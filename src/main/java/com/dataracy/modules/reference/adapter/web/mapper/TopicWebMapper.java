package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.AllTopicsWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllTopicsResponse;
import org.springframework.stereotype.Component;

/**
 * 토픽 웹 DTO와 토픽 도메인 DTO를 변환하는 매퍼
 */
@Component
public class TopicWebMapper {
    // 토픽 조회 도메인 응답 DTO -> 토픽 조회 웹 응답 DTO
    public AllTopicsWebResponse.TopicWebResponse toWebDto(AllTopicsResponse.TopicResponse topicResponse) {
        return new AllTopicsWebResponse.TopicWebResponse(
                topicResponse.id(),
                topicResponse.value(),
                topicResponse.label()
        );
    }

    // 전체 토픽 리스트 조회 도메인 응답 DTO -> 전체 토픽 리스트 조회 웹 응답 DTO
    public AllTopicsWebResponse toWebDto(AllTopicsResponse allTopicsResponse) {
        return new AllTopicsWebResponse(
                allTopicsResponse.topics()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
