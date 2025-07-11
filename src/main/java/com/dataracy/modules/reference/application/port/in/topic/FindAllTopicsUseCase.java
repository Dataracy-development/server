package com.dataracy.modules.reference.application.port.in.topic;

import com.dataracy.modules.reference.application.dto.response.AllTopicsResponse;

/**
 * 전체 토픽 조회 유스케이스
 */
public interface FindAllTopicsUseCase {
    /**
 * 모든 토픽 정보를 조회하여 반환합니다.
 *
 * @return 전체 토픽 목록이 포함된 AllTopicsResponse 객체
 */
AllTopicsResponse allTopics();
}
