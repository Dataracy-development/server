package com.dataracy.modules.reference.application.port.in.topic;

/**
 * 해당 토픽 유효성 유스케이스
 */
public interface ValidateTopicUseCase {
    /**
 * 주어진 토픽 ID에 해당하는 토픽을 검증합니다.
 *
 * @param topicId 검증할 토픽의 ID
 */
void validateTopic(Long topicId);
}
