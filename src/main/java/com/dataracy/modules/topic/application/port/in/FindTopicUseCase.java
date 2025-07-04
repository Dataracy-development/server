package com.dataracy.modules.topic.application.port.in;

/**
 * 도메인 토픽을 조회하는 유스케이스
 */
public interface FindTopicUseCase {
    Long findTopicIdByName(String topicName);
}
