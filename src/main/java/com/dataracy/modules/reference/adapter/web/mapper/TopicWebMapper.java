package com.dataracy.modules.reference.adapter.web.mapper;

import com.dataracy.modules.reference.adapter.web.response.allview.AllTopicsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 토픽 웹 DTO와 토픽 도메인 DTO를 변환하는 매퍼
 */
@Component
public class TopicWebMapper {
    /**
     * 도메인 토픽 응답 DTO를 웹 응답 DTO로 변환합니다.
     *
     * @param topicResponse 변환할 도메인 토픽 응답 DTO
     * @return 변환된 토픽 웹 응답 DTO
     */
    public TopicWebResponse toWebDto(TopicResponse topicResponse) {
        return new TopicWebResponse(
                topicResponse.id(),
                topicResponse.value(),
                topicResponse.label()
        );
    }

    // 전체 토픽 리스트 조회 도메인 응답 DTO -> 전체 토픽 리스트 조회 웹 응답 DTO
    public AllTopicsWebResponse toWebDto(AllTopicsResponse allTopicsResponse) {
        if (allTopicsResponse == null || allTopicsResponse.topics() == null) {
            return new AllTopicsWebResponse(List.of());
        }

        return new AllTopicsWebResponse(
                allTopicsResponse.topics()
                        .stream()
                        .map(this::toWebDto)
                        .toList()
        );
    }
}
