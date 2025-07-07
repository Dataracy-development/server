package com.dataracy.modules.topic.application.port.in;

/**
 * 해당 토픽 존재 유무 확인 유스케이스
 */
public interface IsExistTopicUseCase {
    void validateTopicById(Long topicId);
}
