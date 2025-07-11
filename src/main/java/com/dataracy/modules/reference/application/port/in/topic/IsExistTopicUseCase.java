package com.dataracy.modules.reference.application.port.in.topic;

/**
 * 해당 토픽 존재 유무 확인 유스케이스
 */
public interface IsExistTopicUseCase {
    void validateTopicById(Long topicId);
}
