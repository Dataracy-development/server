package com.dataracy.modules.reference.application.port.in.topic;

import com.dataracy.modules.reference.application.dto.response.AllTopicsResponse;

/**
 * 전체 토픽 조회 유스케이스
 */
public interface FindAllTopicsUseCase {
    AllTopicsResponse allTopics();
}
