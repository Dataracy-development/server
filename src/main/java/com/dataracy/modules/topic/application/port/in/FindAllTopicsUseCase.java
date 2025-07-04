package com.dataracy.modules.topic.application.port.in;

import com.dataracy.modules.topic.application.dto.response.AllTopicsResponse;

/**
 * 전체 토픽 조회 유스케이스
 */
public interface FindAllTopicsUseCase {
    AllTopicsResponse allTopics();
}
